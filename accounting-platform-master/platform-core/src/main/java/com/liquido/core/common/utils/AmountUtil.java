package com.liquido.core.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

@SuppressWarnings("PMD.UseUtilityClass")
public class AmountUtil {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal("100.0");
    public static final BigDecimal ONE_THOUSAND = new BigDecimal("1000.0");
    public static final BigDecimal TEN_THOUSAND = new BigDecimal("10000.0");
    public static final BigDecimal ONE_MILLION = new BigDecimal("1000000.0");
    public static final BigDecimal TEN_MILLION = new BigDecimal("10000000.0");
    public static final BigDecimal HUNDRED_MILLION = new BigDecimal("100000000.0");

    public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("###,###");
    public static final DecimalFormat DIGIT_FORMAT = new DecimalFormat("#,##0.00");
    public static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat("##0.00%");

    public static final int DEFAULT_SCALE = 2;

    public static BigDecimal centToYuan(final BigDecimal amount) {
        return centToYuan(amount, DEFAULT_SCALE);
    }

    public static BigDecimal centToYuan(final BigDecimal amount, final int scale) {
        return Objects.isNull(amount) ? BigDecimal.ZERO
                : amount.divide(ONE_HUNDRED, Math.max(scale, 0), RoundingMode.HALF_UP);
    }

    public static BigDecimal yuanToCent(final BigDecimal amount) {
        return Objects.isNull(amount) ? BigDecimal.ZERO : amount.multiply(ONE_HUNDRED);
    }

    /**
     * Convert string amount from format: 10,000,000.0 to BigDecimal 10000000.0
     *
     * @param strAmount e.g: 10,000,000.0
     * @return 10000000.0
     */
    public static BigDecimal convertAmount(final String strAmount) {
        if (StringUtils.isBlank(strAmount)) {
            return BigDecimal.ZERO;
        }

        final DecimalFormat format = new DecimalFormat();
        format.setParseBigDecimal(true);
        final ParsePosition position = new ParsePosition(0);
        if (strAmount.trim().length() == position.getIndex()) {
            return (BigDecimal) format.parse(strAmount.trim(), position);
        }

        return BigDecimal.ZERO;
    }

    public static BigDecimal division(final BigDecimal divisor,
                                      final BigDecimal dividend) {
        return division(divisor, dividend, DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal division(final BigDecimal divisor,
                                      final BigDecimal dividend,
                                      final int scale) {
        return division(divisor, dividend, scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal division(final BigDecimal divisor,
                                      final Integer dividend) {
        return division(divisor, dividend, DEFAULT_SCALE);
    }

    public static BigDecimal division(final BigDecimal divisor,
                                      final Integer dividend,
                                      final int scale) {

        Assert.notNull(divisor, "divisor can not be null");
        Assert.notNull(dividend, "dividend can not be null");

        return division(divisor, new BigDecimal(dividend.toString()), scale, RoundingMode.HALF_UP);
    }


    public static BigDecimal division(final Integer divisor,
                                      final Integer dividend) {
        return division(divisor, dividend, DEFAULT_SCALE);
    }

    public static BigDecimal division(final Integer divisor,
                                      final Integer dividend,
                                      final int scale) {

        Assert.notNull(divisor, "divisor can not be null");
        Assert.notNull(dividend, "dividend can not be null");

        return division(new BigDecimal(divisor.toString()),
                new BigDecimal(dividend.toString()), scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal division(final BigDecimal divisor,
                                      final Long dividend) {
        return division(divisor, dividend, DEFAULT_SCALE);
    }

    public static BigDecimal division(final BigDecimal divisor,
                                      final Long dividend,
                                      final int scale) {

        Assert.notNull(divisor, "divisor can not be null");
        Assert.notNull(dividend, "dividend can not be null");

        return division(divisor, new BigDecimal(dividend.toString()), scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal division(final Long divisor,
                                      final Long dividend) {
        return division(divisor, dividend, DEFAULT_SCALE);
    }

    public static BigDecimal division(final Long divisor,
                                      final Long dividend,
                                      final int scale) {

        Assert.notNull(divisor, "divisor can not be null");
        Assert.notNull(dividend, "dividend can not be null");

        return division(new BigDecimal(divisor.toString()),
                new BigDecimal(dividend.toString()), scale, RoundingMode.HALF_UP);
    }


    public static BigDecimal division(final BigDecimal divisor,
                                      final Double dividend) {
        return division(divisor, dividend, DEFAULT_SCALE);
    }

    public static BigDecimal division(final BigDecimal divisor,
                                      final Double dividend,
                                      final int scale) {

        Assert.notNull(divisor, "divisor can not be null");
        Assert.notNull(dividend, "dividend can not be null");

        return division(divisor, new BigDecimal(dividend.toString()), scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal division(final Double divisor,
                                      final Double dividend) {
        return division(divisor, dividend, DEFAULT_SCALE);
    }

    public static BigDecimal division(final Double divisor,
                                      final Double dividend,
                                      final int scale) {

        Assert.notNull(divisor, "divisor can not be null");
        Assert.notNull(dividend, "dividend can not be null");

        return division(new BigDecimal(divisor.toString()),
                new BigDecimal(dividend.toString()), scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal division(final BigDecimal divisor,
                                      final BigDecimal dividend,
                                      final int scale,
                                      final RoundingMode roundingMode) {

        Assert.notNull(divisor, "divisor must not be null");
        Assert.notNull(dividend, "dividend must not be null");
        Assert.notNull(roundingMode, "roundingMode must not be null");
        return dividend.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : divisor.divide(dividend, Math.max(scale, 0), roundingMode);
    }
}
