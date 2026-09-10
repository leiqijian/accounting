package com.liquido.base.constant.dynamic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;

import com.liquido.core.common.utils.JsonUtil;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor
@AllArgsConstructor
public class DynamicConstantDeserializer
        extends JsonDeserializer<DynamicConstant<?>> implements ContextualDeserializer {

    private Class<?> constantClass;

    @SneakyThrows
    @Override
    public DynamicConstant<?> deserialize(final JsonParser jsonParser,
                                          final DeserializationContext deserializationContext) {
        final Class<?> convertClazz = Arrays.stream(constantClass.getClasses())
                .filter(x -> AttributeConverter.class.isAssignableFrom(x)
                        && ((ParameterizedType) x.getGenericInterfaces()[0])
                        .getActualTypeArguments()[0].equals(constantClass))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(convertClazz)) {
            return null;
        }

        final Object instance = convertClazz.getDeclaredConstructor().newInstance();

        final Type codeType = ((ParameterizedType) convertClazz
                .getGenericInterfaces()[0]).getActualTypeArguments()[1];

        final Object value = JsonUtil.toBean(jsonParser.getText(), codeType);

        return (DynamicConstant<?>) convertClazz
                .getMethod("convertToEntityAttribute", (Class<?>) codeType)
                .invoke(instance, value);
    }

    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt,
                                                final BeanProperty property) {
        return new DynamicConstantDeserializer(ctxt.getContextualType().getRawClass());
    }
}
