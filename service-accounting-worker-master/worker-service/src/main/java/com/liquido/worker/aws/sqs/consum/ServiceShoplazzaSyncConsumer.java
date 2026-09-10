package com.liquido.worker.aws.sqs.consum;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.worker.aws.sqs.msg.ServiceShoplazzaSyncMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.service.sync.ShoplazzaSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"worker.aws.sqs.queue.enable"}, havingValue = "true")
public class ServiceShoplazzaSyncConsumer extends BaseSqsMessageHandler<ServiceShoplazzaSyncMsg> {

    private final ShoplazzaSyncService shoplazzaSyncService;

    @Override
    public void handle(ServiceShoplazzaSyncMsg msg) {
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.SERVICE_SHOPLAZZA_SYNC, msg);
        shoplazzaSyncService.syncHandle(msg.getDtoList());
    }

    @Override
    public Class<ServiceShoplazzaSyncMsg> messageType() {
        return ServiceShoplazzaSyncMsg.class;
    }

}
