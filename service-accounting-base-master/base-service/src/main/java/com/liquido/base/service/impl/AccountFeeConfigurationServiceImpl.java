package com.liquido.base.service.impl;

import static com.liquido.base.pojo.entity.QAccountFeeConfiguration.accountFeeConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.common.Constant;
import com.liquido.base.common.comparator.CalculationRuleComparator;
import com.liquido.base.common.properties.CalculationRuleProperties;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.exception.BaseExceptionCode;
import com.liquido.base.pojo.dto.AccountFeeConfigurationDto;
import com.liquido.base.pojo.entity.AccountFeeConfiguration;
import com.liquido.base.pojo.mapper.ModelMapper;
import com.liquido.base.pojo.vo.AccountFeeConfigurationVo;
import com.liquido.base.pojo.vo.BatchQueryProductFeeConfigInfoVo;
import com.liquido.base.pojo.vo.BatchQueryProductFeeMatchVo;
import com.liquido.base.pojo.vo.QueryFeeConfigInfoVo;
import com.liquido.base.pojo.vo.QueryProductCalculationRuleVo;
import com.liquido.base.pojo.vo.QueryProductFeeConfigInfoVo;
import com.liquido.base.pojo.vo.QueryProductFeeMatchGroupKey;
import com.liquido.base.pojo.vo.QueryProductFeeMatchVo;
import com.liquido.base.repository.AccountFeeConfigurationRepository;
import com.liquido.base.service.AccountFeeConfigurationService;
import com.liquido.base.service.monitor.LarkRobotMonitor;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;

import com.github.wenhao.jpa.Specifications;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountFeeConfigurationServiceImpl implements AccountFeeConfigurationService {

    private final AccountFeeConfigurationRepository accountFeeConfigurationRepository;
    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final ModelMapper modelMapper;
    private final CalculationRuleProperties calculationRuleProperties;
    private final LarkRobotMonitor larkRobotMonitor;

    @Override
    public List<AccountFeeConfigurationDto> saveAll(final List<AccountFeeConfigurationVo> listVo) {
        if (CollectionUtils.isEmpty(listVo)) {
            return Collections.emptyList();
        }
        final LocalDateTime now = LocalDateTimeUtil.nowUtc();
        final List<AccountFeeConfiguration> listBean = listVo.stream().map(x -> {
            final AccountFeeConfiguration bean = this.voToBean(x);
            // feeOn Default value AMOUNT
            bean.setFeeOn(Objects.nonNull(x.getFeeOn()) ? x.getFeeOn() : FeeOnEnum.AMOUNT);
            bean.setCreatedTime(now);
            bean.setUpdatedTime(now);
            return bean;
        }).collect(Collectors.toList());
        return toDto(accountFeeConfigurationRepository.saveAll(listBean));
    }

    @Override
    public List<AccountFeeConfigurationDto> matchProductFeeConfigs(
            final QueryProductFeeMatchVo vo) {

        // Ensure monthly volume is non-negative
        final BigDecimal monthlyVolume = vo.getMonthlyVolume().max(BigDecimal.ZERO);

        // query all cost configurations of a specified product
        final List<AccountFeeConfiguration> feeConfiguration =
                accountFeeConfigurationRepository.findAll(
                        Specifications.<AccountFeeConfiguration>and()
                                .in("accountProductId", vo.getAccountProductIds().toArray())
                                .eq("version", vo.getAccountFeeVersion())
                                .eq("monthlyVolumeType", vo.getMonthlyVolumeType())
                                .le("minMonthlyVolume", monthlyVolume)
                                .ge("maxMonthlyVolume", monthlyVolume).build());

        return toDto(feeConfiguration);
    }

    @Override
    public List<AccountFeeConfigurationDto> batchMatchProductFeeConfigs(
            final BatchQueryProductFeeMatchVo batchVo) {

        final Map<QueryProductFeeMatchGroupKey, List<Long>> groupedData =
                batchVo.getQueryVoList().stream().collect(Collectors.groupingBy(
                        x -> new QueryProductFeeMatchGroupKey(x.getAccountFeeVersion(),
                                x.getMonthlyVolumeType(), x.getMonthlyVolume()),
                        Collectors.flatMapping(vo -> vo.getAccountProductIds().stream(),
                                Collectors.toList())));

        final List<AccountFeeConfigurationDto> feeConfigurationList =
                groupedData.entrySet().stream().flatMap(entry -> {
                    final QueryProductFeeMatchGroupKey key = entry.getKey();
                    return matchProductFeeConfigs(
                            QueryProductFeeMatchVo.builder().accountProductIds(entry.getValue())
                                    .accountFeeVersion(key.getAccountFeeVersion())
                                    .monthlyVolumeType(key.getMonthlyVolumeType())
                                    .monthlyVolume(key.getMonthlyVolume()).build()).stream();
                }).collect(Collectors.toList());

        // Validate the fee configuration list
        validateFeeConfig(feeConfigurationList);

        return feeConfigurationList;
    }

    /**
     * Ensure a single fixed or percentage fee per tier in fee configuration.
     */
    private void validateFeeConfig(final List<AccountFeeConfigurationDto> feeConfiguration) {

        final Map<String, List<AccountFeeConfigurationDto>> feeConfigurationMap =
                feeConfiguration.stream().collect(Collectors.groupingBy(this::getKey));

        // Find duplicate fee configurations
        final List<AccountFeeConfigurationDto> duplicateFeeConfigList =
                feeConfigurationMap.values().stream().filter(configs -> configs.size() > 1)
                        .flatMap(List::stream).collect(Collectors.toList());

        // If duplicates are found, remove them and generate an alert
        if (!duplicateFeeConfigList.isEmpty()) {
            final List<Long> duplicateProductIds = duplicateFeeConfigList.stream()
                    .map(AccountFeeConfigurationDto::getAccountProductId).distinct()
                    .collect(Collectors.toList());

            feeConfiguration.removeAll(duplicateFeeConfigList);

            final String title = "Duplicate fee configuration found for product IDs";
            String info = "**ProductIds :** " + duplicateProductIds;
            larkRobotMonitor.warn(title, info, "");
        }
    }


    @Override
    public List<Map<String, String>> queryProductCalculationRules(
            final QueryProductCalculationRuleVo vo) {

        final List<Map<String, String>> calculationRuleList =
                jpaQueryFactory.select(accountFeeConfiguration.calculationRule)
                        .from(accountFeeConfiguration)
                        .where(accountFeeConfiguration.accountProductId.eq(vo.getAccountProductId())
                                .and(accountFeeConfiguration.version.eq(vo.getAccountFeeVersion())))
                        .distinct().fetch();

        // Remove duplicate calculationRule entries based on their content
        final List<Map<String, String>> uniqueCalculationRules =
                new ArrayList<>(new HashSet<>(calculationRuleList));

        // Sort the calculation rules according to the weight
        uniqueCalculationRules.sort(
                new CalculationRuleComparator(calculationRuleProperties.getWeights()));

        return uniqueCalculationRules;
    }


    @Override
    public List<AccountFeeConfigurationDto> queryProductFeeConfigInfo(
            final QueryProductFeeConfigInfoVo vo) {
        // Step 1: Get data from cache
        List<AccountFeeConfigurationDto> productFeeConfigList =
                redisCacheUtil.getCacheMapValue(Constant.CACHE.PRODUCT_FEE_CONFIG,
                        String.valueOf(vo.getAccountProductId()));

        // Step 2: If data exists in cache, return it directly
        if (CollectionUtils.isNotEmpty(productFeeConfigList)) {
            return filterValidProductFeeConfigList(productFeeConfigList, vo);
        }

        // Step 3: Get data from database
        productFeeConfigList = listFeeConfigInfo(
                QueryFeeConfigInfoVo.builder().accountProductId(vo.getAccountProductId()).build());

        // Step 4: Set Cache
        setRedisCache(productFeeConfigList);
        return filterValidProductFeeConfigList(productFeeConfigList, vo);
    }

    @Override
    public List<AccountFeeConfigurationDto> batchQueryProductFeeConfigInfo(
            final BatchQueryProductFeeConfigInfoVo batchVo) {
        // Step 1: Validate parameters
        validateParameters(batchVo);

        // Step 2: Get data from cache
        final Set<Long> productIds = getProductIdsFromVo(batchVo);
        final List<AccountFeeConfigurationDto> productFeeConfigCacheList =
                getProductFeeConfigFromCache(productIds);

        // If all data exists in cache, return it directly
        if (productFeeConfigCacheList.stream().map(AccountFeeConfigurationDto::getAccountProductId)
                .collect(Collectors.toSet()).containsAll(productIds)) {
            return filterValidProductFeeConfigList(productFeeConfigCacheList, batchVo);
        }

        // Step 3: Query data from the database for not in cache
        final List<Long> productIdsNotInCache =
                getProductIdsNotInCache(productIds, productFeeConfigCacheList);
        final List<AccountFeeConfigurationDto> productFeeConfigDbList = listFeeConfigInfo(
                QueryFeeConfigInfoVo.builder().accountProductIds(productIdsNotInCache).build());

        // Step 4: Set Cache
        setRedisCache(productFeeConfigDbList);

        // Step 5: Merge the data from cache and database
        productFeeConfigCacheList.addAll(productFeeConfigDbList);
        return filterValidProductFeeConfigList(productFeeConfigCacheList, batchVo);
    }


    private static void validateParameters(final BatchQueryProductFeeConfigInfoVo vo) {
        final List<QueryProductFeeConfigInfoVo> queryVoList = vo.getQueryVoList();

        validateDuplicateAccountProductIds(queryVoList);

        for (final QueryProductFeeConfigInfoVo query : queryVoList) {
            validateRequiredField(query.getAccountProductId(), "accountProductId");
            validateRequiredField(query.getAccountFeeVersion(), "accountFeeVersion");
        }
    }

    private static void validateDuplicateAccountProductIds(
            final List<QueryProductFeeConfigInfoVo> queryVoList) {
        final Set<Long> accountProductIds = new HashSet<>();
        for (QueryProductFeeConfigInfoVo query : queryVoList) {
            if (!accountProductIds.add(query.getAccountProductId())) {
                throw BaseExceptionCode.FIELD_DUPLICATED.exception("accountProductId");
            }
        }
    }

    private static void validateRequiredField(final Object field, final String fieldName) {
        if (field == null) {
            throw BaseExceptionCode.FIELD_REQUIRED.exception(fieldName);
        }
    }

    private static Set<Long> getProductIdsFromVo(final BatchQueryProductFeeConfigInfoVo vo) {
        return vo.getQueryVoList().stream().map(QueryProductFeeConfigInfoVo::getAccountProductId)
                .collect(Collectors.toSet());
    }

    private List<AccountFeeConfigurationDto> getProductFeeConfigFromCache(
            final Set<Long> productIds) {
        final List<String> productIdsAsString =
                productIds.stream().map(String::valueOf).collect(Collectors.toList());

        final List<List<AccountFeeConfigurationDto>> cacheListList =
                redisCacheUtil.getMultiCacheMapValue(Constant.CACHE.PRODUCT_FEE_CONFIG,
                        new ArrayList<>(productIdsAsString));

        return cacheListList.stream().filter(Objects::nonNull).flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Long> getProductIdsNotInCache(
            final Set<Long> productIds,
            final List<AccountFeeConfigurationDto> productFeeConfigCacheList) {

        final Set<Long> productIdsInCache = productFeeConfigCacheList.stream()
                .map(AccountFeeConfigurationDto::getAccountProductId).collect(Collectors.toSet());

        return productIds.stream().filter(x -> !productIdsInCache.contains(x))
                .collect(Collectors.toList());
    }

    private void setRedisCache(final List<AccountFeeConfigurationDto> productFeeConfigList) {
        // Group the fee configuration list by product ID
        final Map<String, List<AccountFeeConfigurationDto>> productFeeConfigMap =
                productFeeConfigList.stream()
                        .collect(Collectors.groupingBy(x -> x.getAccountProductId().toString()));

        // Set the fee configuration list into cache
        redisCacheUtil.setCacheMap(Constant.CACHE.PRODUCT_FEE_CONFIG, productFeeConfigMap);
        redisCacheUtil.expire(Constant.CACHE.PRODUCT_FEE_CONFIG, 1, TimeUnit.DAYS);
    }

    private List<AccountFeeConfigurationDto> filterValidProductFeeConfigList(
            final List<AccountFeeConfigurationDto> productFeeConfigList,
            final QueryProductFeeConfigInfoVo vo) {

        if (Objects.nonNull(vo.getAccountFeeVersion())) {
            productFeeConfigList.removeIf(
                    x -> !Objects.equals(x.getVersion(), vo.getAccountFeeVersion()));
        }

        final List<AccountFeeConfigurationDto> resultList =
                filterByCalculationRule(productFeeConfigList, vo);

        // If the result of the calculation attribute query is empty,
        // the default empty calculation attribute result is returned
        if (vo.getIsCalculationRule() && Objects.nonNull(vo.getCalculationRule())
                && !vo.getCalculationRule().isEmpty() && resultList.isEmpty()) {
            return groupByVersionMax(productFeeConfigList.stream()
                    .filter(x -> Objects.isNull(x.getCalculationRule())
                            || x.getCalculationRule().isEmpty()).collect(Collectors.toList()));
        }
        return groupByVersionMax(resultList);
    }

    private List<AccountFeeConfigurationDto> filterValidProductFeeConfigList(
            final List<AccountFeeConfigurationDto> productFeeConfigList,
            final BatchQueryProductFeeConfigInfoVo batchVo) {

        // Group the product fee configurations by accountProductId
        final Map<Long, List<AccountFeeConfigurationDto>> productFeeConfigMap =
                productFeeConfigList.stream().collect(
                        Collectors.groupingBy(AccountFeeConfigurationDto::getAccountProductId));

        // Create a map of queryList with accountProductId as key
        final Map<Long, QueryProductFeeConfigInfoVo> queryMap = batchVo.getQueryVoList().stream()
                .collect(Collectors.toMap(QueryProductFeeConfigInfoVo::getAccountProductId,
                        Function.identity(), (existingValue, newValue) -> existingValue));

        return productFeeConfigMap.entrySet().stream().flatMap(
                entry -> filterValidProductFeeConfigList(entry.getValue(),
                        queryMap.get(entry.getKey())).stream()).collect(Collectors.toList());
    }

    private List<AccountFeeConfigurationDto> filterByCalculationRule(
            final List<AccountFeeConfigurationDto> productFeeConfigList,
            final QueryProductFeeConfigInfoVo vo) {
        // Step 0: Return directly if calculation rule matching is not enabled
        if (!vo.getIsCalculationRule()) {
            return productFeeConfigList;
        }

        final Map<String, String> calculationRuleVo = vo.getCalculationRule();
        // Step 1: Return empty if query calc rule is null
        if (Objects.isNull(calculationRuleVo) || calculationRuleVo.isEmpty()) {
            return productFeeConfigList.stream()
                    .filter(x -> Objects.isNull(x.getCalculationRule())
                            || x.getCalculationRule().isEmpty()).collect(Collectors.toList());
        }

        // Step 2: Filter by query calc rule, remove empties
        List<AccountFeeConfigurationDto> filteredList = productFeeConfigList.stream()
                .filter(x -> Objects.nonNull(x.getCalculationRule())
                        && !x.getCalculationRule().isEmpty())
                // Step 2.2: Filter data matching calc rule
                .filter(x -> x.getCalculationRule().entrySet().stream().allMatch(
                        entry -> matchField(calculationRuleVo.get(entry.getKey()), entry.getValue(),
                                calculationRuleProperties.getComparer().get(entry.getKey()))))
                .collect(Collectors.toList());

        if (filteredList.isEmpty()) {
            return Collections.emptyList();
        }

        // Step 3: Get best-matching calc results
        final Integer maxAttributesCount =
                filteredList.stream().map(x -> x.getCalculationRule().size())
                        .max(Integer::compareTo).get();
        filteredList.removeIf(x -> x.getCalculationRule().size() != maxAttributesCount);
        return filteredList;
    }


    private List<AccountFeeConfigurationDto> listFeeConfigInfo(final QueryFeeConfigInfoVo vo) {
        if (Objects.isNull(vo)) {
            return Collections.emptyList();
        }
        return toDto(accountFeeConfigurationRepository.findAll(buildSpecification(vo)));
    }

    private Specification<AccountFeeConfiguration> buildSpecification(
            final QueryFeeConfigInfoVo vo) {
        return Specifications.<AccountFeeConfiguration>and()
                .eq(Objects.nonNull(vo.getAccountId()), "accountId", vo.getAccountId())
                .in(CollectionUtils.isNotEmpty(vo.getAccountIds()), "accountId",
                        ListUtils.emptyIfNull(vo.getAccountIds()).toArray())
                .eq(Objects.nonNull(vo.getAccountProductId()), "accountProductId",
                        vo.getAccountProductId())
                .eq(Objects.nonNull(vo.getAccountFeeVersion()), "version",
                        vo.getAccountFeeVersion())
                .in(CollectionUtils.isNotEmpty(vo.getAccountProductIds()), "accountProductId",
                        ListUtils.emptyIfNull(vo.getAccountProductIds()).toArray()).build();
    }

    private static boolean matchField(final String fieldVo, final String field,
                                      final String comparisonType) {
        if (Objects.nonNull(comparisonType)) {
            switch (comparisonType) {
                // Compares equality of fieldVo and field
                case Constant.COMPARISON.EQUALS:
                    return matchField(fieldVo, field);
                // Check if fieldVo in installment range
                case Constant.COMPARISON.INSTALLMENT_CONTAINS:
                    return Objects.isNull(fieldVo) ? Objects.isNull(field) :
                            (fieldVo.contains(",") ? fieldVo.equals(field) :
                                    Arrays.stream(field.split(",")).map(String::trim)
                                            .mapToInt(Integer::parseInt)
                                            .anyMatch(num -> num == Integer.parseInt(fieldVo)));
                default:
                    return false;
            }
        }
        // Default: direct equal comparison
        return matchField(fieldVo, field);
    }

    private static boolean matchField(final String fieldVo, final String field) {
        return Objects.isNull(fieldVo) ? Objects.isNull(field) : fieldVo.equals(field);
    }

    private List<AccountFeeConfigurationDto> groupByVersionMax(
            final List<AccountFeeConfigurationDto> feeConfiguration) {

        final Map<String, AccountFeeConfigurationDto> maxVersionMap = feeConfiguration.stream()
                .collect(Collectors.toMap(this::getKey, Function.identity(),
                        // Get the maximum version
                        BinaryOperator.maxBy(
                                Comparator.comparing(AccountFeeConfigurationDto::getVersion))));

        return new ArrayList<>(maxVersionMap.values());
    }

    private String getKey(final AccountFeeConfigurationDto fee) {
        return fee.getAccountProductId().toString()
                // Calculation rule
                + fee.getCalculationRule()
                // Direction type (SETTLED, REFUND, REJECTED, CHARGE_BACK)
                + fee.getDirectionType()
                // Fee based on (AMOUNT/FEE)
                + fee.getFeeOn()
                // Fee code
                + fee.getFeeCode()
                // Fee value model (FIXED, PERCENTAGE)
                + fee.getFeeValueModel()
                // Minimum monthly volume
                + fee.getMinMonthlyVolume()
                // Maximum monthly volume
                + fee.getMaxMonthlyVolume()
                // Minimum transaction amount
                + fee.getMinVolume()
                // Maximum transaction amount
                + fee.getMaxVolume()
                // Version
                + fee.getVersion();
    }

    private AccountFeeConfiguration voToBean(AccountFeeConfigurationVo vo) {
        final AccountFeeConfiguration bean = new AccountFeeConfiguration();
        BeanCopierUtil.copyProperties(vo, bean);
        return bean;
    }

    private AccountFeeConfigurationDto toDto(final AccountFeeConfiguration original) {
        return modelMapper.convert(original);
    }

    private List<AccountFeeConfigurationDto> toDto(
            final List<AccountFeeConfiguration> originalList) {
        return originalList.stream().map(this::toDto).collect(Collectors.toList());
    }

}
