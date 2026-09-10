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
public class PasswordValidator implements ConstraintValidator<PasswordField, String> {

    private static final int DIGITS = 1;
    private static final int LOWERCASE = 2;
    private static final int UPPERCASE = 3;
    private static final int SPECIAL_CHAR = 4;

    private static Validator validator;
    private PasswordField annotation;

    public PasswordValidator() {
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
    public void initialize(final PasswordField constraintAnnotation) {
        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {

        if (StringUtils.isBlank(password) || this.checkPassword(password)) {
            return true;
        }

        context.buildConstraintViolationWithTemplate(annotation.message()).addConstraintViolation();
        return false;
    }

    private static int checkCharacterType(final char c) {
        if (c >= 48 && c <= 57) {
            return DIGITS;
        }

        if (c >= 65 && c <= 90) {
            return UPPERCASE;
        }

        if (c >= 97 && c <= 122) {
            return LOWERCASE;
        }

        return SPECIAL_CHAR;
    }

    private static int countLetter(final String passwd, final int type) {
        int count = 0;
        if (null != passwd && passwd.length() > 0) {
            for (final char c : passwd.toCharArray()) {
                if (checkCharacterType(c) == type) {
                    count++;
                }
            }
        }
        return count;
    }


    private boolean checkPassword(final String password) {
        if (StringUtils.isBlank(password)
                || password.length() < annotation.minLength()
                || password.length() > annotation.maxLength()) {
            return false;
        }

        if (countLetter(password, DIGITS) <= 0) {
            return false;
        }

        if (countLetter(password, LOWERCASE) <= 0) {
            return false;
        }

        if (countLetter(password, UPPERCASE) <= 0) {
            return false;
        }

        /*if (countLetter(password, SPECIAL_CHAR) <= 0) {
            return false;
        }*/

        return true;
    }

}
