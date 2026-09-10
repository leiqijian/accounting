package com.liquido.statement.aws.sqs.consum;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.statement.aws.sqs.msg.TimerFeeSettleMsg;
import com.liquido.statement.common.Constant;
import com.liquido.statement.service.dailycut.DailyCutService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"statement.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerFeeSettleConsumer extends BaseSqsMessageHandler<TimerFeeSettleMsg> {

    private final DailyCutService dailyCutService;

    @Override
    public void handle(final TimerFeeSettleMsg msg) {
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.TIMER_FEE_SETTLE, msg);
        dailyCutService.runAccountDailyCut();
    }

    @Override
    public Class<TimerFeeSettleMsg> messageType() {
        return TimerFeeSettleMsg.class;
    }

}
