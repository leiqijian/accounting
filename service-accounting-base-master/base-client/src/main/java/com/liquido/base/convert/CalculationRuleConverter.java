package com.liquido.base.convert;

import java.util.Map;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.utils.JsonUtil;

@Converter
public class CalculationRuleConverter implements AttributeConverter<Map<String, String>, String> {

    @Override
    public String convertToDatabaseColumn(final Map<String, String> attribute) {
        return JsonUtil.toJson(attribute);
    }

    @Override
    public Map<String, String> convertToEntityAttribute(final String dbData) {
        return JsonUtil.toMap(dbData);
    }

}
