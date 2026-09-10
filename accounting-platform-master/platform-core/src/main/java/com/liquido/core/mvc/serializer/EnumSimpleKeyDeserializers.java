package com.liquido.core.mvc.serializer;

import java.util.Objects;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.module.SimpleKeyDeserializers;
import com.fasterxml.jackson.databind.type.ClassKey;

public class EnumSimpleKeyDeserializers extends SimpleKeyDeserializers {

    @Override
    public KeyDeserializer findKeyDeserializer(final JavaType type,
                                               final DeserializationConfig config,
                                               final BeanDescription beanDesc) {
        if (_classMappings == null) {
            return null;
        }
        KeyDeserializer deser = _classMappings.get(new ClassKey(type.getRawClass()));
        if (Objects.isNull(deser)) {
            if (type.getRawClass().isEnum()) {
                deser = _classMappings.get(new ClassKey(Enum.class));
            }
        }
        return deser;
    }
}
