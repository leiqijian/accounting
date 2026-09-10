package com.liquido.core.common.validator;

import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.parameternameprovider.ParanamerParameterNameProvider;

/**
 * Default validator
 */
public class GlobalValidator {
    private static Validator validator;

    static {
        validator = Validation
                .byProvider(HibernateValidator.class)
                .configure().failFast(true)
                .parameterNameProvider(new ParanamerParameterNameProvider())
                .buildValidatorFactory()
                .getValidator();
    }

    public Validator getValidator() {
        return validator;
    }

}
