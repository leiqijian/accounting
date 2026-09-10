package com.liquido.statement.aws.sqs.consum;

import java.util.List;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.statement.aws.sqs.msg.TimerLarkMonitorMsg;
import com.liquido.statement.common.Constant;
import com.liquido.statement.service.monitor.StatementMonitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"statement.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerLarkMonitorConsumer extends BaseSqsMessageHandler<TimerLarkMonitorMsg> {

    private final List<StatementMonitor> monitorList;

    @Override
    public void handle(final TimerLarkMonitorMsg msg) {
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.TIMER_LARK_MONITOR, msg);

        if (CollectionUtils.isEmpty(monitorList)) {
            return;
        }

        for (final StatementMonitor sm : monitorList) {
            try {
                sm.monitor();
            } catch (Exception e) {
                log.error("statement monitor error:" + sm.getClass().getName(), e);
            }
        }
    }

    @Override
    public Class<TimerLarkMonitorMsg> messageType() {
        return TimerLarkMonitorMsg.class;
    }

}
