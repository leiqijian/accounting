package com.liquido.statement.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.statement.StatementApplication;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@RequiredArgsConstructor
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = StatementApplication.class)
public class RedisCacheTest {

    @Autowired
    RedisCacheUtil redisCacheUtil;

    @Test
    void testCache() {

        final Long merchantId = 10001L;
        final String cacheKey = String.format("%s:%s", "SUB_MERCHANT", merchantId);

        List<SubMerchantDto> dataList = Lists.newArrayList(SubMerchantDto.builder()
                        .merchantId(merchantId)
                        .subMerchantId("SUB-001")
                        .merchantCode("SC001")
                        .build(),
                SubMerchantDto.builder()
                        .merchantId(merchantId)
                        .subMerchantId("SUB-002")
                        .merchantCode("SC002")
                        .build(),
                SubMerchantDto.builder()
                        .merchantId(merchantId)
                        .subMerchantId("SUB-003")
                        .merchantCode("SC003")
                        .build());

        List<SubMerchantDto> dataList2 = Lists.newArrayList(SubMerchantDto.builder()
                        .merchantId(merchantId)
                        .subMerchantId("SUB-004")
                        .merchantCode("SC005")
                        .build(),
                SubMerchantDto.builder()
                        .merchantId(merchantId)
                        .subMerchantId("SUB-005")
                        .merchantCode("SC005")
                        .build());
        final Map<String, SubMerchantDto> dtoMap = dataList.stream()
                .collect(Collectors.toMap(
                        SubMerchantDto::getSubMerchantId,
                        Function.identity(), (x, y) -> y));

        final Map<String, SubMerchantDto> dtoMap2 = dataList2.stream()
                .collect(Collectors.toMap(
                        SubMerchantDto::getSubMerchantId,
                        Function.identity(), (x, y) -> y));


        redisCacheUtil.setCacheMap(cacheKey, dtoMap);
        redisCacheUtil.setCacheMap(cacheKey, dtoMap);
        redisCacheUtil.setCacheMap(cacheKey, dtoMap2);

        List<SubMerchantDto> resultList = redisCacheUtil.getMultiCacheMapValue(cacheKey,
                Lists.newArrayList("SUB-004", "SUB-005"));

        System.out.println("SUB-Merchant:" + JsonUtil.toJson(resultList));
    }

}
