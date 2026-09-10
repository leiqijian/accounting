package com.liquido.worker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.pojo.dto.WorkingDayDto;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.utils.WorkdayUtil;
import com.liquido.worker.pojo.bo.AccountingScheduleBo;
import com.liquido.worker.pojo.bo.TransactionFeeBo;
import com.liquido.worker.pojo.bo.TransactionMoneyBo;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
public class MainTest {

    private static final ZoneId utcZone = Constant.COMMON.ZONE_UTC;

    private static List<WorkingDayDto> initWorkday() {

        List<WorkingDayDto> workdayList = Lists.newArrayList();

        LocalDate beginDate = LocalDateUtil.formatToLocalDate("2024-01-01");
        LocalDate endDate = LocalDateUtil.formatToLocalDate("2025-12-31");

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

    public static void main(String[] args) {

        final LocalDate transactionDate = LocalDateUtil.formatToLocalDate("2024-08-01");

        final BigDecimal transactionAmount = new BigDecimal("3940");

        final BigDecimal totalInstallment = new BigDecimal("4");

        final BigDecimal totalFeeAmount = new BigDecimal("-297");
        final BigDecimal totalTaxAmount = new BigDecimal("-15");

        System.out.println("beCreditedAmount= " + (transactionAmount.add(totalFeeAmount)
                .add(totalTaxAmount)));

        final BigDecimal totalAccountingAmount = transactionAmount.add(totalTaxAmount);
        final BigDecimal singleInstallmentAmount = totalAccountingAmount
                .divide(totalInstallment, 0, RoundingMode.DOWN);

        final BigDecimal sumSingleInstallmentAmount =
                singleInstallmentAmount.multiply(totalInstallment)
                        .setScale(0, RoundingMode.HALF_UP);

        final BigDecimal roundingDifferenceAmount = totalAccountingAmount.subtract(
                        singleInstallmentAmount.multiply(totalInstallment))
                .setScale(0, RoundingMode.HALF_UP);

        System.out.println("transactionAmount= " + transactionAmount);
        System.out.println("feeAmount= " + totalFeeAmount);
        System.out.println("taxAmount= " + totalTaxAmount);
        System.out.println("totalAccountingAmount= " + totalAccountingAmount);
        System.out.println("singleInstallmentAmount= " + singleInstallmentAmount);
        System.out.println("sumSingleInstallmentAmount= " + sumSingleInstallmentAmount);
        System.out.println("roundingDifferenceAmount= " + roundingDifferenceAmount);

        final List<WorkingDayDto> workdateList = initWorkday();
        final int totalCount = totalInstallment.intValue();
        BigDecimal sumInstallmentAmountAmount = BigDecimal.ZERO;
        for (int i = 1; i <= totalCount; i++) {

            final LocalDate accountingDate = WorkdayUtil.getInstallmentWorkDay(
                    transactionDate, i, 31, workdateList);


            final BigDecimal accountingAmount;
            if (i == 1) {
                // The first installment will deduct the fee immediately
                accountingAmount = singleInstallmentAmount.add(
                        Optional.ofNullable(totalFeeAmount).orElse(BigDecimal.ZERO));

            } else if (i == totalCount) {
                // The last installment will supplement the rounding difference amount
                accountingAmount = singleInstallmentAmount.add(roundingDifferenceAmount);
            } else {
                accountingAmount = singleInstallmentAmount;
            }

            /*
            BigDecimal accountingAmount = singleInstallmentAmount;
            if (i == 1) {
                // The first installment will deduct the fee immediately
                accountingAmount = singleInstallmentAmount
                        .add(fee)
                        .add(roundingDifferenceAmount);

            }
            */
            sumInstallmentAmountAmount = sumInstallmentAmountAmount.add(accountingAmount);

            System.out.println("installment[" + i + "], accountingDate= " + accountingDate
                    + ",accountingAmount= " + accountingAmount);
        }

        System.out.println("sumInstallmentAmountAmount= " + sumInstallmentAmountAmount);

        System.out.println("============================================================");
        buildAccountingScheduleList();
    }

    private static void test() throws Exception {

        final StopWatch watch = new StopWatch("TestStopWatch");

        watch.start("step1");
        System.out.println("step1 do something");
        Thread.sleep(200L);
        watch.stop();
        long step1 = watch.getLastTaskTimeMillis();

        watch.start("step2");
        System.out.println("step2 do something");
        Thread.sleep(100L);
        watch.stop();
        long step2 = watch.getLastTaskTimeMillis();

        watch.start("step3");
        System.out.println("step3 do something");
        Thread.sleep(50L);
        watch.stop();
        long step3 = watch.getLastTaskTimeMillis();

        watch.start("step4");
        System.out.println("step4 do something");
        Thread.sleep(150L);
        watch.stop();
        long step4 = watch.getLastTaskTimeMillis();

        long step5 = watch.getTotalTimeMillis();

        System.out.println(step1);
        System.out.println(step2);
        System.out.println(step3);
        System.out.println(step4);
        System.out.println(step5);
    }

    private static List<TaskFeeCalculation> getDataList() {
        List<TaskFeeCalculation> dataList = Lists.newArrayList();

        for (long i = 0; i < 10; i++) {
            TaskFeeCalculation task = new TaskFeeCalculation();
            task.setId(i);
            if (i % 2 == 0) {
                task.setTransactionTime(LocalDateTimeUtil.nowUtc().plusDays(1));
            } else {
                task.setTransactionTime(LocalDateTimeUtil.nowUtc());
            }
            dataList.add(task);
        }
        return dataList;
    }

    public static void testTimeTransfer() {
        LocalDateTime time1 = LocalDateTime.now().atZone(Constant.COMMON.ZONE_UTC)
                .withZoneSameInstant(ZoneId.of("UTC-3")).toLocalDateTime();

        final LocalDateTime dayStart = LocalDateTime.of(time1.toLocalDate(), LocalTime.MIN);
        final LocalDateTime dayEnd = LocalDateTime.of(time1.toLocalDate(), LocalTime.MAX);

        System.out.println(time1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println(dayStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println(dayEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    }


    /**
     * credit-card transaction installment accounting schedule
     */
    private static void buildAccountingScheduleList() {

        final BigDecimal feeAmount = new BigDecimal("-10");
        final BigDecimal taxAmount = new BigDecimal("-5");
        final TransactionMoneyBo transactionMoney = TransactionMoneyBo.builder()
                .settlementAmount(new BigDecimal("1000"))
                .beCreditedAmount(new BigDecimal("1000").add(feeAmount).add(taxAmount))
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

        final List<WorkingDayDto> workdateList = initWorkday();
        final List<AccountingScheduleBo> scheduleList = Lists.newArrayList();

        final Integer totalInstallment = 6;

        final BigDecimal totalFeeAmount = transactionMoney.getTransactionFeeList().stream()
                .filter(TransactionFeeBo::getInstantFlag)
                .filter(x -> FeeGroupEnum.TRANSACTION_FEE == x.getFeeGroup())
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y.getSettlementAmount()
                        .multiply(y.getAmountPon().getCode())), BigDecimal::add);

        final BigDecimal totalTaxAmount = transactionMoney.getTransactionFeeList().stream()
                .filter(TransactionFeeBo::getInstantFlag)
                .filter(x -> FeeGroupEnum.TRANSACTION_FEE != x.getFeeGroup())
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y.getSettlementAmount()
                        .multiply(y.getAmountPon().getCode())), BigDecimal::add);

        final LocalDate transactionDate = transactionMoney.getTransactionDate();
        final BigDecimal totalAccountingAmount = transactionMoney.getSettlementAmount()
                .add(Optional.ofNullable(totalTaxAmount).orElse(BigDecimal.ZERO));

        final BigDecimal singleInstallmentAmount = totalAccountingAmount.divide(
                new BigDecimal(totalInstallment.toString()), 0, RoundingMode.DOWN);

        final BigDecimal roundingDifferenceAmount =
                totalAccountingAmount.subtract(singleInstallmentAmount.multiply(
                        new BigDecimal(totalInstallment.toString())).setScale(0,
                        RoundingMode.DOWN));

        final BigDecimal beCreditedAmount = transactionMoney.getBeCreditedAmount();
        BigDecimal sumAccountingAmount = BigDecimal.ZERO;
        for (int i = 1; i <= totalInstallment; i++) {

            final LocalDate accountingDate = WorkdayUtil.getInstallmentWorkDay(
                    transactionDate, i, 31, workdateList);

            BigDecimal accountingAmount = singleInstallmentAmount;
            // The first installment will deduct the fee immediately
            if (i == 1) {
                accountingAmount = singleInstallmentAmount.add(Optional.ofNullable(totalFeeAmount)
                        .orElse(BigDecimal.ZERO));
            }

            // The last installment will supplement the rounding difference amount
            if (i == totalInstallment) {
                accountingAmount = singleInstallmentAmount.add(roundingDifferenceAmount);
            }

            scheduleList.add(AccountingScheduleBo.builder()
                    .currentInstallment(i)
                    .totalInstallment(totalInstallment)
                    .accountingDate(accountingDate)
                    .accountingAmount(accountingAmount)
                    .build());

            sumAccountingAmount = sumAccountingAmount.add(accountingAmount);
        }

        if (beCreditedAmount.compareTo(sumAccountingAmount) != 0) {
            final BigDecimal diffAmount = beCreditedAmount.subtract(sumAccountingAmount);
            final AccountingScheduleBo firstInstallmentBo = scheduleList.get(0);
            firstInstallmentBo.setAccountingAmount(
                    firstInstallmentBo.getAccountingAmount().add(diffAmount));
        }

        transactionMoney.setAccountingScheduleList(scheduleList);
        // transactionMoney.setBeCreditedAmount(BigDecimal.ZERO);
        System.out.println("scheduleList=" + JsonUtil.toJson(scheduleList));
        final BigDecimal totalAmt = scheduleList.stream().reduce(BigDecimal.ZERO, (x, y) ->
                x.add(y.getAccountingAmount()), BigDecimal::add);

        System.out.println("totalScheduleAmt=" + totalAmt);
    }

}
