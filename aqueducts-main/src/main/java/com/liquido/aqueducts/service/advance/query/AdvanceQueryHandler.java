package com.liquido.aqueducts.service.advance.query;

import java.util.List;

import com.liquido.aqueducts.vo.request.TransactionAdvanceQueryRequest;

public interface AdvanceQueryHandler {

    // 2023-07-01 00:00:00 UTC+0
    long newPayInTableTimestamp = 1688169600000L;

    // 2024-01-01 00:00:00 UTC+0
    long newPayoutTableTimestamp = 1704067200000L;

    default boolean isAfterTimeLimit(final long timestamp, final long timeLimit) {
        return timestamp >= timeLimit;
    }

    default boolean isBeforeTimeLimit(final long timestamp, final long timeLimit) {
        return timestamp < timeLimit;
    }

    boolean support(TransactionAdvanceQueryRequest request);

    List<String> executeQuery(TransactionAdvanceQueryRequest request);

}
