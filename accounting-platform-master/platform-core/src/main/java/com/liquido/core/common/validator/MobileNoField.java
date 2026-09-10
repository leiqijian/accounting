package com.liquido.core.common.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * MobileNoField
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MobileNoValidator.class)
public @interface MobileNoField {

    String message() default "Mobile number invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
