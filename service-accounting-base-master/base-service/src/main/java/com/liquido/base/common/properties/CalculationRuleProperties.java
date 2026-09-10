package com.liquido.base.common.properties;

import java.util.Map;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties("base.calculation-rule")
public class CalculationRuleProperties {

    private Map<String, String> comparer;

    private Map<String, Integer> weights;

}
