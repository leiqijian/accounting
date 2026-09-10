package com.liquido.worker.aws.sqs.consum.register;

import com.liquido.core.aws.sqs.BaseConsumerRegister;
import com.liquido.worker.aws.sqs.consum.TimerDailyInitExchangeRateConsumer;
import com.liquido.worker.aws.sqs.msg.TimerDailyInitExchangeRateMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.properties.AwsSqsProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;


@Slf4j
@Component
@ConditionalOnProperty(name = {"worker.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerDailyInitExchangeRateConsumerRegister extends
        BaseConsumerRegister<TimerDailyInitExchangeRateMsg> {

    @Autowired
    public TimerDailyInitExchangeRateConsumerRegister(
            final TimerDailyInitExchangeRateConsumer consumer,
            final SqsClient client,
            final ObjectMapper objectMapper,
            final AwsSqsProperties.SqsQueueInfoProperties infoProperties) {
        super(consumer, client, objectMapper, infoProperties.getQueues()
                .get(Constant.AWS_QUEUE_KEYS.TIMER_DAILY_INIT_EXCHANGE_RATE));
    }

}
