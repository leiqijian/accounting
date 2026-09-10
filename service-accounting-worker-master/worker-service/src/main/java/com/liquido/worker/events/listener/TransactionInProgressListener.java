package com.liquido.worker.events.listener;

import com.liquido.worker.aws.sqs.publish.ServiceTransactionInProgressSyncPublisher;
import com.liquido.worker.events.TransactionInProgressEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionInProgressListener {

    private final ServiceTransactionInProgressSyncPublisher
            serviceTransactionInProgressSyncPublisher;

    @Async("inProgressPublishSqsEventExecutor")
    @TransactionalEventListener(fallbackExecution = true)
    public void inProgressPublishSqsEvent(final TransactionInProgressEvent event) {
        log.info("[TransactionInProgressEvent] start publish transaction in progress event");
        serviceTransactionInProgressSyncPublisher.publish(event.getSyncMsg());
    }

}
