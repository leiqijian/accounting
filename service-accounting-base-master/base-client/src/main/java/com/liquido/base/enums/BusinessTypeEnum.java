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
public enum BusinessTypeEnum {

    TRANSACTION("TRANSACTION", "Transaction"),

    /**
     * Topup business enum
     */
    TOPUP("TOPUP", "Topup"),

    REFUND("REFUND", "Refund"),


    /**
     * TransferOut business enum
     */
    TRANSFER_OUT("TRANSFER_OUT", "TransferOut"),
    EXCHANGE("EXCHANGE", "Exchange"),

    /**
     * Account Adjustment
     */
    ADJUSTMENT("ADJUSTMENT", "Adjustment"),

    /**
     * Account extra fee and cost
     */
    SUPPLEMENT("SUPPLEMENT", "Supplement"),
    ;

    private final String code;
    private final String remark;

    public static BusinessTypeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(BusinessTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<BusinessTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final BusinessTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("BusinessType");
            }
            return enumValue.getCode();
        }

        @Override
        public BusinessTypeEnum convertToEntityAttribute(final String dbValue) {
            return BusinessTypeEnum.parse(dbValue);
        }
    }
}
