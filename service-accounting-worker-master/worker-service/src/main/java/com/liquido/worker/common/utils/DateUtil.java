package com.liquido.worker.common.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {
    public static LocalDate getCurrentDate(String timeZone) {
        return LocalDate.now(ZoneId.of(timeZone));
    }

    public static boolean isCurrentMonthFirstDay(final String timeZone) {
        LocalDate currentDate = getCurrentDate(timeZone);
        return currentDate.getDayOfMonth() == 1;
    }

    // target Month Use Format：yyyyMM
    // Check if the target month is before or the same as the current month in the local time zone.
    public static boolean isMonthBeforeOrEqual(final Integer targetMonth, final String timeZone) {
        final LocalDate targetDate = LocalDate.of(targetMonth / 100, targetMonth % 100, 1);
        final LocalDate timeZoneDate = getCurrentDate(timeZone);
        return !targetDate.isAfter(timeZoneDate);
    }

    public static LocalDate getFirstDayOfCurrentMonth(final String timeZone) {
        return getCurrentDate(timeZone).withDayOfMonth(1);
    }

    public static LocalDate getLastDayOfCurrentMonth(final String timeZone) {
        return getCurrentDate(timeZone).with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate getLastDayOfLastMonth(final String timeZone) {
        return getCurrentDate(timeZone).minusMonths(1)
                .with(TemporalAdjusters.lastDayOfMonth());
    }

    // Use Format：yyyyMM
    public static LocalDate getFirstDayOfCurrentMonth(final Integer activeMonth) {
        return LocalDate.of(activeMonth / 100, activeMonth % 100, 1);
    }

    // Use Format：yyyyMM
    public static LocalDate getLastDayOfCurrentMonth(final Integer activeMonth) {
        return LocalDate.of(activeMonth / 100, activeMonth % 100, 1)
                .with(TemporalAdjusters.lastDayOfMonth());
    }

    // Use Format：yyyyMM
    public static LocalDate getLastDayOfLastMonth(final Integer activeMonth) {
        return LocalDate.of(activeMonth / 100, activeMonth % 100, 1)
                .minusDays(1);
    }
}
