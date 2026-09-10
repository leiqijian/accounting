package com.liquido.core.aws.sqs;

import java.time.Duration;
import java.util.Optional;

import com.liquido.starter.sqs.api.ExceptionHandler;
import com.liquido.starter.sqs.api.SqsMessageHandler;
import com.liquido.starter.sqs.api.SqsMessageHandlerProperties;
import com.liquido.starter.sqs.api.SqsMessageHandlerRegistration;
import com.liquido.starter.sqs.api.SqsMessagePollerProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sqs.SqsClient;

@RequiredArgsConstructor
public class BaseConsumerRegister<T> implements SqsMessageHandlerRegistration<T> {

    private final SqsMessageHandler<T> consumer;

    private final SqsClient client;

    private final ObjectMapper objectMapper;

    private final SqsQueuesInfo sqsQueuesInfo;

    @Override
    public SqsMessageHandler<T> messageHandler() {
        return this.consumer;
    }

    @Override
    public String name() {
        return this.sqsQueuesInfo.getName();
    }

    @Override
    public SqsMessageHandlerProperties messageHandlerProperties() {
        return new SqsMessageHandlerProperties()
                .withHandlerThreadPoolSize(
                        Optional.ofNullable(sqsQueuesInfo.getHandlerThreadPoolSize()).orElse(1))
                .withHandlerQueueSize(
                        Optional.ofNullable(sqsQueuesInfo.getHandlerQueueSize()).orElse(1));
    }

    @Override
    public SqsMessagePollerProperties messagePollerProperties() {
        return new SqsMessagePollerProperties(sqsQueuesInfo.getUrl())
                .withPollingThreads(Optional.ofNullable(sqsQueuesInfo.getPollThreads()).orElse(1))
                .withWaitTime(Duration.ofSeconds(
                        Optional.ofNullable(sqsQueuesInfo.getPollWaitTime()).orElse(1)
                ))
                .withPollDelay(Duration.ofSeconds(
                        Optional.ofNullable(sqsQueuesInfo.getPollDelay()).orElse(1)
                ))
                .withExceptionHandler(exceptionHandler());
    }

    protected ExceptionHandler exceptionHandler() {
        return ExceptionHandler.defaultExceptionHandler();
    }

    @Override
    public SqsClient sqsClient() {
        return this.client;
    }

    @Override
    public ObjectMapper objectMapper() {
        return this.objectMapper;
    }
}
