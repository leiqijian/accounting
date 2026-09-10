package com.liquido.core.mvc.serializer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;

import com.liquido.core.common.utils.JsonUtil;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/**
 * VO enum field use @Convert can convert enum code to enum
 */
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("ALL")
public class EnumDeserializer extends JsonDeserializer<Enum> implements ContextualDeserializer {

    private Class<?> clazz;

    @SneakyThrows
    @Override
    public Enum deserialize(final JsonParser jsonParser,
                            final DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        return deserialize(clazz, jsonParser.getText());
    }

    @SneakyThrows
    public static Enum deserialize(Class enumClass, String dbValue) {
        final Class convertClazz = Arrays.stream(enumClass.getClasses())
                .filter(x -> AttributeConverter.class.isAssignableFrom(x)
                        && ((ParameterizedType) x.getGenericInterfaces()[0])
                        .getActualTypeArguments()[0].equals(enumClass))
                .findFirst().orElse(null);
        if (Objects.isNull(convertClazz)) {
            return (Enum) enumClass.getMethod("valueOf", String.class)
                    .invoke(null, dbValue);
        }

        final Object instance = convertClazz.getDeclaredConstructor().newInstance();

        final Type codeType = ((ParameterizedType) convertClazz
                .getGenericInterfaces()[0]).getActualTypeArguments()[1];

        final Object value = JsonUtil.toBean(dbValue, codeType);

        final Object res = convertClazz
                .getMethod("convertToEntityAttribute", (Class<?>) codeType)
                .invoke(instance, value);

        return (Enum) res;
    }

    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt,
                                                final BeanProperty property)
            throws JsonMappingException {
        return new EnumDeserializer(ctxt.getContextualType().getRawClass());
    }
}
