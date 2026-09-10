package com.liquido.statement.service.payment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.liquido.base.enums.PaymentChannelEnum;

import org.springframework.stereotype.Component;

@Component
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PaymentPayoutHandler {

    PaymentChannelEnum value();
}
