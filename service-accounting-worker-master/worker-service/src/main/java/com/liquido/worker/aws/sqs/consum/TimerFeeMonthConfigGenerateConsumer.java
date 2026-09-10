package com.liquido.worker.aws.sqs.consum;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.worker.aws.sqs.msg.TimerFeeMonthConfigGenerateMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.service.fee.MonthFeeConfigGenerationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"worker.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerFeeMonthConfigGenerateConsumer
        extends BaseSqsMessageHandler<TimerFeeMonthConfigGenerateMsg> {

    private final MonthFeeConfigGenerationService monthFeeConfigGenerationService;

    @Override
    public void handle(TimerFeeMonthConfigGenerateMsg msg) {
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.TIMER_FEE_MONTH_CONFIG_GENERATE, msg);
        try {
            monthFeeConfigGenerationService.initAllProductVersion();
        } catch (Exception e) {
            log.error("init product version sqs timer execute error, queue: [key: {}]",
                    Constant.AWS_QUEUE_KEYS.TIMER_FEE_MONTH_CONFIG_GENERATE, e);
        }

        try {
            monthFeeConfigGenerationService.checkMonthFeeConfig();
        } catch (Exception e) {
            log.error("check month fee config sqs timer execute error, queue: [key: {}]",
                    Constant.AWS_QUEUE_KEYS.TIMER_FEE_MONTH_CONFIG_GENERATE, e);
        }
    }

    @Override
    public Class<TimerFeeMonthConfigGenerateMsg> messageType() {
        return TimerFeeMonthConfigGenerateMsg.class;
    }

}
