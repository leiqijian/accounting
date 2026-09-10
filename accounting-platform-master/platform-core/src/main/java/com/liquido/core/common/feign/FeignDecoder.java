package com.liquido.core.common.feign;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * feign decoder for method
 *
 * @see MethodFeignDecoder
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeignDecoder {

    Class<? extends Decoder> value();
}
