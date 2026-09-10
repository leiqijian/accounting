package com.liquido.statement.aws.sqs.publish;

import com.liquido.starter.sqs.api.SqsMessagePublisher;
import com.liquido.statement.aws.sqs.msg.TimerWithdrawalBalanceOverThresholdRemindMsg;
import com.liquido.statement.common.Constant;
import com.liquido.statement.common.properties.AwsSqsProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;

@Component
public class TimerWithdrawalBalanceOverThresholdRemindPublisher
        extends SqsMessagePublisher<TimerWithdrawalBalanceOverThresholdRemindMsg> {

    @Autowired
    public TimerWithdrawalBalanceOverThresholdRemindPublisher(
            final AwsSqsProperties.SqsQueueInfoProperties properties,
            final SqsClient sqsClient,
            final ObjectMapper objectMapper) {

        super(getQueryUrl(properties), sqsClient, objectMapper);
    }

    private static String getQueryUrl(final AwsSqsProperties.SqsQueueInfoProperties properties) {
        return properties.getQueues()
                .get(Constant.AWS_QUEUE_KEYS.TIMER_BALANCE_OVER_THRESHOLD_REMIND).getUrl();
    }
}
