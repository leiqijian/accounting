package com.liquido.core.common.logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Customize sensitive field desensitization serialization rules,
 * inherit Jackson's JsonSerializer class
 */
public class SensitiveFieldSerializer extends JsonSerializer<Object>
        implements ContextualSerializer {

    /**
     * Desensitization Rule Type
     */
    private SensitiveType sensitiveType;

    /**
     * Mark whether the field to be desensitized is a collection type;
     * Scenario: If there is a collection property in the Person.java class
     * that needs to be desensitized
     */
    private boolean isCollectionAttribute;

    public SensitiveFieldSerializer() {
    }

    public SensitiveFieldSerializer(final SensitiveType sensitiveType,
                                    final boolean isCollectionAttribute) {
        this.sensitiveType = sensitiveType;
        this.isCollectionAttribute = isCollectionAttribute;
    }

    /**
     * Preprocessing determines whether the current attribute type is String or Collection
     *
     * @param beanProperty
     */
    private static Pair<Boolean, Boolean> preDeal(final BeanProperty beanProperty) {
        // The property type is String class
        if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
            return Pair.of(true, false);
        }

        // The property type is Collection class
        if (beanProperty.getType().isCollectionLikeType()) {
            final CollectionType ct = (CollectionType) beanProperty.getType();
            if (Objects.equals(ct.getContentType().getRawClass(), String.class)) {
                return Pair.of(true, true);
            }
        }

        return Pair.of(false, false);
    }

    @Override
    public void serialize(final Object source,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider provider) throws IOException {
        if (isCollectionAttribute) {
            // Collection type attribute desensitization
            jsonGenerator.writeObject(SensitiveConverter
                    .collectionConvert((Collection<String>) source, sensitiveType));
        } else {
            // Normal string attribute desensitization
            jsonGenerator.writeString(SensitiveConverter.convert((String) source, sensitiveType));
        }
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializerProvider,
                                              final BeanProperty beanProperty)
            throws JsonMappingException {
        if (beanProperty == null) {
            return serializerProvider.getDefaultNullValueSerializer();
        }

        // Get the @SensitiveField tag configured on the property
        SensitiveField sensitiveField = beanProperty.getAnnotation(SensitiveField.class);
        if (sensitiveField == null) {
            sensitiveField = beanProperty.getContextAnnotation(SensitiveField.class);
        }

        //Preprocessing determines whether the current property type is String or Collection
        final Pair<Boolean, Boolean> option = preDeal(beanProperty);

        // If you can get the annotation, pass the value of the annotation to SensitiveInfoSerialize
        if (option.getLeft() && sensitiveField != null) {
            return new SensitiveFieldSerializer(sensitiveField.value(), option.getRight());
        }

        return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
    }
}
