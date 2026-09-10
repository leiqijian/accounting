package com.liquido.core.common.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {TimezoneValidator.class})
public @interface TimezoneField {

    String message() default "timezone input parameter error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
