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
public enum ReceiptProofEnum {

    GWM("GWM", 0, "gwm"),

    DEFAULT("DEFAULT", 0, null),

    ;

    private final String code;
    /**
     * 0:paymentLink; 1:shopify; 2:shoplazza
     */
    private final Integer type;
    private final String merchantCode;

    public static ReceiptProofEnum parse(final String code) {
        return Arrays.stream(ReceiptProofEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(code));
    }

    public static ReceiptProofEnum parse(final Integer type, final String merchantCode) {
        return Arrays.stream(ReceiptProofEnum.values())
                .filter(v -> {
                    if (Objects.nonNull(v.getType())
                            && v.getType().compareTo(type) != 0) {
                        return false;
                    }
                    return !Objects.nonNull(v.getMerchantCode())
                            || v.getMerchantCode().equals(merchantCode);
                })
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<ReceiptProofEnum, String> {

        @Override
        public String convertToDatabaseColumn(final ReceiptProofEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("ReceiptProofEnum");
            }
            return enumValue.getCode();
        }

        @Override
        public ReceiptProofEnum convertToEntityAttribute(final String dbValue) {
            if (StringUtils.isBlank(dbValue)) {
                throw CommonExceptionCode.ENUM_PARSE_ERROR.exception(dbValue);
            }
            return ReceiptProofEnum.parse(dbValue);
        }
    }

}
