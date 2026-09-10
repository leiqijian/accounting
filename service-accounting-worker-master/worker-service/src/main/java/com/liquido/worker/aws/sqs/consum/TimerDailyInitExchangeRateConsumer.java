package com.liquido.worker.aws.sqs.consum;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.worker.aws.sqs.msg.TimerDailyInitExchangeRateMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.service.calculate.ExchangeRateManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"worker.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerDailyInitExchangeRateConsumer
        extends BaseSqsMessageHandler<TimerDailyInitExchangeRateMsg> {

    private final ExchangeRateManager exchangeRateManager;

    @Override
    public void handle(TimerDailyInitExchangeRateMsg msg) {
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.TIMER_DAILY_INIT_EXCHANGE_RATE, msg);
        exchangeRateManager.initExchangeRate();
    }

    @Override
    public Class<TimerDailyInitExchangeRateMsg> messageType() {
        return TimerDailyInitExchangeRateMsg.class;
    }

}
