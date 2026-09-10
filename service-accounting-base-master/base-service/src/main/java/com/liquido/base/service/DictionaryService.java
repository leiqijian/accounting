package com.liquido.base.service;

import java.util.Map;

import com.liquido.base.enums.DictionaryTypeEnum;

public interface DictionaryService {

    <V> V queryDictValue(final DictionaryTypeEnum dictionaryType,
                         final Class<V> classType,
                         final String key,
                         final String... subTypes);

    <V> Map<String, V> queryDictAllValue(final DictionaryTypeEnum dictionaryType,
                                         final Class<V> classType,
                                         final String... subTypes);

    void addDictValue(final DictionaryTypeEnum dictionaryType,
                      final String key,
                      final String value,
                      final String[] subTypes);
}
