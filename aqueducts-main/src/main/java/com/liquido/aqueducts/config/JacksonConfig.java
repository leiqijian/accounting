package com.liquido.aqueducts.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(
                        DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE,
                        true
                )
                .configure(JsonParser.Feature.IGNORE_UNDEFINED, true)
                .build();
    }
}
