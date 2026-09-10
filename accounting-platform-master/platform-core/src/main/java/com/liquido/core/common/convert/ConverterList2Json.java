package com.liquido.core.common.convert;

import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.utils.JsonUtil;


@Converter
public class ConverterList2Json implements AttributeConverter<Object, String> {
    @Override
    public String convertToDatabaseColumn(final Object object) {

        return Objects.nonNull(object) ? JsonUtil.toJson(object) : null;
    }

    @Override
    public Object convertToEntityAttribute(final String str) {
        return JsonUtil.toList(str);
    }

}
