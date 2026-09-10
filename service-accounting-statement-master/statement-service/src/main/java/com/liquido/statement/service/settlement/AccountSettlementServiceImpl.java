package com.liquido.statement.service.settlement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.AccountBo;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.BatchAccountSettlementBo;
import com.liquido.statement.pojo.bo.TransactionSummaryBo;
import com.liquido.statement.pojo.entity.TransactionFee;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.vo.BatchTransactionMoneyVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;
import com.liquido.statement.service.AccountDailyInitService;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.AccountingScheduleService;
import com.liquido.statement.service.TransactionCostService;
import com.liquido.statement.service.TransactionFeeService;
import com.liquido.statement.service.TransactionMoneyService;
import com.liquido.statement.service.TransactionSummaryService;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSettlementServiceImpl implements AccountSettlementService {
    private final AccountService accountService;
    private final TransactionFeeService transactionFeeService;
    private final TransactionCostService transactionCostService;
    private final TransactionMoneyService transactionMoneyService;
    private final AccountDailyInitService accountDailyInitService;
    private final AccountingScheduleService accountingScheduleService;
    private final SettlementProviderFactory settlementProviderFactory;
    private final TransactionSummaryService transactionSummaryService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void beforeExecutionSettlement(final BatchTransactionMoneyVo batchOrder) {
        final Map<BusinessStrategyEnum, List<TransactionMoneyVo>> businessGroup =
                batchOrder.getTransactionMoneyList().stream()
                        .collect(Collectors.groupingBy(TransactionMoneyVo::getBusinessStrategy));

        for (final Map.Entry<BusinessStrategyEnum, List<TransactionMoneyVo>> entry :
                businessGroup.entrySet()) {
            settlementProviderFactory.getProviderFactory(entry.getKey())
                    .beforeExecuteSettlement(entry.getValue());
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public BatchAccountSettlementBo batchSaveTransactionOrder(
            final BatchTransactionMoneyVo batchOrder) {

        final List<TransactionMoneyVo> transactionOrderList = batchOrder.getTransactionMoneyList();
        final LocalDate transactionDate = transactionOrderList.get(0).getTransactionDate();

        final AccountDailyInitBo billInitInfo = accountDailyInitService.getDailyBillInitInfo(
                batchOrder.getAccountId(), transactionDate);
        if (Objects.isNull(billInitInfo)) {
            log.error("Account daily billId init fail, this batch transactions settlement fail");
            throw StatementExceptionCode.ACCOUNT_DAILY_BILL_INIT_FAIL.exception();
        }

        /* Step1: Batch save transaction order (do interface idempotent judgment) */
        final List<TransactionMoney> orderList =
                transactionMoneyService.batchSave(transactionOrderList, billInitInfo);

        /* Step2: Batch save transaction fees, after save success filter when instantFlag=true */
        final List<TransactionFee> feeList =
                transactionFeeService.batchSave(transactionOrderList, billInitInfo).stream()
                        .filter(TransactionFee::getInstantFlag).collect(Collectors.toList());

        /* Step3: Batch save payin credit-card installment accounting schedule */
        accountingScheduleService.batchSave(transactionOrderList);

        /* Step4: Batch save transaction costs */
        transactionCostService.batchSaveTransactionCost(transactionOrderList, billInitInfo);

        return BatchAccountSettlementBo.builder()
                .accountId(batchOrder.getAccountId())
                .allOriginalMoneyList(Lists.newArrayList())
                .needCreditOriginalMoneyList(Lists.newArrayList())
                .transactionMoneyList(orderList)
                .billInitInfo(billInitInfo)
                .transactionFeeList(feeList)
                .businessType(BusinessTypeEnum.TRANSACTION)
                .transactionDate(transactionDate)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchExecutionSettlement(final BatchAccountSettlementBo batchOrder) {
        /* Step1: Setting execution priority: SETTLED > REFUND > CHARGE_BACK */
        final LinkedHashMap<BusinessStrategyEnum, List<TransactionMoney>> priorityMap =
                this.settingExecutionPriority(batchOrder.getTransactionMoneyList());

        final AccountBo accountInfo = accountService.findById(batchOrder.getAccountId());
        batchOrder.setAccountSnapshot(accountInfo);

        /* Step2: Execute transaction settlement in batches by business policy priority */
        for (final Map.Entry<BusinessStrategyEnum, List<TransactionMoney>> entry :
                priorityMap.entrySet()) {

            final Set<Long> transactionIdSet = entry.getValue().stream()
                    .map(TransactionMoney::getTransactionId).collect(Collectors.toSet());

            final List<TransactionFee> transactionFeeList =
                    batchOrder.getTransactionFeeList().stream()
                            .filter(fee -> transactionIdSet.contains(fee.getTransactionId()))
                            .collect(Collectors.toList());

            settlementProviderFactory.getProviderFactory(entry.getKey())
                    .executeAccountSettlement(BatchAccountSettlementBo.builder()
                            .accountId(accountInfo.getId())
                            .accountSnapshot(accountInfo)
                            .billInitInfo(batchOrder.getBillInitInfo())
                            .allOriginalMoneyList(Lists.newArrayList())
                            .needCreditOriginalMoneyList(Lists.newArrayList())
                            .transactionMoneyList(entry.getValue())
                            .transactionFeeList(transactionFeeList)
                            .businessType(BusinessTypeEnum.TRANSACTION)
                            .build());
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void afterExecutionSettlement(final BatchAccountSettlementBo batchOrder) {
        final int totalTransactionCount = batchOrder.getTransactionMoneyList().size();
        if (totalTransactionCount == 0) {
            return;
        }

        // The transactionDate of the same batch of data is the same
        final TransactionMoney moneyVo = batchOrder.getTransactionMoneyList().get(0);

        /* Sum Transaction amount */
        BigDecimal transactionAmount = BigDecimal.ZERO;
        BigDecimal transactionVolumeAmount = BigDecimal.ZERO;
        /* Sum Settlement amount */
        BigDecimal settlementAmount = BigDecimal.ZERO;
        BigDecimal settlementVolumeAmount = BigDecimal.ZERO;
        /* Sum Settlement amount(USD)*/
        BigDecimal settlementAmountUsd = BigDecimal.ZERO;
        BigDecimal settlementVolumeAmountUsd = BigDecimal.ZERO;

        for (final TransactionMoney order : batchOrder.getTransactionMoneyList()) {
            BigDecimal orderTransactionAmount =
                    order.getAmount().multiply(order.getAmountPon());
            BigDecimal orderSettlementAmount =
                    order.getSettlementAmount().multiply(order.getAmountPon());
            BigDecimal orderSettlementAmountUsd =
                    order.getSettlementAmountUsd().multiply(order.getAmountPon());

            transactionAmount = transactionAmount.add(orderTransactionAmount);
            settlementAmount = settlementAmount.add(orderSettlementAmount);
            settlementAmountUsd = settlementAmountUsd.add(orderSettlementAmountUsd);

            if (order.getDirectionType().equals(DirectionTypeEnum.SETTLED)) {
                transactionVolumeAmount =
                        transactionVolumeAmount.add(orderTransactionAmount.abs());
                settlementVolumeAmount =
                        settlementVolumeAmount.add(orderSettlementAmount.abs());
                settlementVolumeAmountUsd =
                        settlementVolumeAmountUsd.add(orderSettlementAmountUsd.abs());
            } else {
                transactionVolumeAmount =
                        transactionVolumeAmount.subtract(orderTransactionAmount.abs());
                settlementVolumeAmount =
                        settlementVolumeAmount.subtract(orderSettlementAmount.abs());
                settlementVolumeAmountUsd =
                        settlementVolumeAmountUsd.subtract(orderSettlementAmountUsd.abs());
            }
        }

        BigDecimal totalFeeAmount = BigDecimal.ZERO;
        BigDecimal totalTaxAmount = BigDecimal.ZERO;
        for (final TransactionFee fee : batchOrder.getTransactionFeeList()) {
            if (Objects.nonNull(fee.getInstantFlag()) && fee.getInstantFlag()) {
                /* Sum Transaction tax */
                if (FeeGroupEnum.TAX == fee.getFeeGroup()) {
                    totalTaxAmount = totalTaxAmount.add(
                            fee.getSettlementAmount().multiply(fee.getAmountPon()));
                } else {
                    /* Sum Transaction fee */
                    totalFeeAmount = totalFeeAmount.add(
                            fee.getSettlementAmount().multiply(fee.getAmountPon()));
                }
            }
        }

        transactionSummaryService.saveOrUpdate(TransactionSummaryBo.builder()
                .merchantId(moneyVo.getMerchantId())
                .accountId(moneyVo.getAccountId())
                .transactionDate(batchOrder.getTransactionDate())
                .transactionCount(totalTransactionCount)
                .transactionCurrency(moneyVo.getCurrency())
                .settlementCurrency(moneyVo.getSettlementCurrency())
                .transactionAmount(transactionAmount)
                .transactionVolumeAmount(transactionVolumeAmount)
                .settlementAmount(settlementAmount)
                .settlementVolumeAmount(settlementVolumeAmount)
                .settlementAmountUsd(settlementAmountUsd)
                .settlementVolumeAmountUsd(settlementVolumeAmountUsd)
                .feeAmount(totalFeeAmount)
                .taxAmount(totalTaxAmount).build());
    }

    /**
     * Setting the execution priority
     * Group by transaction order by businessStrategy, sortedBy BusinessStrategyEnum.priority
     * businessStrategy process priority: SETTLED > REFUND > CHARGE_BACK
     *
     * @param batchOrderList
     * @return
     */
    private LinkedHashMap<BusinessStrategyEnum, List<TransactionMoney>> settingExecutionPriority(
            final List<TransactionMoney> batchOrderList) {

        /* 1: group by business strategy; 2: set execution priority; */
        return batchOrderList.stream().collect(Collectors.groupingBy(
                        item -> BusinessStrategyEnum.parse(item.getTransactionTypeCode(),
                                item.getDirectionType()))).entrySet().stream()
                .sorted(Comparator.comparingInt(o -> o.getKey().getPriority()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal, LinkedHashMap::new));
    }
}
