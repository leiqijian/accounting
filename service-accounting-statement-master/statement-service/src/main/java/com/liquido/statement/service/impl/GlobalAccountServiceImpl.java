package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.GlobalTargetTypeEnum;
import com.liquido.base.enums.OperateModeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.enums.GlobalAccountStateEnum;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.manage.BizTopupManager;
import com.liquido.statement.pojo.dto.GlobalAccountDto;
import com.liquido.statement.pojo.dto.GlobalAccountInfoDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.GlobalAccount;
import com.liquido.statement.pojo.entity.GlobalStatement;
import com.liquido.statement.pojo.entity.QGlobalAccount;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.GlobalAccountTopupVo;
import com.liquido.statement.pojo.vo.GlobalAccountTransferOutVo;
import com.liquido.statement.pojo.vo.TransactionBizVo;
import com.liquido.statement.repository.GlobalAccountRepository;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.GlobalAccountService;
import com.liquido.statement.service.GlobalStatementService;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalAccountServiceImpl implements GlobalAccountService {
    private final ModelMapper modelMapper;
    private final RedisDistLock redisDistLock;
    private final AccountService accountService;
    private final BizTopupManager bizTopupManager;
    private final JPAQueryFactory jpaQueryFactory;
    private final GlobalAccountRepository repository;
    private final GlobalStatementService globalStatementService;

    @Override
    public GlobalAccount findGlobalAccount(final Long merchantId) {
        final GlobalAccount account = repository.findByMerchantId(merchantId);
        if (Objects.isNull(account) || GlobalAccountStateEnum.DISABLE == account.getState()) {
            return null;
        }
        return account;
    }

    @Override
    public GlobalAccountDto queryGlobalAccount(final Long merchantId) {
        return modelMapper.convert(this.findGlobalAccount(merchantId));
    }

    @Override
    public GlobalAccountInfoDto queryGlobalAccountInfo(final Long merchantId) {
        final GlobalAccountDto globalAccount = this.queryGlobalAccount(merchantId);
        return GlobalAccountInfoDto.builder()
                .globalAccount(globalAccount)
                .openFlag(Objects.isNull(globalAccount) ? Boolean.FALSE : Boolean.TRUE).build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void topupGlobalAccount(final GlobalAccountTopupVo vo) {
        /* data pre-check repeat request (prevent duplicate submissions) */
        this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER,
                vo.getRequestId(), 7, TimeUnit.DAYS);

        final QGlobalAccount entity = QGlobalAccount.globalAccount;
        final GlobalAccount account = this.findGlobalAccount(vo.getMerchantId());
        if (Objects.isNull(account)) {
            throw StatementExceptionCode.UNKNOWN_ACCOUNT.exception();
        }

        if (account.getCurrency() != vo.getCurrency()) {
            log.error("Inconsistent currency types, operation failed, vo={}", vo);
            throw StatementExceptionCode.INCONSISTENT_CURRENCY_TYPES.exception();
        }

        // Tty lock global account
        final String lockVal = DataUtil.getUuid();
        this.tryLockGlobalAccount(account.getId(), lockVal);
        try {
            final boolean result = jpaQueryFactory.update(entity)
                    .set(entity.globalBalance, entity.globalBalance.add(vo.getTransactionAmount()))
                    .set(entity.version, entity.version.add(1))
                    .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                    .where(entity.id.eq(account.getId())
                            .and(entity.version.eq(account.getVersion()))).execute() > 0;

            if (!result) {
                log.error("Global account topup fail, merchant={}", vo.getMerchantId());
                throw StatementExceptionCode.ACCOUNT_TOPUP_FAIL.exception();
            }

            // save account change flow
            globalStatementService.saveAccountFlow(GlobalStatement.builder()
                    .id(SnowflakeIdUtil.generate())
                    .globalAccountId(account.getId())
                    .subAccountId(0L)
                    .businessType(BusinessTypeEnum.TOPUP)
                    .targetType(GlobalTargetTypeEnum.SELF)
                    .amount(vo.getTransactionAmount())
                    .startBalance(account.getGlobalBalance())
                    .endBalance(account.getGlobalBalance().add(vo.getTransactionAmount()))
                    .currency(vo.getCurrency())
                    .createdTime(LocalDateTimeUtil.nowUtc())
                    .updatedTime(LocalDateTimeUtil.nowUtc())
                    .version(1)
                    .build());
        } finally {
            this.unLockGlobalAccount(account.getId(), lockVal);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean transferOutGlobalAccount(final GlobalAccountTransferOutVo vo) {
        log.info("execute globalAccount transferOut params={}", vo);
        final GlobalAccount ga = this.findGlobalAccount(vo.getMerchantId());

        // skip if not config global account;
        if (Objects.isNull(ga)) {
            log.warn("Skip transferOut operation, globalAccount not config params={}", vo);
            return false;
        }

        if (ga.getCurrency() != vo.getCurrency()) {
            log.warn("Skip transferOut operation, Inconsistent currency types params={}", vo);
            return false;
        }

        if (Objects.isNull(ga.getGlobalBalance())
                || ga.getGlobalBalance().compareTo(BigDecimal.ZERO) <= 0
                || vo.getAmount().compareTo(BigDecimal.ZERO) <= 0
                || ga.getGlobalBalance().compareTo(vo.getAmount()) < 0) {
            log.warn("Insufficient global balance, globalAccountId={}, globalBalance={}, "
                            + "subAccountId={}, transactionAmount={}", ga.getId(),
                    ga.getGlobalBalance(), vo.getSubAccountId(), vo.getAmount());
            return false;
        }

        // Tty lock global account
        final String lockVal = DataUtil.getUuid();
        this.tryLockGlobalAccount(ga.getId(), lockVal);
        try {
            final QGlobalAccount entity = QGlobalAccount.globalAccount;
            final boolean result = jpaQueryFactory.update(entity)
                    .set(entity.globalBalance, entity.globalBalance.subtract(vo.getAmount()))
                    .set(entity.version, entity.version.add(1))
                    .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                    .where(entity.id.eq(ga.getId())
                            .and(entity.version.eq(ga.getVersion()))
                            .and(entity.globalBalance.subtract(vo.getAmount())
                                    .goe(BigDecimal.ZERO))).execute() > 0;

            if (!result) {
                throw StatementExceptionCode.ACCOUNT_TRANSFER_OUT_FAIL.exception();
            }

            globalStatementService.saveAccountFlow(GlobalStatement.builder()
                    .id(SnowflakeIdUtil.generate())
                    .globalAccountId(ga.getId())
                    .subAccountId(vo.getSubAccountId())
                    .businessType(BusinessTypeEnum.TRANSFER_OUT)
                    .targetType(GlobalTargetTypeEnum.SUB)
                    .amount(vo.getAmount())
                    .startBalance(ga.getGlobalBalance())
                    .endBalance(ga.getGlobalBalance().subtract(vo.getAmount()))
                    .currency(vo.getCurrency())
                    .createdTime(LocalDateTimeUtil.nowUtc())
                    .updatedTime(LocalDateTimeUtil.nowUtc())
                    .version(1)
                    .build());

            return true;
        } finally {
            this.unLockGlobalAccount(ga.getId(), lockVal);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void autoRechargeToSubAccount(final Long subAccountId) {

        /* Reload real-time account balance*/
        final Account subAccount = accountService.getById(subAccountId);

        /* Just pay-out account can execute auto charge from global account;*/
        if (TransactionTypeCodeEnum.PAY_OUT != subAccount.getTransactionTypeCode()) {
            return;
        }

        final BigDecimal latestDailyBalance = subAccount.getLatestDailyBalance();
        /* Skip when latestDailyBalance >=0 */
        if (Objects.isNull(latestDailyBalance)
                || latestDailyBalance.compareTo(BigDecimal.ZERO) >= 0) {
            return;
        }

        /* Step1: try deduction of global account amount */
        boolean result = this.transferOutGlobalAccount(GlobalAccountTransferOutVo.builder()
                .merchantId(subAccount.getMerchantId())
                .subAccountId(subAccount.getId())
                .amount(latestDailyBalance.abs())
                .currency(subAccount.getCurrency()).build());

        /* Step2: deduction of global account amount success, then charge to subAccount*/
        if (result) {
            bizTopupManager.accountTopUp(TransactionBizVo.builder()
                    .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                    .accountId(subAccount.getId())
                    .transactionCurrency(subAccount.getCurrency())
                    .operateMode(OperateModeEnum.AUTO)
                    .exchangeRate(BigDecimal.ONE)
                    .feeAmount(BigDecimal.ZERO)
                    .taxAmount(BigDecimal.ZERO)
                    .settlementAmount(latestDailyBalance.abs())
                    .settlementCurrency(subAccount.getCurrency())
                    .comments("Auto recharge from global account")
                    .createdBy(0L).build());
        }
    }

    @Override
    public void tryLockGlobalAccount(final Long globalAccountId, final String lockVal) {
        if (Objects.isNull(globalAccountId) || globalAccountId <= 0
                || StringUtil.isBlank(lockVal)) {
            throw StatementExceptionCode.GET_ACCOUNT_LOCKED_FAIL.exception();
        }

        final boolean isLocked =
                redisDistLock.tryLock(CacheConstant.GLOBAL_ACCOUNT_LOCK_KEY + globalAccountId,
                        lockVal, 2, TimeUnit.MINUTES);
        if (!isLocked) {
            log.warn("get and locked global account fail: accountId={}, lockVal={}",
                    globalAccountId, lockVal);
            throw StatementExceptionCode.GET_ACCOUNT_LOCKED_FAIL.exception();
        }
    }

    @Override
    public void unLockGlobalAccount(final Long globalAccountId, final String lockVal) {
        redisDistLock.unlock(CacheConstant.GLOBAL_ACCOUNT_LOCK_KEY + globalAccountId, lockVal);
    }

}
