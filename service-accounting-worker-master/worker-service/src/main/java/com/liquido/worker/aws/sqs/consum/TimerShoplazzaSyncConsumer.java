package com.liquido.worker.aws.sqs.consum;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.worker.aws.sqs.msg.TimerShoplazzaSyncMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.service.sync.ShoplazzaSyncService;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RefreshScope
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"worker.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerShoplazzaSyncConsumer extends BaseSqsMessageHandler<TimerShoplazzaSyncMsg> {

    private final RedisDistLock redisDistLock;
    private final LarkRobotMonitor larkRobotMonitor;
    private final ShoplazzaSyncService shoplazzaSyncService;
    private final WorkerProperties.ShoplazzaProperties shoplazzaProperties;

    @Override
    @SneakyThrows
    public void handle(TimerShoplazzaSyncMsg msg) {
        final long begin = System.currentTimeMillis();
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.TIMER_SHOPLAZZA_SYNC, msg);

        if (!Optional.ofNullable(shoplazzaProperties.getEnable()).orElse(false)) {
            log.warn("aws sqs queue[key: {}] not enable",
                    Constant.AWS_QUEUE_KEYS.TIMER_SHOPLAZZA_SYNC);
            return;
        }

        final String lockVal = DataUtil.getUuid();
        if (!redisDistLock.tryLock(
                Constant.CACHE.SHOPLAZZA_SYNC_TIMER_LOCK, lockVal, 10, TimeUnit.SECONDS)) {
            return;
        }

        try {
            shoplazzaSyncService.sync(TransactionTypeCodeEnum.PAY_IN);
        } catch (Exception e) {
            log.error(String.format("sqs timer execute error, queue: [key: %s]",
                    Constant.AWS_QUEUE_KEYS.TIMER_SHOPLAZZA_SYNC), e);

            larkRobotMonitor.error("Shoplazza Sync Error",
                    "Shoplazza sync timer execute error",
                    String.format("Error msg: %s", e.getMessage()));

        } finally {
            log.info("sync transaction shoplazza form dw end. ts:{}ms",
                    System.currentTimeMillis() - begin);
        }
    }

    @Override
    public Class<TimerShoplazzaSyncMsg> messageType() {
        return TimerShoplazzaSyncMsg.class;
    }

}
