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
public enum DataSyncRefundStatusEnum {

    IN_PROGRESS("IN_PROGRESS", "in_progress", 0),
    REFUNDED("REFUNDED", "refunded", 1),
    FAILED("FAILED", "failed", 1),

    ;

    private final String code;
    private final String remark;
    private final Integer rank;

    public static DataSyncRefundStatusEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(DataSyncRefundStatusEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<DataSyncRefundStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final DataSyncRefundStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception(
                        "PaymentLinkRefundStatusEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public DataSyncRefundStatusEnum convertToEntityAttribute(final String dbValue) {
            return DataSyncRefundStatusEnum.parse(dbValue);
        }
    }

}
