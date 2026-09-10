package com.liquido.core.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;

import com.liquido.core.mvc.serializer.BigDecimalSerializer;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @see BigDecimalSerializer
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = BigDecimalSerializer.class)
public @interface BigDecimalFormat {

    /**
     * scale
     */
    int value() default 2;

    /**
     * rounding mode
     */
    RoundingMode roundingMode() default RoundingMode.HALF_UP;

}
