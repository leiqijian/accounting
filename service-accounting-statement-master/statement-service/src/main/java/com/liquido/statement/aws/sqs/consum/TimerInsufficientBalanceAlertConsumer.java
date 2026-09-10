package com.liquido.statement.aws.sqs.consum;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.statement.aws.sqs.msg.TimerInsufficientBalanceAlertMsg;
import com.liquido.statement.common.Constant;
import com.liquido.statement.event.InsufficientBalanceAlertEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"statement.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerInsufficientBalanceAlertConsumer extends
        BaseSqsMessageHandler<TimerInsufficientBalanceAlertMsg> {

    private final ApplicationEventPublisher publisher;

    @Override
    public void handle(final TimerInsufficientBalanceAlertMsg msg) {
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.TIMER_INSUFFICIENT_BALANCE_ALERT, msg);
        publisher.publishEvent(new InsufficientBalanceAlertEvent(this));
    }

    @Override
    public Class<TimerInsufficientBalanceAlertMsg> messageType() {
        return TimerInsufficientBalanceAlertMsg.class;
    }

}
