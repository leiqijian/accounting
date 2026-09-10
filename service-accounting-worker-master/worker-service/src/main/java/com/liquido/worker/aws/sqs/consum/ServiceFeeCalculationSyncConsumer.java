package com.liquido.worker.aws.sqs.consum;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.worker.aws.sqs.msg.ServiceFeeCalculationSyncMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.service.sync.TransactionSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"worker.aws.sqs.queue.enable"}, havingValue = "true")
public class ServiceFeeCalculationSyncConsumer extends
        BaseSqsMessageHandler<ServiceFeeCalculationSyncMsg> {

    private final TransactionSyncService transactionSyncService;
    private final RedisCacheUtil redisCacheUtil;
    private final WorkerProperties.FeeCalculationProperties feeCalculationProperties;
    private final LarkRobotMonitor larkRobotMonitor;

    @Override
    public void handle(ServiceFeeCalculationSyncMsg msg) {
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.SERVICE_FEE_CALCULATION_SYNC, msg);
        try {
            transactionSyncService.syncHandle(msg.getBos());
        } catch (Exception e) {
            log.error(String.format("sqs timer execute error, queue: [key: %s]",
                    Constant.AWS_QUEUE_KEYS.SERVICE_FEE_CALCULATION_SYNC), e);
            final String key = String.format(Constant.CACHE.TOKENIZATION_SYNC_ERROR_COUNT,
                    msg.getSyncId());
            final int limitCount =
                    Optional.ofNullable(feeCalculationProperties.getSyncErrorWarnLimitCount())
                            .orElse(30);
            final int errorCount = Optional.ofNullable(redisCacheUtil.<Integer>getCacheObject(key))
                    .orElse(0) + 1;
            if (errorCount % limitCount == 0) {
                larkRobotMonitor.error("Data Warehouse Sync Warn",
                        "The S3 bucket 'service-fee-calculation-sync' msg backlog in.",
                        String.format("SyncId: %s", msg.getSyncId()));
            }
            redisCacheUtil.setCacheObject(key, errorCount, 30, TimeUnit.MINUTES);
            throw e;
        }
    }

    @Override
    public Class<ServiceFeeCalculationSyncMsg> messageType() {
        return ServiceFeeCalculationSyncMsg.class;
    }

}
