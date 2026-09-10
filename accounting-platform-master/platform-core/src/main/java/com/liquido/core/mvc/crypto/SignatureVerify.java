package com.liquido.core.mvc.crypto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signature Verify
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SignatureVerify {

    /**
     * algorithm
     *
     * @return
     */
    SignAlgorithm algorithm() default SignAlgorithm.HmacSHA256;

    /**
     * assess partner
     *
     * @return
     */
    String assessPartner() default "default";
}
