package com.liquido.base.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import com.liquido.base.common.properties.CalculationRuleProperties;
import com.liquido.base.enums.FeeCodeEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.exception.BaseExceptionCode;
import com.liquido.base.pojo.dto.AccountProductVersionDto;
import com.liquido.base.pojo.dto.MonthFxLoseConfigDto;
import com.liquido.base.pojo.dto.MonthlyAccountFeeConfigDto;
import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;
import com.liquido.base.pojo.dto.MonthlyProductFeeConfigDto;
import com.liquido.base.pojo.entity.AccountProduct;
import com.liquido.base.pojo.entity.MonthlyFeeConfiguration;
import com.liquido.base.pojo.mapper.ModelMapper;
import com.liquido.base.pojo.vo.BatchQueryProductMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.ListAccountMonthFxLoseVo;
import com.liquido.base.pojo.vo.MonthlyFeeConfigurationVo;
import com.liquido.base.pojo.vo.QueryAccountMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.QueryAccountProductVersionVo;
import com.liquido.base.pojo.vo.QueryMonthFeeConfigVo;
import com.liquido.base.pojo.vo.QueryProductMonthlyFeeConfigVo;
import com.liquido.base.repository.AccountProductRepository;
import com.liquido.base.repository.MonthlyFeeConfigurationRepository;
import com.liquido.base.service.AccountProductVersionService;
import com.liquido.base.service.MonthlyFeeConfigurationService;
import com.liquido.base.service.monitor.LarkRobotMonitor;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonthlyFeeConfigurationServiceImpl implements MonthlyFeeConfigurationService {
    private final AccountProductRepository accountProductRepository;
    private final MonthlyFeeConfigurationRepository monthlyFeeConfigurationRepository;
    private final RedisCacheUtil redisCacheUtil;
    private final AccountProductVersionService accountProductVersionService;
    private final ModelMapper modelMapper;
    private final CalculationRuleProperties calculationRuleProperties;
    private final LarkRobotMonitor larkRobotMonitor;

    @Override
    public List<MonthlyFeeConfigurationDto> saveAll(final List<MonthlyFeeConfigurationVo> listVo) {
        if (CollectionUtils.isEmpty(listVo)) {
            return Collections.emptyList();
        }
        final LocalDateTime now = LocalDateTimeUtil.nowUtc();
        final List<MonthlyFeeConfiguration> listBean = listVo.stream().map(x -> {
            final MonthlyFeeConfiguration bean = this.voToBean(x);
            bean.setCreatedTime(now);
            bean.setUpdatedTime(now);
            return bean;
        }).collect(Collectors.toList());
        return toDto(monthlyFeeConfigurationRepository.saveAll(listBean));
    }

    @Override
    public List<MonthlyFeeConfigurationDto> queryProductMonthFeeConfigInfo(
            final QueryProductMonthlyFeeConfigVo vo) {

        // Step 1: Get data from cache
        final String cacheKey = Constant.CACHE.MONTH_FEE_CONFIG + "-" + vo.getActiveMonth();
        List<MonthlyFeeConfigurationDto> monthlyFeeConfigList =
                redisCacheUtil.getCacheMapValue(cacheKey, String.valueOf(vo.getAccountProductId()));

        // Step 2: If data exists in cache, return it directly
        if (CollectionUtils.isNotEmpty(monthlyFeeConfigList)) {
            return filterValidMonthlyFeeConfigList(monthlyFeeConfigList, vo);
        }

        // Step 3: Get data from database
        monthlyFeeConfigList = listMonthlyFeeConfigInfo(
                QueryMonthFeeConfigVo.builder().activeMonth(vo.getActiveMonth())
                        .accountProductId(vo.getAccountProductId()).build());

        // Step 4: Set Cache
        setRedisCache(cacheKey, monthlyFeeConfigList);
        return filterValidMonthlyFeeConfigList(monthlyFeeConfigList, vo);
    }

    @Override
    public List<MonthlyFeeConfigurationDto> batchQueryProductMonthlyFeeConfigInfo(
            final BatchQueryProductMonthlyFeeConfigVo batchVo) {
        // Step 1: Validate parameters
        validateParameters(batchVo);

        // Step 2: Get data from cache
        final Set<Long> productIds = getProductIdsFromVo(batchVo);
        final String cacheKey = Constant.CACHE.MONTH_FEE_CONFIG + "-" + batchVo.getActiveMonth();
        final List<MonthlyFeeConfigurationDto> monthlyFeeConfigCacheList =
                getMonthlyFeeConfigFromCache(cacheKey, productIds);

        // If all data exists in cache, return it directly
        if (monthlyFeeConfigCacheList.stream().map(MonthlyFeeConfigurationDto::getAccountProductId)
                .collect(Collectors.toSet()).containsAll(productIds)) {
            return filterValidMonthlyFeeConfigList(monthlyFeeConfigCacheList, batchVo);
        }

        // Step 3: Query data from the database for not in cache
        final List<Long> productIdsNotInCache =
                getProductIdsNotInCache(productIds, monthlyFeeConfigCacheList);
        final List<MonthlyFeeConfigurationDto> monthlyFeeConfigDbList = listMonthlyFeeConfigInfo(
                QueryMonthFeeConfigVo.builder().activeMonth(batchVo.getActiveMonth())
                        .accountProductIds(productIdsNotInCache).build());

        // Step 4: Set Cache
        setRedisCache(cacheKey, monthlyFeeConfigDbList);

        // Step 5: Merge the data from cache and database
        monthlyFeeConfigCacheList.addAll(monthlyFeeConfigDbList);
        return filterValidMonthlyFeeConfigList(monthlyFeeConfigCacheList, batchVo);
    }

    /**
     * Query account monthly fee configuration(Excluding 'FX_ LOSE')
     */
    @Override
    public MonthlyAccountFeeConfigDto queryAccountMonthFeeConfigExclFxLose(
            final QueryAccountMonthlyFeeConfigVo vo) {

        log.info("query monthly account fee config request vo={}", vo);
        final LocalDate activeDate = vo.getActiveDate();
        int activeMonth = activeDate.getYear() * 100 + activeDate.getMonthValue();

        // Step1: Get Products for the given account.
        final var productList = accountProductRepository.findByAccountId(vo.getAccountId());
        // Step2: Get Product Versions specified date.
        final var productVersionMap = accountProductVersionService.listAccountProductVersion(
                QueryAccountProductVersionVo.builder().accountId(vo.getAccountId())
                        .activeDate(vo.getActiveDate()).build()).stream().collect(
                Collectors.toMap(AccountProductVersionDto::getAccountProductId,
                        Function.identity()));

        // Create queryVos containing product IDs and monthly fee versions.
        final var queryVoList = productList.stream()
                .map(product -> BatchQueryProductMonthlyFeeConfigVo.ProductMonthlyFeeConfigVo
                        .builder()
                        .accountProductId(product.getId())
                        // If the product has a version, use the version, otherwise use 1.
                        .monthlyFeeVersion(productVersionMap.containsKey(product.getId())
                                ? productVersionMap.get(product.getId()).getMonthlyFeeVersion() : 1)
                        .calculationRule(vo.getCalculationRule()).build())
                .collect(Collectors.toList());

        // Step3: Batch query monthly fee configurations
        var monthlyFeeConfigList = batchQueryProductMonthlyFeeConfigInfo(
                BatchQueryProductMonthlyFeeConfigVo.builder().activeMonth(activeMonth)
                        .queryVoList(queryVoList).build());

        // Step4: Remove FX_LOSE entries from the results.
        monthlyFeeConfigList.removeIf(x -> FeeTypeCodeEnum.FX_LOSE == x.getFeeTypeCode());

        // Step5: Assemble the final return data.
        final var monthlyProductFeeConfigList = monthlyFeeConfigList.stream()
                .collect(Collectors.groupingBy(MonthlyFeeConfigurationDto::getProductCode))
                .entrySet().stream()
                .map(entry -> new MonthlyProductFeeConfigDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // Step5: verify configuration（lark alarm）
        verifyMonthlyFeeConfig(monthlyProductFeeConfigList, vo);

        return new MonthlyAccountFeeConfigDto(monthlyProductFeeConfigList);
    }

    @Override
    public List<MonthFxLoseConfigDto> listAllAccountMonthFxLose(final ListAccountMonthFxLoseVo vo) {
        // Step 1: Query all products
        final List<AccountProduct> productList = accountProductRepository.findAll();
        final List<Long> productIds = productList.stream().map(AccountProduct::getId)
                .collect(Collectors.toList());

        // Step 2: Query all product versions
        final List<AccountProductVersionDto> productVersionList = new ArrayList<>();
        // Step 2.1: Group products by timezone and query product versions for each timezone
        productList.stream().collect(Collectors.groupingBy(AccountProduct::getTimezone)).forEach(
                (timeZone, products) -> {
                    final LocalDate activeDate = Objects.isNull(vo.getActiveTime())
                            ? LocalDate.now(ZoneId.of(timeZone))
                            : LocalDateTimeUtil.utcToLocal(
                                    vo.getActiveTime(), ZoneId.of(timeZone)).toLocalDate();
                    productVersionList.addAll(
                            accountProductVersionService.listAccountProductVersion(
                                    QueryAccountProductVersionVo.builder()
                                            .activeDate(activeDate).build()));
                });

        // Step 2.2: Filter productVersionList to keep only valid data in productList
        List<AccountProductVersionDto> validProductVersionList =
                productVersionList.stream().filter(productVersion ->
                                productIds.contains(productVersion.getAccountProductId()))
                        .collect(Collectors.toList());

        // Step 2.3: Send an alert for products without version configuration
        if (productList.size() > validProductVersionList.size()) {
            final Set<Long> validProductIds = validProductVersionList.stream().map(
                            AccountProductVersionDto::getAccountProductId)
                    .collect(Collectors.toSet());

            final List<AccountProduct> productsWithoutVersionConfig = productList.stream()
                    .filter(product -> !validProductIds.contains(product.getId()))
                    .collect(Collectors.toList());

            // Lark Alert
            if (CollectionUtils.isNotEmpty(productsWithoutVersionConfig)) {
                final String title = "Products without Version Configuration";
                final StringBuilder sb = new StringBuilder()
                        .append("**ProductList:** ").append(productsWithoutVersionConfig)
                        .append("\\n");
                larkRobotMonitor.error(title, sb.toString(), "");
            }
        }

        // Step 3: Query month FX_LOSE configuration for all products
        final List<MonthlyFeeConfigurationDto> monthlyFxLoseFeeConfigList = new ArrayList<>();
        // Group by monthly fee version
        final Map<Integer, List<AccountProductVersionDto>> versionProductsMap =
                validProductVersionList.stream()
                        .collect(Collectors.groupingBy(
                                AccountProductVersionDto::getMonthlyFeeVersion));

        for (Map.Entry<Integer, List<AccountProductVersionDto>> entry
                : versionProductsMap.entrySet()) {
            // Group by month and query
            entry.getValue().stream().collect(Collectors.groupingBy(
                            AccountProductVersionDto::getActiveMonth, Collectors.mapping(
                                    AccountProductVersionDto::getAccountProductId,
                                    Collectors.toList())))
                    .forEach((activeMonth, accountProductIds) -> monthlyFxLoseFeeConfigList.addAll(
                            this.listMonthlyFeeConfigInfo(QueryMonthFeeConfigVo.builder()
                                    .activeMonth(activeMonth)
                                    .monthlyFeeVersion(entry.getKey())
                                    .accountProductIds(accountProductIds)
                                    .feeCode(FeeCodeEnum.FX)
                                    .build())));
        }

        // Step 4: Convert to the account loss information return structure
        return monthlyFxLoseFeeConfigList.stream()
                .collect(Collectors.toMap(MonthlyFeeConfigurationDto::getAccountId,
                        Function.identity(), (existing, replacement) -> existing))
                .values().stream().map(monthlyFeeConfig -> {
                    final MonthFxLoseConfigDto bean = new MonthFxLoseConfigDto();
                    BeanCopierUtil.copyProperties(monthlyFeeConfig, bean);
                    return bean;
                })
                .sorted(Comparator.comparing(MonthFxLoseConfigDto::getAccountId))
                .collect(Collectors.toList());
    }


    private void verifyMonthlyFeeConfig(
            final List<MonthlyProductFeeConfigDto> monthlyProductFeeConfigList,
            final QueryAccountMonthlyFeeConfigVo vo) {

        // Ensure that only one unique calculation rule exists under the same product
        final List<ProductCodeEnum> productList = monthlyProductFeeConfigList.stream()
                .filter(feeConfigDto -> feeConfigDto.getProductFeeConfigList().stream()
                        .map(MonthlyFeeConfigurationDto::getCalculationRule).distinct().count() > 1)
                .map(MonthlyProductFeeConfigDto::getProductCode).collect(Collectors.toList());

        // Remove the product with multiple calculation rules
        monthlyProductFeeConfigList.removeIf(x -> productList.contains(x.getProductCode()));

        // lark alarm
        if (!productList.isEmpty()) {
            log.warn(
                    "Monthly fee configuration verification failed, accountId={}, activeDate={}, "
                            + "calculationRule={}, productList={}", vo.getAccountId(),
                    vo.getActiveDate(), vo.getCalculationRule(), productList);

            final String title = "Query account monthly fee configuration verification failed";
            final StringBuilder sb =
                    new StringBuilder().append("**AccountId :** ").append(vo.getAccountId())
                            .append("\\n").append("**ActiveDate:** ").append(vo.getActiveDate())
                            .append("\\n").append("**CalculationRule:** ")
                            .append(vo.getCalculationRule()).append("\\n")
                            .append("**ProductList:** ").append(productList).append("\\n");
            larkRobotMonitor.error(title, sb.toString(), "");
        }
    }

    public List<MonthlyFeeConfigurationDto> listMonthlyFeeConfigInfo(
            final QueryMonthFeeConfigVo vo) {

        final PredicateBuilder<MonthlyFeeConfiguration> spec =
                Specifications.<MonthlyFeeConfiguration>and()
                        .eq(Objects.nonNull(vo.getActiveMonth()), "activeMonth",
                                vo.getActiveMonth())
                        .eq(Objects.nonNull(vo.getAccountId()), "accountId", vo.getAccountId())
                        .in(CollectionUtils.isNotEmpty(vo.getAccountIds()), "accountId",
                                ListUtils.emptyIfNull(vo.getAccountIds()).toArray())
                        .eq(Objects.nonNull(vo.getAccountProductId()), "accountProductId",
                                vo.getAccountProductId())
                        .in(CollectionUtils.isNotEmpty(vo.getAccountProductIds()),
                                "accountProductId",
                                ListUtils.emptyIfNull(vo.getAccountProductIds()).toArray())
                        .eq(Objects.nonNull(vo.getMonthlyFeeVersion()), "version",
                                vo.getMonthlyFeeVersion())
                        .eq(Objects.nonNull(vo.getProductCode()), "productCode",
                                vo.getProductCode())
                        .eq(Objects.nonNull(vo.getFeeCode()), "feeCode", vo.getFeeCode());

        return toDto(monthlyFeeConfigurationRepository.findAll(spec.build()));
    }

    @Override
    public List<MonthlyFeeConfigurationDto> queryMonthlyFeeConfigurationByIds(
            final List<Long> ids) {
        final PredicateBuilder<MonthlyFeeConfiguration> spec =
                Specifications.<MonthlyFeeConfiguration>and()
                        .in(CollectionUtils.isNotEmpty(ids), "id",
                                ListUtils.emptyIfNull(ids).toArray())
                        .eq(true, "feeOn", FeeOnEnum.FEE);
        return toDto(monthlyFeeConfigurationRepository.findAll(spec.build()));
    }

    private static void validateParameters(final BatchQueryProductMonthlyFeeConfigVo vo) {
        final var queryVoList = vo.getQueryVoList();

        validateDuplicateAccountProductIds(queryVoList);

        for (final var query : queryVoList) {
            validateRequiredField(query.getAccountProductId(), "accountProductId");
            validateRequiredField(query.getMonthlyFeeVersion(), "monthlyFeeVersion");
        }
    }

    private static void validateDuplicateAccountProductIds(
            final List<BatchQueryProductMonthlyFeeConfigVo.ProductMonthlyFeeConfigVo> queryVoList) {
        final Set<Long> accountProductIds = new HashSet<>();
        for (BatchQueryProductMonthlyFeeConfigVo.ProductMonthlyFeeConfigVo query : queryVoList) {
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

    private static Set<Long> getProductIdsFromVo(final BatchQueryProductMonthlyFeeConfigVo vo) {
        return vo.getQueryVoList().stream().map(
                BatchQueryProductMonthlyFeeConfigVo.ProductMonthlyFeeConfigVo
                        ::getAccountProductId).collect(Collectors.toSet());
    }

    private List<MonthlyFeeConfigurationDto> getMonthlyFeeConfigFromCache(
            final String cacheKey,
            final Set<Long> productIds) {
        final List<String> productIdsAsString =
                productIds.stream().map(String::valueOf).collect(Collectors.toList());

        final List<List<MonthlyFeeConfigurationDto>> cacheListList =
                redisCacheUtil.getMultiCacheMapValue(cacheKey, new ArrayList<>(productIdsAsString));

        return cacheListList.stream().filter(Objects::nonNull).flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<Long> getProductIdsNotInCache(
            final Set<Long> productIds,
            final List<MonthlyFeeConfigurationDto> productFeeConfigCacheList) {
        final Set<Long> productIdsInCache = productFeeConfigCacheList.stream()
                .map(MonthlyFeeConfigurationDto::getAccountProductId).collect(Collectors.toSet());

        return productIds.stream().filter(x -> !productIdsInCache.contains(x))
                .collect(Collectors.toList());
    }

    private void setRedisCache(final String cacheKey,
                               final List<MonthlyFeeConfigurationDto> monthlyFeeConfigList) {
        // Group the fee configuration list by product ID
        final Map<String, List<MonthlyFeeConfigurationDto>> monthlyFeeConfigMap =
                monthlyFeeConfigList.stream()
                        .collect(Collectors.groupingBy(x -> x.getAccountProductId().toString()));

        // Set the fee configuration list into cache
        redisCacheUtil.setCacheMap(cacheKey, monthlyFeeConfigMap);
        redisCacheUtil.expire(cacheKey, 1, TimeUnit.DAYS);
    }


    private List<MonthlyFeeConfigurationDto> filterValidMonthlyFeeConfigList(
            final List<MonthlyFeeConfigurationDto> monthlyFeeConfigList,
            final QueryProductMonthlyFeeConfigVo vo) {

        if (Objects.nonNull(vo.getMonthlyFeeVersion())) {
            monthlyFeeConfigList.removeIf(
                    x -> !Objects.equals(x.getVersion(), vo.getMonthlyFeeVersion()));
        }

        final List<MonthlyFeeConfigurationDto> resultList =
                filterByCalculationRule(monthlyFeeConfigList, vo);

        // If the result of the calculation attribute query is empty,
        // the default empty calculation attribute result is returned
        if (vo.getIsCalculationRule() && Objects.nonNull(vo.getCalculationRule())
                && !vo.getCalculationRule().isEmpty() && resultList.isEmpty()) {
            return groupByVersionMax(monthlyFeeConfigList.stream()
                    .filter(x -> Objects.isNull(x.getCalculationRule())
                            || x.getCalculationRule().isEmpty()).collect(Collectors.toList()));
        }
        return groupByVersionMax(resultList);
    }

    private List<MonthlyFeeConfigurationDto> filterValidMonthlyFeeConfigList(
            final List<MonthlyFeeConfigurationDto> monthlyFeeConfigList,
            final BatchQueryProductMonthlyFeeConfigVo batchVo) {

        // Group the monthly fee configurations by accountProductId
        final Map<Long, List<MonthlyFeeConfigurationDto>> monthlyFeeConfigMap =
                monthlyFeeConfigList.stream().collect(
                        Collectors.groupingBy(MonthlyFeeConfigurationDto::getAccountProductId));

        // Create a map of queryList with accountProductId as key
        final Map<Long, QueryProductMonthlyFeeConfigVo> queryMap = batchVo.getQueryVoList().stream()
                .collect(Collectors.toMap(
                        BatchQueryProductMonthlyFeeConfigVo.ProductMonthlyFeeConfigVo
                                ::getAccountProductId,
                        v -> {
                            QueryProductMonthlyFeeConfigVo queryVo =
                                    new QueryProductMonthlyFeeConfigVo();
                            queryVo.setActiveMonth(batchVo.getActiveMonth());
                            queryVo.setAccountProductId(v.getAccountProductId());
                            queryVo.setMonthlyFeeVersion(v.getMonthlyFeeVersion());
                            queryVo.setCalculationRule(v.getCalculationRule());
                            return queryVo;
                        }, (existingValue, newValue) -> existingValue));

        return monthlyFeeConfigMap.entrySet().stream().flatMap(
                entry -> filterValidMonthlyFeeConfigList(entry.getValue(),
                        queryMap.get(entry.getKey())).stream()).collect(Collectors.toList());
    }

    private List<MonthlyFeeConfigurationDto> filterByCalculationRule(
            final List<MonthlyFeeConfigurationDto> monthlyFeeConfigList,
            final QueryProductMonthlyFeeConfigVo vo) {
        // Step 0: Return directly if calculation rule matching is not enabled
        if (!vo.getIsCalculationRule()) {
            return monthlyFeeConfigList;
        }

        final Map<String, String> calculationRuleVo = vo.getCalculationRule();
        // Step 1: Return empty if query calc rule is null
        if (Objects.isNull(calculationRuleVo) || calculationRuleVo.isEmpty()) {
            return monthlyFeeConfigList.stream()
                    .filter(x -> Objects.isNull(x.getCalculationRule())
                            || x.getCalculationRule().isEmpty()).collect(Collectors.toList());
        }

        // Step 2: Filter by query calc rule, remove empties
        List<MonthlyFeeConfigurationDto> filteredList = monthlyFeeConfigList.stream()
                .filter(dto -> Objects.nonNull(dto.getCalculationRule())
                        && !dto.getCalculationRule().isEmpty())
                // Step 2.2: Filter data matching calc rule
                .filter(dto -> dto.getCalculationRule().entrySet().stream().allMatch(
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

    private List<MonthlyFeeConfigurationDto> groupByVersionMax(
            final List<MonthlyFeeConfigurationDto> listMonthFeeConfig) {

        final Map<String, MonthlyFeeConfigurationDto> maxVersionMap = listMonthFeeConfig.stream()
                .collect(Collectors.toMap(this::getKey, Function.identity(), BinaryOperator.maxBy(
                        Comparator.comparing(MonthlyFeeConfigurationDto::getVersion))));

        return new ArrayList<>(maxVersionMap.values());
    }

    private String getKey(final MonthlyFeeConfigurationDto fee) {
        return fee.getAccountProductId().toString()
                // ActiveMonth
                + fee.getActiveMonth()
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
                // Minimum transaction amount
                + fee.getMinVolume()
                // Maximum transaction amount
                + fee.getMaxVolume();
    }

    private MonthlyFeeConfiguration voToBean(MonthlyFeeConfigurationVo vo) {
        final MonthlyFeeConfiguration bean = new MonthlyFeeConfiguration();
        BeanCopierUtil.copyProperties(vo, bean);
        return bean;
    }

    private MonthlyFeeConfigurationDto toDto(final MonthlyFeeConfiguration original) {
        return modelMapper.convert(original);
    }

    private List<MonthlyFeeConfigurationDto> toDto(
            final List<MonthlyFeeConfiguration> originalList) {
        return originalList.stream().map(this::toDto).collect(Collectors.toList());
    }

}
