package com.liquido.statement.convert;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.base.enums.CurrencyEnum;

import com.google.common.collect.Lists;

@Converter
public class ListToCurrencyConvert implements AttributeConverter<List<CurrencyEnum>, String> {
    @Override
    public String convertToDatabaseColumn(final List<CurrencyEnum> currencyList) {
        return Objects.isNull(currencyList) ? null : currencyList.stream()
                .map(CurrencyEnum::getCode)
                .reduce((x, y) -> String.format("%s,%s", x, y))
                .orElse(null);
    }

    @Override
    public List<CurrencyEnum> convertToEntityAttribute(final String dbData) {
        return Objects.isNull(dbData) ? Lists.newArrayList() : Arrays.stream(dbData.split(","))
                .map(x -> CurrencyEnum.parse(x.trim()))
                .collect(Collectors.toList());
    }
}
