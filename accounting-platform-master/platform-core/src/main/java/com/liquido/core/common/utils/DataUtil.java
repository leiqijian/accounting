package com.liquido.core.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;

import com.liquido.core.common.exception.CommonExceptionCode;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * data util
 */
public class DataUtil {

    private static final String REG_NUMERIC = "[+-]?\\d*([.]?\\d*)?$";
    private static final String REG_NUMBER = "\\d*$";
    private static final String REG_INTEGER = "[0-9]{1,}$";
    private static final String REG_DOUBLE = "\\d{0,}.\\d+$";
    private static final String REG_IP =
            "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
    private static final String REG_MOBILE = "^1[0-9]{10}$";
    private static final String REG_EMAIL =
            "^[\\wa-z\\d-_A-Z]+(\\.[a-z\\d]+)*@([\\da-z](-[\\da-z])?)+(\\.{1,2}[a-zA-Z]+)+$";

    public static int getCode() {
        return (int) ((Math.random() * 9 + 1) * 100000);
    }

    public static String getUuid() {
        return UUID.randomUUID().toString()
                .replaceAll("-", "");
    }

    /**
     * isNumeric
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(final String str) {
        // [+-]?\\d*([.]?\\d*)?$
        // [+-]?\\d{0,}([.]?\\d{0,})?$
        // [+-]{0,1}\\d{0,}([.]{0,1}\\d{0,}){0,1}$
        if (StringUtils.isNotBlank(str) && str.matches(REG_NUMERIC)) {
            return true;
        }
        return false;
    }

    /**
     * isNumber
     *
     * @param str
     * @return
     */
    public static boolean isNumber(final String str) {
        return StringUtils.isNotBlank(str) && str.matches(REG_NUMBER);
    }

    /**
     * isInteger
     *
     * @param str
     * @return
     */
    public static boolean isInteger(final String str) {
        return StringUtils.isNotBlank(str) && str.matches(REG_INTEGER);
    }

    /**
     * isDouble
     *
     * @param str
     * @return
     */
    public static boolean isDouble(final String str) {
        return StringUtils.isNotBlank(str) && str.matches(REG_DOUBLE);
    }

    /**
     * @param amount BigDecimal
     * @param how    int
     * @return BigDecimal
     */
    public static BigDecimal formatAmount(final BigDecimal amount, final int how) {
        return amount.setScale(how, RoundingMode.HALF_UP);
    }

    /**
     * @param amount BigDecimal
     * @param how    int
     * @param round
     * @return BigDecimal
     */
    public static BigDecimal formatAmount(final BigDecimal amount, final int how,
                                          final boolean round) {
        if (round) {
            return amount.setScale(how, RoundingMode.HALF_UP);
        }
        return amount.setScale(how, RoundingMode.DOWN);
    }

    /**
     * @param value double
     * @param how   int
     * @return BigDecimal
     */
    public static BigDecimal formatNumbers(final double value, final int how) {
        return new BigDecimal(value).setScale(how, RoundingMode.HALF_UP);
    }

    /**
     * @param value double
     * @param how   int
     * @return BigDecimal
     */
    public static double formatNumber(final double value, final int how) {
        return new BigDecimal(value).setScale(how, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * @param value double
     * @param how   int
     * @param round
     * @return
     */
    public static BigDecimal formatNumbers(final double value,
                                           final int how,
                                           final boolean round) {
        if (round) {
            return new BigDecimal(value).setScale(how, RoundingMode.HALF_UP);
        }
        return new BigDecimal(value).setScale(how, RoundingMode.DOWN);
    }

    /**
     * @param value
     * @param scale
     * @param isPercentage
     * @return
     */
    public static double formatDouble(final double value,
                                      final int scale,
                                      final boolean isPercentage) {
        final int ratio = (int) Math.pow(10, scale);
        return (double) Math.round(value * (isPercentage ? 100 : 1) * ratio) / ratio;
    }

    /**
     * @param value
     * @param scale
     * @return
     */
    public static double formatDouble(final double value,
                                      final int scale) {
        return formatDouble(value, scale, false);
    }

    /**
     * @param beginTime
     * @return hour
     */
    public static double postTime(final Date beginTime) {
        final long end = new Date(System.currentTimeMillis()).getTime();
        return DataUtil.formatNumbers(
                        Double.parseDouble((end - beginTime.getTime()) + "")
                                / 1000.0 / 60.0 / 60.0, 0)
                .doubleValue();
    }

    /**
     * @param str
     * @return
     */
    public static boolean isIpAddress(final String str) {
        if (StringUtils.isNotBlank(str) && str.matches(REG_IP)) {
            return true;
        }
        return false;
    }

    /**
     * @param mobile
     * @return
     */
    public static boolean isMobile(final String mobile) {
        if (StringUtils.isNotBlank(mobile) && mobile.matches(REG_MOBILE)) {
            return true;
        }
        return false;
    }

    /**
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        int num = 0;
        int leftNum = 0;
        int rightNum = 0;
        if (StringUtils.isNotBlank(email)) {
            email = email.trim();
            for (int i = 0; i < email.length(); i++) {
                if (email.substring(i, i + 1).equals("@")) {
                    num++;
                } else {
                    if (num == 0) {
                        leftNum++;
                    } else {
                        rightNum++;
                    }
                }
            }
        }
        if (num == 1 && leftNum > 0 && rightNum > 0) {
            return true;
        }
        return false;
    }

    /**
     * @param word
     * @return
     */
    public static String replaceWord(String word) {
        if (word != null && word.trim().length() > 0) {
            word = word.replaceAll("\"", "&quot;").replaceAll("\'", "&apos;");
        }
        return word;
    }

    public static String randomPassword() {
        return randomPassword(16);
    }

    public static String randomPassword(final int length) {
        if (length <= 0) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL.exception(length);
        }

        final List<Character> list = new ArrayList<>();
        final int[] split = split(length, 4);

        for (final char c : RandomStringUtils.randomNumeric(split[0]).toCharArray()) {
            list.add(c);
        }
        for (final char c : RandomStringUtils.random(split[1], "ABCDEFGHIJKLMNPQRSTUVWXYZ")
                .toCharArray()) {
            list.add(c);
        }
        for (final char c : RandomStringUtils.random(split[2], "abcdefghijkmnpqrstuvwxyz")
                .toCharArray()) {
            list.add(c);
        }
        for (final char c : RandomStringUtils.random(split[3], "!@#$%^&*").toCharArray()) {
            list.add(c);
        }

        Collections.shuffle(list);
        return StringUtils.join(list, "");
    }

    public static int[] split(final int number, final int limit) {
        if (limit <= 0) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL.exception(limit);
        }
        int[] result = new int[limit];
        for (int i = 0; i < limit; i++) {
            final int remain = number % limit;
            if (remain < 0) {
                result[i] = number / limit + (-remain > i ? -1 : 0);
            } else if (remain > 0) {
                result[i] = number / limit + (remain > i ? 1 : 0);
            } else {
                result[i] = number / limit;
            }
        }
        return result;
    }

    public static TreeSet<Integer> getAllNumberByCompareNumber(final Integer number1,
                                                               final Integer number2) {
        final int max = Math.max(number1, number2);
        int min = Math.min(number1, number2);
        final TreeSet<Integer> set = new TreeSet<>();
        while (true) {
            set.add(min);

            if (min >= max) {
                break;
            }
            min++;
        }
        return set;
    }
}
