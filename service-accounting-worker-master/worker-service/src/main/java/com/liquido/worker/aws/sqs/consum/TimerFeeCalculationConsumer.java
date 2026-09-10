package com.liquido.worker.aws.sqs.consum;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.worker.aws.sqs.msg.TimerFeeCalculationMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.service.sync.TransactionSyncService;

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
public class TimerFeeCalculationConsumer extends BaseSqsMessageHandler<TimerFeeCalculationMsg> {

    private final RedisDistLock redisDistLock;
    private final LarkRobotMonitor larkRobotMonitor;
    private final TransactionSyncService transactionSyncService;
    private final WorkerProperties.FeeCalculationProperties feeCalculationProperties;

    @SneakyThrows
    @Scheduled(cron = "${worker.aws.sqs.queue.sync.transaction-cron}")
    public void scheduledHandler() {

        log.info("scheduled sync task-order form dw begin.");
        this.handle(TimerFeeCalculationMsg.builder().build());
        log.info("scheduled sync task-order form dw end.");
    }


    @Override
    @SneakyThrows
    public void handle(TimerFeeCalculationMsg msg) {
        final long begin = System.currentTimeMillis();
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.TIMER_FEE_CALCULATION, msg);

        if (!Optional.ofNullable(feeCalculationProperties.getEnable()).orElse(false)) {
            log.warn("aws sqs queue[key: {}] not enable",
                    Constant.AWS_QUEUE_KEYS.TIMER_FEE_CALCULATION);
            return;
        }

        final String lockVal = DataUtil.getUuid();
        if (!redisDistLock.tryLock(
                Constant.CACHE.FEE_CALCULATION_TIMER_LOCK, lockVal, 10, TimeUnit.SECONDS)) {
            return;
        }

        try {
            log.info("sync transaction order form dw begin.");
            try {
                transactionSyncService.sync(TransactionTypeCodeEnum.PAY_IN);
            } catch (Exception e) {
                log.error(String.format("sqs timer execute error, queue: [key: %s]",
                        Constant.AWS_QUEUE_KEYS.TIMER_FEE_CALCULATION), e);

                larkRobotMonitor.error("PayIn Data Warehouse Sync Error",
                        "PayIn Data warehouse sync timer execute error",
                        String.format("Error msg: %s", e.getMessage()));

            }

            try {
                transactionSyncService.sync(TransactionTypeCodeEnum.PAY_OUT);
            } catch (Exception e) {
                log.error(String.format("sqs timer execute error, queue: [key: %s]",
                        Constant.AWS_QUEUE_KEYS.TIMER_FEE_CALCULATION), e);

                larkRobotMonitor.error("PayOut Data Warehouse Sync Error",
                        "PayOut Data warehouse sync timer execute error",
                        String.format("Error msg: %s", e.getMessage()));
            }

        } finally {
            log.info("sync transaction order form dw end. ts:{}ms",
                    System.currentTimeMillis() - begin);
        }
    }

    @Override
    public Class<TimerFeeCalculationMsg> messageType() {
        return TimerFeeCalculationMsg.class;
    }

}
