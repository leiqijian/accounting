package com.liquido.core.common.utils;

import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSupplier implements Supplier<ObjectMapper> {

    @Override
    public ObjectMapper get() {
        return JsonUtil.getObjectMapper();
    }
}
