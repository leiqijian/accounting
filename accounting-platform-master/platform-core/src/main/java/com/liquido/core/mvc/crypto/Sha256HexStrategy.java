package com.liquido.core.mvc.crypto;

import com.liquido.core.common.security.Sha256Util;

import lombok.extern.slf4j.Slf4j;

/**
 * Sha256Hex Strategy
 */
@Slf4j
public class Sha256HexStrategy implements SignatureStrategy {

    @Override
    public SignAlgorithm getAlgorithm() {
        return SignAlgorithm.HmacSHA256;
    }

    @Override
    public boolean signature(final String content, final String secret, final String sign) {
        return Sha256Util.checkSignature(content, secret, sign);
    }
}
