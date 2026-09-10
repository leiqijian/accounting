package com.liquido.statement.service.monitor;

public interface StatementMonitor {

    void monitor();

    default void manualPush() {

    }

}
