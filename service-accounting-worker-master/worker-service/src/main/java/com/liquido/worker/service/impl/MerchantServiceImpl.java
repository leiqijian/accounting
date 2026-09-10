package com.liquido.worker.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.BaseApis;
import com.liquido.base.pojo.dto.AccountProductDto;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.vo.ListAccountProductVo;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.statement.StatementApis;
import com.liquido.worker.common.Constant;
import com.liquido.worker.pojo.bo.MerchantAccountBo;
import com.liquido.worker.service.MerchantService;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
public class MerchantServiceImpl implements MerchantService {

    private final BaseApis.BaseFeign baseFeign;
    private final RedisCacheUtil redisCacheUtil;
    private final StatementApis.StatementFeign statementFeign;

    private final LoadingCache<String, MerchantAccountBo> merchantAccountLocalCache =
            Caffeine.newBuilder()
                    // Maximum number of caches
                    .maximumSize(800)
                    // Fixed time expires after last write
                    .expireAfterWrite(2, TimeUnit.MINUTES)
                    // If the value in the cache is empty or null, trigger reload from the database
                    .build(this::loadMerchantAccountInfo);

    @Override
    public MerchantAccountBo getMerchantAccountInfo(final String key) {
        return StringUtils.isBlank(key) ? null : merchantAccountLocalCache.get(key);
    }

    private MerchantAccountBo loadMerchantAccountInfo(final String key) {

        final MerchantAccountBo merchantAccountBo =
                redisCacheUtil.getCacheMapValue(Constant.CACHE.MERCHANT_ACCOUNT_INFO, key);

        if (Objects.nonNull(merchantAccountBo)) {
            return merchantAccountBo;
        }
        final Map<String, MerchantAccountBo> merchantAccountBoMap = this.loadMerchantAccountInfo();

        log.info("all account info size={}", merchantAccountBoMap.size());

        merchantAccountBoMap.forEach((mapKey, value) -> redisCacheUtil.setCacheMapValue(
                Constant.CACHE.MERCHANT_ACCOUNT_INFO, mapKey, value));

        return merchantAccountBoMap.get(key);
    }


    private Map<String, MerchantAccountBo> loadMerchantAccountInfo() {
        final Map<Long, MerchantDto> merchantDtoMap = baseFeign.queryAllMerchant().getData()
                .stream().collect(Collectors.toMap(MerchantDto::getId, Function.identity()));

        final Map<Long, List<AccountProductDto>> productMap =
                baseFeign.listAccountProduct(new ListAccountProductVo()).getData()
                        .stream().collect(Collectors.groupingBy(AccountProductDto::getAccountId));

        return statementFeign.queryAllAccountBasicInfo().getData().stream()
                .map(v -> MerchantAccountBo.builder()
                        .merchantId(v.getMerchantId())
                        .accountId(v.getId())
                        .merchantCode(merchantDtoMap.get(v.getMerchantId()).getCode())
                        .countryCode(v.getCountryCode())
                        .transactionTypeCode(v.getTransactionTypeCode())
                        .products(Optional.ofNullable(productMap.get(v.getId())).orElse(List.of())
                                .stream().collect(Collectors.toMap(
                                        p -> p.getProductCode().getCode(), Function.identity())))
                        .timezone(v.getTimezone())
                        .build())
                .collect(Collectors.toMap(v -> String.format("%s_%s_%s", v.getMerchantCode(),
                                v.getCountryCode().getCode(), v.getTransactionTypeCode().getCode()),
                        Function.identity()));
    }
}
