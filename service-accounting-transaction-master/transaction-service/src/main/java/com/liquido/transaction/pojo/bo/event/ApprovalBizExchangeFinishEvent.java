package com.liquido.transaction.pojo.bo.event;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ApprovalBizExchangeFinishEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    private final ApprovalBizExchangeFinishEventBo eventArgs;

    public ApprovalBizExchangeFinishEvent(final ApprovalBizExchangeFinishEventBo eventArgs) {
        super(eventArgs);
        this.eventArgs = eventArgs;
    }
}
