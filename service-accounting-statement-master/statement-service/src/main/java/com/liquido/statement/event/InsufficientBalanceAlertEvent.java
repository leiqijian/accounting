package com.liquido.statement.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Insufficient Balance Alert
 */
@Getter
public class InsufficientBalanceAlertEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public InsufficientBalanceAlertEvent(final Object source) {
        super(source);
    }

}
