package com.liquido.core.common.validator;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.parameternameprovider.ParanamerParameterNameProvider;

public class TimezoneValidator implements ConstraintValidator<TimezoneField, String> {

    private static Validator validator;
    private TimezoneField annotation;
    private static final List<String> TIMEZONE_LIST =
            List.of("UTC-12", "UTC-11", "UTC-10", "UTC-9", "UTC-8", "UTC-7", "UTC-6", "UTC-5",
                    "UTC-4", "UTC-3", "UTC-2", "UTC-1", "UTC+0", "UTC+1", "UTC+2", "UTC+3",
                    "UTC+4", "UTC+5", "UTC+6", "UTC+7", "UTC+8", "UTC+9", "UTC+10", "UTC+11",
                    "UTC+12");

    public TimezoneValidator() {
        if (validator == null) {
            validator = Validation
                    .byProvider(HibernateValidator.class)
                    .configure()
                    .failFast(true)
                    .parameterNameProvider(new ParanamerParameterNameProvider())
                    .buildValidatorFactory().getValidator();
        }
    }

    @Override
    public void initialize(final TimezoneField constraintAnnotation) {
        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(final String timezone, final ConstraintValidatorContext context) {
        if (StringUtils.isBlank(timezone) || TIMEZONE_LIST.contains(timezone)) {
            return true;
        }

        context.buildConstraintViolationWithTemplate(annotation.message()).addConstraintViolation();
        return false;

    }

}
