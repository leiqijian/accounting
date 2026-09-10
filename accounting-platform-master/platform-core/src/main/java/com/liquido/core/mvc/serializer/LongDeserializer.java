package com.liquido.core.mvc.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

/**
 * Long Deserializer
 */
public class LongDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(final JsonParser jsonParser,
                            final DeserializationContext deserializationContext)
            throws IOException {

        return StringUtils.isBlank(jsonParser.getText()) ? null :
                Long.valueOf(jsonParser.getText());
    }

}
