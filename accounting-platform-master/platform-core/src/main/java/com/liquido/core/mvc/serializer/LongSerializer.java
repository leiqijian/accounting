package com.liquido.core.mvc.serializer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.liquido.core.mvc.vo.PageVo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * Long Serializer
 */
@SuppressWarnings("rawtypes")
public class LongSerializer extends JsonSerializer<Long> {
    private static final List<Class> IGNORE = List.of(PageVo.class);

    @Override
    public void serialize(final Long value,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        final Class clazz = Optional.ofNullable(jsonGenerator)
                .map(JsonGenerator::getCurrentValue)
                .map(Object::getClass)
                .orElse(null);
        
        if (Objects.nonNull(clazz) && IGNORE.contains(clazz)) {
            jsonGenerator.writeNumber(value);
        } else {
            jsonGenerator.writeString(value.toString());
        }
    }

    @Override
    public void serializeWithType(final Long value,
                                  final JsonGenerator g,
                                  final SerializerProvider provider,
                                  final TypeSerializer typeSer) throws IOException {
        final WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, JsonToken.VALUE_STRING));
        serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
}
