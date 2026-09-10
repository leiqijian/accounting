package com.liquido.base.constant.dynamic;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;

import com.liquido.core.common.utils.JsonUtil;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import lombok.SneakyThrows;

public class DynamicConstantSerializer extends JsonSerializer<DynamicConstant<?>> {

    @SneakyThrows
    @Override
    public void serialize(final DynamicConstant<?> constant,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) {
        final Class<?> convertClazz = Arrays.stream(constant.getClass().getClasses())
                .filter(x -> AttributeConverter.class.isAssignableFrom(x)
                        && ((ParameterizedType) x.getGenericInterfaces()[0])
                        .getActualTypeArguments()[0].equals(constant.getClass()))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(convertClazz)) {
            jsonGenerator.writeString(JsonUtil.toJson(constant));
        } else {
            final Object instance = convertClazz.getDeclaredConstructor().newInstance();
            final Object res = convertClazz
                    .getMethod("convertToDatabaseColumn", constant.getClass())
                    .invoke(instance, constant);
            jsonGenerator.writeObject(res);
        }
    }

    @Override
    public void serializeWithType(final DynamicConstant value,
                                  final JsonGenerator g,
                                  final SerializerProvider provider,
                                  final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, JsonToken.VALUE_STRING));
        serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
}
