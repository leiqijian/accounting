package com.liquido.core.mvc.serializer;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class EnumKeyDeserializer extends KeyDeserializer implements ContextualKeyDeserializer {

    private Class<?> clazz;

    @Override
    public Object deserializeKey(final String s,
                                 final DeserializationContext deserializationContext) {
        return EnumDeserializer.deserialize(clazz, s);
    }

    @Override
    public KeyDeserializer createContextual(final DeserializationContext ctxt,
                                            final BeanProperty property) {
        return new EnumKeyDeserializer(ctxt.getContextualType().getKeyType().getRawClass());
    }
}
