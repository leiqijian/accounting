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
 * MobileNo Validator
 */
public class MobileNoValidator implements ConstraintValidator<MobileNoField, String> {
    private static Validator validator;
    private MobileNoField annotation;

    public MobileNoValidator() {
        if (validator == null) {
            validator = Validation.byProvider(HibernateValidator.class).configure().failFast(true)
                    .parameterNameProvider(new ParanamerParameterNameProvider())
                    .buildValidatorFactory().getValidator();
        }
    }

    @Override
    public void initialize(MobileNoField constraintAnnotation) {
        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(final String str, final ConstraintValidatorContext context) {

        if (StringUtils.isBlank(str) || DataUtil.isMobile(str.trim())) {
            return true;
        }

        context.buildConstraintViolationWithTemplate(annotation.message()).addConstraintViolation();
        return false;
    }

}
