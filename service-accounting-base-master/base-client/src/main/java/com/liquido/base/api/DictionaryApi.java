package com.liquido.base.api;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.DictionaryTypeEnum;
import com.liquido.base.feign.QueryDictAllValueDecoder;
import com.liquido.base.feign.QueryDictValueDecoder;
import com.liquido.core.common.feign.FeignDecoder;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface DictionaryApi {

    @Cacheable(cacheNames = "LOCAL:1d#REDIS:3d", keyGenerator = "dictValueKeyGenerator")
    @FeignDecoder(QueryDictValueDecoder.class)
    @PostMapping("/base/dict/value/query")
    <V> V queryDictValue(
            @NotNull @RequestParam("dictionaryType") final DictionaryTypeEnum dictionaryType,
            @NotNull @RequestParam("classType") final Class<V> classType,
            @NotBlank @RequestParam("key") final String key,
            @RequestParam(value = "subTypes", required = false) final String... subTypes);

    @FeignDecoder(QueryDictAllValueDecoder.class)
    @PostMapping("/base/dict/all/value/query")
    <V> Map<String, V> queryDictAllValue(
            @NotNull @RequestParam("dictionaryType") final DictionaryTypeEnum dictionaryType,
            @NotNull @RequestParam("classType") final Class<V> classType,
            @RequestParam(value = "subTypes", required = false) final String... subTypes);

    @PostMapping("/base/dict/value/add")
    ResponseDto<Void> addDictValue(
            @NotNull @RequestParam("dictionaryType") final DictionaryTypeEnum dictionaryType,
            @NotBlank @RequestParam("key") final String key,
            @NotBlank @RequestParam("value") final String value,
            @RequestParam(value = "subTypes", required = false) final String... subTypes);
}
