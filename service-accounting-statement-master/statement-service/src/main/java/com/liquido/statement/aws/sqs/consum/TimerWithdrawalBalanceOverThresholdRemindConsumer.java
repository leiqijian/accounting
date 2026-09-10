package com.liquido.statement.aws.sqs.consum;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.statement.aws.sqs.msg.TimerWithdrawalBalanceOverThresholdRemindMsg;
import com.liquido.statement.common.Constant;
import com.liquido.statement.event.WithdrawalBalanceOverThresholdRemindEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"statement.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerWithdrawalBalanceOverThresholdRemindConsumer extends
        BaseSqsMessageHandler<TimerWithdrawalBalanceOverThresholdRemindMsg> {

    private final ApplicationEventPublisher publisher;

    @Override
    public void handle(final TimerWithdrawalBalanceOverThresholdRemindMsg msg) {
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.TIMER_BALANCE_OVER_THRESHOLD_REMIND, msg);
        publisher.publishEvent(new WithdrawalBalanceOverThresholdRemindEvent(this));
    }

    @Override
    public Class<TimerWithdrawalBalanceOverThresholdRemindMsg> messageType() {
        return TimerWithdrawalBalanceOverThresholdRemindMsg.class;
    }
}
