package com.liquido.worker.aws.sqs.consum;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.worker.aws.sqs.msg.ServiceShopifySyncMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.service.sync.ShopifySyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"worker.aws.sqs.queue.enable"}, havingValue = "true")
public class ServiceShopifySyncConsumer extends BaseSqsMessageHandler<ServiceShopifySyncMsg> {

    private final ShopifySyncService shopifySyncService;

    @Override
    public void handle(ServiceShopifySyncMsg msg) {
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.SERVICE_SHOPIFY_SYNC, msg);
        shopifySyncService.syncHandle(msg.getDtoList());
    }

    @Override
    public Class<ServiceShopifySyncMsg> messageType() {
        return ServiceShopifySyncMsg.class;
    }

}
