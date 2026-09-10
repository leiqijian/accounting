package com.liquido.statement.common.properties;


import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.ProductCodeEnum;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "statement")
public class StatementProperties {

    @NestedConfigurationProperty
    private DailyBillProperties dailyBill;

    @NestedConfigurationProperty
    private ApprovalProperties approval;

    @NestedConfigurationProperty
    private LarkProperties lark;

    @NestedConfigurationProperty
    private ChargeBackOrder chargeBackOrder;

    @NestedConfigurationProperty
    private PayOutInsufficientBalanceAlert payOutInsufficientBalanceAlert;

    @NestedConfigurationProperty
    private CostIncomeExtraProperties costIncomeExtra;

    @NestedConfigurationProperty
    private InProgressCalculationProperties inProgressCalculation;

    @NestedConfigurationProperty
    private UploadConfigProperties uploadConfig;

    @NestedConfigurationProperty
    private Calendar calendar;

    @Bean
    @RefreshScope
    public DailyBillProperties getDailyBillProperties() {
        return this.dailyBill;
    }

    @Bean
    @RefreshScope
    public ApprovalProperties getApproval() {
        return this.approval;
    }

    @Bean
    @RefreshScope
    public LarkProperties getLarkProperties() {
        return this.lark;
    }

    @Bean
    @RefreshScope
    public ChargeBackOrder getChargeBackOrder() {
        return this.chargeBackOrder;
    }

    @Bean
    @RefreshScope
    public PayOutInsufficientBalanceAlert getPayOutInsufficientBalanceAlert() {
        return this.payOutInsufficientBalanceAlert;
    }

    @Bean
    @RefreshScope
    public CostIncomeExtraProperties getCostIncomeExtraProperties() {
        return this.costIncomeExtra;
    }

    @Bean
    @RefreshScope
    public InProgressCalculationProperties getInProgressCalculationProperties() {
        return this.inProgressCalculation;
    }

    @Bean
    @RefreshScope
    public UploadConfigProperties getUploadConfig() {
        return this.uploadConfig;
    }

    @Bean
    @RefreshScope
    public Calendar getCalendar() {
        return this.calendar;
    }

    @Data
    public static class DailyBillProperties {
        private List<CustomizedDailyBillConfig> customConfig;
    }

    @Data
    public static class CustomizedDailyBillConfig {
        private Long accountId;

        @Convert(converter = CurrencyEnum.Convert.class)
        private CurrencyEnum postpaidCurrency;

    }

    @Data
    public static class ApprovalProperties {
        private ApprovalBizTransferOut bizTransferOut;
    }

    @Data
    public static class PayOutInsufficientBalanceAlert {
        private AlarmRobot alarmRobot;
        private String email;
    }

    @Data
    public static class ApprovalBizTransferOut {
        private List<Long> accountIds;
        private Boolean overWithdrawalAmount;
    }

    @Data
    public static class LarkProperties {
        private AlarmRobot alarmRobot;
        private MonitorProperties monitor;
    }

    @Data
    public static class AlarmRobot {
        private String webhook;
        private String signKey;
    }

    @Data
    public static class MonitorProperties {

        /**
         * Unit: hours
         */
        private Integer insufficientBalanceAlarmInterval;

    }

    @Data
    public static class Calendar {

        /**
         * Unit: hours
         */
        private Integer maxMonthInterval;

    }


    @Data
    public static class ChargeBackOrder {
        private Set<ProductCodeEnum> supportProducts;

        private Integer defaultDaysLeftToDefend;
    }

    @Data
    public static class CostIncomeExtraProperties {
        private AlarmRobot alarmRobot;
    }

    @Data
    public static class InProgressCalculationProperties {

        private Integer syncErrorWarnLimitCount;

    }

    @Data
    public static class UploadConfigProperties implements Serializable {
        private static final long serialVersionUID = 1L;

        private String allowedFileNameRegex;

        private List<String> allowedFileExtensions;

        private List<String> allowedFileTypes;
    }
}
