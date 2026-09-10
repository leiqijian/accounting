package com.liquido.core.common.utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.liquido.core.common.exception.CommonExceptionCode;

import com.google.common.base.Verify;
import com.google.common.collect.ImmutableSetMultimap;
import lombok.Getter;
import org.apache.http.util.TextUtils;

/**
 * Reference:
 * https://github.com/sualeh/creditcardnumber/blob/master/src/main/java/us/fatehi/creditcardnumber/CardBrand.java
 * https://github.com/braintree/credit-card-type/blob/main/src/lib/card-types.ts
 * https://www.bincodes.com/bin-list/
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Getter
public enum CardBrand {

    AMEX(List.of(
            new BinPrefixCheck("34"),
            new BinPrefixCheck("37")),
            new LengthCheck(15)
    ),

    CARTE_BLANCHE(List.of(
            new BinRangeCheck(300, 305)),
            new LengthCheck(14)
    ),

    DINERS_CLUB(List.of(
            new BinPrefixCheck("36"),
            new BinRangeCheck(38, 39)),
            new LengthCheck(14, 16, 19)
    ),

    DISCOVER(List.of(
            new BinPrefixCheck("6011"),
            new BinRangeCheck(644, 649),
            new BinPrefixCheck("65")),
            new LengthCheck(16, 19)),

    ELO(List.of(
            new BinPrefixCheck("401178"),
            new BinPrefixCheck("401179"),
            new BinPrefixCheck("438935"),
            new BinPrefixCheck("457631"),
            new BinPrefixCheck("457632"),
            new BinPrefixCheck("431274"),
            new BinPrefixCheck("451416"),
            new BinPrefixCheck("457393"),
            new BinPrefixCheck("504175"),
            new BinRangeCheck(506699, 506778),
            new BinRangeCheck(509000, 509999),
            new BinPrefixCheck("627780"),
            new BinPrefixCheck("636297"),
            new BinPrefixCheck("636368"),
            new BinRangeCheck(650031, 650033),
            new BinRangeCheck(650035, 650051),
            new BinRangeCheck(650405, 650439),
            new BinRangeCheck(650485, 650538),
            new BinRangeCheck(650541, 650598),
            new BinRangeCheck(650700, 650718),
            new BinRangeCheck(650720, 650727),
            new BinRangeCheck(650901, 650978),
            new BinRangeCheck(651652, 651679),
            new BinRangeCheck(655000, 655019),
            new BinRangeCheck(655021, 655058)),
            new LengthCheck(16)
    ),

    ENROUTE(List.of(
            new BinPrefixCheck("2014"),
            new BinPrefixCheck("2149")),
            new LengthCheck(15)
    ),

    HIPER(List.of(
            new BinPrefixCheck("637095"),
            new BinPrefixCheck("63737423"),
            new BinPrefixCheck("63743358"),
            new BinPrefixCheck("637568"),
            new BinPrefixCheck("637599"),
            new BinPrefixCheck("637609"),
            new BinPrefixCheck("637612")),
            new LengthCheck(16)
    ),

    HIPERCARD(List.of(
            new BinPrefixCheck("606282")),
            new LengthCheck(16)
    ),

    JCB(List.of(
            new BinPrefixCheck("1800"),
            new BinPrefixCheck("2131"),
            new BinRangeCheck(3528, 3589)),
            new LengthRangeCheck(16, 19)
    ),

    MAESTRO(List.of(
            new BinPrefixCheck("493698"),
            new BinRangeCheck(500000, 504174),
            new BinRangeCheck(504176, 506698),
            new BinRangeCheck(506779, 508999),
            new BinRangeCheck(56, 59),
            new BinPrefixCheck("63"),
            new BinPrefixCheck("67"),
            new BinPrefixCheck("6")),
            new LengthRangeCheck(12, 19)
    ),

    MASTERCARD(List.of(
            new BinRangeCheck(2221, 2720),
            new BinRangeCheck(51, 55)),
            new LengthCheck(16)
    ),

    MIR(List.of(
            new BinRangeCheck(2200, 2204)),
            new LengthRangeCheck(16, 19)
    ),

    UATP(List.of(
            new BinPrefixCheck("1")),
            new LengthCheck(15)
    ),

    // UnionPay numbers start with 62
    UNIONPAY(List.of(
            new BinPrefixCheck("620"),
            new BinRangeCheck(62100, 62182),
            new BinRangeCheck(62184, 62197),
            new BinRangeCheck(622, 626),
            new BinPrefixCheck("6270"),
            new BinPrefixCheck("6272"),
            new BinPrefixCheck("6276"),
            new BinRangeCheck(627700, 627779),
            new BinRangeCheck(627781, 627799),
            new BinRangeCheck(6282, 6289),
            new BinPrefixCheck("6291"),
            new BinPrefixCheck("6292"),
            new BinPrefixCheck("810"),
            new BinRangeCheck(8110, 8171)),
            new LengthRangeCheck(14, 19)
    ),

    // Visa numbers start with 4
    VISA(List.of(
            new BinPrefixCheck("4")),
            new LengthCheck(13, 16, 19)
    ),

    UNKNOWN(List.of(),
            new LengthRangeCheck(13, 19)
    );

    private static final class BinPrefixCheck implements BinChecker {
        private final String prefix;

        BinPrefixCheck(final String prefix) {
            this.prefix = prefix;
        }

        @Override
        public int check(final String cardNumber) {
            if (cardNumber.startsWith(prefix)) {
                return prefix.length();
            }
            return 0;
        }
    }

    private static final class BinRangeCheck implements BinChecker {
        private final int min;
        private final int max;
        private final int digits;

        BinRangeCheck(final int min, final int max) {
            this.min = min;
            this.max = max;

            final var minStr = String.valueOf(min);
            this.digits = minStr.length();
            Verify.verify(minStr.length() == String.valueOf(max).length());
        }

        @Override
        public int check(final String cardNumber) {
            if (cardNumber.length() < digits) {
                return 0;
            }
            final var prefix = Integer.parseInt(cardNumber.substring(0, digits));
            if (prefix >= min && prefix <= max) {
                return digits;
            }
            return 0;
        }
    }

    private static final class LengthCheck implements LengthChecker<Integer> {
        private final int[] validLengths;

        LengthCheck(final int... validLengths) {
            this.validLengths = validLengths.clone();
        }

        @Override
        public boolean check(final Integer length) {
            for (final int validLength : validLengths) {
                if (length == validLength) {
                    return true;
                }
            }
            return false;
        }
    }

    private static final class LengthRangeCheck implements LengthChecker<Integer> {

        private final int minLength;
        private final int maxLength;

        LengthRangeCheck(final int minLength, final int maxLength) {
            this.minLength = minLength;
            this.maxLength = maxLength;
        }

        @Override
        public boolean check(final Integer length) {
            return length >= minLength && length <= maxLength;
        }
    }

    private interface BinChecker {

        // This BIN checker function should match all or nothing. When matches all, the length
        // of the bin prefix is returned. Returns 0 when there's no match.
        int check(String cardNumber);
    }

    private interface LengthChecker<T> {
        boolean check(T t);

    }

    public static CardBrand from(final String cardNumber) {
        final List<CardBrand> brands = bestMatches(cardNumber);
        for (final CardBrand brand : brands) {
            if (brand.isLengthValid(cardNumber.length())) {
                return brand;
            }
        }

        throw CommonExceptionCode.INVALID_CARD_NUMBER.exception();
    }

    public static Optional<CardBrand> fromSafe(final String cardNumber) {
        final List<CardBrand> brands = bestMatches(cardNumber);
        for (final CardBrand brand : brands) {
            if (brand.isLengthValid(cardNumber.length())) {
                return Optional.of(brand);
            }
        }
        return Optional.empty();
    }

    /* Based on the card brands and bin ranges supported currently, this function should
       return EXACTLY one card brand that matches the input card number. In case that there might
       be overlaps, we need to figure out a way to sort these brands by a preference score.
    */
    static List<CardBrand> bestMatches(final String cardNumber) {
        if (TextUtils.isBlank(cardNumber)) {
            return List.of(UNKNOWN);
        }
        final ImmutableSetMultimap<Integer, CardBrand> matchStrengths =
                Stream.of(values())
                        .map(cardBrand -> Map.entry(
                                        cardBrand.binCheckers
                                                .stream()
                                                .mapToInt(binChecker -> binChecker.check(cardNumber))
                                                .max()
                                                .orElse(0),
                                cardBrand
                                )
                        ).collect(ImmutableSetMultimap.toImmutableSetMultimap(
                                Map.Entry::getKey, Map.Entry::getValue));
        final int maxStrength = matchStrengths.keys().stream().max(Integer::compare).orElse(0);
        if (maxStrength > 0) {
            return matchStrengths.get(maxStrength).asList();
        }
        return List.of(UNKNOWN);
    }

    private final List<BinChecker> binCheckers;
    private final LengthChecker<Integer> lengthCheck;

    CardBrand(final List<BinChecker> binCheckers, final LengthChecker<Integer> lengthCheck) {
        this.binCheckers = binCheckers;
        this.lengthCheck = lengthCheck;
    }

    public boolean isLengthValid(final int cardNumberLength) {
        return lengthCheck.check(cardNumberLength);
    }
}