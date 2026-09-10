package com.liquido.worker.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.liquido.core.common.thread.CommonThreadMdcDecorator;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Setter
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "worker.thread-pool")
public class ThreadPoolConfiguration {

    private Integer coreSize;

    private Integer maxSize;

    private Integer queueCapacity;

    private Integer keepAlive;

    private ThreadPoolTaskExecutor createExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(new CommonThreadMdcDecorator());
        return executor;
    }

    @Bean
    public Executor inProgressPublishSqsEventExecutor() {
        final ThreadPoolTaskExecutor executor = createExecutor();
        executor.setThreadNamePrefix("inProgressPublishSqsEventExecutor-");
        return executor;
    }

    @Bean
    public Executor feePublishSqsEventExecutor() {
        final ThreadPoolTaskExecutor executor = createExecutor();
        executor.setThreadNamePrefix("feePublishSqsEvent-");
        return executor;
    }

    @Bean
    public Executor monitorExecutor() {
        final ThreadPoolTaskExecutor executor = createExecutor();
        executor.setThreadNamePrefix("monitorExecutor-");
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("task-executor-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(new CommonThreadMdcDecorator());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();

        return executor;
    }

}
