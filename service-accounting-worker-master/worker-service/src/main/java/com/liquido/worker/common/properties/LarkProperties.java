package com.liquido.worker.common.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "worker.lark")
public class LarkProperties {

    @NestedConfigurationProperty
    private AlarmRobotProperties alarmRobot;

    @NestedConfigurationProperty
    private MonitorProperties monitor;

    @Bean
    @RefreshScope
    public AlarmRobotProperties getAlarmRobotProperties() {
        return this.alarmRobot;
    }

    @Bean
    @RefreshScope
    public MonitorProperties getMonitorProperties() {
        return this.monitor;
    }

    @Data
    public static class AlarmRobotProperties {

        private String webhook;

        private String signKey;

    }

    @Data
    public static class MonitorProperties {

        /**
         * Unit: hours
         */
        private Integer warnAlarmInterval;

    }
}
