package com.liquido.core.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import com.liquido.core.common.feign.MethodFeignDecoder;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.mvc.interceptor.CustomizeFeignInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Request;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@Import({ObjectMapper.class})
@ConditionalOnClass({Feign.class, Decoder.class})
public class CustomizeFeignConfiguration {

    @Resource
    private ApplicationContext context;

    @Bean
    public CustomizeFeignInterceptor customizeFeignInterceptor(final ObjectMapper objectMapper) {
        return new CustomizeFeignInterceptor(objectMapper);
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(2,
                TimeUnit.MINUTES, 2,
                TimeUnit.MINUTES, true);
    }

    @Bean
    public Decoder feignDecoder(final ObjectProvider<HttpMessageConverterCustomizer> customizers) {
        return new MethodFeignDecoder(context, feignHttpMessageConverter(), customizers);
    }

    public ObjectFactory<HttpMessageConverters> feignHttpMessageConverter() {
        final MappingJackson2HttpMessageConverter jsonConverter =
                new MappingJackson2HttpMessageConverter();

        // Set the parsing JSON tool class
        jsonConverter.setObjectMapper(JsonUtil.getObjectMapper());

        final List<MediaType> list = new ArrayList<>();
        list.add(MediaType.APPLICATION_JSON);
        jsonConverter.setSupportedMediaTypes(list);

        return () -> new HttpMessageConverters(jsonConverter);
    }
}
