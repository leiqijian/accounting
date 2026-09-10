package com.liquido.worker.feign;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.BaseApis;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.AccountProductDto;
import com.liquido.base.pojo.dto.ApmCostConfigDto;
import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.dto.CardCostConfigDto;
import com.liquido.base.pojo.dto.CardCostConfigurationDto;
import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.MonthFxLoseConfigDto;
import com.liquido.base.pojo.dto.MonthlyAccountFeeConfigDto;
import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;
import com.liquido.base.pojo.dto.MonthlyProductFeeConfigDto;
import com.liquido.base.pojo.dto.WorkingDayDto;
import com.liquido.base.pojo.vo.ListAccountMonthFxLoseVo;
import com.liquido.base.pojo.vo.ListAccountProductVo;
import com.liquido.base.pojo.vo.QueryAccountMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.QueryApmCostConfigVo;
import com.liquido.base.pojo.vo.QueryExtraIncomeConfigurationVo;
import com.liquido.base.pojo.vo.QueryMerchantVo;
import com.liquido.base.pojo.vo.QueryWorkingDayVo;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.security.Md5Util;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.pojo.bo.AccountProductKey;
import com.liquido.worker.pojo.bo.MonthlyFeeConfigurationBo;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.vo.ApmCostConfigVo;
import com.liquido.worker.pojo.vo.CardCostConfigVo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseService {

    private final BaseApis.BaseFeign baseFeign;
    private final RedisCacheUtil redisCacheUtil;
    private final WorkerProperties workerProperties;
    private final LarkRobotMonitor larkRobotMonitor;

    private List<DirectionTypeEnum> NON_SETTLEMENT_LIST = Lists.newArrayList(
            DirectionTypeEnum.REJECTED,
            DirectionTypeEnum.CHARGE_BACK,
            DirectionTypeEnum.CHARGE_BACK_REJECTED,
            DirectionTypeEnum.REFUND);

    private final LoadingCache<AccountProductKey, AccountProductDto> accountProductLocalCache =
            Caffeine.newBuilder()
                    // Maximum number of caches
                    .maximumSize(1000)
                    // Fixed time expires after last write
                    .expireAfterWrite(2, TimeUnit.MINUTES)
                    // If the value in the cache is empty or null, trigger reload from the database
                    .build(this::loadAccountProductConfig);

    private final LoadingCache<String, List<CardCostConfigDto>> cardCostLocalCache =
            Caffeine.newBuilder()
                    // Maximum number of caches
                    .maximumSize(10000)
                    // Fixed time expires after last write
                    .expireAfterWrite(15, TimeUnit.MINUTES)
                    // If the value in the cache is empty or null, trigger reload from the database
                    .build(this::loadCardCostConfig);

    private final LoadingCache<String, List<ApmCostConfigDto>> apmCostLocalCache =
            Caffeine.newBuilder()
                    // Maximum number of caches
                    .maximumSize(10000)
                    // Fixed time expires after last write
                    .expireAfterWrite(15, TimeUnit.MINUTES)
                    // If the value in the cache is empty or null, trigger reload from the database
                    .build(this::loadApmCostConfig);

    public List<MonthFxLoseConfigDto> listAllAccountMonthFxLose(final ListAccountMonthFxLoseVo vo) {
        final ResponseDto<List<MonthFxLoseConfigDto>> responseDto =
                baseFeign.listAllAccountMonthFxLose(vo);

        if (ResponseDto.isFail(responseDto)) {
            throw WorkerExceptionCode.LOAD_FX_LOSE_FAILURE.exception();
        }

        return CollectionUtils.isEmpty(responseDto.getData()) ? Collections.emptyList() :
                responseDto.getData();
    }

    @Cacheable(cacheNames = "LOCAL:2min#REDIS:1d",
            key = "'EXTRA_INCOME_CONFIGURATION:' + #accountId")
    public List<ExtraIncomeConfigurationDto> queryExtraIncomeConfiguration(
            final Long accountId,
            final CountryCodeEnum countryCode,
            final TransactionTypeCodeEnum transactionTypeCode) {

        final ResponseDto<List<ExtraIncomeConfigurationDto>> resp =
                baseFeign.queryExtraIncomeConfiguration(QueryExtraIncomeConfigurationVo.builder()
                        .accountId(accountId)
                        .countryCode(countryCode)
                        .transactionTypeCode(transactionTypeCode)
                        .build());
        CheckResponseUtil.checkResponse(resp);
        return resp.getData();
    }

    /**
     * @param accountInfo
     * @param transactionDate
     *
     * @return
     */
    public MonthlyFeeConfigurationBo getMonthlyProductFeeConfig(
            final AccountDto accountInfo,
            final LocalDate transactionDate,
            final TaskFeeCalculation taskOrder) {

        log.info("Load product fee config: uniqueId={}, accountId={}, transactionDate={}",
                taskOrder.getUniqueId(), accountInfo.getId(), transactionDate);

        // build calculation rule
        final Map<String, String> calculationRuleMap = this.buildCalculationRule(taskOrder);

        // if DirectionType is payin.CHARGE_BACK_REJECTED, payout.REJECTED
        // No need to calculate fee;
        if (List.of(DirectionTypeEnum.CHARGE_BACK_REJECTED, DirectionTypeEnum.REJECTED)
                .contains(taskOrder.getDirectionType())) {
            return MonthlyFeeConfigurationBo.builder()
                    .calculationRuleMap(calculationRuleMap)
                    .feeConfigList(Collections.emptyList()).build();
        }

        // load monthly product fee config list
        final List<MonthlyProductFeeConfigDto> feeConfigList =
                this.loadMonthlyProductFeeConfig(accountInfo, taskOrder, transactionDate,
                        calculationRuleMap);

        // filter fee config list
        final List<MonthlyFeeConfigurationDto> configList = this.filterMonthlyFeeConfigList(
                accountInfo, taskOrder, calculationRuleMap, feeConfigList);

        return MonthlyFeeConfigurationBo.builder()
                .calculationRuleMap(calculationRuleMap)
                .feeConfigList(configList)
                .build();
    }

    private List<MonthlyFeeConfigurationDto> filterMonthlyFeeConfigList(
            final AccountDto accountInfo,
            final TaskFeeCalculation taskOrder,
            final Map<String, String> calculationRuleMap,
            final List<MonthlyProductFeeConfigDto> feeConfigList) {

        if (CollectionUtils.isEmpty(feeConfigList)) {
            log.error("Load fee config failed: accountId={}, transactionId={}, calculationRule={}",
                    accountInfo.getId(), taskOrder.getId(), calculationRuleMap);
            throw WorkerExceptionCode.LOAD_FEE_CONFIG_FAILURE.exception(
                    String.valueOf(taskOrder.getUniqueId()));
        }

        final List<MonthlyFeeConfigurationDto> monthlyFeeConfigList = feeConfigList.stream()
                .filter(item -> taskOrder.getProductCode() == item.getProductCode())
                .map(MonthlyProductFeeConfigDto::getProductFeeConfigList)
                .flatMap(Collection::stream)
                .filter(item -> item.getSourceCurrency().contains(taskOrder.getCurrency())
                        && accountInfo.getCurrency() == item.getAccountCurrency())
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(monthlyFeeConfigList)) {
            log.error("Load fee config failed: accountId={}, transactionId={}, calculationRule={}",
                    accountInfo.getId(), taskOrder.getId(), calculationRuleMap);
            throw WorkerExceptionCode.LOAD_FEE_CONFIG_FAILURE.exception(
                    String.valueOf(taskOrder.getUniqueId()));
        }

        return monthlyFeeConfigList.stream()
                .filter(item -> taskOrder.getDirectionType() == item.getDirectionType()
                        && taskOrder.getAmount().compareTo(item.getMinVolume()) >= 0
                        && taskOrder.getAmount().compareTo(item.getMaxVolume()) < 0)
                .collect(Collectors.toList());
    }

    private Map<String, String> buildCalculationRule(final TaskFeeCalculation taskOrder) {

        if (Objects.isNull(workerProperties.getCalculationRule())
                || CollectionUtils.isEmpty(workerProperties.getCalculationRule().getFieldSign())
                || CollectionUtils.isEmpty(
                workerProperties.getCalculationRule().getOtherFieldSign())) {
            larkRobotMonitor.error("CalculationRule sign missing",
                    "configuration about calculationRule sign missing", "");
            return null;
        }

        final LinkedHashMap<String, String> ruleMap = new LinkedHashMap<>();
        try {
            // get and build calculationRule from task_fee_calculation other filed
            final ObjectNode others = taskOrder.getOthers();
            Optional.ofNullable(others)
                    .ifPresent(node -> workerProperties.getCalculationRule().getOtherFieldSign()
                            .forEach(key -> Optional.ofNullable(node.get(key)).ifPresent(
                                    v -> ruleMap.putIfAbsent(key, v.asText().trim()))));

            // get and build calculationRule from task_fee_calculation filed
            Map<String, String> map = BeanUtils.describe(taskOrder);
            workerProperties.getCalculationRule().getFieldSign()
                    .forEach(sign -> Optional.ofNullable(map.get(sign))
                            .ifPresent(v -> ruleMap.put(sign, v)));

        } catch (Exception e) {
            log.error("buildCalculationRule error", e);
            larkRobotMonitor.error("Build CalculationRule Sign Error",
                    "build calculationRule sign error taskId" + taskOrder.getId(), e.getMessage());
        }

        log.info("calculation rule taskFeeCalculation={},rule={}", taskOrder.getId(), ruleMap);

        return ruleMap;

    }

    /**
     * @param accountInfo
     * @param transactionDate
     *
     * @return
     */
    public List<MonthlyProductFeeConfigDto> loadMonthlyProductFeeConfig(
            final AccountDto accountInfo,
            final TaskFeeCalculation taskOrder,
            final LocalDate transactionDate,
            final Map<String, String> calculationRuleMap) {

        log.info("load monthly fee config uniqueID={}, accountId={}, transactionDate={}, " +
                        "calculationRule={}",
                taskOrder.getUniqueId(), accountInfo.getId(), transactionDate, calculationRuleMap);
        /* Merchant-account time zone conversion completed, load from remote cache*/
        final String hashKey = String.format(Constant.CACHE.MONTHLY_FEE_CONFIG_HASH,
                transactionDate.format(LocalDateUtil.FORMAT_YYYYMMDD));

        /* load from remote cache*/
        final String cacheKey = String.join(":", accountInfo.getId().toString(),
                        Md5Util.getMd5(JsonUtil.toJson(calculationRuleMap)))
                .toLowerCase(Locale.ROOT);

        List<MonthlyProductFeeConfigDto> productCodeFeeList =
                redisCacheUtil.getCacheMapValue(hashKey, cacheKey);

        if (CollectionUtils.isNotEmpty(productCodeFeeList)) {
            return productCodeFeeList;
        }

        final QueryAccountMonthlyFeeConfigVo configVo = QueryAccountMonthlyFeeConfigVo.builder()
                .accountId(accountInfo.getId())
                .activeDate(transactionDate)
                .calculationRule(calculationRuleMap)
                .build();
        // get config from base-service
        final ResponseDto<MonthlyAccountFeeConfigDto> result =
                baseFeign.queryAccountMonthFeeConfig(configVo);

        if (ResponseDto.isFail(result)
                || Objects.isNull(result.getData())
                || CollectionUtils.isEmpty(result.getData().getMonthlyProductFeeConfigList())) {

            log.error("get config from base-service fail, uniqueId={}, configVo={}",
                    taskOrder.getUniqueId(), configVo);
            throw WorkerExceptionCode.LOAD_FEE_CONFIG_FAILURE.exception(
                    String.valueOf(taskOrder.getUniqueId()));
        }

        // set cache and expire time
        productCodeFeeList = result.getData().getMonthlyProductFeeConfigList();
        redisCacheUtil.setCacheMapValue(hashKey, cacheKey, productCodeFeeList);
        redisCacheUtil.expire(cacheKey, 3, TimeUnit.DAYS);

        return productCodeFeeList;
    }

    /**
     * @param accountId
     * @param productCode
     *
     * @return
     */
    public AccountProductDto loadAccountProductConfig(final Long accountId,
                                                      final ProductCodeEnum productCode) {

        Assert.notNull(accountId, "accountId must not be null");
        Assert.notNull(productCode, "productCode must not be null");
        return accountProductLocalCache.get(AccountProductKey.builder()
                .accountId(accountId).productCode(productCode).build());
    }

    /**
     * @param apKey accountProductKey
     *
     * @return
     */
    public AccountProductDto loadAccountProductConfig(final AccountProductKey apKey) {
        /* load from remote cache*/
        final String cacheKey = String.join(":",
                apKey.getAccountId().toString(), apKey.getProductCode().getCode());
        final AccountProductDto cacheAccountProduct =
                redisCacheUtil.getCacheMapValue(Constant.CACHE.ACCOUNT_PRODUCT_CONFIG, cacheKey);

        if (Objects.nonNull(cacheAccountProduct)) {
            return cacheAccountProduct;
        }

        final Map<String, AccountProductDto> resultMap = this.loadAllAccountProductConfig();
        if (MapUtils.isEmpty(resultMap) || Objects.isNull(resultMap.get(cacheKey))) {
            throw WorkerExceptionCode.LOAD_ACCOUNT_PRODUCT_FAILURE.exception();
        }

        return resultMap.get(cacheKey);
    }

    public Map<String, AccountProductDto> loadAllAccountProductConfig() {

        /* load from remote cache*/
        final Map<String, AccountProductDto> cacheMap =
                redisCacheUtil.getCacheMap(Constant.CACHE.ACCOUNT_PRODUCT_CONFIG);

        if (MapUtils.isNotEmpty(cacheMap)) {
            return cacheMap;
        }

        final ResponseDto<List<AccountProductDto>> responseDto =
                baseFeign.listAccountProduct(new ListAccountProductVo());

        if (ResponseDto.isFail(responseDto) || CollectionUtils.isEmpty(responseDto.getData())) {
            throw WorkerExceptionCode.LOAD_ACCOUNT_PRODUCT_FAILURE.exception();
        }

        final Map<String, AccountProductDto> resultMap = responseDto.getData().stream().collect(
                Collectors.toMap(item -> String.join(":", item.getAccountId().toString(),
                        item.getProductCode().getCode()), Function.identity(), (k1, k2) -> k1));

        redisCacheUtil.setCacheMap(Constant.CACHE.ACCOUNT_PRODUCT_CONFIG, resultMap);

        return resultMap;
    }

    public MerchantDto getMerchantByCode(final String merchantCode) {
        if (StringUtils.isBlank(merchantCode)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
        }

        final MerchantDto merchantInfo =
                redisCacheUtil.getCacheMapValue(Constant.CACHE.MERCHANT_INFO_HASH, merchantCode);
        if (Objects.nonNull(merchantInfo)) {
            return merchantInfo;
        }

        final ResponseDto<MerchantDto> responseDto = baseFeign.getEffectiveMerchantInfo(
                QueryMerchantVo.builder().code(merchantCode).build());

        if (ResponseDto.isFail(responseDto) || Objects.isNull(responseDto.getData())) {
            throw WorkerExceptionCode.UNKNOWN_MERCHANT_INFO.exception(merchantCode);
        }

        redisCacheUtil.setCacheMapValue(Constant.CACHE.MERCHANT_INFO_HASH, merchantCode,
                responseDto.getData());

        return responseDto.getData();
    }

    public MerchantDto getMerchantById(final Long merchantId) {
        if (Objects.isNull(merchantId) || merchantId <= 0) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
        }

        final String merchantIdStr = merchantId.toString();
        MerchantDto merchantInfo =
                redisCacheUtil.getCacheMapValue(Constant.CACHE.MERCHANT_INFO_HASH, merchantIdStr);
        if (Objects.nonNull(merchantInfo)) {
            return merchantInfo;
        }

        final ResponseDto<MerchantDto> responseDto = baseFeign.getEffectiveMerchantInfo(
                QueryMerchantVo.builder().id(merchantId).build());
        if (ResponseDto.isFail(responseDto) || Objects.isNull(responseDto.getData())) {
            throw WorkerExceptionCode.UNKNOWN_MERCHANT_INFO.exception(merchantIdStr);
        }

        merchantInfo = responseDto.getData();
        redisCacheUtil.setCacheMapValue(Constant.CACHE.MERCHANT_INFO_HASH, merchantInfo.getCode(),
                merchantInfo);

        return merchantInfo;
    }

    public ApmCostConfigurationDto getApmCostConfig(final ApmCostConfigVo vo) {
        final List<ApmCostConfigDto> configList = apmCostLocalCache.get("ALL");
        if (CollectionUtils.isEmpty(configList)) {
            return ApmCostConfigurationDto.builder()
                    .costConfigList(Collections.emptyList()).build();
        }

        final List<ApmCostConfigDto> resultList = Lists.newArrayList();

        /* Customize(FEE) config */
        final List<ApmCostConfigDto> costFeeList = configList.stream()
                .filter(item -> item.getAccountId().equals(vo.getAccountId())
                        && item.getVendorCode() == vo.getVendor()
                        && item.getProductCode() == vo.getProductCode()
                        && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup())
                .collect(Collectors.toList());

        /* if no customize FEE, then use default apm config */
        if (CollectionUtils.isEmpty(costFeeList)) {
            costFeeList.addAll(configList.stream()
                    .filter(item -> item.getAccountId().equals(0L)
                            && item.getCountryCode() == vo.getCountry()
                            && item.getTransactionTypeCode() == vo.getTransactionType()
                            && item.getVendorCode() == vo.getVendor()
                            && item.getProductCode() == vo.getProductCode()
                            && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup())
                    .collect(Collectors.toList()));
        }

        /* Customize FEE(Shopify Fee) config */
        costFeeList.addAll(configList.stream()
                .filter(item -> item.getAccountId().equals(vo.getAccountId())
                        && Objects.isNull(item.getVendorCode())
                        && Objects.isNull(item.getProductCode())
                        && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup())
                .collect(Collectors.toList()));

        /* Customize(TAX, FX) config */
        final List<ApmCostConfigDto> costTaxFxList = configList.stream()
                .filter(item -> vo.getAccountId().equals(item.getAccountId())
                        && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup()))
                .collect(Collectors.toList());

        /* if no customize(TAX, FX), then use default tax fx config */
        if (org.springframework.util.CollectionUtils.isEmpty(costTaxFxList)) {
            costTaxFxList.addAll(configList.stream()
                    .filter(item -> item.getAccountId().equals(0L)
                            && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup())
                            && item.getCountryCode() == vo.getCountry()
                            && item.getTransactionTypeCode() == vo.getTransactionType()
                            && Objects.isNull(item.getVendorCode())
                            && Objects.isNull(item.getProductCode()))
                    .collect(Collectors.toList()));
        }

        /* Cost adjustment(TAX, FX) config */
        costTaxFxList.addAll(configList.stream()
                .filter(item -> item.getAccountId().equals(0L)
                        && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup())
                        && item.getCountryCode() == vo.getCountry()
                        && item.getTransactionTypeCode() == vo.getTransactionType()
                        && item.getVendorCode() == vo.getVendor()
                        && (item.getProductCode() == vo.getProductCode()))
                .collect(Collectors.toList()));

        resultList.addAll(costFeeList);
        resultList.addAll(costTaxFxList);

        // deduplication
        final List<ApmCostConfigDto> dataList = Lists.newArrayList(resultList.stream()
                .collect(Collectors.toMap(ApmCostConfigDto::getId,
                        Function.identity(), (x, y) -> x)).values());
        // sort by id
        dataList.sort(Comparator.comparing(ApmCostConfigDto::getId));

        this.monitorApmCostConfig(vo, dataList);

        return ApmCostConfigurationDto.builder().costConfigList(dataList).build();
    }

    public CardCostConfigurationDto getCardCostConfig(final CardCostConfigVo vo) {
        log.info("get card cost config accountId={}, params={}", vo.getAccountId(), vo);

        final List<CardCostConfigDto> configList = cardCostLocalCache.get("ALL");

        final Set<CreditCardGroupCodeEnum> allCardGroupList = configList.stream()
                .map(CardCostConfigDto::getCardGroup).collect(Collectors.toSet());

        // If the card-group does not have cost rules defined, use the DEFAULT configuration
        if (!allCardGroupList.contains(vo.getCardGroup())) {
            vo.setCardGroup(CreditCardGroupCodeEnum.DEFAULT);
        }

        /* Apm transaction_fee config */
        final List<CardCostConfigDto> costConfigList = configList.stream()
                .filter(item -> (Optional.ofNullable(item.getAccountId()).orElse(0L).equals(0L)
                        && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup()
                        && item.getCountryCode() == vo.getCountry()
                        && item.getVendorCode() == vo.getVendor()
                        && item.getDirectionType() == vo.getDirectionType()
                        && item.getCardType() == vo.getCardType()
                        && item.getCardGroup() == vo.getCardGroup()
                        && vo.getInstallment() >= item.getInstallmentBegin()
                        && vo.getInstallment() <= item.getInstallmentEnd()))
                .collect(Collectors.toList());

        final List<FeeGroupEnum> taxFxFee = List.of(FeeGroupEnum.TAX, FeeGroupEnum.FX);
        /* customize(TAX, FX) config */
        List<CardCostConfigDto> customizeTaxFxList = configList.stream().filter(item ->
                        (vo.getAccountId().equals(item.getAccountId())
                                && taxFxFee.contains(item.getFeeGroup())
                                && vo.getInstallment() >= item.getInstallmentBegin()
                                && vo.getInstallment() <= item.getInstallmentEnd()))
                .collect(Collectors.toList());

        /* if no customize(TAX, FX), use default config */
        if (CollectionUtils.isEmpty(customizeTaxFxList)) {
            customizeTaxFxList = configList.stream().filter(item ->
                            (Optional.ofNullable(item.getAccountId()).orElse(0L).equals(0L)
                                    && taxFxFee.contains(item.getFeeGroup())
                                    && vo.getCountry() == item.getCountryCode()
                                    && vo.getInstallment() >= item.getInstallmentBegin()
                                    && vo.getInstallment() <= item.getInstallmentEnd()))
                    .collect(Collectors.toList());
        }

        /* merge transactionFeeList and customizeTaxFxList */
        costConfigList.addAll(customizeTaxFxList);

        /* merge customize supplementary revenue */
        costConfigList.addAll(configList.stream().filter(item ->
                        (vo.getAccountId().equals(item.getAccountId())
                                && vo.getInstallment() >= item.getInstallmentBegin()
                                && vo.getInstallment() <= item.getInstallmentEnd()))
                .collect(Collectors.toList()));

        // deduplication
        final CardCostConfigurationDto cardCostConfig = CardCostConfigurationDto.builder()
                .cardCostConfigList(Lists.newArrayList(costConfigList.stream()
                        .collect(Collectors.toMap(CardCostConfigDto::getId, Function.identity(),
                                (x, y) -> x)).values())).build();

        log.info("return card-cost config accountId={}, params={}, cardCostConfig={}",
                vo.getAccountId(), vo, cardCostConfig);

        this.monitorCardCostConfig(vo, cardCostConfig.getCardCostConfigList());

        return cardCostConfig;
    }

    public List<ApmCostConfigDto> loadApmCostConfig(final String cacheKey) {
        /* load from remote cache*/
        List<ApmCostConfigDto> configList =
                redisCacheUtil.getCacheList(Constant.CACHE.COST_CONFIG_APM_LIST);
        if (CollectionUtils.isNotEmpty(configList)) {
            return configList;
        }

        final ResponseDto<ApmCostConfigurationDto> responseDto =
                baseFeign.queryApmCostConfig(QueryApmCostConfigVo.builder().build());
        if (ResponseDto.isFail(responseDto)) {
            log.error("query apm cost config fail, responseDto={}", responseDto);
            throw CommonExceptionCode.SYSTEM_SERVICE_ERROR.exception();
        }

        configList = Optional.ofNullable(responseDto.getData())
                .map(ApmCostConfigurationDto::getCostConfigList)
                .orElse(Collections.emptyList());

        if (CollectionUtils.isNotEmpty(configList)) {
            redisCacheUtil.setCacheList(Constant.CACHE.COST_CONFIG_APM_LIST, configList);
        }

        return configList;
    }

    public List<CardCostConfigDto> loadCardCostConfig(final String cacheKey) {
        /* load from remote cache*/
        List<CardCostConfigDto> configList =
                redisCacheUtil.getCacheList(Constant.CACHE.COST_CONFIG_CARD_LIST);
        if (CollectionUtils.isNotEmpty(configList)) {
            return configList;
        }

        final ResponseDto<CardCostConfigurationDto> responseDto = baseFeign.queryCardCostConfig();
        if (ResponseDto.isFail(responseDto)) {
            log.error("query card cost config fail.ResponseDto={}", responseDto);
            throw CommonExceptionCode.SYSTEM_SERVICE_ERROR.exception();
        }

        configList = Optional.ofNullable(responseDto.getData())
                .map(CardCostConfigurationDto::getCardCostConfigList)
                .orElse(Lists.newArrayList());

        if (CollectionUtils.isNotEmpty(configList)) {
            redisCacheUtil.setCacheList(Constant.CACHE.COST_CONFIG_CARD_LIST, configList);
        }

        return configList;
    }

    public List<WorkingDayDto> getWorkingDay(final Integer year,
                                             final CountryCodeEnum country) {

        log.info("get workday year= {}, country={}", year, country.getCode());
        final String hashKey = String.format(Constant.CACHE.WORKDAY_HASH, year.toString());
        List<WorkingDayDto> configList =
                redisCacheUtil.getCacheMapValue(hashKey, country.getCode());
        if (CollectionUtils.isNotEmpty(configList)) {
            return configList;
        }

        final ResponseDto<List<WorkingDayDto>> responseDto = baseFeign.queryWorkingDay(
                QueryWorkingDayVo.builder()
                        .year(year)
                        .country(country)
                        .workday(true).build());

        if (ResponseDto.isFail(responseDto)) {
            log.error("get workday`s config from base-service fail. year={}, country={}",
                    year, country.getCode());
            throw CommonExceptionCode.SYSTEM_SERVICE_ERROR.exception();
        }

        configList = Lists.newArrayList();
        if (Objects.nonNull(responseDto.getData())) {
            configList = responseDto.getData();
        }

        redisCacheUtil.setCacheMapValue(hashKey, country.getCode(), configList);
        return configList;
    }

    private void monitorApmCostConfig(
            final ApmCostConfigVo vo,
            final List<ApmCostConfigDto> configList) {

        if (NON_SETTLEMENT_LIST.contains(vo.getDirectionType())
                || configList.stream().anyMatch(x -> vo.getVendor() == x.getVendorCode())
                || configList.stream().anyMatch(x -> vo.getVendor() == x.getVendorCode())) {
            return;
        }

        final String cacheKey =
                String.format("VENDOR:%s:%s", vo.getAccountId(), vo.getVendor().getCode());
        if (StringUtils.isBlank(redisCacheUtil.getCacheObject(cacheKey))) {
            larkRobotMonitor.warn("Cost Vendor[ " + vo.getVendor().getCode() + " ] Not Config",
                    vo.getMerchantCode()
                            + " | " + vo.getCountry().getCode()
                            + " | " + vo.getTransactionType().getCode()
                            + " | " + vo.getProductCode().getCode()
                            + " | " + vo.getVendor().getCode(),
                    "Vendor not config info=" + JsonUtil.toJson(vo));
            redisCacheUtil.setCacheObject(cacheKey, vo.getVendor().getCode(), 4, TimeUnit.HOURS);
        }
    }

    private void monitorCardCostConfig(final CardCostConfigVo vo,
                                       final List<CardCostConfigDto> configList) {

        if (List.of(DirectionTypeEnum.REFUND,
                DirectionTypeEnum.CHARGE_BACK,
                DirectionTypeEnum.CHARGE_BACK_REJECTED).contains(vo.getDirectionType())
                || configList.stream().anyMatch(item -> vo.getVendor() == item.getVendorCode())) {
            return;
        }

        final String cacheKey =
                String.format("VENDOR:%s:%s", vo.getAccountId(), vo.getVendor().getCode());
        if (StringUtils.isBlank(redisCacheUtil.getCacheObject(cacheKey))) {
            larkRobotMonitor.warn("Cost Vendor[ " + vo.getVendor().getCode() + " ] Not Config",
                    vo.getMerchantCode()
                            + " | " + vo.getCountry().getCode()
                            + " | " + vo.getTransactionType().getCode()
                            + " | " + vo.getProductCode().getCode()
                            + " | " + vo.getVendor().getCode(),
                    "Vendor not config info=" + JsonUtil.toJson(vo));

            redisCacheUtil.setCacheObject(cacheKey, vo.getVendor().getCode(), 4, TimeUnit.HOURS);
        }
    }
}
