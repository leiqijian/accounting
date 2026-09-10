package com.liquido.statement.service.dailycut;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.statement.common.properties.StatementProperties;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.AccountBo;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.DailyBillSummaryBo;
import com.liquido.statement.pojo.bo.DailyExtractableAmountInfo;
import com.liquido.statement.pojo.bo.DailyTransactionBizBo;
import com.liquido.statement.pojo.bo.DailyTransactionFeeBo;
import com.liquido.statement.pojo.bo.DailyTransactionMoneyBo;
import com.liquido.statement.pojo.entity.AccountDailyBill;
import com.liquido.statement.pojo.entity.DailyExtractableInfo;
import com.liquido.statement.service.AccountDailyBillService;
import com.liquido.statement.service.AccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDailyCutServiceImpl implements AccountDailyCutService {
    private final AccountService accountService;
    private final AccountDailyBillService accountDailyBillService;
    private final StatementProperties.DailyBillProperties dailyBillProperties;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public AccountDailyBill executeAccountDailyCut(final DailyBillSummaryBo summary) {

        // Update account daily balance
        if (!accountService.processAccountDailyCut(summary)) {
            log.error("daily-cut execute account daily cut fail summary={}", summary);
            throw StatementExceptionCode.ACCOUNT_DAILY_CUT_FAILED.exception();
        }

        // Save account daily bill
        return accountDailyBillService.saveAccountDailyBill(this.generateAccountDailyBill(summary));
    }

    /**
     * generate daily bill entity
     *
     * @param summary
     *
     * @return
     */
    private AccountDailyBill generateAccountDailyBill(final DailyBillSummaryBo summary) {

        final AccountDailyInitBo dailyInitInfo = summary.getDailyInitInfo();
        final DailyTransactionMoneyBo transactionMoney = summary.getTransactionMoney();
        final List<DailyTransactionFeeBo> transactionFees = summary.getTransactionFeeList();

        final DailyTransactionBizBo topupAmount =
                summary.getTransactionBizMap().get(BusinessTypeEnum.TOPUP);
        final DailyTransactionBizBo transferOutAmount =
                summary.getTransactionBizMap().get(BusinessTypeEnum.TRANSFER_OUT);
        final DailyTransactionBizBo refundAmount =
                summary.getTransactionBizMap().get(BusinessTypeEnum.REFUND);
        final DailyTransactionBizBo adjustmentAmount =
                summary.getTransactionBizMap().get(BusinessTypeEnum.ADJUSTMENT);

        final AccountDailyBill bill = new AccountDailyBill();
        bill.setId(dailyInitInfo.getBillId());
        bill.setAccountId(summary.getAccount().getId());
        bill.setMerchantId(summary.getAccount().getMerchantId());
        bill.setCountryCode(summary.getAccount().getCountryCode());
        bill.setTransactionTypeCode(summary.getAccount().getTransactionTypeCode());
        bill.setBillTimestamp(LocalDateTimeUtil.nowUtc());
        bill.setTimezone(summary.getAccount().getTimezone());
        bill.setBillMonth(Integer.parseInt(dailyInitInfo.getTransactionDate()
                .format(LocalDateUtil.FORMAT_YYYYMM)));
        bill.setBillDate(dailyInitInfo.getTransactionDate());

        // Set topup bill
        bill.setTopupAmount(topupAmount.getTotalAmount());
        bill.setTopupCount(topupAmount.getTotalCount());
        bill.setTopupFee(topupAmount.getTotalFee());
        bill.setTopupTax(topupAmount.getTotalTax());

        // Set transactions bill
        bill.setTransactionCount(transactionMoney.getTotalCount());
        bill.setTransactionAmount(transactionMoney.getTotalTransactionAmount());
        bill.setTransactionCurrency(transactionMoney.getTransactionCurrency());
        bill.setTransactionAmountUsd(transactionMoney.getTotalTransactionAmountUsd());

        bill.setSettlementAmount(transactionMoney.getTotalSettlementAmount());
        bill.setAdditionalCharge(transactionMoney.getTotalAdditionalCharge());
        bill.setSettlementCurrency(Optional.ofNullable(transactionMoney.getSettlementCurrency())
                .orElse(summary.getAccount().getCurrency()));

        // Set transaction fees
        this.setTransactionFeesToBill(summary.getAccount(), bill, transactionFees);

        // Set transfer bill
        bill.setTransferAmount(transferOutAmount.getTotalAmount());
        bill.setTransferCount(transferOutAmount.getTotalCount());
        bill.setTransferFee(transferOutAmount.getTotalFee());
        bill.setTransferTax(transferOutAmount.getTotalTax());

        // Set refund bill
        bill.setRefundAmount(refundAmount.getTotalAmount());
        bill.setRefundCount(refundAmount.getTotalCount());
        bill.setRefundFee(refundAmount.getTotalFee());
        bill.setRefundTax(refundAmount.getTotalTax());

        // Set adjustment bill
        bill.setAdjustmentAmount(adjustmentAmount.getTotalAmount());
        bill.setAdjustmentCount(adjustmentAmount.getTotalCount());

        // Set daily balance
        bill.setStartBalance(summary.getAccount().getLatestDailyBalance());
        bill.setHappenAmount(summary.getLatestDailyOccurredAmount());
        bill.setEndBalance(summary.getAccount().getLatestDailyBalance()
                .add(summary.getLatestDailyOccurredAmount()));

        final DailyExtractableAmountInfo extractableInfo = summary.getDailyExtractableAmountInfo();
        bill.setExtractableBalance(extractableInfo.getLatestDailyExtractableEndBalance());
        bill.setRecordedAmount(extractableInfo.getLatestDailyOccurredExtractableAmount());

        bill.setDailyExtractableInfo(DailyExtractableInfo.builder()
                .currentT0ExtractableAmount(
                        extractableInfo.getLatestDailyT0ExtractableAmount())
                .currentTnExtractableAmount(
                        extractableInfo.getLatestDailyTnExtractableAmount())
                .currentBizOccurredAmount(
                        extractableInfo.getLatestDailyBizOccurredAmount())
                .currentOccurredExtractableAmount(
                        extractableInfo.getLatestDailyOccurredExtractableAmount())
                .currentExtractableEndBalance(
                        extractableInfo.getLatestDailyExtractableEndBalance())
                .nextT0ExtractableAmount(
                        extractableInfo.getCurrentDailyT0ExtractableAmount())
                .nextTnExtractableAmount(
                        extractableInfo.getCurrentDailyTnExtractableAmount())
                .build());

        bill.setCreatedBy(0L);
        bill.setUpdatedBy(0L);
        bill.setVersion(1);
        bill.setCreatedTime(LocalDateTimeUtil.nowUtc());
        bill.setUpdatedTime(LocalDateTimeUtil.nowUtc());
        return bill;
    }

    /**
     * set transaction fee
     *
     * @param account
     * @param bill
     * @param feeAmountList
     */
    private void setTransactionFeesToBill(final AccountBo account,
                                          final AccountDailyBill bill,
                                          final List<DailyTransactionFeeBo> feeAmountList) {
        // fee init default value
        bill.setCalculateFee(BigDecimal.ZERO);
        bill.setCalculateTax(BigDecimal.ZERO);
        bill.setTransactionFee(BigDecimal.ZERO);
        bill.setTransactionTax(BigDecimal.ZERO);
        bill.setTransactionFeeCurrency(account.getCurrency());

        bill.setCalculateFee2(BigDecimal.ZERO);
        bill.setCalculateTax2(BigDecimal.ZERO);
        bill.setTransactionFee2(BigDecimal.ZERO);
        bill.setTransactionTax2(BigDecimal.ZERO);
        bill.setTransactionFee2Currency(null);
        if (CollectionUtils.isEmpty(feeAmountList)) {
            return;
        }

        final Map<CurrencyEnum, List<DailyTransactionFeeBo>> mapByCurrency = feeAmountList.stream()
                .collect(Collectors.groupingBy(DailyTransactionFeeBo::getSettlementCurrency));

        for (final Map.Entry<CurrencyEnum, List<DailyTransactionFeeBo>> entry :
                mapByCurrency.entrySet()) {
            final CurrencyEnum currency = entry.getKey();
            final List<DailyTransactionFeeBo> feeList = entry.getValue();

            BigDecimal calculateFee = BigDecimal.ZERO;
            BigDecimal calculateTax = BigDecimal.ZERO;
            BigDecimal transactionFee = BigDecimal.ZERO;
            BigDecimal transactionTax = BigDecimal.ZERO;

            for (final DailyTransactionFeeBo fee : feeList) {
                if (FeeGroupEnum.TAX == fee.getFeeGroup()) {
                    calculateTax = calculateTax.add(fee.getTotalCalculateAmount());
                    transactionTax = transactionTax.add(fee.getTotalAmount());
                } else {
                    calculateFee = calculateFee.add(fee.getTotalCalculateAmount());
                    transactionFee = transactionFee.add(fee.getTotalAmount());
                }
            }

            if (currency == account.getCurrency()) {
                bill.setCalculateFee(calculateFee);
                bill.setCalculateTax(calculateTax);
                bill.setTransactionFee(transactionFee);
                bill.setTransactionTax(transactionTax);
                bill.setTransactionFeeCurrency(currency);
            } else {
                bill.setCalculateFee2(calculateFee);
                bill.setCalculateTax2(calculateTax);
                bill.setTransactionFee2(transactionFee);
                bill.setTransactionTax2(transactionTax);
                bill.setTransactionFee2Currency(currency);
            }
        }

        // process customized daily bill
        this.processCustomizedDailyBill(bill);
    }

    private void processCustomizedDailyBill(final AccountDailyBill bill) {

        final List<StatementProperties.CustomizedDailyBillConfig> configList =
                dailyBillProperties.getCustomConfig();

        if (CollectionUtils.isEmpty(configList)) {
            return;
        }

        /* The CHENG_FAN Postpaid Currency config */
        for (final StatementProperties.CustomizedDailyBillConfig customConfig : configList) {
            if (bill.getAccountId().equals(customConfig.getAccountId())
                    && Objects.isNull(bill.getTransactionFee2Currency())) {
                bill.setTransactionFee2Currency(customConfig.getPostpaidCurrency());
            }
        }
    }
}
