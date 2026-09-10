package com.liquido.statement.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Triggered event after account daily-cut
 */
@Getter
public class DailyCutSuccessEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    private final DailyCutSuccessEventArgs eventArgs;

    public DailyCutSuccessEvent(final DailyCutSuccessEventArgs eventArgs) {
        super(eventArgs);
        this.eventArgs = eventArgs;
    }
}
