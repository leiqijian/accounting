package com.liquido.core.mvc.crypto;

/**
 * Signature Strategy
 */
public interface SignatureStrategy {

    SignAlgorithm getAlgorithm();

    /**
     * signature
     *
     * @param content content
     * @param sign    sign
     * @param secret  secret
     * @return boolean
     */
    boolean signature(final String content, final String secret, final String sign);
}
