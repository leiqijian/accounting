package com.liquido.core.configuration;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;

import com.liquido.core.common.logger.LogWrapper;
import com.liquido.core.common.mq.MqLogTraceInterceptor;
import com.liquido.core.common.mq.MqLogTraceMessageConverter;
import com.liquido.core.common.snowflake.SnowflakeInitializer;
import com.liquido.core.common.thread.CommonThreadMdcDecorator;
import com.liquido.core.common.utils.SpringUtils;
import com.liquido.core.common.validator.GlobalValidator;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.aop.interceptor.AsyncExecutionAspectSupport;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Initialize public configuration
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({CommonProperties.class, SharedProperties.class})
public class CommonConfiguration {

    @Resource
    private CommonProperties commonProperties;

    @Resource
    private SharedProperties sharedProperties;

    /**
     * Rewrite the thread decorator, mainly used to copy the MDC
     * of the main thread to the child thread
     * Scenario: Implementing asynchronous threads when using the @Async annotation is .
     */
    @Bean
    public TaskDecorator taskDecorator() {
        return new CommonThreadMdcDecorator();
    }

    /**
     * Integrated snowflake algorithm, compatible with K8S and non-K8S environments
     */
    @Bean
    @ConditionalOnProperty(prefix = "ms.common.snowflake", name = "mode", matchIfMissing = false)
    public SnowflakeInitializer snowflakeInitializer() {
        return new SnowflakeInitializer();
    }


    @Bean
    public GlobalValidator globalValidator() {
        return new GlobalValidator();
    }

    @Bean(AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    @ConditionalOnMissingBean(name = AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    public TaskExecutor taskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(180);
        executor.setThreadNamePrefix("async-pool-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(new CommonThreadMdcDecorator());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean
    public LogWrapper logWrapper() {
        return new LogWrapper();
    }

    @ConditionalOnClass({SimpleRabbitListenerContainerFactory.class, ConnectionFactory.class})
    public static class RabbitMqConfiguration {

        @Bean
        public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
                SimpleRabbitListenerContainerFactoryConfigurer configurer,
                ConnectionFactory connectionFactory) {

            final SimpleRabbitListenerContainerFactory factory =
                    new SimpleRabbitListenerContainerFactory();
            configurer.configure(factory, connectionFactory);

            final List<Advice> advices = null == factory.getAdviceChain() ? Lists.newArrayList() :
                    Lists.newArrayList(factory.getAdviceChain());

            advices.add(new MqLogTraceInterceptor());
            factory.setAdviceChain(advices.toArray(new Advice[advices.size()]));
            return factory;
        }

        @Bean
        public MessageConverter mqLogTraceMessageConverter() {
            return new MqLogTraceMessageConverter();
        }
    }

    @Bean
    public SpringUtils springUtils() {
        return new SpringUtils();
    }
}
