package com.liquido.statement.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.enums.OperateModeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.enums.TransferConfigStateEnum;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.AccountTransferConfigDto;
import com.liquido.statement.pojo.entity.AccountTransferConfig;
import com.liquido.statement.pojo.entity.QAccountTransferConfig;
import com.liquido.statement.pojo.vo.ListAccountVo;
import com.liquido.statement.pojo.vo.ModifyAccountTransferConfigStateVo;
import com.liquido.statement.pojo.vo.QueryAccountTransferConfigPageVo;
import com.liquido.statement.pojo.vo.SaveAccountTransferConfigVo;
import com.liquido.statement.repository.AccountTransferConfigRepository;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.AccountTransferConfigService;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AccountTransferConfigServiceImpl implements AccountTransferConfigService {
    private final BaseService baseService;
    private final AccountService accountService;
    private final JPAQueryFactory jpaQueryFactory;
    private final AccountTransferConfigRepository accountTransferConfigRepository;

    @Override
    public void saveAccountAutoTransferConfig(final SaveAccountTransferConfigVo vo) {

        final Map<TransactionTypeCodeEnum, AccountDto> accounts =
                accountService.listAccount(ListAccountVo.builder()
                                .merchantId(vo.getMerchantId())
                                .countryCode(vo.getCountryCode())
                                .build()).stream()
                        .collect(Collectors.toMap(AccountDto::getTransactionTypeCode,
                                Function.identity()));

        if (!accounts.containsKey(TransactionTypeCodeEnum.PAY_IN)) {
            throw StatementExceptionCode.ACCOUNT_NOT_FOUND
                    .exception(TransactionTypeCodeEnum.PAY_IN.getCode());
        }

        if (!accounts.containsKey(TransactionTypeCodeEnum.PAY_OUT)) {
            throw StatementExceptionCode.ACCOUNT_NOT_FOUND
                    .exception(TransactionTypeCodeEnum.PAY_OUT.getCode());
        }

        final AccountDto payinAccount = accounts.get(TransactionTypeCodeEnum.PAY_IN);
        final AccountDto payoutAccount = accounts.get(TransactionTypeCodeEnum.PAY_OUT);

        final AccountTransferConfig existRecord = this.findAccountTransferConfig(
                payinAccount.getMerchantId(), payinAccount.getId(), payoutAccount.getId());

        // remove exist record
        if (Objects.nonNull(existRecord)) {
            accountTransferConfigRepository.delete(existRecord);
        }

        final MerchantDto merchant = baseService.getMerchantById(payinAccount.getMerchantId());
        // save new record
        accountTransferConfigRepository.saveAndFlush(AccountTransferConfig.builder()
                .id(SnowflakeIdUtil.generate())
                .merchantId(payinAccount.getMerchantId())
                .payinAccountId(payinAccount.getId())
                .payoutAccountId(payoutAccount.getId())
                .timerCron("")
                .countryCode(payinAccount.getCountryCode())
                .merchantCode(merchant.getCode())
                .operateMode(OperateModeEnum.AUTO)
                .state(TransferConfigStateEnum.ENABLE)
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(0L)
                .updatedBy(0L)
                .version(1)
                .delFlag(Boolean.FALSE)
                .remark("")
                .build());
    }

    @Override
    public void updateAccountAutoTransferConfigState(final ModifyAccountTransferConfigStateVo vo) {
        final QAccountTransferConfig entity = QAccountTransferConfig.accountTransferConfig;
        jpaQueryFactory.update(entity)
                .set(entity.state, vo.getState())
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .set(entity.version, entity.version.add(1))
                .where(entity.id.eq(vo.getId()))
                .execute();
    }

    @Override
    public AccountTransferConfig findAccountTransferConfig(final Long merchantId,
                                                           final Long payinAccountId,
                                                           final Long payoutAccountId) {

        final QAccountTransferConfig entity = QAccountTransferConfig.accountTransferConfig;
        final BooleanExpression condition = entity.merchantId.eq(merchantId)
                .and(entity.payinAccountId.eq(payinAccountId))
                .and(entity.payoutAccountId.eq(payoutAccountId));

        return jpaQueryFactory.select(entity).from(entity).where(condition).fetchOne();
    }

    @Override
    public AccountTransferConfig findByMerchantIdAndPayinAccountId(final Long merchantId,
                                                                   final Long payinAccountId) {
        final QAccountTransferConfig entity = QAccountTransferConfig.accountTransferConfig;
        final BooleanExpression condition = entity.merchantId.eq(merchantId)
                .and(entity.payinAccountId.eq(payinAccountId))
                .and(entity.state.eq(TransferConfigStateEnum.ENABLE))
                .and(entity.delFlag.eq(Boolean.FALSE));

        return jpaQueryFactory.select(entity).from(entity).where(condition).fetchOne();
    }

    @Override
    public AccountTransferConfig findByMerchantIdAndPayoutAccountId(final Long merchantId,
                                                                    final Long payoutAccountId) {
        final QAccountTransferConfig entity = QAccountTransferConfig.accountTransferConfig;
        final BooleanExpression condition = entity.merchantId.eq(merchantId)
                .and(entity.payoutAccountId.eq(payoutAccountId))
                .and(entity.state.eq(TransferConfigStateEnum.ENABLE))
                .and(entity.delFlag.eq(Boolean.FALSE));

        return jpaQueryFactory.select(entity).from(entity).where(condition).fetchOne();
    }

    @Override
    public List<AccountTransferConfig> queryCustomizeTimerConfig() {
        final QAccountTransferConfig entity = QAccountTransferConfig.accountTransferConfig;
        final BooleanExpression condition = entity.timerCron.isNotEmpty()
                .and(entity.operateMode.eq(OperateModeEnum.AUTO))
                .and(entity.state.eq(TransferConfigStateEnum.ENABLE))
                .and(entity.delFlag.eq(Boolean.FALSE));

        return jpaQueryFactory.select(entity).from(entity).where(condition).fetch();
    }

    @Override
    public PageVo<AccountTransferConfigDto> queryAccountAutoTransferConfigPage(
            final QueryAccountTransferConfigPageVo vo) {

        final QAccountTransferConfig entity = QAccountTransferConfig.accountTransferConfig;
        final List<BooleanExpression> condition = Lists.newArrayList();
        condition.add(entity.delFlag.eq(Boolean.FALSE));
        if (Objects.nonNull(vo.getCountryCode())) {
            condition.add(entity.countryCode.eq(vo.getCountryCode()));
        }
        if (Objects.nonNull(vo.getMerchantCode())) {
            condition.add(entity.merchantCode.eq(vo.getMerchantCode()));
        }
        if (Objects.nonNull(vo.getState())) {
            condition.add(entity.state.eq(vo.getState()));
        }

        final QBean<AccountTransferConfigDto> bean = Projections.fields(
                AccountTransferConfigDto.class,
                entity.id,
                entity.merchantId,
                entity.countryCode,
                entity.merchantCode,
                entity.payinAccountId,
                entity.payoutAccountId,
                entity.timerCron,
                entity.state,
                entity.createdTime,
                entity.updatedTime,
                entity.remark);

        final BooleanExpression[] conditions = condition.toArray(new BooleanExpression[] {});
        final long count = Optional.ofNullable(jpaQueryFactory.select(entity.id.count())
                .from(entity)
                .where(conditions)
                .fetchOne()).orElse(0L);
        if (count <= 0) {
            return new PageVo<>(vo.getPageNo(), vo.getPageSize(), count, Collections.emptyList());
        }

        final List<AccountTransferConfigDto> resultList = jpaQueryFactory.select(bean)
                .from(entity)
                .where(conditions)
                .orderBy(entity.id.desc())
                .offset(vo.getOffset())
                .limit(vo.getPageSize())
                .fetch();

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(), count, resultList);
    }
}
