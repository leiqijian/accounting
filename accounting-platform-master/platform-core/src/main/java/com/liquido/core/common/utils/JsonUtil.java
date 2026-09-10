package com.liquido.core.common.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.liquido.core.mvc.serializer.EnumDeserializer;
import com.liquido.core.mvc.serializer.EnumKeyDeserializer;
import com.liquido.core.mvc.serializer.EnumKeySerializer;
import com.liquido.core.mvc.serializer.EnumSerializer;
import com.liquido.core.mvc.serializer.EnumSimpleKeyDeserializers;
import com.liquido.core.mvc.serializer.LongDeserializer;
import com.liquido.core.mvc.serializer.LongSerializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Encapsulates Jackson serialization and deserialization tool classes
 */
@Slf4j
public class JsonUtil {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        /** General configuration */
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        /** Serialization configuration */
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        /** deserialize configuration */
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        // datetime default format: yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_FORMAT));

        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Enum.class, new EnumSerializer());
        simpleModule.addDeserializer(Enum.class, new EnumDeserializer());
        simpleModule.addSerializer(Long.class, new LongSerializer());
        simpleModule.addDeserializer(Long.class, new LongDeserializer());
        simpleModule.setKeyDeserializers(new EnumSimpleKeyDeserializers());
        simpleModule.addKeySerializer(Enum.class, new EnumKeySerializer());
        simpleModule.addKeyDeserializer(Enum.class, new EnumKeyDeserializer());

        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(LocalDateTimeUtil.FORMAT_DATETIME));
        javaTimeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(LocalDateUtil.FORMAT_DATE));
        javaTimeModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(LocalTimeUtil.FORMAT_TIME));
        javaTimeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(LocalDateTimeUtil.FORMAT_DATETIME));
        javaTimeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(LocalDateUtil.FORMAT_DATE));
        javaTimeModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(LocalTimeUtil.FORMAT_TIME));

        objectMapper.registerModule(simpleModule);
        objectMapper.registerModule(javaTimeModule);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * object to json string
     *
     * @param objBean
     *
     * @return json string
     */
    public static <T> String toJson(final T objBean) {
        if (objBean == null) {
            return "";
        }

        try {
            return objBean instanceof String ? objBean.toString() :
                    objectMapper.writeValueAsString(objBean);
        } catch (JsonProcessingException e) {
            log.error("Parse Object to String error", e);
        }

        return "";
    }

    /**
     * object to byte[]
     *
     * @param objBean
     *
     * @return
     */
    public static final byte[] toBytes(final Object objBean) {
        if (objBean == null) {
            return new byte[0];
        }

        try {
            return objectMapper.writeValueAsBytes(objBean);
        } catch (JsonProcessingException e) {
            log.error("Parse Object to String error", e);
        }

        return new byte[0];
    }

    /**
     * byte[] to object
     *
     * @param bytes
     * @param clazz
     * @param <T>
     *
     * @return
     */
    public static final <T> T toBean(final byte[] bytes, final Class<T> clazz) {
        if (bytes.length <= 0 || clazz == null) {
            return null;
        }

        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            log.error("Parse bytes to Bean error", e);
        }

        return null;
    }

    /**
     * json string to bean
     *
     * @param jsonString
     * @param clazz
     */
    public static <T> T toBean(final String jsonString, final Class<T> clazz) {
        if (StringUtils.isBlank(jsonString) || clazz == null) {
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T) jsonString :
                    objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            log.error("Parse String to Bean error", e);
        }
        return null;
    }

    /**
     * json string to bean
     *
     * @param jsonString
     * @param type       JavaType
     *
     * @return
     */
    public static <T> T toBean(final String jsonString, final JavaType type) {
        if (StringUtils.isBlank(jsonString) || type == null) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonString, type);
        } catch (Exception e) {
            log.error("Parse String to Bean error", e);
        }
        return null;
    }

    /**
     * json string to bean
     *
     * @param jsonString
     * @param type       JavaType
     *
     * @return
     */
    public static <T> T toBean(final String jsonString, final Type type) {
        if (StringUtils.isBlank(jsonString) || type == null) {
            return null;
        }

        try {
            return type.equals(String.class) ? (T) jsonString :
                    objectMapper.readValue(jsonString,
                            objectMapper.getTypeFactory().constructType(type));
        } catch (Exception e) {
            log.error("Parse String to Bean error", e);
        }
        return null;
    }

    /**
     * json string to bean
     *
     * @param jsonString
     * @param typeReference
     * @param <T>
     *
     * @return
     */
    public static <T> T toBean(final String jsonString, final TypeReference<T> typeReference) {
        if (StringUtils.isBlank(jsonString) || typeReference == null) {
            return null;
        }

        try {
            return (T) (typeReference.getType().equals(String.class) ? jsonString :
                    objectMapper.readValue(jsonString, typeReference));
        } catch (IOException e) {
            log.error("Parse String to Bean error", e);
        }

        return null;
    }

    /**
     * json string to bean
     *
     * @param jsonString
     * @param collectionClass
     * @param elementClass
     * @param <T>
     *
     * @return
     */
    public static <T> T toBean(final String jsonString,
                               final Class<?> collectionClass,
                               final Class<?>... elementClass) {
        if (StringUtils.isBlank(jsonString) || elementClass == null) {
            return null;
        }

        try {
            final JavaType javaType = objectMapper.getTypeFactory()
                    .constructParametricType(collectionClass, elementClass);
            return objectMapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            log.error("Parse String to Bean error", e);
        }

        return null;
    }

    /**
     * json string to List
     *
     * @param jsonString
     * @param beanType
     * @param <T>
     *
     * @return
     */
    public static <T> List<T> toList(final String jsonString, final Class<T> beanType) {
        if (StringUtils.isBlank(jsonString) || beanType == null) {
            return null;
        }

        try {
            final JavaType javaType =
                    objectMapper.getTypeFactory().constructParametricType(List.class, beanType);
            final List<T> resultList = objectMapper.readValue(jsonString, javaType);
            return resultList;
        } catch (Exception e) {
            log.error("Parse String to List error", e);
        }

        return null;
    }

    /**
     * json string to List
     *
     * @param jsonString
     * @param <T>
     *
     * @return
     */
    public static <T> List<T> toList(final String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }

        try {
            final List<T> resultList = objectMapper.readValue(jsonString, new TypeReference<>() {
            });
            return resultList;
        } catch (Exception e) {
            log.error("Parse String to List error", e);
        }

        return null;
    }

    /**
     * json string to Map
     *
     * @param jsonString
     * @param keyType
     * @param beanType
     * @param <K>
     * @param <V>
     *
     * @return
     */
    public static <K, V> Map<K, V> toMap(final String jsonString,
                                         final Class<K> keyType,
                                         final Class<V> beanType) {
        if (StringUtils.isBlank(jsonString) || beanType == null) {
            return null;
        }

        try {
            final JavaType javaType =
                    objectMapper.getTypeFactory().constructMapType(Map.class, keyType, beanType);
            final Map<K, V> resultMap = objectMapper.readValue(jsonString, javaType);
            return resultMap;
        } catch (Exception e) {
            log.error("Parse String to Map error", e);
        }

        return null;
    }

    /**
     * json string to Map
     *
     * @param jsonString
     * @param <T>
     *
     * @return
     */
    public static <T> Map<String, T> toMap(final String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }

        try {
            final Map<String, T> resultMap =
                    objectMapper.readValue(jsonString, new TypeReference<>() {
                    });
            return resultMap;
        } catch (Exception e) {
            log.error("Parse String to Map error", e);
        }

        return null;
    }

    /**
     * json string to Map
     *
     * @param jsonNode
     * @param <T>
     *
     * @return
     */
    public static <T> Map<String, T> toMap(final JsonNode jsonNode) {
        if (Objects.isNull(jsonNode)) {
            return null;
        }

        try {
            final Map<String, T> resultMap =
                    objectMapper.convertValue(jsonNode, new TypeReference<>() {
                    });
            return resultMap;
        } catch (Exception e) {
            log.error("Parse String to Map error", e);
        }

        return null;
    }

    /**
     * Factory method for constructing {@link JavaType} that represents a parameterized type
     *
     * @param parametrized     Type-erased type to parameterize
     * @param parameterClasses Type parameters to apply
     */
    public static JavaType constructParametricType(final Class<?> parametrized,
                                                   final Class<?>... parameterClasses) {
        return objectMapper
                .getTypeFactory()
                .constructParametricType(parametrized, parameterClasses);
    }

    /**
     * Factory method for constructing {@link JavaType} that represents a parameterized type
     *
     * @param rawType        Actual type-erased type
     * @param parameterTypes Type parameters to apply
     */
    public static JavaType constructParametricType(final Class<?> rawType,
                                                   final JavaType... parameterTypes) {
        return objectMapper
                .getTypeFactory()
                .constructParametricType(rawType, parameterTypes);
    }
}
