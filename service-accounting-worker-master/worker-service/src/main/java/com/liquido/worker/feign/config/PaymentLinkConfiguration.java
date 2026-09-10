package com.liquido.worker.feign.config;

import com.liquido.worker.feign.PaymentLinkFeign;
import com.liquido.worker.pojo.bo.PaymentLinkResponse;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentLinkConfiguration implements FallbackFactory<PaymentLinkFeign> {

    private <T> PaymentLinkResponse<T> commonHandle(final Throwable cause) {
        return PaymentLinkResponse.<T>builder().code(500).message(cause.getMessage()).data(null)
                .build();
    }

    @Override
    public PaymentLinkFeign create(final Throwable cause) {
        return linkId -> commonHandle(cause);
    }
}
