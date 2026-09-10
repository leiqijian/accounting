package com.liquido.base.constant.dynamic;

import static com.liquido.base.enums.DictionaryTypeEnum.DYNAMIC_CONSTANT;
import static com.liquido.base.exception.BaseExceptionCode.DICTIONARY_DUPLICATE_KEY_ERROR;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.liquido.base.BaseApis;
import com.liquido.base.exception.BaseExceptionCode;
import com.liquido.base.pojo.vo.DynamicConstantCreateMonitorVo;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.SpringUtils;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DictionaryDynamicConstantDefinitionRegistry
        implements DynamicConstantDefinitionRegistry {

    private final Class<? extends DynamicConstant<?>> constantClass;

    private final BaseApis.BaseFeign baseFeign;

    private final DictValueCache dictValueCache;

    public DictionaryDynamicConstantDefinitionRegistry(
            final Class<? extends DynamicConstant<?>> constantClass) {
        this.constantClass = constantClass;
        this.baseFeign = SpringUtils.getBean(BaseApis.BaseFeign.class);
        this.dictValueCache = new DictValueCache();
    }

    @Override
    public DynamicConstantDefinition getDefinition(final String name) {
        final String dictValue = dictValueCache.queryDictValue(name);
        if (Objects.isNull(dictValue)) {
            return null;
        }
        final Map<String, String> properties =
                JsonUtil.toMap(dictValue, String.class, String.class);
        return new DynamicConstantDefinition(name, constantClass.getName(), properties);
    }

    @Override
    public void registerDefinition(final String name,
                                   final DynamicConstantDefinition definition) {
        final ResponseDto<Void> responseDto = baseFeign
                .addDictValue(DYNAMIC_CONSTANT, name,
                        JsonUtil.toJson(definition.getProperties()), constantClass.getSimpleName());

        if (Objects.isNull(responseDto)) {
            throw BaseExceptionCode.reException(null);
        }

        if (responseDto.isSuccess()) {
            log.info("registerDefinition success name={} definition={}", name, definition);
            registerDefinitionMonitor(name, definition);
            return;
        }

        if (responseDto.getCode().equals(DICTIONARY_DUPLICATE_KEY_ERROR.getCode())) {
            log.info("registerDefinition duplicate key name={} definition={}", name, definition);
            return;
        }

        throw BaseExceptionCode.reException(responseDto);
    }

    private void registerDefinitionMonitor(final String name,
                                           final DynamicConstantDefinition definition) {
        try {
            final String classSimpleName = constantClass.getSimpleName();
            final String code = JsonUtil.toJson(definition.getProperties().get("code"));
            baseFeign.dynamicConstantCreateMonitor(
                    new DynamicConstantCreateMonitorVo(classSimpleName, code));
        } catch (Exception e) {
            log.error(String.format("registerDefinitionMonitor error name=%s definition=%s",
                    name, definition), e);
        }
    }

    @Override
    public Set<String> getDefinitionNames() {
        return baseFeign
                .queryDictAllValue(DYNAMIC_CONSTANT, constantClass.getSimpleName())
                .keySet();
    }

    private class DictValueCache {

        private final Map<String, String> dictValues;

        private DictValueCache() {
            this.dictValues = baseFeign
                    .queryDictAllValue(DYNAMIC_CONSTANT, constantClass.getSimpleName())
                    .entrySet()
                    .stream()
                    .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        private String queryDictValue(final String dictKey) {
            String dictValue = dictValues.get(dictKey);
            if (Objects.nonNull(dictValue)) {
                return dictValue;
            }
            final String simpleName = constantClass.getSimpleName();
            dictValue = baseFeign.queryDictValue(DYNAMIC_CONSTANT, dictKey, simpleName);
            if (Objects.isNull(dictValue)) {
                return null;
            }
            dictValues.put(dictKey, dictValue);
            return dictValue;
        }
    }
}
