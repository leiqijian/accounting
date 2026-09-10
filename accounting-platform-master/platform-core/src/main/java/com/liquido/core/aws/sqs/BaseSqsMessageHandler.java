package com.liquido.core.aws.sqs;

import java.util.Optional;

import com.liquido.core.common.logger.LogConstant;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.starter.sqs.api.SqsMessageHandler;

import org.slf4j.MDC;

public class BaseSqsMessageHandler<T> implements SqsMessageHandler<T> {

    @Override
    public void handle(T message) {

    }

    @Override
    public Class<T> messageType() {
        return null;
    }

    @Override
    public void onBeforeHandle(T message) {
        MDC.put(LogConstant.MESSAGE_ID, Optional.ofNullable(MDC.get(LogConstant.MESSAGE_ID))
                .orElse(DataUtil.getUuid()));
        MDC.put(LogConstant.TRACE_ID, Optional.ofNullable(MDC.get(LogConstant.TRACE_ID))
                .orElse(DataUtil.getUuid()));
    }

    @Override
    public void onAfterHandle(T message) {
        MDC.clear();
    }
}
