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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * TransactionType Enum
 */
@Getter
@RequiredArgsConstructor
public enum TransactionTypeCodeEnum {

    PAY_IN("PAY_IN", "Payin", "payin"),

    PAY_OUT("PAY_OUT", "Payout", "payout"),

    MARKET_PLACE_ORDERS("MARKET_PLACE_ORDERS", "MarketPlace", "marketplace"),

    ;

    private final String code;
    private final String remark;
    private final String smallRemark;

    public static TransactionTypeCodeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(TransactionTypeCodeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<TransactionTypeCodeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final TransactionTypeCodeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("TransactionTypeCode");
            }
            return enumValue.getCode();
        }

        @Override
        public TransactionTypeCodeEnum convertToEntityAttribute(final String dbValue) {
            return TransactionTypeCodeEnum.parse(dbValue);
        }
    }

    @Converter
    public static class ListConvert
            implements AttributeConverter<List<TransactionTypeCodeEnum>, String> {
        @Override
        public String convertToDatabaseColumn(
                final List<TransactionTypeCodeEnum> channelList) {
            return CollectionUtils.isEmpty(channelList) ? null : channelList.stream()
                    .map(TransactionTypeCodeEnum::getCode)
                    .reduce((x, y) -> String.format("%s,%s", x, y))
                    .orElse(null);
        }

        @Override
        public List<TransactionTypeCodeEnum> convertToEntityAttribute(final String dbData) {
            return StringUtils.isBlank(dbData) ? List.of() : Arrays.stream(dbData.split(","))
                    .map(x -> TransactionTypeCodeEnum.parse(x.trim()))
                    .collect(Collectors.toList());
        }
    }

}
