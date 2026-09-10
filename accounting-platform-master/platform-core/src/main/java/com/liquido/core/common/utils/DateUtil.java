package com.liquido.core.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DateUtil
 */
@Slf4j
public class DateUtil {

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

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
                log.error("strToDate error", e2);
            }
        }
        return date;
    }


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
            logger.debug("string: {} Convert to time format：{} fail", str, pattern, e);
            return false;
        }
        return true;
    }

    /**
     * @param str format: yyyy-MM-dd
     * @return java.util.Date
     */
    public static Date str2Date(final String str) {
        return str2Date(str, FORMAT_DATE);
    }

    /**
     * @param str format: yyyy-MM-dd HH:mm:ss
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
            return new SimpleDateFormat(pattern).format(date);
        } catch (Exception e) {
            log.error("dateToStr error", e);
        }

        return null;
    }

    /**
     * @param date java.util.Date格
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
    public static String datetime2Str(final Date date) {
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
     * @return int(yyyy)
     */
    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
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
            log.error("formatDate error", e);
        }
        return null;
    }

    public static Date add(final Date date, final TimeUnit unit, final long amount) {
        try {
            final Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.MILLISECOND, (int) unit.toMillis(amount));
            return c.getTime();
        } catch (Exception e) {
            log.error("add data error", e);
        }
        return null;
    }

    public static Date add(final Date date, final int field, final int amount) {
        try {
            final Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(field, amount);
            return c.getTime();
        } catch (Exception e) {
            log.error("add data error", e);
        }
        return null;
    }

    /**
     * @param date
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date set(final Date date, final int hour, final int minute, final int second) {
        try {
            final Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, second);
            return c.getTime();
        } catch (Exception e) {
            log.error("set data error", e);
        }
        return null;
    }

    public static Date add(final Date date,
                           final int year,
                           final int month,
                           final int day,
                           final int hour,
                           final int minute,
                           final int second) {
        try {
            final Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.YEAR, year);
            c.add(Calendar.MONTH, month);
            c.add(Calendar.DATE, day);
            c.add(Calendar.HOUR, hour);
            c.add(Calendar.MINUTE, minute);
            c.add(Calendar.SECOND, second);
            return c.getTime();
        } catch (Exception e) {
            log.error("add data error", e);
        }
        return null;
    }

    public static Date addYear(final Date date, final int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    public static Date addMonth(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    public static Date addDay(final Date date, final int amount) {
        return add(date, Calendar.DATE, amount);
    }

    public static Date addWeek(final Date date, final int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    public static Date addHour(final Date date, final int amount) {
        return add(date, Calendar.HOUR, amount);
    }

    public static Date addSecond(final Date date, final int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    /**
     * @param date
     * @return
     */
    public static Date getMonthHead(final Date date) {
        return formatDate(date, "yyyy-MM-01");
    }

    /**
     * @param date
     * @param pattern
     * @return
     */
    public static Date getMonthTail(final Date date, final String pattern) {
        return formatDate(addSecond(addMonth(getMonthHead(date), 1), -1), pattern);
    }

    public static boolean isSameDay(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date2Str(date1).equals(date2Str(date2));
    }

    /**
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
        return DateUtil.datetime2Str(new Date(timestamp));
    }

    /**
     * @param date
     * @return
     */
    public static Date lastSecondOfDay(final Date date) {
        return DateUtil.add(DateUtil.formatDate(date),
                0, 0, 1, 0, 0, -1);
    }

    public static Date lastSecondOfToday() {
        return DateUtil.add(DateUtil.formatDate(new Date()),
                0, 0, 1, 0, 0, -1);
    }

    /**
     *
     */
    public static Date lastDayOfYear(final int year) {
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     * @param date
     * @return
     */
    public static int getMonthDayCount(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * @param endDate
     * @return
     */
    public static long getDayLeft(final Date endDate) {
        long timeLeft = 0L;
        try {
            final Date now = new Date(System.currentTimeMillis());
            final long leftTime = (endDate.getTime() - now.getTime()) / 1000;
            return leftTime / (24 * 60 * 60);
        } catch (Exception e) {
            log.error("getDayLeft error", e);
        }
        return timeLeft;
    }

    /**
     * @param endDate
     * @return
     */
    public static String getTimeLeft(Date endDate) {
        try {
            final long leftTime = (endDate.getTime() - new Date(System.currentTimeMillis()).getTime()) / 1000;
            final long day = leftTime / (24 * 60 * 60);
            final long hour = leftTime / (60 * 60) - day * 24;
            final long minute = leftTime / 60 - day * 24 * 60 - hour * 60;
            final long second = leftTime - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60;
            return (day + "D" + hour + "H" + minute + "m" + second + "s");
        } catch (Exception e) {
            log.error("getTimeLeft error", e);
        }
        return "";
    }
}
