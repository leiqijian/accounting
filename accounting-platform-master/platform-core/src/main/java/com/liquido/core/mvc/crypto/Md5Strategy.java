package com.liquido.core.mvc.crypto;

import lombok.extern.slf4j.Slf4j;

/**
 * Md5 Strategy
 */
@Slf4j
public class Md5Strategy implements SignatureStrategy {

    @Override
    public SignAlgorithm getAlgorithm() {
        return SignAlgorithm.MD5;
    }

    @Override
    public boolean signature(final String content, final String secret, final String sign) {

        // #################################################
        // ##################### TODO function to be implemented #############
        // #################################################

        return true;
    }

}
