package com.liquido.base.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.base.enums.DictionaryTypeEnum;
import com.liquido.base.exception.BaseExceptionCode;
import com.liquido.base.pojo.dto.DictionaryDto;
import com.liquido.base.pojo.entity.Dictionary;
import com.liquido.base.repository.DictionaryRepository;
import com.liquido.base.service.DictionaryService;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.mvc.serializer.EnumDeserializer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private final DictionaryRepository dictionaryRepository;

    @Override
    public <V> V queryDictValue(final DictionaryTypeEnum dictionaryType,
                                final Class<V> classType,
                                final String key,
                                final String... subTypes) {

        final String finalType = dictionaryType.getFinalType(subTypes);
        final Dictionary<String> dictionary = dictionaryRepository.findByTypeAndKey(finalType, key);
        return Optional.ofNullable(dictionary)
                .map(x -> parse(x, classType))
                .map(DictionaryDto::getValue)
                .orElse(null);
    }

    @Override
    public <V> Map<String, V> queryDictAllValue(final DictionaryTypeEnum dictionaryType,
                                                final Class<V> classType,
                                                final String... subTypes) {

        final String finalType = dictionaryType.getFinalType(subTypes);
        final List<Dictionary<String>> list = dictionaryRepository.findAllByType(finalType);
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }

        final List<DictionaryDto<V>> resultList = Lists.newArrayList();
        for (final Dictionary<String> dict : list) {
            final DictionaryDto<V> dto = new DictionaryDto<>();
            dto.setId(dict.getId());
            dto.setKey(dict.getKey());
            dto.setDelFlag(dict.getDelFlag());

            if (Enum.class.isAssignableFrom(classType)) {
                dto.setValue((V) EnumDeserializer.deserialize(classType, dict.getValue()));
            } else {
                dto.setValue(JsonUtil.toBean(dict.getValue(), classType));
            }

            resultList.add(dto);
        }

        return resultList.stream().collect(Collectors.toMap(DictionaryDto::getKey,
                DictionaryDto::getValue));
    }

    @Override
    public void addDictValue(final DictionaryTypeEnum dictionaryType,
                             final String key,
                             final String value,
                             final String[] subTypes) {
        try {
            final Dictionary<String> dictionary = new Dictionary<>();
            dictionary.setType(dictionaryType.getFinalType(subTypes));
            dictionary.setKey(key);
            dictionary.setValue(value);
            dictionaryRepository.save(dictionary);
        } catch (Exception e) {
            if (Objects.nonNull(
                    this.queryDictValue(dictionaryType, String.class, key, subTypes))) {
                throw BaseExceptionCode.DICTIONARY_DUPLICATE_KEY_ERROR.exception();
            }
            throw CommonExceptionCode.SYSTEM_ERROR.exception();
        }
    }

    @SuppressWarnings("unchecked")
    private <V> DictionaryDto<V> parse(final Dictionary<String> dictionary,
                                       final Class<V> classType) {
        final DictionaryDto<V> dto = new DictionaryDto<>();
        dto.setId(dictionary.getId());
        dto.setKey(dictionary.getKey());
        dto.setDelFlag(dictionary.getDelFlag());

        if (Enum.class.isAssignableFrom(classType)) {
            dto.setValue((V) EnumDeserializer.deserialize(classType, dictionary.getValue()));
        } else {
            dto.setValue(JsonUtil.toBean(dictionary.getValue(), classType));
        }

        return dto;
    }
}
