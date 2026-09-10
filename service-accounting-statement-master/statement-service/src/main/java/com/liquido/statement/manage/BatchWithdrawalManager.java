package com.liquido.statement.manage;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BizFinanceTypeEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountBo;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.AccountStatementBizBo;
import com.liquido.statement.pojo.dto.BatchWithdrawalApplyDto;
import com.liquido.statement.pojo.dto.DealershipWithdrawalInfoDto;
import com.liquido.statement.pojo.dto.SubAccountDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountStatement;
import com.liquido.statement.pojo.entity.BatchWithdrawalApply;
import com.liquido.statement.pojo.entity.BatchWithdrawalDetail;
import com.liquido.statement.pojo.entity.TransactionBiz;
import com.liquido.statement.pojo.entity.TransactionCost;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.ApprovalBatchApplyItemVo;
import com.liquido.statement.pojo.vo.ApprovalBatchApplyVo;
import com.liquido.statement.pojo.vo.BatchWithdrawalApplyVo;
import com.liquido.statement.pojo.vo.BatchWithdrawalApprovedHandleDetailVo;
import com.liquido.statement.pojo.vo.BatchWithdrawalApprovedHandleVo;
import com.liquido.statement.pojo.vo.ListBatchWithdrawalApplyVo;
import com.liquido.statement.pojo.vo.ListSubAccountVo;
import com.liquido.statement.pojo.vo.TransactionBizVo;
import com.liquido.statement.service.AccountDailyInitService;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.AccountStatementBizService;
import com.liquido.statement.service.AccountStatementService;
import com.liquido.statement.service.BatchWithdrawalApplyService;
import com.liquido.statement.service.BatchWithdrawalDetailService;
import com.liquido.statement.service.SubAccountService;
import com.liquido.statement.service.TransactionBizService;
import com.liquido.statement.service.TransactionCostService;
import com.liquido.statement.service.TransactionMoneyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchWithdrawalManager {

    private final ModelMapper mapper;
    private final RedisDistLock redisDistLock;
    private final BaseService baseService;
    private final AccountService accountService;
    private final SubAccountService subAccountService;
    private final TransactionMoneyService transactionMoneyService;
    private final BatchWithdrawalApplyService batchWithdrawalApplyService;
    private final BatchWithdrawalDetailService batchWithdrawalDetailService;
    private final AccountDailyInitService accountDailyInitService;
    private final TransactionBizService transactionBizService;
    private final AccountStatementService accountStatementService;
    private final TransactionCostService transactionCostService;
    private final AccountStatementBizService accountStatementBizService;

    @Transactional(rollbackFor = Throwable.class)
    public BatchWithdrawalApplyDto batchWithdrawalApply(final BatchWithdrawalApplyVo vo) {

        final MerchantDto merchant = baseService.getMerchantById(vo.getMerchantId());

        final List<SubMerchantDto> subMerchantList =
                baseService.querySubMerchantList(vo.getMerchantId());

        final AccountBo account = accountService.findById(vo.getAccountId());
        final List<SubAccountDto> subAccountList =
                subAccountService.listSubAccount(ListSubAccountVo.builder()
                        .merchantId(merchant.getId())
                        .countryCode(account.getCountryCode()).build());

        final LocalDate nowDate = LocalDateTimeUtil.nowUtcZonedDateTime()
                .withZoneSameInstant(ZoneId.of(account.getTimezone()))
                .toLocalDate();

        if (vo.getCreditedDate().equals(nowDate) || vo.getCreditedDate().isAfter(nowDate)) {
            throw StatementExceptionCode.BATCH_WITHDRAWAL_FAIL
                    .exception("The extracted transaction date is invalid");
        }

        // statistical transaction
        final List<TransactionMoney> moneyList =
                transactionMoneyService.queryTransactionMoneyList(vo);

        log.info("Batch withdrawal apply subMerchant.size={},subAccount.size={},moneyList.size={}",
                subMerchantList.size(), subAccountList.size(), moneyList.size());
        if (CollectionUtils.isEmpty(subMerchantList)
                || CollectionUtils.isEmpty(subAccountList)
                || CollectionUtils.isEmpty(moneyList)) {
            return BatchWithdrawalApplyDto.builder()
                    .merchantId(merchant.getId())
                    .merchantCode(merchant.getCode())
                    .countryCode(account.getCountryCode())
                    .accountId(account.getId())
                    .withdrawalCount(0)
                    .withdrawalAmount(BigDecimal.ZERO)
                    .settlementCurrency(account.getCurrency())
                    .creditedDate(vo.getCreditedDate())
                    .applyDate(nowDate)
                    .dataList(Collections.emptyList())
                    .build();
        }

        final long batchId = SnowflakeIdUtil.generate();

        final List<BatchWithdrawalDetail> orderList = Lists.newArrayList();
        for (final TransactionMoney order : moneyList) {
            // idempotent check
            if (batchWithdrawalDetailService.idempotentCheck(
                    order.getTransactionId(),
                    order.getDirectionType(),
                    order.getAccountId())) {

                orderList.add(this.createBatchWithdrawalDetail(
                        batchId, order, subMerchantList, subAccountList));
            }
        }

        try {
            // save withdrawal detail
            final List<BatchWithdrawalDetail> detailList =
                    batchWithdrawalDetailService.batchSave(orderList);

            // save withdrawal apply
            final List<BatchWithdrawalApply> applyList =
                    batchWithdrawalApplyService.batchSave(batchId, detailList);

            return BatchWithdrawalApplyDto.builder()
                    .merchantId(merchant.getId())
                    .merchantCode(merchant.getCode())
                    .countryCode(account.getCountryCode())
                    .accountId(account.getId())
                    .batchId(batchId)
                    .withdrawalCount(applyList.size())
                    .withdrawalAmount(applyList.stream()
                            .map(BatchWithdrawalApply::getWithdrawalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .settlementCurrency(account.getCurrency())
                    .creditedDate(vo.getCreditedDate())
                    .applyDate(nowDate)
                    .dataList(this.buildDealershipWithdrawalInfo(applyList))
                    .build();

        } catch (DataIntegrityViolationException e) {
            if (e.getRootCause() instanceof SQLIntegrityConstraintViolationException) {
                log.error("unique key conflict, msg={}", e.getMessage(), e);
                throw StatementExceptionCode.BATCH_WITHDRAWAL_APPLY_FAIL
                        .exception(e, "unique key conflict");
            }
        }

        throw StatementExceptionCode.BATCH_WITHDRAWAL_FAIL.exception("system error");
    }

    public List<DealershipWithdrawalInfoDto> listBatchWithdrawalApply(
            final ListBatchWithdrawalApplyVo vo) {

        return this.buildDealershipWithdrawalInfo(batchWithdrawalApplyService.queryApplyList(vo));
    }

    private BatchWithdrawalDetail createBatchWithdrawalDetail(
            final Long batchId,
            final TransactionMoney money,
            final List<SubMerchantDto> subMerchantList,
            final List<SubAccountDto> subAccountList) {

        final String subMerchantName = subMerchantList.stream()
                .filter(x -> x.getSubMerchantId().equals(money.getSubMerchantId()))
                .findFirst().map(SubMerchantDto::getCommercialName)
                .orElseThrow(StatementExceptionCode.SUB_MERCHANT_UNDEFINE::exception);

        final Long subAccountId = subAccountList.stream()
                .filter(x -> x.getSubMerchantId().equals(money.getSubMerchantId()))
                .findFirst().map(SubAccountDto::getId)
                .orElseThrow(StatementExceptionCode.SUB_ACCOUNT_UNDEFINE::exception);

        return BatchWithdrawalDetail.builder()
                .id(SnowflakeIdUtil.generate())
                .batchId(batchId)
                .transactionId(money.getTransactionId())
                .uniqueId(money.getUniqueId())
                .merchantId(money.getMerchantId())
                .subMerchantId(money.getSubMerchantId())
                .subMerchantName(subMerchantName)
                .accountId(money.getAccountId())
                .subAccountId(subAccountId)
                .directionType(money.getDirectionType())
                .creditedDate(money.getBeCreditedDate())
                .creditedAmount(money.getBeCreditedAmount())
                .settlementCurrency(money.getSettlementCurrency())
                .applyDate(LocalDate.now())
                .state(0)
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(0L)
                .updatedBy(0L)
                .version(0)
                .delFlag(false)
                .build();
    }

    private List<DealershipWithdrawalInfoDto> buildDealershipWithdrawalInfo(
            final List<BatchWithdrawalApply> applyList) {

        if (CollectionUtils.isEmpty(applyList)) {
            return Collections.emptyList();
        }

        return mapper.convertWithdrawalApplyList(applyList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void approvalApply(final ApprovalBatchApplyVo vo) {
        this.redisDistLock.checkRepeatRequest(
                CacheConstant.TRANSACTION_ORDER, vo.getRequestId(), 3, TimeUnit.DAYS);

        accountService.batchWithdrawalFrozenAmount(vo);

        this.updateBatchWithdrawalState(vo);
    }

    /**
     * update
     * batch_withdrawal_apply=processing
     * batch_withdrawal_detail=processing
     */
    @Transactional(rollbackFor = Throwable.class)
    public void updateBatchWithdrawalState(final ApprovalBatchApplyVo vo) {
        final List<Long> batchWithdrawalApplyIdList = vo.getDataList().stream()
                .map(ApprovalBatchApplyItemVo::getTransactionId).collect(Collectors.toList());

        batchWithdrawalApplyService.batchUpdateStateProcessing(batchWithdrawalApplyIdList);
        batchWithdrawalDetailService.batchUpdateStateProcessing(vo.getBatchId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void approvedHandler(final BatchWithdrawalApprovedHandleVo vo) {
        try {

            this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER,
                    vo.getRequestId(), 1, TimeUnit.DAYS);

            final Account account = accountService.getAccountLocked(
                    vo.getAccountId(), vo.getRequestId());

            final MerchantDto merchant = baseService.getMerchantById(account.getMerchantId());
            if (account.getCurrency() != vo.getSettlementCurrency()) {
                throw StatementExceptionCode.INCONSISTENT_CURRENCY_TYPES.exception();
            }

            this.unfrozenAmount(vo, account);

            this.accountFlowHandler(vo, account, merchant);

            this.updateBatchWithdrawalDetail(vo);

        } finally {
            accountService.releaseAccountLock(vo.getAccountId(), vo.getRequestId());
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateBatchWithdrawalDetail(final BatchWithdrawalApprovedHandleVo vo) {

        log.info("Update batch withdrawal detail and apply state");

        final AccountBo accountBo = accountService.findAccountInfoById(vo.getAccountId());

        final LocalDateTime completeTime = LocalDateTimeUtil.nowUtc();
        final LocalDate completeDate = completeTime.atZone(LocalDateTimeUtil.UTC_ZONE)
                .withZoneSameInstant(ZoneId.of(accountBo.getTimezone())).toLocalDate();

        if (Objects.nonNull(vo.getApprovalApprovedList())
                && !vo.getApprovalApprovedList().isEmpty()) {
            final Set<String> subMerchantIdSet = vo.getApprovalApprovedList().stream()
                    .map(BatchWithdrawalApprovedHandleDetailVo::getSubMerchantId)
                    .collect(Collectors.toSet());
            batchWithdrawalApplyService.batchUpdateFinishState(completeTime, completeDate,
                    vo.getBatchId(), subMerchantIdSet, 2);
            batchWithdrawalDetailService.batchUpdateFinishState(completeTime, completeDate,
                    vo.getBatchId(), subMerchantIdSet, 2);
        }

        if (Objects.nonNull(vo.getApprovalRejectedList())
                && !vo.getApprovalRejectedList().isEmpty()) {
            final Set<String> subMerchantIdSet = vo.getApprovalRejectedList().stream()
                    .map(BatchWithdrawalApprovedHandleDetailVo::getSubMerchantId)
                    .collect(Collectors.toSet());
            batchWithdrawalApplyService.batchUpdateFinishState(completeTime, completeDate,
                    vo.getBatchId(), subMerchantIdSet, 3);
            batchWithdrawalDetailService.batchUpdateFinishState(completeTime, completeDate,
                    vo.getBatchId(), subMerchantIdSet, 3);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void unfrozenAmount(final BatchWithdrawalApprovedHandleVo vo, final Account account) {
        log.info("try to unfrozen amount, batchId:{}, unfrozen amount:{}",
                vo.getBatchId(), vo.getUnFrozenAmount());

        if (Objects.isNull(account.getFrozenAmount())
                || account.getFrozenAmount().compareTo(vo.getUnFrozenAmount()) < 0) {
            log.error(
                    "Insufficient frozenAmount, accountId={}, frozenAmount={}, settleAmount={}",
                    account.getId(), account.getFrozenAmount(), vo.getUnFrozenAmount());
            throw StatementExceptionCode.INSUFFICIENT_EXTRACTABLE_BALANCE.exception(
                    AmountUtil.centToYuan(account.getExtractableBalance()),
                    account.getCurrency());
        }

        if (!accountService.unfrozenAmount(
                account, vo.getUnFrozenAmount(), vo.getApprovalApprovedCount(),
                vo.getApprovalApprovedAmount(), vo.getApprovalRejectedAmount())) {
            throw StatementExceptionCode.ACCOUNT_BALANCE_CHANGE_FAILED.exception();
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void accountFlowHandler(final BatchWithdrawalApprovedHandleVo vo,
                                   final Account account,
                                   final MerchantDto merchant) {

        log.info("create a running water after unfreezing the amount. count={}, amount={}",
                vo.getApprovalApprovedList().size(), vo.getApprovalApprovedAmount());

        BigDecimal startBalance = account.getLatestDailyBalance().add(account.getSubTotalAmount());

        final List<TransactionBiz> transactionBizList = Lists.newArrayList();
        final List<TransactionCost> transactionCostList = Lists.newArrayList();
        final List<AccountStatement> accountStatementList = Lists.newArrayList();
        final List<AccountStatementBizBo> accountStatementBizList = Lists.newArrayList();

        final LocalDateTime transactionTime = LocalDateTimeUtil.nowUtc();

        /* SettleTime is UTC+0 time, need to transfer to merchant-account time zone; */
        final LocalDateTime accountZoneTime = transactionTime.atZone(LocalDateTimeUtil.UTC_ZONE)
                .withZoneSameInstant(ZoneId.of(account.getTimezone())).toLocalDateTime();
        final AccountDailyInitBo billInitInfo =
                accountDailyInitService.getDailyBillInitInfo(account.getId(),
                        accountZoneTime.toLocalDate());

        for (final BatchWithdrawalApprovedHandleDetailVo order : vo.getApprovalApprovedList()) {
            final TransactionBizVo transactionBizVo =
                    this.convert(account, order, transactionTime);
            final TransactionBiz biz = transactionBizService.buildTransactionBiz(
                    account, transactionBizVo, billInitInfo);
            transactionBizList.add(biz);
            final BigDecimal endBalance =
                    startBalance.subtract(transactionBizVo.getSettlementAmount());
            accountStatementList.add(accountStatementService.buildAccountStatement(
                    biz, transactionBizVo.getSettlementAmount(), startBalance, endBalance));
            startBalance = endBalance;
            transactionCostList.add(transactionCostService.buildBizTransactionCost(
                    transactionBizVo, biz, merchant, account));
            accountStatementBizList.add(buildApprovalPassAccountStatementBiz(account, biz));
        }

        for (final BatchWithdrawalApprovedHandleDetailVo order : vo.getApprovalRejectedList()) {
            accountStatementBizList.add(
                    buildRollBackAccountStatementBiz(account, order, billInitInfo));
        }

        accountStatementBizService.saveAccountStatementBiz(accountStatementBizList);
        transactionBizService.saveBatchTransactionBiz(transactionBizList);
        accountStatementService.batchGenerateAndSaveSettlementFlow(accountStatementList);
        transactionCostService.batchSaveTransactionCost(transactionCostList);
    }

    private TransactionBizVo convert(
            final Account account, final BatchWithdrawalApprovedHandleDetailVo vo,
            final LocalDateTime transactionTime) {
        return TransactionBizVo.builder()
                .businessType(BusinessTypeEnum.TRANSFER_OUT)
                .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                .transactionId(vo.getTransactionId())
                .accountId(account.getId())
                .subMerchantId(vo.getSubMerchantId())
                .amountPon(AmountPonEnum.NEGATIVE)
                .paymentChannel(PaymentChannelEnum.getDefaultChannel(account.getCountryCode()))
                .transactionAmount(vo.getWithdrawalAmount())
                .transactionCurrency(vo.getSettlementCurrency())
                .transactionTime(transactionTime)
                .settlementTime(transactionTime)
                .exchangeRate(BigDecimal.ONE)
                .feeAmount(vo.getFeeAmount())
                .taxAmount(vo.getTaxAmount())
                .settlementAmount(vo.getWithdrawalAmount())
                .settlementCurrency(vo.getSettlementCurrency())
                .build();
    }

    private AccountStatementBizBo buildApprovalPassAccountStatementBiz(
            final Account account, final TransactionBiz biz) {
        return AccountStatementBizBo.builder()
                .requestId(biz.getRequestId())
                .transactionId(biz.getTransactionId())
                .merchantId(biz.getMerchantId())
                .subMerchantId(StringUtils.defaultIfBlank(biz.getSubMerchantId(), ""))
                .accountId(biz.getAccountId())
                .billId(biz.getBillId())
                .businessType(biz.getBusinessType())
                .financeType(BizFinanceTypeEnum.TRANSACTION_DEAL)
                .transactionTime(biz.getTransactionTime())
                .extractableAmount(BigDecimal.ZERO)
                .frozenAmount(BusinessTypeEnum.TRANSFER_OUT == biz.getBusinessType()
                        ? biz.getSettlementAmount().multiply(biz.getAmountPon().getCode())
                        : BigDecimal.ZERO)
                .exchangeAmount(BusinessTypeEnum.EXCHANGE == biz.getBusinessType()
                        ? biz.getSettlementAmount().multiply(biz.getAmountPon().getCode())
                        : BigDecimal.ZERO)
                .currency(account.getCurrency())
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(0L)
                .updatedBy(0L)
                .version(1)
                .delFlag(Boolean.FALSE)
                .remark(biz.getRemark())
                .build();
    }

    private AccountStatementBizBo buildRollBackAccountStatementBiz(
            final Account account,
            final BatchWithdrawalApprovedHandleDetailVo order,
            final AccountDailyInitBo billBo) {
        return AccountStatementBizBo.builder()
                .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                .transactionId(order.getTransactionId())
                .merchantId(account.getMerchantId())
                .subMerchantId(StringUtils.defaultIfBlank(order.getSubMerchantId(),""))
                .accountId(account.getId())
                .billId(billBo.getBillId())
                .businessType(BusinessTypeEnum.TRANSFER_OUT)
                .financeType(BizFinanceTypeEnum.UNFREEZE)
                .transactionTime(LocalDateTimeUtil.nowUtc())
                .extractableAmount(order.getWithdrawalAmount())
                .frozenAmount(order.getWithdrawalAmount().negate())
                .exchangeAmount(BigDecimal.ZERO)
                .currency(account.getCurrency())
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(0L)
                .updatedBy(0L)
                .version(1)
                .delFlag(Boolean.FALSE)
                .remark("Approval RollBack:" + BusinessTypeEnum.TRANSFER_OUT.getCode()).build();
    }
}
