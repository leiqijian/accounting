package com.liquido.transaction.pojo.bo.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ApprovalBizBatchWithdrawalFinishEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final ApprovalBizBatchWithdrawalFinishEventBo eventArgs;

    public ApprovalBizBatchWithdrawalFinishEvent(final ApprovalBizBatchWithdrawalFinishEventBo bo) {
        super(bo);
        this.eventArgs = bo;
    }
}
