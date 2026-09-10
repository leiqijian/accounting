package com.liquido.statement.aws.sqs.consum.register;

import com.liquido.core.aws.sqs.BaseConsumerRegister;
import com.liquido.statement.aws.sqs.consum.TimerChargeBackOrderDefenseTimeoutConsumer;
import com.liquido.statement.aws.sqs.msg.TimerChargeBackOrderDefenseTimeoutMsg;
import com.liquido.statement.common.Constant;
import com.liquido.statement.common.properties.AwsSqsProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;

@Slf4j
@Component
@ConditionalOnProperty(name = {"statement.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerChargeBackOrderDefenseTimeoutRegister
        extends BaseConsumerRegister<TimerChargeBackOrderDefenseTimeoutMsg> {

    @Autowired
    public TimerChargeBackOrderDefenseTimeoutRegister(
            final TimerChargeBackOrderDefenseTimeoutConsumer consumer,
            final SqsClient client,
            final ObjectMapper objectMapper,
            final AwsSqsProperties.SqsQueueInfoProperties infoProperties) {
        super(consumer, client, objectMapper, infoProperties.getQueues()
                .get(Constant.AWS_QUEUE_KEYS.TIMER_CHARGE_BACK_ORDER_DEFENSE_TIMEOUT));
    }

}
