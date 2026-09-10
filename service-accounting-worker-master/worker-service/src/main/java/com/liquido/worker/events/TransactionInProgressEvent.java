package com.liquido.worker.events;

import com.liquido.worker.aws.sqs.msg.ServiceTransactionInProgressSyncMsg;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransactionInProgressEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final ServiceTransactionInProgressSyncMsg syncMsg;

    public TransactionInProgressEvent(final ServiceTransactionInProgressSyncMsg syncMsg) {
        super(syncMsg);
        this.syncMsg = syncMsg;
    }

}
