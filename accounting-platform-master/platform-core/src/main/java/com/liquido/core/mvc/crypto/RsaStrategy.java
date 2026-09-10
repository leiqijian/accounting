package com.liquido.core.mvc.crypto;

import lombok.extern.slf4j.Slf4j;

/**
 * Rsa Strategy
 */
@Slf4j
public class RsaStrategy implements SignatureStrategy {

    @Override
    public SignAlgorithm getAlgorithm() {
        return SignAlgorithm.RSA;
    }

    @Override
    public boolean signature(final String content, final String secret, final String sign) {

        // #################################################
        // ##################### TODO function to be implemented #############
        // #################################################

        return true;
    }

}
