package com.liquido.core.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Common Properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "share")
@RefreshScope
public class SharedProperties {

    @NestedConfigurationProperty
    private Lark lark;

    @Bean
    @RefreshScope
    public Lark getLark() {
        return this.lark;
    }

    @Data
    public static class Lark {
        private String alarmEnvironment;
    }
}

