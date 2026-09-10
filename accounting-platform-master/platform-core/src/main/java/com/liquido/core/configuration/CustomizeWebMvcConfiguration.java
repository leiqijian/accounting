package com.liquido.core.configuration;


import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Resource;

import com.liquido.core.common.exception.resolver.GlobalHandlerExceptionResolver;
import com.liquido.core.common.logger.LoggerMessageConverter;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveFieldSerializer;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.core.common.utils.LocalTimeUtil;
import com.liquido.core.mvc.aop.ReqRspAspect;
import com.liquido.core.mvc.crypto.Md5Strategy;
import com.liquido.core.mvc.crypto.RsaStrategy;
import com.liquido.core.mvc.crypto.Sha256HexStrategy;
import com.liquido.core.mvc.crypto.SignatureScanner;
import com.liquido.core.mvc.crypto.SignatureStrategy;
import com.liquido.core.mvc.filter.LogMdcFilter;
import com.liquido.core.mvc.filter.SignRequestFilter;
import com.liquido.core.mvc.serializer.EnumDeserializer;
import com.liquido.core.mvc.serializer.EnumKeyDeserializer;
import com.liquido.core.mvc.serializer.EnumKeySerializer;
import com.liquido.core.mvc.serializer.EnumSerializer;
import com.liquido.core.mvc.serializer.EnumSimpleKeyDeserializers;
import com.liquido.core.mvc.serializer.LongDeserializer;
import com.liquido.core.mvc.serializer.LongSerializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(WebMvcConfigurer.class)
public class CustomizeWebMvcConfiguration implements WebMvcConfigurer {

    @Resource
    private CommonProperties commonProperties;

    @Bean
    @Order(-1000)
    public GlobalHandlerExceptionResolver globalHandlerExceptionResolver() {
        final GlobalHandlerExceptionResolver resolver = new GlobalHandlerExceptionResolver();
        resolver.setExtendException(null);
        resolver.setRestService(commonProperties.getMvc().getRestService());
        return resolver;
    }

    @Override
    public void configureHandlerExceptionResolvers(
            final List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(globalHandlerExceptionResolver());
    }

    @Bean
    public LoggerMessageConverter commonLogMessageConverter() {
        return new LoggerMessageConverter();
    }

    /**
     * log MDC filter
     */
    @Bean
    public LogMdcFilter logMdcFilter() {
        return new LogMdcFilter();
    }

    @Bean
    public ReqRspAspect reqRspAspect() {
        return new ReqRspAspect();
    }

    /**
     * Use Jackson to customize serialize MapperBuilder
     * Override the default Jackson built-in Jackson2ObjectMapperBuilderCustomizerConfiguration.
     * StandardJackson2ObjectMapperBuilderCustomizer class
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilder() {

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

        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_NULL)
                .failOnUnknownProperties(false)
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .modules(simpleModule, javaTimeModule)
                .annotationIntrospector(new SensitiveFieldIntrospector())
                .configure(JsonUtil.getObjectMapper());

    }

    private class SensitiveFieldIntrospector extends JacksonAnnotationIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        public Object findSerializer(final Annotated annotated) {
            final Annotation annotation = annotated.getAnnotation(SensitiveField.class);
            return (Objects.nonNull(annotation)
                    && Optional.ofNullable(commonProperties.getMvc()
                    .getDesensitization()).orElse(false))
                    ? SensitiveFieldSerializer.class : super.findSerializer(annotated);
        }
    }

    @Configuration
    @ConditionalOnProperty(name = "ms.common.mvc.signature.enable", havingValue = "true")
    public static class SignatureRequestConfig {

        @Bean
        public SignRequestFilter signRequestFilter(final SignatureScanner signatureScanner,
                                                   final GlobalHandlerExceptionResolver resolver) {
            return new SignRequestFilter(signatureScanner, resolver);
        }

        @Bean
        public SignatureScanner signatureScanner(
                ObjectProvider<List<SignatureStrategy>> signatureStrategyProvider,
                final CommonProperties commonProperties) {
            return new SignatureScanner(
                    signatureStrategyProvider.getIfAvailable(),
                    commonProperties.getMvc().getSignature()
            );
        }

        @Bean
        public SignatureStrategy rsaStrategy() {
            return new RsaStrategy();
        }

        @Bean
        public SignatureStrategy sha256HexStrategy() {
            return new Sha256HexStrategy();
        }

        @Bean
        public SignatureStrategy md5Strategy() {
            return new Md5Strategy();
        }
    }
}
