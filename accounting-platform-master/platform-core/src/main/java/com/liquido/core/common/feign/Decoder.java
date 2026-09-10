package com.liquido.core.common.feign;

import java.io.IOException;
import java.lang.reflect.Type;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;


public interface Decoder {

    Object decode(final Response response, final Type type)
            throws IOException, DecodeException, FeignException;
}
