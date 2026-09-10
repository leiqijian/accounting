package com.liquido.aqueducts.config;

import java.util.List;
import java.util.Map;

import com.liquido.aqueducts.config.source.SourceNode;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "source-config")
@Configuration
@Getter
@Setter
public class SourceConfig {

    private Map<String, SourceNode> payin;

    private Map<String, SourceNode> payout;

    private Map<String, SourceNode> marketplace;

    private Map<String, SourceNode> subAccount;

}
