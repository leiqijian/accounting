package com.liquido.statement.service.payment;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.statement.exception.StatementExceptionCode;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * PayIn or Payout settlement provider factory
 */
@Slf4j
@Component
public class PaymentPayoutFactory {

    private static final Map<PaymentChannelEnum, PaymentPayoutService> providerMap =
            Maps.newConcurrentMap();

    private PaymentPayoutFactory(final ObjectProvider<List<PaymentPayoutService>> provider) {
        // Use interface
        final List<PaymentPayoutService> strategyList = provider.getIfAvailable();
        if (CollectionUtils.isEmpty(strategyList)) {
            return;
        }

        for (final PaymentPayoutService handler : strategyList) {
            if (Objects.nonNull(handler.getPayoutChannel())) {
                providerMap.put(handler.getPayoutChannel(), handler);
            } else {
                // Use customize annotation
                final PaymentPayoutHandler annotationHandler =
                        AnnotationUtils.findAnnotation(handler.getClass(),
                                PaymentPayoutHandler.class);
                if (Objects.nonNull(annotationHandler)) {
                    providerMap.put(annotationHandler.value(), handler);
                }
            }
        }
    }

    public <T extends PaymentPayoutService> T getProviderFactory(final String paymentChannel) {
        if (StringUtils.isBlank(paymentChannel)) {
            throw StatementExceptionCode.PAYMENT_CHANNEL_UNDEFINED.exception();
        }

        return this.getProviderFactory(PaymentChannelEnum.parse(paymentChannel));
    }

    public <T extends PaymentPayoutService> T getProviderFactory(
            final PaymentChannelEnum paymentChannel) {
        if (paymentChannel == null) {
            throw StatementExceptionCode.PAYMENT_CHANNEL_UNDEFINED.exception();
        }

        return (T) providerMap.get(paymentChannel);
    }
}
