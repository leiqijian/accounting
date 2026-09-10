package com.liquido.base.cache;

import java.lang.reflect.Method;
import javax.annotation.Nonnull;

import com.liquido.base.enums.DictionaryTypeEnum;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

@Component("dictValueKeyGenerator")
public class DictValueKeyGenerator implements KeyGenerator {

    @Nonnull
    @Override
    public Object generate(@Nonnull final Object target,
                           @Nonnull final Method method,
                           final Object... params) {
        final DictionaryTypeEnum dictionaryType = (DictionaryTypeEnum) params[0];
        final Object key = params[2];
        String[] subTypes = new String[params.length - 3];
        for (int i = 3; i < params.length; i++) {
            subTypes[i - 3] = (String) params[i];
        }
        return "DICTIONARY:" + dictionaryType.getFinalType(subTypes) + ":" + key;
    }
}
