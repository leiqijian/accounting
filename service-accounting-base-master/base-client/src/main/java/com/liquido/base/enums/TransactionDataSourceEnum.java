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
public enum TransactionDataSourceEnum {

    TRADE("TRADE", "transaction data from trade service"),

    ACCOUNT("ACCOUNT", "transaction data from account service, current project");

    private final String code;
    private final String remark;

    public static TransactionDataSourceEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(TransactionDataSourceEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    @Converter
    public static class Convert implements AttributeConverter<TransactionDataSourceEnum, String> {

        @Override
        public String convertToDatabaseColumn(final TransactionDataSourceEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("TransactionDataSourceEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public TransactionDataSourceEnum convertToEntityAttribute(final String dbValue) {
            return TransactionDataSourceEnum.parse(dbValue);
        }
    }
}
