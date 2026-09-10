package com.liquido.statement.aws.sqs.consum;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.statement.aws.sqs.msg.TimerInternalAccountAutoTransferMsg;
import com.liquido.statement.common.Constant;
import com.liquido.statement.manage.InternalAccountTransferManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"statement.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerInternalAccountAutoTransferConsumer extends
        BaseSqsMessageHandler<TimerInternalAccountAutoTransferMsg> {

    private final InternalAccountTransferManager internalAccountTransferManager;

    @Override
    public void handle(final TimerInternalAccountAutoTransferMsg msg) {
        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.TIMER_INTERNAL_ACCOUNT_AUTO_TRANSFER, msg);

        internalAccountTransferManager.customizeProcessInternalAccountTransfer();
    }

    @Override
    public Class<TimerInternalAccountAutoTransferMsg> messageType() {
        return TimerInternalAccountAutoTransferMsg.class;
    }

}
