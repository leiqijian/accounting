package com.liquido.worker.aws.sqs.consum.register;

import com.liquido.core.aws.sqs.BaseConsumerRegister;
import com.liquido.worker.aws.sqs.consum.ServicePaymentLinkSyncConsumer;
import com.liquido.worker.aws.sqs.msg.ServicePaymentLinkSyncMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.properties.AwsSqsProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;

@Slf4j
@Component
@ConditionalOnProperty(name = {"worker.aws.sqs.queue.enable"}, havingValue = "true")
public class ServicePaymentLinkSyncConsumerRegister extends
        BaseConsumerRegister<ServicePaymentLinkSyncMsg> {

    @Autowired
    public ServicePaymentLinkSyncConsumerRegister(
            final ServicePaymentLinkSyncConsumer consumer,
            final SqsClient client,
            final ObjectMapper objectMapper,
            final AwsSqsProperties.SqsQueueInfoProperties infoProperties) {
        super(consumer, client, objectMapper, infoProperties.getQueues()
                .get(Constant.AWS_QUEUE_KEYS.SERVICE_PAYMENT_LINK_SYNC));
    }

}
