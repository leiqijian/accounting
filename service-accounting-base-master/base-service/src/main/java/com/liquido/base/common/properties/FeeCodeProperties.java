package com.liquido.base.common.properties;

import java.util.Map;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties("base.fee-code")
public class FeeCodeProperties {

    private Map<String, Integer> weights;

}
