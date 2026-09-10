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
public enum DataSyncStatusEnum {

    INITIAL_STATUS("INITIAL_STATUS", "initial_status", 0),
    IN_PROGRESS("IN_PROGRESS", "in_progress", 1),
    SETTLED("SETTLED", "settled", 2),
    FAILED("FAILED", "failed", 2),
    CANCELLED("CANCELLED", "cancelled", 2),
    CHARGED_BACK("CHARGED_BACK", "charged_back", 3),

    ;

    private final String code;
    private final String remark;
    private final Integer rank;

    public static DataSyncStatusEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(DataSyncStatusEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<DataSyncStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final DataSyncStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("PaymentLinkStatus");
            }
            return enumValue.getCode();
        }

        @Override
        public DataSyncStatusEnum convertToEntityAttribute(final String dbValue) {
            return DataSyncStatusEnum.parse(dbValue);
        }
    }

}
