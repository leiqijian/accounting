package com.liquido.worker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.CardTypeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.pojo.dto.WorkingDayDto;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.utils.WorkdayUtil;
import com.liquido.worker.pojo.bo.AccountingScheduleBo;
import com.liquido.worker.pojo.bo.TransactionFeeBo;
import com.liquido.worker.pojo.bo.TransactionMoneyBo;

import com.google.common.collect.Lists;

public class InstallmentTest {

    private static final ZoneId utcZone = Constant.COMMON.ZONE_UTC;

    public static void main(String[] args) {
        final BigDecimal feeAmount = new BigDecimal("10");
        final BigDecimal taxAmount = new BigDecimal("5");
        final TransactionMoneyBo transactionMoney = TransactionMoneyBo.builder()
                .settlementAmount(new BigDecimal("1000"))
                .beCreditedAmount(new BigDecimal("1000").subtract(feeAmount).subtract(taxAmount))
                .transactionDate(LocalDateUtil.formatToLocalDate("2025-09-15"))
                .transactionFeeList(Lists.newArrayList(
                        TransactionFeeBo.builder().instantFlag(true)
                                .feeGroup(FeeGroupEnum.TRANSACTION_FEE)
                                .amountPon(AmountPonEnum.NEGATIVE)
                                .settlementAmount(feeAmount).build(),
                        TransactionFeeBo.builder().instantFlag(true)
                                .amountPon(AmountPonEnum.NEGATIVE)
                                .feeGroup(FeeGroupEnum.TAX)
                                .settlementAmount(taxAmount).build()
                )).build();

        System.out.println(JsonUtil.toJson(buildAccountingScheduleList(transactionMoney)));
        ;
    }


    private static TransactionMoneyBo buildAccountingScheduleList(
            final TransactionMoneyBo transactionMoney) {

        final List<AccountingScheduleBo> scheduleList = Lists.newArrayList();
        final int totalInstallment = 6;

        transactionMoney.setInstallmentFlag(true);
        final BigDecimal totalFeeAmount =
                Optional.ofNullable(transactionMoney.getTransactionFeeList().stream()
                                .filter(TransactionFeeBo::getInstantFlag)
                                .filter(x -> FeeGroupEnum.TRANSACTION_FEE == x.getFeeGroup())
                                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y.getSettlementAmount()
                                        .multiply(y.getAmountPon().getCode())), BigDecimal::add))
                        .orElse(BigDecimal.ZERO);

        final BigDecimal totalTaxAmount =
                Optional.ofNullable(transactionMoney.getTransactionFeeList().stream()
                                .filter(TransactionFeeBo::getInstantFlag)
                                .filter(x -> FeeGroupEnum.TRANSACTION_FEE != x.getFeeGroup())
                                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y.getSettlementAmount()
                                        .multiply(y.getAmountPon().getCode())), BigDecimal::add))
                        .orElse(BigDecimal.ZERO);

        final LocalDate transactionDate = transactionMoney.getTransactionDate();
        final BigDecimal totalAccountingAmount = transactionMoney.getSettlementAmount()
                .add(totalTaxAmount).add(totalFeeAmount);

        // account amount divide installment
        final BigDecimal singleInstallAmount = totalAccountingAmount.divide(
                new BigDecimal(totalInstallment), 0, RoundingMode.HALF_UP);
        final BigDecimal roundingDifferenceAmount = totalAccountingAmount
                .subtract(singleInstallAmount.multiply(new BigDecimal(totalInstallment))
                        .setScale(0, RoundingMode.HALF_UP));

        // settlement amount divide installment
        final BigDecimal singleInstallSettleAmount = transactionMoney.getSettlementAmount()
                .divide(new BigDecimal(totalInstallment), 0, RoundingMode.HALF_UP);
        final BigDecimal roundingDifferenceSettleAmount = transactionMoney.getSettlementAmount()
                .subtract(singleInstallSettleAmount.multiply(new BigDecimal(totalInstallment))
                        .setScale(0, RoundingMode.HALF_UP));

        // fee amount divide installment
        final BigDecimal singleInstallFeeAmount = totalFeeAmount.divide(
                new BigDecimal(totalInstallment), 0, RoundingMode.HALF_UP);
        final BigDecimal roundingDifferenceFeeAmount = totalFeeAmount
                .subtract(singleInstallFeeAmount
                        .multiply(new BigDecimal(totalInstallment))
                        .setScale(0, RoundingMode.HALF_UP));

        // tax amount divide installment
        final BigDecimal singleInstallTaxAmount = totalTaxAmount.divide(
                new BigDecimal(totalInstallment), 0, RoundingMode.HALF_UP);
        final BigDecimal roundingDifferenceTaxAmount = totalTaxAmount
                .subtract(singleInstallTaxAmount.multiply(new BigDecimal(totalInstallment))
                        .setScale(0, RoundingMode.HALF_UP));

        final List<WorkingDayDto> workdayList = initWorkday();
        for (int i = 1; i <= totalInstallment; i++) {

            final LocalDate accountingDate = WorkdayUtil.getInstallmentWorkDay(
                    transactionDate, i, 31, workdayList);

            BigDecimal settlementAmount = singleInstallSettleAmount;
            BigDecimal accountingAmount = singleInstallAmount;
            BigDecimal feeAmount = singleInstallFeeAmount;
            BigDecimal taxAmount = singleInstallTaxAmount;

            if (i == 1) {
                transactionMoney.setBeCreditedDate(accountingDate);
            }

            // The last installment will supplement the rounding difference amount
            if (i == totalInstallment) {
                settlementAmount = singleInstallSettleAmount.add(roundingDifferenceSettleAmount);
                accountingAmount = singleInstallAmount.add(roundingDifferenceAmount);
                feeAmount = singleInstallFeeAmount.add(roundingDifferenceFeeAmount);
                taxAmount = singleInstallTaxAmount.add(roundingDifferenceTaxAmount);
            }
            scheduleList.add(AccountingScheduleBo.builder()
                    .currentInstallment(i)
                    .totalInstallment(totalInstallment)
                    .accountingDate(accountingDate)
                    .settlementAmount(settlementAmount)
                    .accountingAmount(accountingAmount)
                    .feeAmount(feeAmount)
                    .taxAmount(taxAmount)
                    .cardType(CardTypeEnum.CREDIT_CARD.getCode())
                    .cardBrand(CreditCardGroupCodeEnum.VISA.getCode())
                    .build());
        }

        transactionMoney.setAccountingScheduleList(scheduleList);
        transactionMoney.setBeCreditedAmount(BigDecimal.ZERO);

        return transactionMoney;
    }


    private static List<WorkingDayDto> initWorkday() {

        List<WorkingDayDto> workdayList = Lists.newArrayList();

        LocalDate beginDate = LocalDateUtil.formatToLocalDate("2025-01-01");
        LocalDate endDate = LocalDateUtil.formatToLocalDate("2026-12-31");

        int i = 0;
        LocalDate date = beginDate;
        while (endDate.isAfter(date)) {
            date = beginDate.plusDays(i);
            workdayList.add(WorkingDayDto.builder()
                    .workDate(date)
                    .weekday(date.getDayOfWeek().getValue())
                    .workday(date.getDayOfWeek().getValue() <= 5)
                    .build());
            i++;
        }

        return workdayList;
    }
}
