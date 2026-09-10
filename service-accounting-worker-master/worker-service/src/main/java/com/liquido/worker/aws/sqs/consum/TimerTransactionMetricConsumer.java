package com.liquido.worker.aws.sqs.consum;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.worker.aws.sqs.msg.TimerTransactionMetricMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.pojo.vo.SyncTransactionVo;
import com.liquido.worker.service.TransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"worker.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerTransactionMetricConsumer
        extends BaseSqsMessageHandler<TimerTransactionMetricMsg> {

    private final TransactionService transactionService;
    private final RedisDistLock redisDistLock;

    @Override
    public void handle(TimerTransactionMetricMsg msg) {

        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.TIMER_TRANSACTION_METRIC, msg);

        if (!redisDistLock.tryLock(Constant.CACHE.TRANSACTION_METRIC_LOCK,
                Constant.CACHE.TRANSACTION_METRIC_LOCK, 30000)) {
            return;
        }

        try {

            // Transaction amount/count metric
            transactionService.syncTransactionMetric(SyncTransactionVo.builder()
                    .typeCodeEnum(TransactionTypeCodeEnum.PAY_IN).build());

            // Transaction real-time success rate
            transactionService.syncTransactionRatio(SyncTransactionVo.builder()
                    .typeCodeEnum(TransactionTypeCodeEnum.PAY_IN).build());

            transactionService.syncTransactionMetric(SyncTransactionVo.builder()
                    .typeCodeEnum(TransactionTypeCodeEnum.PAY_OUT).build());

            transactionService.syncTransactionRatio(SyncTransactionVo.builder()
                    .typeCodeEnum(TransactionTypeCodeEnum.PAY_OUT).build());

        } finally {
            redisDistLock.unlock(Constant.CACHE.TRANSACTION_METRIC_LOCK,
                    Constant.CACHE.TRANSACTION_METRIC_LOCK);
        }

    }

    @Override
    public Class<TimerTransactionMetricMsg> messageType() {
        return TimerTransactionMetricMsg.class;
    }

}
