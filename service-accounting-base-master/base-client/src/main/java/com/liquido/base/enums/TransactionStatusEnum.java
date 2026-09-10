package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum TransactionStatusEnum {

    INITIAL_STATUS("INITIAL_STATUS", "initial_status", 0),

    CREATED("CREATED", "created", 1),

    AUTHORIZED("AUTHORIZED", "authorized", 1),

    IN_PROGRESS("IN_PROGRESS", "in_progress", 2),

    FAILED("FAILED", "failed", 3),

    EXPIRED("EXPIRED", "expired", 3),

    CANCELLED("CANCELLED", "cancelled", 3),

    SETTLED("SETTLED", "settled", 4),

    REFUNDING("REFUNDING", "refunding", 4),

    REFUND("REFUND", "refund", 5),

    REFUNDED("REFUNDED", "refunded", 5),

    CHARGED_BACK("CHARGED_BACK", "charged_back", 5),

    REJECTED("REJECTED", "rejected", 5),

    ;

    private final String code;
    private final String remark;
    private final Integer rank;

    public static TransactionStatusEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(TransactionStatusEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<TransactionStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final TransactionStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("TransactionStatus");
            }
            return enumValue.getCode();
        }

        @Override
        public TransactionStatusEnum convertToEntityAttribute(final String dbValue) {
            return TransactionStatusEnum.parse(dbValue);
        }
    }

}
