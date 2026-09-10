package com.liquido.statement.common.properties;

import java.util.Map;

import com.liquido.core.aws.sqs.SqsQueuesInfo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "statement.aws.sqs")
public class AwsSqsProperties {

    private String region;

    private String accessKeyId;

    private String accessSecret;

    @NestedConfigurationProperty
    private SqsQueueInfoProperties queue;

    @Bean
    @RefreshScope
    public SqsQueueInfoProperties getSqsQueueInfoProperties() {
        return this.queue;
    }

    @Bean
    @RefreshScope
    public SqsQueueRetryProperties getSqsQueueRetryProperties() {
        return this.queue.getRetry();
    }

    @Data
    public static class SqsQueueInfoProperties {

        private Boolean enable;

        @NestedConfigurationProperty
        private SqsQueueRetryProperties retry;

        @NestedConfigurationProperty
        private Map<String, SqsQueuesInfo> queues;

    }

    @Data
    public static class SqsQueueRetryProperties {

        /**
         * retry total
         */
        private Integer total;

        /**
         * retry Avoidance algorithm modulus, per retry increase the interval
         * : duration = randomMs + (randomMs * modulus * retry of times);
         */
        private Integer modulus;

        /**
         * first retry min interval duration of ms (min of randomMs)
         */
        private Integer minIntervalDurationMs;

        /**
         * first retry max interval duration of ms (max of randomMs)
         */
        private Integer maxIntervalDurationMs;

    }

}
