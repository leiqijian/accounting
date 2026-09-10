package com.liquido.base.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum DictionaryTypeEnum {

    BANK_CODE("BANK_CODE"),

    PAYMENT_LINK_PRODUCT("PAYMENT_LINK_PRODUCT"),

    DYNAMIC_CONSTANT("DYNAMIC_CONSTANT"),

    COUNTRY_NAME("COUNTRY_NAME"),

    BUSINESS_TAG_EXTRA_COST("BUSINESS_TAG_EXTRA_COST");

    private final String code;

    public String getFinalType(final String... subTypes) {
        if (ArrayUtils.isEmpty(subTypes)) {
            return this.code;
        }
        final List<String> subs = Arrays.stream(subTypes)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(subs)) {
            return this.code;
        }
        return this.code + "_" + StringUtils.join(subs, "_");
    }

    public static DictionaryTypeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(DictionaryTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<DictionaryTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final DictionaryTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("DictionaryType");
            }
            return enumValue.getCode();
        }

        @Override
        public DictionaryTypeEnum convertToEntityAttribute(final String dbValue) {
            return DictionaryTypeEnum.parse(dbValue);
        }
    }
}
