package com.liquido.core.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.parameternameprovider.ParanamerParameterNameProvider;

/**
 * Email Validator
 */
public class ColumnValidator implements ConstraintValidator<ColumnField, String> {

    private static final String COLUMN_REGEX = "^[a-zA-Z][a-zA-Z0-9_]{0,40}$";

    private static Validator validator;
    private ColumnField annotation;

    public ColumnValidator() {
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
    public void initialize(final ColumnField constraintAnnotation) {
        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(final String columnName, final ConstraintValidatorContext context) {

        if (StringUtils.isBlank(columnName) || columnName.matches(COLUMN_REGEX)) {
            return true;
        }

        context.buildConstraintViolationWithTemplate(annotation.message()).addConstraintViolation();
        return false;
    }
}
