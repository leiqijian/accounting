package com.liquido.core.mvc.serializer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import lombok.SneakyThrows;

@SuppressWarnings("all")
public class EnumKeySerializer extends JsonSerializer<Enum> {

    @SneakyThrows
    @Override
    public void serialize(final Enum anEnum, final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) {
        final Class<?> convertClazz = Arrays.stream(anEnum.getDeclaringClass().getClasses())
                .filter(x -> AttributeConverter.class.isAssignableFrom(x)
                        && ((ParameterizedType) x.getGenericInterfaces()[0])
                        .getActualTypeArguments()[0].equals(anEnum.getClass()))
                .findFirst().orElse(null);

        if (Objects.isNull(convertClazz)) {
            jsonGenerator.writeFieldName(anEnum.toString());
        } else {
            final Object instance = convertClazz.getDeclaredConstructor().newInstance();
            final Object res = convertClazz
                    .getMethod("convertToDatabaseColumn", anEnum.getDeclaringClass())
                    .invoke(instance, anEnum);
            jsonGenerator.writeFieldName(res.toString());
        }
    }

    @Override
    public void serializeWithType(final Enum value,
                                  final JsonGenerator g,
                                  final SerializerProvider provider,
                                  final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, JsonToken.VALUE_STRING));
        serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
}
