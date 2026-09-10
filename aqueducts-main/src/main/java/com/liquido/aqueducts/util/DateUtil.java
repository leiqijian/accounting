package com.liquido.aqueducts.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    /**
     * Format UTC Time
     *
     * @param UTCDateStr a UTC time string like "yyyy-MM-dd'T'HH:mm:ss'Z'"
     * @return time string like "yyyy-MM-dd"
     * @throws ParseException
     */
    public static String formatUTCDate(String UTCDateStr) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Date parse = sdf.parse(UTCDateStr);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(parse);
    }

    /**
     * Converts the UTC time to a timestamp
     *
     * @param UTCDateStr a UTC time string like 2022-09-16T08:19:20Z
     * @return timestamp
     */
    public static long UTCDateToTimestamp(String UTCDateStr) {
        if (!StringUtils.hasText(UTCDateStr)) {
            return 0;
        }
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            final Date date = sdf.parse(UTCDateStr);
            return date.getTime();
        } catch (ParseException ignored) {
        }
        return 0;
    }

    /**
     * Format GMT Time
     *
     * @param GMTDateStr a GMT time string like "yyyy-MM-dd HH:mm:ss 'GMT'+00:00"
     * @return time string like "yyyy-MM-dd"
     * @throws ParseException
     */
    public static String formatGMTDate(String GMTDateStr) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'GMT'+00:00");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        final Date parse = sdf.parse(GMTDateStr);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(parse);
    }

}
