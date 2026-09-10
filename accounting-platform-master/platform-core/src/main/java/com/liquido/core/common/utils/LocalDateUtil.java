package com.liquido.core.common.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;

public class LocalDateUtil {

    public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMAT_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter FORMAT_YYYYMM = DateTimeFormatter.ofPattern("yyyyMM");

    public static LocalDateTime dateToDatetime(final LocalDate date,
                                               final LocalTime time,
                                               final ZoneId from,
                                               final ZoneId to) {
        return LocalDateTimeUtil.zoneTransfer(LocalDateTime.of(date, time), from, to);
    }

    public static LocalDateTime dateToDatetime(final LocalDate date,
                                               final LocalTime time,
                                               final String from,
                                               final String to) {
        return LocalDateTimeUtil.zoneTransfer(LocalDateTime.of(date, time), from, to);
    }

    public static LocalDateTime dateToUtcDatetime(final LocalDate date,
                                                  final LocalTime time,
                                                  final ZoneId localZoneId) {
        return LocalDateTimeUtil.localToUtc(LocalDateTime.of(date, time), localZoneId);
    }

    public static LocalDateTime dateToUtcDatetime(final LocalDate date,
                                                  final LocalTime time,
                                                  final String localZoneId) {
        return LocalDateTimeUtil.localToUtc(LocalDateTime.of(date, time), localZoneId);
    }

    public static LocalDateTime dateToLocalDatetime(final LocalDate date,
                                                    final LocalTime time,
                                                    final ZoneId localZoneId) {
        return LocalDateTimeUtil.utcToLocal(LocalDateTime.of(date, time), localZoneId);
    }

    public static LocalDateTime dateToLocalDatetime(final LocalDate date,
                                                    final LocalTime time,
                                                    final String localZoneId) {
        return LocalDateTimeUtil.utcToLocal(LocalDateTime.of(date, time), localZoneId);
    }

    public static Date convertToDate(final LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate formatToLocalDate(final String dateStr) {
        return LocalDate.parse(dateStr, FORMAT_DATE);
    }

    public static LocalDate formatToLocalDate(final String dateStr,
                                              final DateTimeFormatter formatter) {
        return LocalDate.parse(dateStr, formatter);
    }

    public static LocalDate firstDayOfMonth() {
        return firstDayOfMonth(LocalDate.now());
    }

    public static LocalDate firstDayOfMonth(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate lastDayOfMonth() {
        return lastDayOfMonth(LocalDate.now());
    }

    public static LocalDate lastDayOfMonth(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static Integer getMonth(final LocalDate localDate) {
        return localDate.getMonthValue();
    }

    public static Integer getYearMonth(final LocalDate localDate) {
        final int month = getMonth(localDate);
        return Integer.valueOf(String.format("%s%s",
                getYear(localDate), String.format("%02d", month)));
    }

    /**
     * recover yearMonth
     *
     * @param yearMonth yearMonth
     * @return Pair.of(year, month)
     */
    public static Pair<Integer, Integer> recoverYearMonth(final Integer yearMonth) {
        return Pair.of(yearMonth / 100, yearMonth % 100);
    }

    public static boolean inThisMonth(final LocalDate localDate, final ZoneId localZoneId) {
        LocalDate localDateNow = LocalDateTimeUtil
                .utcToLocal(LocalDateTimeUtil.nowUtc(), localZoneId)
                .toLocalDate();
        return Objects.equals(getYear(localDate), getYear(localDateNow))
                && Objects.equals(getMonth(localDate), getMonth(localDateNow));
    }

    public static boolean inThisMonth(final LocalDate localDate, final String localZoneId) {
        return inThisMonth(localDate, ZoneId.of(localZoneId));
    }

    public static Integer getWeek(final LocalDate localDate) {
        final WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        return localDate.get(weekFields.weekOfYear());
    }

    public static Integer getYearWeek(final LocalDate localDate) {
        final int week = getWeek(localDate);
        return Integer.valueOf(String.format("%s%s",
                getYear(localDate), String.format("%02d", week)));
    }

    /**
     * recover yearWeek
     *
     * @param yearWeek yearWeek
     * @return Pair.of(year, week)
     */
    public static Pair<Integer, Integer> recoverYearWeek(final Integer yearWeek) {
        return Pair.of(yearWeek / 100, yearWeek % 100);
    }

    public static boolean inThisWeek(final LocalDate localDate, final ZoneId localZoneId) {
        LocalDate localDateNow = LocalDateTimeUtil
                .utcToLocal(LocalDateTimeUtil.nowUtc(), localZoneId)
                .toLocalDate();
        return Objects.equals(getYear(localDate), getYear(localDateNow))
                && Objects.equals(getWeek(localDate), getWeek(localDateNow));
    }

    public static boolean inThisWeek(final LocalDate localDate, final String localZoneId) {
        return inThisWeek(localDate, ZoneId.of(localZoneId));
    }

    public static Integer getYear(final LocalDate localDate) {
        return localDate.getYear();
    }

    /**
     * The first day of the week
     *
     * @param yearWeek
     * @return
     */
    public static LocalDate yearWeekToFirstDayOfWeekLocalDate(final Integer yearWeek) {
        final Pair<Integer, Integer> yearWeekPair = recoverYearWeek(yearWeek);

        final LocalDate firstWeek = LocalDate.ofYearDay(yearWeekPair.getLeft(), 1);
        if (yearWeekPair.getRight() == 1) {
            return firstWeek;
        }

        final int weekToDay =
                lastDayOfWeek(firstWeek).getDayOfYear() + (yearWeekPair.getRight() - 2) * 7 + 1;
        return firstDayOfWeek(
                LocalDate.ofYearDay(yearWeekPair.getLeft(), weekToDay));
    }

    /**
     * The last day of the week
     *
     * @param yearWeek
     * @return
     */
    public static LocalDate yearWeekToLastDayOfWeekLocalDate(final Integer yearWeek) {
        LocalDate firstDayOfWeek = yearWeekToFirstDayOfWeekLocalDate(yearWeek);
        LocalDate lastDayOfWeek = lastDayOfWeek(firstDayOfWeek);

        if (lastDayOfWeek.getYear() > firstDayOfWeek.getYear()) {
            return lastDayOfYear(firstDayOfWeek);
        }
        return lastDayOfWeek;
    }

    public static LocalDate firstDayOfYear(final LocalDate localDate) {
        return localDate.with(TemporalAdjusters.firstDayOfYear());
    }

    public static LocalDate lastDayOfYear(final LocalDate localDate) {
        return localDate.with(TemporalAdjusters.lastDayOfYear());
    }

    public static LocalDate firstDayOfWeek(final LocalDate localDate) {
        return localDate.with(DayOfWeek.MONDAY);
    }

    public static LocalDate lastDayOfWeek(final LocalDate localDate) {
        return localDate.with(DayOfWeek.SUNDAY);
    }

}
