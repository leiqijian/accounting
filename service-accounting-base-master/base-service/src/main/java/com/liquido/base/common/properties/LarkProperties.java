package com.liquido.base.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "base.lark")
public class LarkProperties {

    private AlarmRobot alarmRobot;

    private Auth auth;

    @Bean
    @RefreshScope
    public AlarmRobot getAlarmRobot() {
        return this.alarmRobot;
    }

    @Bean
    @RefreshScope
    public Auth getAuth() {
        return this.auth;
    }

    @Data
    public static class AlarmRobot {

        private String webhook;

        private String signKey;
    }

    @Data
    public static class Auth {

        private String appId;

        private String appSecret;

        private String url;
    }
}
