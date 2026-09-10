package com.liquido.worker.aws.sqs.consum;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.worker.aws.sqs.msg.ServiceCardTokenizationSyncMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.service.sync.CardTokenizationSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"worker.aws.sqs.queue.enable"}, havingValue = "true")
public class ServiceTokenizationSyncConsumer
        extends BaseSqsMessageHandler<ServiceCardTokenizationSyncMsg> {

    private final CardTokenizationSyncService cardTokenizationSyncService;

    @Override
    public void handle(final ServiceCardTokenizationSyncMsg msg) {
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.SERVICE_TOKENIZATION_SYNC, msg);
        cardTokenizationSyncService.syncHandle(msg.getDtoList());
    }

    @Override
    public Class<ServiceCardTokenizationSyncMsg> messageType() {
        return ServiceCardTokenizationSyncMsg.class;
    }

}
