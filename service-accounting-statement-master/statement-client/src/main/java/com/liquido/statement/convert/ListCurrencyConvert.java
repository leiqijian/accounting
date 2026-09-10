package com.liquido.statement.convert;

import java.util.List;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.core.common.utils.JsonUtil;

import com.google.common.collect.Lists;

@Converter
public class ListCurrencyConvert implements AttributeConverter<List<CurrencyEnum>, String> {
    @Override
    public String convertToDatabaseColumn(final List<CurrencyEnum> currencyList) {
        return Objects.isNull(currencyList) ? null : JsonUtil.toJson(currencyList);
    }

    @Override
    public List<CurrencyEnum> convertToEntityAttribute(final String dbData) {

        final List<CurrencyEnum> result = JsonUtil.toList(dbData);

        return Objects.isNull(result)
                ? Lists.newArrayList()
                : result;
    }
}
