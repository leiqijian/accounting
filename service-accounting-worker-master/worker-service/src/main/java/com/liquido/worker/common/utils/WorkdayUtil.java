package com.liquido.worker.common.utils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.pojo.dto.WorkingDayDto;
import com.liquido.worker.exception.WorkerExceptionCode;

import org.springframework.util.Assert;

public class WorkdayUtil {

    public static LocalDate getNextWorkDay(final LocalDate transactionDate,
                                           final TradingModelEnum tradingModel,
                                           final List<WorkingDayDto> workdayList) {
        Assert.notNull(transactionDate, "transactionDate can not be null");

        // if trading-model is null used default T7
        if (Objects.isNull(tradingModel)) {
            throw WorkerExceptionCode.TRADING_MODEL_UNDEFINED.exception();
        }

        if (TradingModelEnum.INSTANT_TRADING.contains(tradingModel)) {
            return transactionDate;
        }

        // Natural Day(D+n)
        if (tradingModel.getCode().startsWith("D")) {
            return transactionDate.plusDays(tradingModel.getValue());
        }

        // Working Day(T+n: exclude SATURDAY, SUNDAY, HOLIDAY)
        return workdayList.stream()
                .filter(item -> item.getWorkday() && item.getWorkDate().isAfter(transactionDate))
                .limit(tradingModel.getValue() <= 0 ? 1 : tradingModel.getValue())
                .sorted(Comparator.comparing(WorkingDayDto::getWorkDate, Comparator.reverseOrder()))
                .findFirst().map(WorkingDayDto::getWorkDate)
                .orElseThrow(() -> WorkerExceptionCode.WORKDAY_UNDEFINED.exception());
    }


    public static LocalDate getInstallmentWorkDay(final LocalDate transactionDate,
                                                  final int installmentIndex,
                                                  final int periodDays,
                                                  final List<WorkingDayDto> workdayList) {
        Assert.notNull(transactionDate, "transactionDate can not be null");

        final LocalDate workdate = transactionDate.plusDays(installmentIndex * periodDays);
        final WorkingDayDto workingDay = workdayList.stream()
                .filter(x -> workdate.equals(x.getWorkDate()))
                .findFirst().orElse(null);

        if (Objects.nonNull(workingDay) && workingDay.getWorkday()) {
            return workingDay.getWorkDate();
        }

        // get next workdate
        return getNextWorkDay(workdate, TradingModelEnum.T1, workdayList);
    }

}
