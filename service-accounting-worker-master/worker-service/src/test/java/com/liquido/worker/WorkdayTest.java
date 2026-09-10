package com.liquido.worker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.pojo.dto.WorkingDayDto;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.utils.WorkdayUtil;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkdayTest {

    private static final ZoneId utcZone = Constant.COMMON.ZONE_UTC;

    public static void main(String[] args) {

        final List<WorkingDayDto> workdayList = initWorkday();
        System.out.println(JsonUtil.toJson(workdayList));

        final LocalDate date = LocalDateTimeUtil.utcToLocal(
                        LocalDateTimeUtil.formatToLocalDateTime("2024-08-14 22:09:44"), utcZone)
                .toLocalDate();

        System.out.println(
                WorkdayUtil.getNextWorkDay(date, TradingModelEnum.T0, workdayList));

//        System.out.println(
//                WorkdayUtil.getNextWorkDay(date, TradingModelEnum.T5, workdayList));
//
//        System.out.println(
//                WorkdayUtil.getNextWorkDay(date, TradingModelEnum.T7, workdayList));
//
//        System.out.println(
//                WorkdayUtil.getNextWorkDay(date, TradingModelEnum.T15, workdayList));
//
//        System.out.println(
//                WorkdayUtil.getNextWorkDay(date, TradingModelEnum.T30, workdayList));
    }


    private static List<WorkingDayDto> initWorkday() {

        List<WorkingDayDto> workdayList = Lists.newArrayList();

        LocalDate beginDate = LocalDateUtil.formatToLocalDate("2024-01-01");
        LocalDate endDate = LocalDateUtil.formatToLocalDate("2024-12-31");
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
