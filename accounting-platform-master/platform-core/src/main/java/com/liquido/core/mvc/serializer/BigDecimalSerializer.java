package com.liquido.core.mvc.serializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.liquido.core.mvc.annotation.BigDecimalFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * BigDecimal Serializer
 *
 * @see BigDecimalFormat
 */
@AllArgsConstructor
@NoArgsConstructor
public class BigDecimalSerializer extends JsonSerializer<BigDecimal>
        implements ContextualSerializer {

    private int scale;

    private RoundingMode roundingMode;

    @Override
    public void serialize(final BigDecimal value,
                          final JsonGenerator gen,
                          final SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            gen.writeNull();
        } else {
            gen.writeNumber(value.setScale(scale, roundingMode));
        }
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider prov,
                                              final BeanProperty property)
            throws JsonMappingException {
        if (Objects.isNull(property)) {
            return prov.findNullValueSerializer(null);
        }
        if (property.getType().getRawClass().equals(BigDecimal.class)) {
            final BigDecimalFormat format = property.getAnnotation(BigDecimalFormat.class);
            if (Objects.nonNull(format)) {
                return new BigDecimalSerializer(format.value(), format.roundingMode());
            }
        }
        return prov.findValueSerializer(property.getType(), property);
    }

    /**
     * reference: https://blog.csdn.net/Ellen_Tangxiang/article/details/111310153
     */
    @Override
    public void serializeWithType(BigDecimal value, JsonGenerator g, SerializerProvider provider,
                                  TypeSerializer typeSer) throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, JsonToken.VALUE_STRING));
        serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
}
