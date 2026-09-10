package com.liquido.statement.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Insufficient Balance Alert
 */
@Getter
public class WithdrawalBalanceOverThresholdRemindEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public WithdrawalBalanceOverThresholdRemindEvent(final Object source) {
        super(source);
    }

}
