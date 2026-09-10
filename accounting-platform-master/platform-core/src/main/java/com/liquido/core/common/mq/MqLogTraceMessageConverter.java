package com.liquido.core.common.mq;

import java.nio.charset.StandardCharsets;

import com.liquido.core.common.logger.LogConstant;
import com.liquido.core.common.logger.LogWrapper;
import com.liquido.core.common.utils.DataUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

/**
 * Rabbitmq Production new message add traceId
 */
@Slf4j
public class MqLogTraceMessageConverter extends SimpleMessageConverter {

    @Override
    protected Message createMessage(final Object object, final MessageProperties messageProperties)
            throws MessageConversionException {
        final Message message = super.createMessage(object, messageProperties);
        final String msgId = DataUtil.getUuid();
        final String traceId = DataUtil.getUuid();
        message.getMessageProperties().getHeaders().put(LogConstant.TRACE_ID, traceId);
        message.getMessageProperties().getHeaders().put(LogConstant.MESSAGE_ID, msgId);
        log.info(LogWrapper.op("MqLogTraceMessageConverter.createMessage")
                .wrap("message", new String(message.getBody(), StandardCharsets.UTF_8))
                .wrap(LogConstant.TRACE_ID, traceId).toString());

        return message;
    }

    @Override
    public Object fromMessage(final Message message) throws MessageConversionException {
        final Object traceId =
                message.getMessageProperties().getHeaders().get(LogConstant.TRACE_ID);
        log.info(LogWrapper.op("MqLogTraceMessageConverter.fromMessage")
                .wrap("message", new String(message.getBody(), StandardCharsets.UTF_8))
                .wrap(LogConstant.TRACE_ID, traceId).toString());

        return super.fromMessage(message);
    }
}
