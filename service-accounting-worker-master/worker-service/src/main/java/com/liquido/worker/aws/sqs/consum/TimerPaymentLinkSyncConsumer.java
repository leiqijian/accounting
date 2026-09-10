package com.liquido.worker.aws.sqs.consum;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.worker.aws.sqs.msg.TimerPaymentLinkSyncMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.service.sync.PaymentLinkSyncService;

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
public class TimerPaymentLinkSyncConsumer extends BaseSqsMessageHandler<TimerPaymentLinkSyncMsg> {

    private final RedisDistLock redisDistLock;
    private final LarkRobotMonitor larkRobotMonitor;
    private final PaymentLinkSyncService paymentLinkSyncService;
    private final WorkerProperties.PaymentLinkProperties paymentLinkProperties;

    @SneakyThrows
    @Scheduled(cron = "${worker.aws.sqs.queue.sync.payment-link-cron}")
    public void scheduledHandler() {

        log.info("scheduled sync payment-link form dw begin.");
        this.handle(TimerPaymentLinkSyncMsg.builder().build());
        log.info("scheduled sync payment-link form dw end.");

    }

    @Override
    @SneakyThrows
    public void handle(TimerPaymentLinkSyncMsg msg) {
        final long begin = System.currentTimeMillis();
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.TIMER_PAYMENT_LINK_SYNC, msg);

        if (!Optional.ofNullable(paymentLinkProperties.getEnable()).orElse(false)) {
            log.warn("aws sqs queue[key: {}] not enable",
                    Constant.AWS_QUEUE_KEYS.TIMER_PAYMENT_LINK_SYNC);
            return;
        }

        final String lockVal = DataUtil.getUuid();
        if (!redisDistLock.tryLock(
                Constant.CACHE.PAYMENT_LINK_SYNC_TIMER_LOCK, lockVal, 10, TimeUnit.SECONDS)) {
            return;
        }

        try {
            paymentLinkSyncService.sync(TransactionTypeCodeEnum.PAY_IN);
        } catch (Exception e) {
            log.error(String.format("sqs timer execute error, queue: [key: %s]",
                    Constant.AWS_QUEUE_KEYS.TIMER_PAYMENT_LINK_SYNC), e);

            larkRobotMonitor.error("Payment Link Sync Error",
                    "payment link sync timer execute error",
                    String.format("Error msg: %s", e.getMessage()));
        } finally {
            log.info("sync transaction payment-link form dw end. ts:{}ms",
                    System.currentTimeMillis() - begin);
        }
    }

    @Override
    public Class<TimerPaymentLinkSyncMsg> messageType() {
        return TimerPaymentLinkSyncMsg.class;
    }

}
