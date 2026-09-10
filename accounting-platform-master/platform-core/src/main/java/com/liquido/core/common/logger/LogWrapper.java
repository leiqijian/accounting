package com.liquido.core.common.logger;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.liquido.core.common.utils.JsonUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * log wrapper tool
 */
@Slf4j
public class LogWrapper {

    /**
     * log format [message] [operation],{key1=value1,key2=value2,key3=value3}
     */
    private static final String LOG_FORMAT = "%s|%s|%s";
    private static final String EMPTY_STR = "";
    /**
     * log desensitization switch, ON; OFF
     */
    private static String sensitiveSwitch;

    private static ObjectMapper sensitiveObjectMapper;

    static {
        sensitiveObjectMapper = JsonUtil.getObjectMapper().copy();
        sensitiveObjectMapper.setAnnotationIntrospector(new SensitiveInfoIntrospector());
    }


    /**
     * operation type
     */
    private String operation;
    /**
     * log msg
     */
    private String message = EMPTY_STR;
    private Map<String, Object> params = new HashMap<>();

    public LogWrapper() {
    }

    /**
     * @param operation
     */
    public LogWrapper(final String operation) {
        this.operation = operation;
    }

    /**
     * @param op
     * @return
     */
    public static LogWrapper op(final String op) {
        return new LogWrapper(op);
    }

    /**
     * @param value
     * @return
     */
    public static Object convertMessage(final Object value) {
        try {
            // Exclude nested complex objects
            if (value == null || value instanceof String) {
                return value;
            }

            if (LogConstant.SWITCH_ON.equalsIgnoreCase(sensitiveSwitch)) {
                /** Handling sensitive field information */
                return sensitiveObjectMapper.writeValueAsString(value);
            }

            return JsonUtil.toJson(value);
        } catch (Throwable e) {
            log.error("LogWrapper convertMessage error:", e);
        }

        log.warn("LogWrapper.convertMessage error, original value={}, valueClass={}", value,
                value.getClass());
        return value;
    }

    @Value("${ms.common.logger.sensitive-switch:ON}")
    public void setSensitiveSwitch(final String sensitiveSwitch) {
        LogWrapper.sensitiveSwitch = sensitiveSwitch;
    }

    /**
     * @param message
     * @return
     */
    public LogWrapper msg(final String message) {
        this.message = message;
        return this;
    }

    /**
     * @param message
     * @return
     */
    public LogWrapper msg(final String message, final Object... args) {
        this.message = MessageFormat.format(message, args);
        return this;
    }

    /**
     * 键值对
     *
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("rawtypes")
    public LogWrapper wrap(final String key, final Object value) {
        params.put(key, convertMessage(value));
        return this;
    }

    /**
     * @param maps
     * @return
     */
    public LogWrapper wrap(final Map<String, Object> maps) {
        params.putAll(maps);
        return this;
    }

    public LogWrapper wrap1(final String key, final Object value) {
        params.put(key, value);
        return this;
    }


    @Override
    public String toString() {
        return String.format(LOG_FORMAT, operation, message, params.toString());
    }

    /**
     * Custom Desensitized Bean Introspector
     */
    static class SensitiveInfoIntrospector extends JacksonAnnotationIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        public Object findSerializer(final Annotated annotated) {

            /** If the field attribute is marked with @SensitiveField annotation,
             *  it will be uniformly processed by the custom SensitiveFieldSerializer
             */
            final Annotation annotation = annotated.getAnnotation(SensitiveField.class);
            if (annotation != null) {
                return SensitiveFieldSerializer.class;
            }

            return super.findSerializer(annotated);
        }
    }
}
