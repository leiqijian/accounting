package com.liquido.worker.common.properties;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionStatusEnum;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "worker")
public class WorkerProperties {

    @NestedConfigurationProperty
    private ThreadPoolProperties threadPool;

    @NestedConfigurationProperty
    private ExchangeRateProperties exchangeRate;

    @NestedConfigurationProperty
    private DataWarehouseProperties dataWarehouse;

    @NestedConfigurationProperty
    private CoRejectedOrderProperties coRejectedOrder;

    @NestedConfigurationProperty
    private FeeCalculationProperties feeCalculation;

    @NestedConfigurationProperty
    private PaymentLinkProperties paymentLink;

    @NestedConfigurationProperty
    private ShopifyProperties shopify;

    @NestedConfigurationProperty
    private ShoplazzaProperties shoplazza;

    @NestedConfigurationProperty
    private TokenizationProperties tokenization;

    @NestedConfigurationProperty
    private CalculationRule calculationRule;

    @NestedConfigurationProperty
    private AccountingSchedule accountingSchedule;

    @NestedConfigurationProperty
    private DefenseOrder defenseOrder;

    @NestedConfigurationProperty
    private MonthlyFeeConfig monthlyFeeConfig;

    @Bean
    @RefreshScope
    public ThreadPoolProperties getThreadPoolProperties() {
        return this.threadPool;
    }

    @Bean
    @RefreshScope
    public ExchangeRateProperties getExchangeRateProperties() {
        return this.exchangeRate;
    }

    @Bean
    @RefreshScope
    public DataWarehouseProperties getDataWarehouseProperties() {
        return this.dataWarehouse;
    }

    @Bean
    @RefreshScope
    public CoRejectedOrderProperties getCoRejectedOrderProperties() {
        return this.coRejectedOrder;
    }

    @Bean
    @RefreshScope
    public FeeCalculationProperties getFeeCalculationProperties() {
        return this.feeCalculation;
    }

    @Bean
    @RefreshScope
    public PaymentLinkProperties getPaymentLinkProperties() {
        return this.paymentLink;
    }

    @Bean
    @RefreshScope
    public ShopifyProperties getShopifyProperties() {
        return this.shopify;
    }

    @Bean
    @RefreshScope
    public ShoplazzaProperties getShoplazzaProperties() {
        return this.shoplazza;
    }

    @Bean
    @RefreshScope
    public TokenizationProperties getTokenizationProperties() {
        return this.tokenization;
    }

    @Bean
    @RefreshScope
    public CalculationRule getCalculationRule() {
        return this.calculationRule;
    }

    @Bean
    @RefreshScope
    public AccountingSchedule getAccountingSchedule() {
        return this.accountingSchedule;
    }

    @Bean
    @RefreshScope
    public DefenseOrder getDefenseOrder() {
        return this.defenseOrder;
    }


    @Bean
    @RefreshScope
    public MonthlyFeeConfig getMonthlyFeeConfig() {
        return this.monthlyFeeConfig;
    }

    @Data
    public static class ThreadPoolProperties {

        private Integer coreSize;

        private Integer maxSize;

        private Integer queueCapacity;

        private Integer keepAlive;

    }

    @Data
    public static class ExchangeRateProperties {

        private String authUrl;

        private String clientId;

        private String clientSecret;

        private String grantType;

        private String quoteUrl;

        private String rangeUrl;

        private List<CurrencyPair> hourlyExchangeRate;

    }

    @Data
    public static class DataWarehouseProperties {

        private Long timeSpace;

        private Long maxGetValueInterval;

        private Long requestWindowExtension;

        private Map<String, DataWarehouseEnvProperties> env;

    }

    @Data
    public static class DataWarehouseEnvProperties {

        private Boolean enable;

        private String url;

        private Boolean authNeed;

        private String authUrl;

        private String authGrantType;

        private String authClientId;

        private String authClientSecret;

    }

    @Data
    public static class FeeCalculationProperties {

        private Boolean enable;

        private Boolean onlyUpdate;

        private Long syncDelayMs;

        private Integer batchQuantity;

        private Integer syncErrorWarnLimitCount;

        private List<String> syncExcludeAccount;

        private List<String> calculationExcludeAccount;

        private List<TransactionStatusEnum> updateFinalStatusTimestampStatus;

    }

    @Data
    public static class PaymentLinkProperties {

        private Boolean enable;

        private Long syncDelayMs;

        private Integer batchQuantity;

        private List<String> excludeAccount;

    }

    @Data
    public static class ShopifyProperties {

        private Boolean enable;

        private Long syncDelayMs;

        private Integer batchQuantity;

        private List<String> excludeAccount;

    }

    @Data
    public static class ShoplazzaProperties {

        private Boolean enable;

        private Long syncDelayMs;

        private Integer batchQuantity;

        private List<String> excludeAccount;

    }

    @Data
    public static class TokenizationProperties {

        private Boolean enable;

        private Long syncDelayMs;

        private Integer batchQuantity;

        private List<String> excludeAccount;

    }

    @Data
    public static class CalculationRule {

        private List<String> fieldSign;

        private List<String> otherFieldSign;
    }

    @Data
    public static class AccountingSchedule {

        private Boolean enable;

        private List<Long> accountList;
    }

    @Data
    public static class DefenseOrder {

        private Set<ProductCodeEnum> supportProducts;

    }

    @Data
    public static class CurrencyPair {

        @Convert(converter = CurrencyEnum.Convert.class)
        private CurrencyEnum sourceCurrency;

        @Convert(converter = CurrencyEnum.Convert.class)
        private CurrencyEnum targetCurrency;

    }

    @Data
    public static class MonthlyFeeConfig {
        private LarkMessage larkMessage;
    }

    @Data
    public static class LarkMessage {

        private List<String> receiveId;
    }

    @Data
    public static class CoRejectedOrderProperties {

        private List<String> specifiedMerchants;

        private List<String> debitExcludeErrorCode;

        private Long enableTimestamp;

    }

}
