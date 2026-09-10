package com.liquido.core.common.mq;

import java.util.Objects;

import com.liquido.core.common.logger.LogConstant;
import com.liquido.core.common.utils.DataUtil;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;

/**
 * intercept rabbitMq invokeListener add traceId
 * <p>
 * org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
 * .ContainerDelegate#invokeListener(com.rabbitmq.client.Channel,
 * org.springframework.amqp.core.Message)
 * RabbitMqConfiguration.rabbitListenerContainerFactory(
 * org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer,
 * org.springframework.amqp.rabbit.connection.ConnectionFactory)
 */
@Slf4j
public class MqLogTraceInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        try {
            Object msgId = null;
            Object traceId = null;
            final Object[] args = invocation.getArguments();
            for (final Object arg : args) {
                if (arg instanceof Message) {
                    final Message message = (Message) arg;
                    msgId = message.getMessageProperties().getHeaders().get(LogConstant.MESSAGE_ID);
                    traceId = message.getMessageProperties().getHeaders().get(LogConstant.TRACE_ID);
                    break;
                }
            }

            if (Objects.isNull(msgId) || StringUtils.isBlank(msgId.toString())) {
                msgId = DataUtil.getUuid();
            }

            if (Objects.isNull(traceId) || StringUtils.isBlank(traceId.toString())) {
                traceId = DataUtil.getUuid();
            }

            MDC.put(LogConstant.MESSAGE_ID, msgId.toString());
            MDC.put(LogConstant.TRACE_ID, traceId.toString());

            return invocation.proceed();
        } finally {
            MDC.clear();
        }
    }
}
