package com.liquido.base.config;

import java.util.concurrent.ThreadPoolExecutor;

import com.liquido.core.common.thread.CommonThreadMdcDecorator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BaseConfiguration {

    @Bean(value = "monitorExecutor")
    public TaskExecutor monitorExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(120);
        executor.setThreadNamePrefix("monitor-task-pool-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(new CommonThreadMdcDecorator());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

}
