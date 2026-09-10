package com.liquido.base.common.properties;

import java.util.List;
import java.util.Set;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "base")
public class BaseProperties {

    @NestedConfigurationProperty
    private List<MergerConfig> mergerAccount;


    @NestedConfigurationProperty
    private SubMerchantProperties subMerchant;


    @Bean
    @RefreshScope
    public List<MergerConfig> getMergerAccount() {
        return mergerAccount;
    }

    @Bean
    @RefreshScope
    public SubMerchantProperties getSubMerchant() {
        return this.subMerchant;
    }

    @Data
    public static class MergerConfig {

        private String merchantCode;

        @Convert(converter = CountryCodeEnum.Convert.class)
        private List<CountryCodeEnum> notSupportCountry;
    }

    @Data
    public static class SubMerchantProperties {

        private Set<Long> dividedBillMerchantIds;

    }
}
