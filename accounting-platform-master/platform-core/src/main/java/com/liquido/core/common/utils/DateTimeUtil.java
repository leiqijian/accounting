package com.liquido.core.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

/**
 * DateTimeUtil
 */
@Slf4j
public class DateTimeUtil {

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    /**
     * @param str
     * @param pattern
     * @return
     */
    public static boolean isFormat(final String str, final String pattern) {
        try {
            if (str.length() != pattern.length()) {
                return false;
            }

            final DateFormat format = new SimpleDateFormat(pattern, Locale.CHINESE);
            format.setLenient(false);
            format.parse(str);
        } catch (Exception e) {
            log.error("string: {} Convert to time format：{} fail", str, pattern, e);
            return false;
        }
        return true;
    }

    /**
     * @param str
     * @param pattern
     * @return java.util.Date
     */
    public static Date str2Date(String str, String pattern) {
        Date date = null;
        DateFormat format;
        try {
            if (str.length() < pattern.length()) {
                pattern = pattern.substring(0, str.length());
            } else if (str.length() > pattern.length()) {
                str = str.substring(0, pattern.length());
            }
            format = new SimpleDateFormat(pattern, Locale.CHINESE);
            date = format.parse(str);
        } catch (Exception e) {
            try {
                format = new SimpleDateFormat(pattern, Locale.ENGLISH);
                date = format.parse(str);
            } catch (Exception e2) {
                log.error("string: {} Convert to time format：{} fail", str, pattern, e2);
            }
        }
        return date;
    }

    /**
     * @param str format: yyyy-MM-dd
     * @return java.util.Date
     */
    public static Date str2Date(final String str) {
        return str2Date(str, FORMAT_DATE);
    }

    /**
     * @param str format:yyyy-MM-dd HH:mm:ss
     * @return java.util.Date
     */
    public static Date str2DateTime(final String str) {
        return str2Date(str, FORMAT_DATETIME);
    }


    /**
     * @param date    java.util.Date
     * @param pattern
     * @return String
     */
    public static String date2Str(final Date date, final String pattern) {
        if (date == null) {
            return null;
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.format(date);
        } catch (Exception e) {
            log.error("date convert fail", date, pattern, 2);
        }
        return null;
    }

    /**
     * @param date java.util.Date
     * @return String(yyyy - MM - dd)
     */
    public static String date2Str(final Date date) {
        return date2Str(date, FORMAT_DATE);
    }

    public static String date2Str() {
        return date2Str(new Date());
    }

    /**
     * @param date java.util.Date
     * @return String(yyyy - MM - dd HH : mm : ss)
     */
    public static String datetime2Str(Date date) {
        return date2Str(date, FORMAT_DATETIME);
    }

    /**
     * @return String(yyyy - MM - dd HH : mm : ss)
     */
    public static String getCurrentDateTime() {
        return date2Str(new Date(), FORMAT_DATETIME);
    }

    /**
     * @return String(yyyy - MM - dd HH : mm : ss)
     */
    public static String getCurrentDateTimeNum() {
        return date2Str(new Date(), FORMAT_YYYYMMDDHHMMSS);
    }

    public static String getCurrentDate() {
        return date2Str(new Date(), FORMAT_DATE);
    }


    /**
     * @return java.util.Date (yyyy-MM-dd 00:00:00)
     */
    public static Date today() {
        return formatDate(new Date(), FORMAT_DATE);
    }

    /**
     * @param date
     * @return java.util.Date (yyyy-MM-dd 00:00:00)
     */
    public static Date formatDate(final Date date) {
        return formatDate(date, FORMAT_DATE);
    }


    /**
     * @param date
     * @param pattern
     * @return java.util.Date
     */
    public static Date formatDate(final Date date, final String pattern) {
        try {
            final SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(format.format(date));
        } catch (ParseException e) {
            log.error("formatDate error ", e);
        }
        return null;
    }

    /**
     * @param date
     * @return
     */
    public static Date getMonthHead(final Date date) {
        return formatDate(date, "yyyy-MM-01");
    }

    public static boolean isSameDay(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date2Str(date1).equals(date2Str(date2));
    }

    /**
     * date1 - date2
     *
     * @param date1
     * @param date2
     * @param unit
     * @return
     */
    public static long getTimeDiff(final Date date1, final Date date2, final TimeUnit unit) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException(
                    "date1:" + date1 + ", date2:" + date2 + " both cannot be null!!");
        }
        return unit.convert(date1.getTime() - date2.getTime(), TimeUnit.MILLISECONDS);
    }


    /**
     * @param timestamp
     * @return
     */
    public static String milli2str(final long timestamp) {
        return DateTimeUtil.datetime2Str(new Date(timestamp));
    }

    /**
     * @param endDate
     * @return
     */
    public static long getDayLeft(final Date endDate) {
        try {
            final Date now = new Date(System.currentTimeMillis());
            final long timeLeft = (endDate.getTime() - now.getTime()) / 1000;
            return timeLeft / (24 * 60 * 60);
        } catch (Exception e) {
            log.error("getDayLeft error", e);
        }
        return 0L;
    }

    /**
     * @param endDate
     * @return
     */
    public static String getTimeLeft(final Date endDate) {
        String timeLeft = "";
        try {
            final Date now = new Date(System.currentTimeMillis());
            final long leftTime = (endDate.getTime() - now.getTime()) / 1000;
            final long day = leftTime / (24 * 60 * 60);
            final long hour = leftTime / (60 * 60) - day * 24;
            final long minute = leftTime / 60 - day * 24 * 60 - hour * 60;
            final long second = leftTime - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60;
            timeLeft = (day + "day" + hour + "hour" + minute + "hour" + second + "hour");
        } catch (Exception e) {
            log.error("getTimeLeft error", e);
        }
        return timeLeft;
    }

    /**
     * @param begin
     * @param end
     * @return
     */
    public static String getTimeLeft(final Date begin, final Date end) {
        final DateTime dt1 = new DateTime(begin);
        final DateTime dt2 = new DateTime(end);
        return ((Days.daysBetween(dt1, dt2).getDays()) + "day"
                + (Hours.hoursBetween(dt1, dt2).getHours() % 24) + "hour"
                + (Minutes.minutesBetween(dt1, dt2).getMinutes() % 60) + "minute"
                + (Seconds.secondsBetween(dt1, dt2).getSeconds() % 60) + "second");
    }

    /**
     * @param begin
     * @param end
     * @return
     */
    public static int getDateDiff(final Date begin, final Date end) {
        return Days.daysBetween(new DateTime(begin), new DateTime(end)).getDays();
    }

    /**
     * @param begin
     * @param end
     * @return
     */
    public static int getHourDiff(final Date begin, final Date end) {
        return Hours.hoursBetween(new DateTime(begin), new DateTime(end)).getHours() % 24;
    }

    /**
     * @param begin
     * @param end
     * @return
     */
    public static int getMinuteDiff(final Date begin, final Date end) {
        return Minutes.minutesBetween(new DateTime(begin), new DateTime(end)).getMinutes() % 60;
    }

    /**
     * @param begin
     * @param end
     * @return
     */
    public static int getSecondDiff(final Date begin, final Date end) {
        return Seconds.secondsBetween(new DateTime(begin), new DateTime(end)).getSeconds() % 60;
    }
}
