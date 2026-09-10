package com.liquido.core.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Validation;
import javax.validation.Validator;

import com.liquido.core.common.utils.DataUtil;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.parameternameprovider.ParanamerParameterNameProvider;

/**
 * Email Validator
 */
public class EmailValidator implements ConstraintValidator<EmailField, String> {
    private static Validator validator;
    private EmailField annotation;

    public EmailValidator() {
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
    public void initialize(EmailField constraintAnnotation) {
        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(final String str, final ConstraintValidatorContext context) {

        if (StringUtils.isBlank(str) || DataUtil.isEmail(str.trim())) {
            return true;
        }

        context.buildConstraintViolationWithTemplate(annotation.message()).addConstraintViolation();
        return false;
    }

}
