package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * SETTLED/REFUND/TOPUP/CHARGE_BACK/TRANSFER_OUT
 * <p>
 * PAY_IN  rank: SETTLED(0) >> REFUND(1) >> CHARGE_BACK(2) >> CHARGE_BACK_REJECTED(3);
 * PAY_OUT rank: SETTLED(0) >> REJECTED(2);
 */
@Getter
@RequiredArgsConstructor
public enum DirectionTypeEnum {

    SETTLED("SETTLED", "settled", 0),

    REFUND("REFUND", "refund", 1),

    REJECTED("REJECTED", "rejected", 2),

    REJECTED_DEBIT("REJECTED_DEBIT", "rejected_debit", 3),

    CHARGE_BACK("CHARGE_BACK", "charge_back", 2),

    CHARGE_BACK_REJECTED("CHARGE_BACK_REJECTED", "charge_back_rejected", 3);

    private final String code;
    private final String remark;
    private final Integer rank;

    public static DirectionTypeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(DirectionTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<DirectionTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final DirectionTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("DirectionType");
            }
            return enumValue.getCode();
        }

        @Override
        public DirectionTypeEnum convertToEntityAttribute(final String dbValue) {
            return DirectionTypeEnum.parse(dbValue);
        }
    }

}
