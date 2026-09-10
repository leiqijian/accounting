package com.liquido.core.common.logger;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * General log print switch
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoggerSwitch {

    /**
     * request log switch, true:ON, false=OFF
     */
    boolean request() default true;

    /**
     * response log switch, true:ON, false=OFF
     */
    boolean response() default true;
}
