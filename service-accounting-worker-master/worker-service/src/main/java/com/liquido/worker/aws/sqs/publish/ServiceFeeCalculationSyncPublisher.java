package com.liquido.worker.aws.sqs.publish;

import com.liquido.starter.sqs.api.SqsMessagePublisher;
import com.liquido.worker.aws.sqs.msg.ServiceFeeCalculationSyncMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.properties.AwsSqsProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;

@Component
public class ServiceFeeCalculationSyncPublisher extends
        SqsMessagePublisher<ServiceFeeCalculationSyncMsg> {

    @Autowired
    public ServiceFeeCalculationSyncPublisher(
            final AwsSqsProperties.SqsQueueInfoProperties properties,
            final SqsClient sqsClient,
            final ObjectMapper objectMapper) {
        super(getQueryUrl(properties), sqsClient, objectMapper);
    }

    private static String getQueryUrl(final AwsSqsProperties.SqsQueueInfoProperties properties) {
        return properties.getQueues().get(Constant.AWS_QUEUE_KEYS.SERVICE_FEE_CALCULATION_SYNC)
                .getUrl();
    }

}
