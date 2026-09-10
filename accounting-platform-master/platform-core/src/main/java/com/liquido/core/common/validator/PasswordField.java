package com.liquido.core.common.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * EmailField
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordField {

    String message() default "The Password must contain both upper and lower case letters, digits";

    int minLength() default 8;

    int maxLength() default 32;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
