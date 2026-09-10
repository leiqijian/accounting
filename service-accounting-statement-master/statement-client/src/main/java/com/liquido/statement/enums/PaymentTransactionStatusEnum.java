package com.liquido.statement.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * PaymentTransactionStatusEnum : SETTLED, IN_PROGRESS, FAILED or REJECTED
 */
@Getter
@RequiredArgsConstructor
public enum PaymentTransactionStatusEnum {

    WAITING("WAITING", "Waiting"),
    PENDING("PENDING", "Pending"),
    IN_PROGRESS("IN_PROGRESS", "inProgress"),
    SETTLED("SETTLED", "Settled"),
    FAILED("FAILED", "Failed"),
    REJECTED("REJECTED", "Rejected"),
    ;

    private final String code;
    private final String remark;

    public static PaymentTransactionStatusEnum parse(final String code) {
        return Arrays.stream(PaymentTransactionStatusEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                // other status parse fail as IN_PROGRESS
                .orElse(PaymentTransactionStatusEnum.IN_PROGRESS);
    }

    @Converter
    public static class Convert
            implements AttributeConverter<PaymentTransactionStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final PaymentTransactionStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("PaymentTransactionStatus");
            }
            return enumValue.getCode();
        }

        @Override
        public PaymentTransactionStatusEnum convertToEntityAttribute(final String dbValue) {
            if (StringUtils.isBlank(dbValue)) {
                throw CommonExceptionCode.ENUM_PARSE_ERROR.exception(dbValue);
            }
            return PaymentTransactionStatusEnum.parse(dbValue);
        }
    }
}
