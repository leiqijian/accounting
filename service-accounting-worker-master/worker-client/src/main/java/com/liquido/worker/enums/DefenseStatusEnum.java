package com.liquido.worker.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DefenseStatusEnum {

    CHARGE_BACK("CHARGE_BACK", "charge back"),
    UNDER_DEFENSE("UNDER_DEFENSE", "under defense"),
    DEFENSE_WON("DEFENSE_WON", "defense won"),
    DEFENSE_LOST("DEFENSE_LOST", "defense lost"),
    ;

    private final String code;
    private final String remark;

    public static DefenseStatusEnum parse(final String code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(DefenseStatusEnum.values())
                .filter(tmp -> tmp.getCode().compareTo(code) == 0)
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert
            implements AttributeConverter<DefenseStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final DefenseStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception(
                        "DefenseStatusEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public DefenseStatusEnum convertToEntityAttribute(final String dbValue) {
            return DefenseStatusEnum.parse(dbValue);
        }
    }

}
