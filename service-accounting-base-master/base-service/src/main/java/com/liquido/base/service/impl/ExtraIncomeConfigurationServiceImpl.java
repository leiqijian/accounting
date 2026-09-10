package com.liquido.base.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.liquido.base.enums.CostTypeEnum;
import com.liquido.base.enums.ExtraFeeGroupEnum;
import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.base.pojo.entity.ExtraIncomeConfiguration;
import com.liquido.base.pojo.entity.QExtraIncomeConfiguration;
import com.liquido.base.pojo.mapper.ModelMapper;
import com.liquido.base.pojo.vo.QueryExtraIncomeConfigurationVo;
import com.liquido.base.service.CostConfigurationVersionService;
import com.liquido.base.service.ExtraIncomeConfigurationService;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ExtraIncomeConfigurationServiceImpl implements ExtraIncomeConfigurationService {
    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final CostConfigurationVersionService costConfigurationVersionService;

    private final LoadingCache<QueryExtraIncomeConfigurationVo, List<ExtraIncomeConfigurationDto>>
            localCache = Caffeine.newBuilder()
            // Maximum number of caches
            .maximumSize(1000)
            // Fixed time expires after last write
            .expireAfterWrite(1, TimeUnit.MINUTES)
            // If the value in the cache is empty or null, trigger reload from the database
            .build(this::loadExtraIncomeConfiguration);

    @Override
    public List<ExtraIncomeConfigurationDto> queryExtraIncomeConfiguration(
            final QueryExtraIncomeConfigurationVo vo) {
        return localCache.get(vo);
    }

    private List<ExtraIncomeConfigurationDto> loadExtraIncomeConfiguration(
            final QueryExtraIncomeConfigurationVo vo) {

        log.info("load extra-income config from db");
        final int activeVersion =
                costConfigurationVersionService.queryActiveConfig(CostTypeEnum.EXTRA_INCOME);

        final QExtraIncomeConfiguration entity = QExtraIncomeConfiguration.extraIncomeConfiguration;
        final List<ExtraIncomeConfiguration> customizeConfig = jpaQueryFactory.select(entity)
                .from(entity).where(entity.accountId.eq(vo.getAccountId())
                        .and(entity.activeVersion.eq(activeVersion))).fetch();

        final List<ExtraIncomeConfiguration> defaultConfig = jpaQueryFactory.select(entity)
                .from(entity).where(entity.accountId.eq(0L)
                        .and(entity.activeVersion.eq(activeVersion))
                        .and(entity.countryCode.eq(vo.getCountryCode()))
                        .and(entity.transactionTypeCode.eq(vo.getTransactionTypeCode())))
                .fetch();

        final Map<ExtraFeeGroupEnum, List<ExtraIncomeConfiguration>> customizeByFeeGroup =
                customizeConfig.stream()
                        .collect(Collectors.groupingBy(ExtraIncomeConfiguration::getFeeGroup));

        final Map<ExtraFeeGroupEnum, List<ExtraIncomeConfiguration>> defaultByFeeGroup =
                defaultConfig.stream()
                        .collect(Collectors.groupingBy(ExtraIncomeConfiguration::getFeeGroup));

        return Arrays.stream(ExtraFeeGroupEnum.values())
                .map(x -> customizeByFeeGroup.getOrDefault(x, defaultByFeeGroup.get(x)))
                .filter(CollectionUtils::isNotEmpty)
                .map(modelMapper::convertExtraFeeConfig)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
