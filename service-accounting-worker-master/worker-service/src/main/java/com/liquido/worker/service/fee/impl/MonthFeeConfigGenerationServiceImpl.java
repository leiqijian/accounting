package com.liquido.worker.service.fee.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.Convert;

import com.liquido.base.BaseApis;
import com.liquido.base.convert.CalculationRuleConverter;
import com.liquido.base.enums.FeeCodeEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.LarkRemindRankEnum;
import com.liquido.base.enums.MonthlyVolumeTypeEnum;
import com.liquido.base.pojo.dto.AccountFeeConfigurationDto;
import com.liquido.base.pojo.dto.AccountProductDto;
import com.liquido.base.pojo.dto.AccountProductGroupDto;
import com.liquido.base.pojo.dto.AccountProductVersionDto;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;
import com.liquido.base.pojo.vo.AccountProductVersionVo;
import com.liquido.base.pojo.vo.BatchAddMonthlyFeeConfigVo;
import com.liquido.base.pojo.vo.BatchQueryProductFeeMatchVo;
import com.liquido.base.pojo.vo.EditAccountProductVersionMonthlyFlagVo;
import com.liquido.base.pojo.vo.ListAccountProductVo;
import com.liquido.base.pojo.vo.MonthlyFeeConfigurationVo;
import com.liquido.base.pojo.vo.QueryAccountProductGroupVo;
import com.liquido.base.pojo.vo.QueryAccountProductVersionVo;
import com.liquido.base.pojo.vo.QueryMonthFeeConfigVo;
import com.liquido.base.pojo.vo.QueryProductFeeMatchVo;
import com.liquido.base.pojo.vo.lark.LarkBathMessageVo;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.ListTransactionSummaryDto;
import com.liquido.statement.pojo.vo.ListAccountVo;
import com.liquido.statement.pojo.vo.ListTransactionSummaryVo;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.common.utils.DateUtil;
import com.liquido.worker.events.CompareMonthConfigEvent;
import com.liquido.worker.events.FeeMonthConfigGenerateEvent;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.feign.StatementService;
import com.liquido.worker.pojo.bo.AccountMonthTransactionSumBo;
import com.liquido.worker.pojo.bo.ProductTransactionSumBo;
import com.liquido.worker.pojo.vo.QueryMerchantMonthVo;
import com.liquido.worker.service.fee.MonthFeeConfigGenerationService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class MonthFeeConfigGenerationServiceImpl implements MonthFeeConfigGenerationService {

    private final RedisCacheUtil redisCacheUtil;
    private final BaseApis.BaseFeign baseFeign;
    private final StatementApis.StatementFeign statementFeign;
    private final ApplicationEventPublisher publisher;
    private final LarkRobotMonitor larkRobotMonitor;
    private final StatementService statementService;
    private final WorkerProperties.MonthlyFeeConfig monthlyFeeConfig;
    private static final String YEAR_MONTH_REGEX = "^(19|20)\\d{2}(0[1-9]|1[0-2])$";

    /**
     * initialize the product version of the first day of each month
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<AccountProductVersionDto> initProductVersion() {

        // Step1: Query all account products
        final List<AccountProductDto> productList = queryAllProducts();

        // Step2: Filter out products that are the first day of the month in the local time zone
        final List<AccountProductDto> firstDayProducts = filterFirstDayOfMonthProducts(productList);

        // Step3: Filter Products without initialized product version
        // Record product versions that have not generated monthly fee configuration
        final List<AccountProductVersionDto> unGenMonthFeeProductVersions = new ArrayList<>();
        final List<AccountProductDto> unInitProducts =
                filterUnInitProducts(firstDayProducts, unGenMonthFeeProductVersions);

        // Step4: Get the collection of product versions that need to be initialized
        final List<AccountProductVersionVo> initProductVersionList =
                getInitProductVersionList(unInitProducts);

        // Step5: Batch insert product version
        batchInsertProductVersions(initProductVersionList, unGenMonthFeeProductVersions);

        // Step6: Publish event
        publisher.publishEvent(new FeeMonthConfigGenerateEvent(
                unGenMonthFeeProductVersions, true, null));

        return unGenMonthFeeProductVersions;
    }

    /**
     * At the last day of the month to initialize the next month's fee configuration
     */
    @Override
    public List<AccountProductVersionDto> initAllProductVersion() {

        // Step1: Query all account products
        final Map<String, List<AccountProductDto>> productListMap =
                queryAllProducts().stream().collect(
                        Collectors.groupingBy(AccountProductDto::getTimezone));

        // Step2: activeMonth is determined based at 22:00 on the last day of the earliest timezone
        final Integer activeMonth = determineActiveMonth(productListMap.keySet().stream()
                .max(Comparator.comparingInt(this::getOffsetHours)).orElse(null));
        log.info("generate all product version, activeMonth: {}", activeMonth);

        final List<AccountProductVersionDto> allUnGenMonthFeeProductVersions = new ArrayList<>();

        for (Map.Entry<String, List<AccountProductDto>> entry : productListMap.entrySet()) {
            try {
                final List<AccountProductDto> productList = entry.getValue();

                // Step3: Filter Products without initialized product version
                // Record product versions that have not generated monthly fee configuration
                final List<AccountProductVersionDto>
                        unGenMonthFeeProductVersions = new ArrayList<>();
                final List<AccountProductDto> unInitProducts = filterUnInitProducts(
                        productList, unGenMonthFeeProductVersions, activeMonth);

                log.info("start to init product version for timezone: {}, " +
                                "unInit product size: {}, activeMonth: {}",
                        entry.getKey(), unInitProducts.size(), activeMonth);

                // Step4: Get the collection of product versions that need to be initialized
                final List<AccountProductVersionVo> initProductVersionList =
                        getInitProductVersionList(unInitProducts, activeMonth);

                // Step5: Batch insert product version
                batchInsertProductVersions(initProductVersionList, unGenMonthFeeProductVersions);

                // Step6: Publish event
                publisher.publishEvent(new FeeMonthConfigGenerateEvent(
                        unGenMonthFeeProductVersions, true, entry.getKey()));

                allUnGenMonthFeeProductVersions.addAll(unGenMonthFeeProductVersions);
            } catch (Exception e) {
                log.error("init product version for timezone: {} failed", entry.getKey(), e);
                final String content = String.format(
                        "timezone: %s\\n activeMonth: %s", entry.getKey(), activeMonth);
                larkRobotMonitor.error(
                        "Init Product Version Error", content, e.getMessage());
            }
        }

        return allUnGenMonthFeeProductVersions;
    }

    @Override
    public void checkMonthFeeConfig() {
        // Step1: Query all account products
        final List<AccountProductDto> allProductList = queryAllProducts();

        // Step2: Filter out products that are the first day of the month in the local time zone
        final List<AccountProductDto> firstDayProducts =
                filterFirstDayOfMonthProducts(allProductList);

        if (CollectionUtils.isEmpty(firstDayProducts)) {
            log.info("no product need to check month fee config");
            return;
        }
        final Map<String, List<AccountProductDto>> AccountProductMap = firstDayProducts.stream()
                .collect(Collectors.groupingBy(AccountProductDto::getTimezone));

        for (Map.Entry<String, List<AccountProductDto>> entry : AccountProductMap.entrySet()) {
            try {
                log.info("start to check month fee config for timezone: {}", entry.getKey());
                final Boolean correctRecord = redisCacheUtil.getCacheObject(String.format(
                        Constant.CACHE.CHECK_MONTH_FEE_CONFIG_FLAG, entry.getKey()));
                if (Optional.ofNullable(correctRecord).orElse(false)) {
                    log.info("month fee config for timezone: {} is correct", entry.getKey());
                    continue;
                }
                final boolean hasError = monthFeeConfigError(
                        entry.getKey(), null, entry.getValue());
                if (!hasError) {
                    redisCacheUtil.setCacheObject(String.format(
                                    Constant.CACHE.CHECK_MONTH_FEE_CONFIG_FLAG, entry.getKey()),
                            true, 1, TimeUnit.DAYS);
                }
            } catch (Exception e) {
                log.error("check month fee config for timezone: {} failed", entry.getKey(), e);
            }
            log.info("end to check month fee config for timezone: {}", entry.getKey());
        }
    }

    @Override
    public Boolean checkMonthFeeConfig(final String timezone, final LocalDate activeDate) {
        final List<AccountProductDto> productList = queryAllProducts().stream()
                .filter(product -> timezone.equals(product.getTimezone()))
                .collect(Collectors.toList());

        return monthFeeConfigError(timezone, activeDate, productList);
    }

    private boolean monthFeeConfigError(final String timezone,
                                        final LocalDate date,
                                        final List<AccountProductDto> productList) {
        if (CollectionUtils.isEmpty(productList)) {
            return true;
        }

        final List<Long> productIdList =
                productList.stream().map(AccountProductDto::getId).collect(Collectors.toList());
        final LocalDate activeDate =
                Objects.isNull(date) ? DateUtil.getCurrentDate(timezone) : date;
        final Integer activeMonth = Integer.valueOf(activeDate.format(LocalDateUtil.FORMAT_YYYYMM));

        // Query all data of the version table
        final ResponseDto<List<AccountProductVersionDto>> listVersionResponseDto =
                baseFeign.listAccountProductVersion(QueryAccountProductVersionVo.builder()
                        .accountProductIds(productIdList)
                        .activeDate(activeDate)
                        .monthlyFlag(true)
                        .build());
        final List<AccountProductVersionDto> toBeCheckVersionList =
                CheckResponseUtil.checkAndReturnResponseData(listVersionResponseDto);

        // The first inspection is to check whether all products have been generated
        if (toBeCheckVersionList.size() != productList.size()) {
            final String content = String.format(
                    "timezone: %s\\n activeMonth: %s\\n activeDate: %s",
                    timezone, activeMonth, activeDate);
            final String detail = String.format(
                    "There are %s products have been generated fee config, " +
                            "but %s products need to be generated",
                    toBeCheckVersionList.size(), productList.size());
            larkRobotMonitor.error("Check Month Fee Config Error", content, detail);
        }

        // Get the products and product groups to initialize
        final AccountProductGroupDto dto =
                getGroupProductsOfProduct(new HashSet<>(productIdList));
        final List<AccountProductDto> productDtoList = dto.getProductList();
        final List<AccountProductDto> productGroupList = dto.getProductGroupList();

        // Filter out products that are the first day of the month in the local time zone
        final LocalDate prevMonthDate =
                toBeCheckVersionList.get(0).getStartDate().minusMonths(1);
        final LocalDate prevMonthStartDate = prevMonthDate.withDayOfMonth(1);
        final LocalDate prevMonthEndDate =
                prevMonthDate.withDayOfMonth(prevMonthDate.lengthOfMonth());
        final Map<Long, ProductTransactionSumBo> mergedProductBillsMap =
                processAccountBilling(productDtoList, productGroupList, prevMonthStartDate,
                        prevMonthEndDate);

        // Attempted to regenerate monthly configuration
        final Map<String, MonthlyFeeConfigurationVo> newMonthlyFeeConfigMap =
                generateMonthFeeConfig(activeMonth, productDtoList,
                        toBeCheckVersionList.stream().collect(Collectors.toMap(
                                AccountProductVersionDto::getAccountProductId,
                                obj -> obj)),
                        mergedProductBillsMap)
                        .stream().collect(Collectors.toMap(
                                v -> String.format("%s_%s_%s_%s_%s",
                                        v.getAccountProductId(),
                                        Optional.ofNullable(JsonUtil.toJson(v.getCalculationRule()))
                                                .filter(s -> !s.isEmpty())
                                                .orElse("NULL"),
                                        v.getFeeCode(),
                                        v.getFeeValueModel(),
                                        v.getDirectionType()),
                                obj -> obj
                        ));

        // Query the monthly configurations that have been generated
        final AtomicBoolean hasError = new AtomicBoolean(false);
        final List<MonthlyFeeConfigurationDto> oldMonthlyFeeConfigList =
                CheckResponseUtil.checkAndReturnResponseData(
                        baseFeign.listMonthFeeConfig(QueryMonthFeeConfigVo.builder()
                                .accountProductIds(productIdList)
                                .activeMonth(activeMonth)
                                .build()));
        oldMonthlyFeeConfigList.forEach(v -> {
            try {
                final MonthlyFeeConfigurationVo config = newMonthlyFeeConfigMap.getOrDefault(
                        v.getAccountProductId() + "_"
                                + Optional.ofNullable(JsonUtil.toJson(v.getCalculationRule()))
                                .filter(s -> !s.isEmpty()).orElse("NULL") + "_"
                                + v.getFeeCode() + "_"
                                + v.getFeeValueModel() + "_"
                                + v.getDirectionType(), null);
                if (Objects.isNull(config)) {
                    hasError.set(true);
                    final String content = String.format(
                            "timezone: %s\\n activeMonth: %s\\n activeDate: %s\\n" +
                                    "accountId: %s\\n accountProductId: %s", timezone, activeMonth,
                            activeDate, v.getAccountId(), v.getAccountProductId());
                    final String detail = String.format("%s not found", v.getFeeName());
                    larkRobotMonitor.error("Check Month Fee Config Error", content, detail);
                    return;
                }
                if (!v.getAccountFeeConfigurationId()
                        .equals(config.getAccountFeeConfigurationId())) {
                    hasError.set(true);
                    final String content = String.format(
                            "timezone: %s\\n activeMonth: %s\\n activeDate: %s\\n" +
                                    "accountId: %s\\n accountProductId: %s", timezone, activeMonth,
                            activeDate, v.getAccountId(), v.getAccountProductId());
                    final String detail = String.format("%s should be %s instead of %s",
                            v.getFeeName(),
                            buildCalculationFormula(config.getFeeValueModel(),
                                    config.getFeeValue(), config.getFeeOn()),
                            buildCalculationFormula(
                                    v.getFeeValueModel(), v.getFeeValue(), v.getFeeOn()));
                    larkRobotMonitor.error("Check Month Fee Config Error", content, detail);
                }
            } catch (Exception e) {
                log.error("Check Month Fee Config Error, accountId: {}, accountProductId: {}",
                        v.getAccountId(), v.getAccountProductId(), e);
            }
        });
        return hasError.get();
    }

    private String buildCalculationFormula(final FeeValueModelEnum feeValueModel,
                                           final BigDecimal feeValue, final FeeOnEnum feeOn) {
        if (FeeValueModelEnum.PERCENTAGE.equals(feeValueModel)) {
            return String.format("%s * %s", feeValue, feeOn);
        } else {
            return String.format("%s", feeValue);
        }
    }

    private int getOffsetHours(final String timezone) {
        if (timezone.startsWith("UTC")) {
            final String offsetStr = timezone.substring(3);
            if (offsetStr.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(offsetStr);
        }
        return 0;
    }

    /**
     * Generate monthly fee configuration for specified merchant account product
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer generateMerchantMonthFeeConfig(final QueryMerchantMonthVo vo) {
        final Integer activeMonth = vo.getActiveMonth();

        validateYearMonthFormat(activeMonth);
        // Step1: Query valid products
        final List<AccountProductDto> productList = queryValidProducts(vo);

        // Step 2: Filter products with activeMonth <= account's local time zone month
        List<AccountProductDto> finalProductList = productList;
        if (!Optional.ofNullable(vo.getIsAdvance()).orElse(false)) {
            finalProductList = filterProductsBeforeLocalDate(productList, activeMonth);
        }

        // Step3: Filter Products without initialized product version
        // Record product versions that have not generated monthly fee configuration
        final List<AccountProductVersionDto> unGenMonthFeeProductVersions = new ArrayList<>();
        final List<AccountProductDto> unInitProducts =
                filterUnInitProducts(finalProductList, unGenMonthFeeProductVersions,
                        activeMonth);

        // Step4: Get the collection of product versions that need to be initialized
        final List<AccountProductVersionVo> initProductVersionList =
                getInitProductVersionList(unInitProducts, activeMonth);

        // Step5: Batch insert product version
        batchInsertProductVersions(initProductVersionList, unGenMonthFeeProductVersions);

        // Step6: Publish event
        publisher.publishEvent(new FeeMonthConfigGenerateEvent(
                unGenMonthFeeProductVersions, false, null));

        return unGenMonthFeeProductVersions.size();
    }

    private void validateYearMonthFormat(final Integer activeMonth) {
        if (!Pattern.matches(YEAR_MONTH_REGEX, String.valueOf(activeMonth))) {
            throw new IllegalArgumentException(
                    "Invalid month format. Please provide a valid month in the format yyyyMM.");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void generateMonthFeeConfigEvent(final FeeMonthConfigGenerateEvent event) {

        final List<AccountProductVersionDto> productVersionList = event.getProductVersionVoList();
        if (CollectionUtils.isEmpty(productVersionList)) {
            return;
        }

        final Map<Long, AccountProductVersionDto> productVersionMap = productVersionList.stream()
                .collect(Collectors.toMap(AccountProductVersionDto::getAccountProductId,
                        Function.identity()));
        final Integer activeMonth = productVersionList.get(0).getActiveMonth();  // yyyyMM
        final LocalDate prevMonthDate = productVersionList.get(0).getStartDate().minusMonths(1);
        final LocalDate prevMonthStartDate = prevMonthDate.withDayOfMonth(1);
        final LocalDate prevMonthEndDate =
                prevMonthDate.withDayOfMonth(prevMonthDate.lengthOfMonth());

        // Step 1: Get the product Ids to initialize
        final Set<Long> productIds = productVersionList.stream()
                .map(AccountProductVersionDto::getAccountProductId).collect(Collectors.toSet());

        // Step 2: Get the products and product groups to initialize
        final AccountProductGroupDto dto = getGroupProductsOfProduct(productIds);
        final List<AccountProductDto> productList = dto.getProductList();
        final List<AccountProductDto> productGroupList = dto.getProductGroupList();

        // Step 3: Retrieve last month's billing stats，merge data for the same product group.
        final Map<Long, ProductTransactionSumBo> mergedProductBillsMap =
                processAccountBilling(productList, productGroupList, prevMonthStartDate,
                        prevMonthEndDate);

        // Step 4: Generate And Insert monthly fee configuration data
        final List<MonthlyFeeConfigurationDto> monthlyFeeConfigDtoList =
                generateAndInsertMonthlyFeeConfiguration(activeMonth, productList,
                        productVersionMap, mergedProductBillsMap);

        // Step 5: Reset AccountProductVersion monthFlag = 1
        resetProductVersionMonthFlag(monthlyFeeConfigDtoList, productVersionList);

        // Step6: lark remind
        larkRemind(monthlyFeeConfigDtoList, productIds);

        // Step7: compare current month fee config and last month fee config
        if (Boolean.TRUE.equals(event.getAutoGenerate())) {
            log.info("Generate monthly fee configuration successfully! " +
                            "activeMonth: {}, timezone: {}",
                    activeMonth, productList.get(0).getTimezone());
            publisher.publishEvent(new CompareMonthConfigEvent(monthlyFeeConfigDtoList));
        }
    }

    private List<AccountProductDto> queryAllProducts() {
        final ResponseDto<List<AccountProductDto>> response =
                baseFeign.listAccountProduct(new ListAccountProductVo());
        CheckResponseUtil.checkResponseData(response);
        return response.getData();
    }

    private List<AccountProductDto> queryValidProducts(final QueryMerchantMonthVo vo) {
        // step0: query merchant
        List<Long> merchantIds = new ArrayList<>();
        if (Objects.isNull(vo.getMerchantId())) {
            final ResponseDto<List<MerchantDto>> merchantResponse = baseFeign.queryAllMerchant();
            CheckResponseUtil.checkResponseData(merchantResponse);
            merchantIds.addAll(merchantResponse.getData().stream()
                    .map(MerchantDto::getId).collect(Collectors.toList()));
        } else {
            merchantIds.add(vo.getMerchantId());
        }

        // step1: query all accounts of merchant
        final ResponseDto<List<AccountDto>> accountResponse = statementFeign.listAccount(
                ListAccountVo.builder().merchantIds(merchantIds).build());
        CheckResponseUtil.checkResponseData(accountResponse);
        final List<AccountDto> accounts = accountResponse.getData();

        // step2: parameter accountId exists, filter out unmatched accountId
        if (Objects.nonNull(vo.getAccountId())) {
            accounts.removeIf(account -> !account.getId().equals(vo.getAccountId()));
            if (accounts.isEmpty()) {
                throw WorkerExceptionCode.UNKNOWN_ACCOUNT_INFO.exception(vo.getAccountId());
            }
        }

        // step3: query the product list for all accounts
        final List<Long> accountIds =
                accounts.stream().map(AccountDto::getId).collect(Collectors.toList());

        final List<AccountProductDto> products = baseFeign.listAccountProduct(
                ListAccountProductVo.builder().accounts(accountIds).build()).getData();

        // step4: parameter accountProductId exists, filter out unmatched accountProductId
        if (ObjectUtils.isNotEmpty(vo.getAccountProductId())) {
            products.removeIf(product -> !product.getId().equals(vo.getAccountProductId()));
            if (products.isEmpty()) {
                throw WorkerExceptionCode.UNKNOWN_PRODUCT_INFO.exception();
            }
        }
        return products;
    }

    private List<AccountProductDto> filterProductsBeforeLocalDate(
            final List<AccountProductDto> productDtoList, final Integer activeMonth) {

        if (productDtoList.isEmpty()) {
            return Collections.emptyList();
        }

        // group by time zone
        final Map<String, List<AccountProductDto>> timeZoneProductsMap = productDtoList.stream()
                .collect(Collectors.groupingBy(AccountProductDto::getTimezone));

        // Filter out products before activeMonth
        List<AccountProductDto> filteredProducts = new ArrayList<>();
        timeZoneProductsMap.entrySet().stream()
                .filter(entry -> DateUtil.isMonthBeforeOrEqual(activeMonth, entry.getKey()))
                .forEach(entry -> filteredProducts.addAll(entry.getValue()));

        return filteredProducts;
    }

    private static List<AccountProductDto> filterFirstDayOfMonthProducts(
            final List<AccountProductDto> productDtoList) {

        if (productDtoList.isEmpty()) {
            return Collections.emptyList();
        }

        // group by time zone
        final Map<String, List<AccountProductDto>> timeZoneProductsMap = productDtoList.stream()
                .collect(Collectors.groupingBy(AccountProductDto::getTimezone));

        // filter out the first day of the month
        return timeZoneProductsMap.entrySet().stream()
                .filter(entry -> DateUtil.isCurrentMonthFirstDay(entry.getKey()))
                .flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());
    }

    private Integer determineActiveMonth(final String earliestTimezone) {
        final LocalDateTime monthLastLimitTime = LocalDateTime.of(
                DateUtil.getLastDayOfCurrentMonth(earliestTimezone),
                LocalTime.of(22, 0));

        final LocalDate earliestTimezoneNowDate = LocalDate.now(ZoneId.of(earliestTimezone));

        if (LocalDateTimeUtil.nowUtcToLocal(earliestTimezone).isAfter(monthLastLimitTime)) {
            return Integer.valueOf(earliestTimezoneNowDate.plusMonths(1)
                    .format(LocalDateUtil.FORMAT_YYYYMM));
        }

        return Integer.valueOf(earliestTimezoneNowDate.format(LocalDateUtil.FORMAT_YYYYMM));
    }

    private List<AccountProductDto> filterUnInitProducts(
            final List<AccountProductDto> productDtoList,
            List<AccountProductVersionDto> unGenMonthFeeProductVersions) {
        return filterUnInitProducts(productDtoList, unGenMonthFeeProductVersions, null);
    }

    private List<AccountProductDto> filterUnInitProducts(
            final List<AccountProductDto> productDtoList,
            final List<AccountProductVersionDto> unGenMonthFeeProductVersions,
            Integer activeMonth) {

        // if there is no product on the first day of the month, return directly
        if (productDtoList.isEmpty()) {
            return Collections.emptyList();
        }

        if (activeMonth == null) {
            // get the current date in the time zone where the first product is located
            // since all dates in firstDayProducts represent the first day of the current month.
            activeMonth = Integer.valueOf(
                    ZonedDateTime.now(ZoneId.of(productDtoList.get(0).getTimezone()))
                            .format(LocalDateUtil.FORMAT_YYYYMM));
        }

        // query the version of the product that has been initialized for the current month
        final ResponseDto<List<AccountProductVersionDto>> productVersionResponse =
                baseFeign.listAccountProductVersion(
                        QueryAccountProductVersionVo.builder().activeMonth(activeMonth)
                                .accountProductIds(
                                        productDtoList.stream().map(AccountProductDto::getId)
                                                .collect(Collectors.toList()))
                                .build());
        CheckResponseUtil.checkResponse(productVersionResponse);
        final List<AccountProductVersionDto> productVersionList = productVersionResponse.getData();

        final Set<Long> initProductIdSet = productVersionResponse.getData().stream()
                .map(AccountProductVersionDto::getAccountProductId)
                .collect(Collectors.toSet());

        // monthlyFlag = false, indicating that the monthly fee has not been generated
        unGenMonthFeeProductVersions.addAll(productVersionList.stream()
                .filter(x -> !x.getMonthlyFlag())
                .collect(Collectors.toList()));

        // filter out products that have not been initialized for the current month
        return productDtoList.stream().filter(
                        product -> !initProductIdSet.contains(product.getId()))
                .collect(Collectors.toList());
    }

    private List<AccountProductVersionVo> getInitProductVersionList(
            final List<AccountProductDto> productsWithBill) {
        return getInitProductVersionList(productsWithBill, null);
    }

    private List<AccountProductVersionVo> getInitProductVersionList(
            final List<AccountProductDto> productsWithBill, Integer activeMonth) {

        if (productsWithBill.isEmpty()) {
            return Collections.emptyList();
        }

        if (activeMonth == null) {
            // get the last day of the previous month in the timezone of the first product
            // since all dates in productsWithBill represent the first day of the current month.
            final String timezone = productsWithBill.get(0).getTimezone();
            activeMonth = Integer.valueOf(
                    DateUtil.getCurrentDate(timezone).format(LocalDateUtil.FORMAT_YYYYMM));
        }
        LocalDate lastDayOfLastMonth = DateUtil.getLastDayOfLastMonth(activeMonth);
        LocalDate firstDayOfCurrentMonth = DateUtil.getFirstDayOfCurrentMonth(activeMonth);
        LocalDate lastDayOfCurrentMonth = DateUtil.getLastDayOfCurrentMonth(activeMonth);

        // query the maximum version number of the previous month's fee configuration
        final ResponseDto<List<AccountProductVersionDto>> lastMonthMaxVersionResponse =
                baseFeign.listAccountProductVersion(QueryAccountProductVersionVo.builder()
                        .accountProductIds(productsWithBill.stream().map(AccountProductDto::getId)
                                .collect(Collectors.toList())).activeDate(lastDayOfLastMonth)
                        .build());
        CheckResponseUtil.checkResponse(lastMonthMaxVersionResponse);
        final Map<Long, AccountProductVersionDto> productMaxVersionMap =
                lastMonthMaxVersionResponse.getData().stream().collect(
                        Collectors.toMap(AccountProductVersionDto::getAccountProductId,
                                Function.identity()));

        final Integer finalActiveMonth = activeMonth;
        return productsWithBill.stream().map(x -> {
            final AccountProductVersionVo.AccountProductVersionVoBuilder build =
                    AccountProductVersionVo.builder().accountId(x.getAccountId())
                            .accountProductId(x.getId()).activeMonth(finalActiveMonth)
                            .startDate(firstDayOfCurrentMonth).endDate(lastDayOfCurrentMonth)
                            .accountFeeVersion(1).monthlyFeeVersion(1)
                            .monthlyFlag(false);

            // version on the basis of the previous month's fee configuration
            if (productMaxVersionMap.containsKey(x.getId())) {
                build.accountFeeVersion(
                        productMaxVersionMap.get(x.getId()).getAccountFeeVersion());
            }
            return build.build();
        }).collect(Collectors.toList());
    }

    private void batchInsertProductVersions(
            final List<AccountProductVersionVo> initProductVersionList,
            final List<AccountProductVersionDto> unGenMonthFeeProductVersions) {

        if (initProductVersionList.isEmpty()) {
            return;
        }

        final ResponseDto<List<AccountProductVersionDto>> batchAddResponse =
                baseFeign.batchAddAccountProductVersion(initProductVersionList);
        CheckResponseUtil.checkResponseData(batchAddResponse);
        final List<AccountProductVersionDto> responseData = batchAddResponse.getData();
        unGenMonthFeeProductVersions.addAll(responseData);
    }

    private AccountProductGroupDto getGroupProductsOfProduct(final Set<Long> productIds) {
        final ResponseDto<AccountProductGroupDto> accountProductGroupResponse =
                baseFeign.queryGroupProductsOfProduct(QueryAccountProductGroupVo.builder()
                        .accountProductIds(new ArrayList<>(productIds)).build());

        CheckResponseUtil.checkResponseData(accountProductGroupResponse);
        return accountProductGroupResponse.getData();
    }

    private Map<Long, ProductTransactionSumBo> processAccountBilling(
            final List<AccountProductDto> productList,
            final List<AccountProductDto> productGroupList,
            final LocalDate prevMonthStartDate,
            final LocalDate prevMonthEndDate) {
        // Get previous month account billing statistics
        final Map<Long, AccountMonthTransactionSumBo> prevMonthAccountBillMap =
                getAccountBillSumMap(productGroupList, prevMonthStartDate, prevMonthEndDate);

        // Merge billing data of each product
        return mergeSameGroupBill(productList, prevMonthAccountBillMap);
    }

    private Map<Long, AccountMonthTransactionSumBo> getAccountBillSumMap(
            final List<AccountProductDto> productList, final LocalDate startDate,
            final LocalDate endDate) {

        if (productList.isEmpty()) {
            return Collections.emptyMap();
        }

        final List<Long> accountIds =
                productList.stream().map(AccountProductDto::getAccountId).distinct()
                        .collect(Collectors.toList());

        final ResponseDto<List<ListTransactionSummaryDto>> summaryResponse =
                statementFeign.listTransactionSummary(
                        ListTransactionSummaryVo.builder().accountIds(accountIds)
                                .startDate(startDate).endDate(endDate).build());
        CheckResponseUtil.checkResponse(summaryResponse);

        return summaryResponse.getData().stream()
                .collect(Collectors.groupingBy(ListTransactionSummaryDto::getAccountId)).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    final BigDecimal transactionCountSum = entry.getValue().stream()
                            .map(summaryDto -> new BigDecimal(summaryDto.getTransactionCount()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    final BigDecimal transactionAmountSum = entry.getValue().stream()
                            .map(ListTransactionSummaryDto::getSettlementVolumeAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new AccountMonthTransactionSumBo(entry.getKey(), transactionCountSum,
                            transactionAmountSum);
                }));
    }

    private Map<Long, ProductTransactionSumBo> mergeSameGroupBill(
            final List<AccountProductDto> productList,
            final Map<Long, AccountMonthTransactionSumBo> prevMonthAccountBillMap) {

        // step1: toMap: key-> product group name, value->product id list
        final Map<String, List<Long>> groupNameMap = productList.stream().collect(
                Collectors.groupingBy(AccountProductDto::getMonthlyVolumeGroup,
                        Collectors.mapping(AccountProductDto::getId, Collectors.toList())));

        // step2: iterate through the groups
        final Map<Long, ProductTransactionSumBo> mergedProductBillsMap = new HashMap<>();
        groupNameMap.forEach((groupName, productIds) -> {
            log.info("group name:{},product id list:{}", groupName, productIds);

            BigDecimal countSum = BigDecimal.ZERO;
            BigDecimal amountSum = BigDecimal.ZERO;

            // step2.1: merge the account bills of the same group
            for (String accountIdStr : groupName.split(",")) {
                final Long accountId = Long.valueOf(accountIdStr);
                final AccountMonthTransactionSumBo accountSumBo =
                        prevMonthAccountBillMap.get(accountId);
                if (accountSumBo != null) {
                    countSum = countSum.add(accountSumBo.getTransactionCountSum());
                    amountSum = amountSum.add(accountSumBo.getTransactionAmountSum());
                }
            }

            // step2.2 put the merged product bills into the map
            for (Long productId : productIds) {
                mergedProductBillsMap.put(productId,
                        new ProductTransactionSumBo(productId, countSum, amountSum));
            }
        });
        return mergedProductBillsMap;
    }

    private List<MonthlyFeeConfigurationDto> generateAndInsertMonthlyFeeConfiguration(
            final Integer activeMonth,
            final List<AccountProductDto> productList,
            final Map<Long, AccountProductVersionDto> productVersionMap,
            final Map<Long, ProductTransactionSumBo> mergedProductBillsMap) {

        // Get monthly fee configuration data
        final List<MonthlyFeeConfigurationVo> monthlyFeeConfgList =
                generateMonthFeeConfig(activeMonth, productList, productVersionMap,
                        mergedProductBillsMap);

        // batch insert monthly fee configuration
        final ResponseDto<List<MonthlyFeeConfigurationDto>> addResponse =
                baseFeign.batchAddMonthlyFeeConfig(
                        new BatchAddMonthlyFeeConfigVo(monthlyFeeConfgList));
        CheckResponseUtil.checkResponseData(addResponse);

        return addResponse.getData();
    }

    private List<MonthlyFeeConfigurationVo> generateMonthFeeConfig(
            final Integer activeMonth,
            final List<AccountProductDto> productList,
            final Map<Long, AccountProductVersionDto> productVersionMap,
            final Map<Long, ProductTransactionSumBo> mergedProductBillsMap) {

        // build queryVoList
        final List<QueryProductFeeMatchVo> queryVoList = productList.stream().map(product -> {
            final Long productId = product.getId();
            final ProductTransactionSumBo productSumBo = mergedProductBillsMap.get(productId);
            final MonthlyVolumeTypeEnum monthlyVolumeType = product.getMonthlyVolumeType();
            final BigDecimal monthlyVolume = (monthlyVolumeType == MonthlyVolumeTypeEnum.AMOUNT)
                    ? productSumBo.getTransactionAmountSum()
                    : productSumBo.getTransactionCountSum();
            return new QueryProductFeeMatchVo(List.of(productId),
                    productVersionMap.get(productId).getAccountFeeVersion(), monthlyVolumeType,
                    monthlyVolume);
        }).collect(Collectors.toList());

        // batch match product fee configs
        final ResponseDto<List<AccountFeeConfigurationDto>> matchResponseVo =
                baseFeign.batchMatchProductFeeConfigs(new BatchQueryProductFeeMatchVo(queryVoList));
        CheckResponseUtil.checkResponse(matchResponseVo);

        // convert the matched fee configuration to the monthly fee configuration
        return matchResponseVo.getData().stream().map(feeDto -> {
            final MonthlyFeeConfigurationVo bean = new MonthlyFeeConfigurationVo();
            BeanCopierUtil.copyProperties(feeDto, bean);
            bean.setId(null);
            bean.setActiveMonth(activeMonth);
            bean.setAccountFeeConfigurationId(feeDto.getId());
            // The monthly version in the product version table
            bean.setVersion(
                    productVersionMap.get(feeDto.getAccountProductId()).getMonthlyFeeVersion());
            return bean;
        }).collect(Collectors.toList());
    }

    /**
     * Reset the monthly fee configuration generation flag of the product version table
     */
    private void resetProductVersionMonthFlag(
            final List<MonthlyFeeConfigurationDto> monthlyFeeConfigDtoList,
            final List<AccountProductVersionDto> productVersionList) {

        final List<Long> monthlyProductIds = monthlyFeeConfigDtoList.stream()
                .map(MonthlyFeeConfigurationDto::getAccountProductId)
                .distinct()
                .collect(Collectors.toList());

        final List<Long> productVersionIds = productVersionList.stream()
                .filter(x -> monthlyProductIds.contains(x.getAccountProductId()))
                .map(AccountProductVersionDto::getId).collect(Collectors.toList());

        final ResponseDto<Long> editResponse = baseFeign.updateAccountProductVersionMonthFlag(
                new EditAccountProductVersionMonthlyFlagVo(productVersionIds, true));
        CheckResponseUtil.checkResponseData(editResponse);
        log.info("update AccountProductVersion flag = 1 success, ids: {}", productVersionIds);
    }

    private void larkRemind(final List<MonthlyFeeConfigurationDto> monthlyFeeConfgList,
                            final Set<Long> productIds) {
        final List<Long> monthlyProductIds = monthlyFeeConfgList.stream()
                .map(MonthlyFeeConfigurationDto::getAccountProductId)
                .distinct()
                .collect(Collectors.toList());

        final int generatedProductCount = monthlyProductIds.size();
        final int generatedConfigCount = monthlyFeeConfgList.size();

        final String successTitle = "Monthly Fee Configuration Generated Successfully";
        final StringBuilder successMessage = new StringBuilder()
                .append("**Products Generated:** ").append(generatedProductCount).append("\\n")
                .append("**Configurations Generated:** ").append(generatedConfigCount);

        larkRobotMonitor.info(successTitle, successMessage.toString(), "");

        final int failedProductCount = productIds.size() - generatedProductCount;
        if (failedProductCount > 0) {
            monthlyProductIds.forEach(productIds::remove);
            if (!productIds.isEmpty()) {
                final String errorTitle = "Monthly Fee Configuration Generation Failed";
                final String errorMessage = "**Failed ProductIds:** " + productIds;
                larkRobotMonitor.error(errorTitle, errorMessage, "");
            }
        }
    }

    @Override
    public void compareMonthConfigDifferenceAndLastMonth(final CompareMonthConfigEvent event) {
        final List<MonthlyFeeConfigurationDto> currentMonthFeeConfig =
                event.getMonthlyFeeConfigDtoList();

        if (ObjectUtils.isEmpty(currentMonthFeeConfig)) {
            return;
        }

        if (Objects.isNull(monthlyFeeConfig.getLarkMessage())
                || ObjectUtils.isEmpty(monthlyFeeConfig.getLarkMessage().getReceiveId())) {
            log.error("monthly fee config lark message config error");
        }

        final Integer activeMonth = currentMonthFeeConfig.get(0).getActiveMonth();
        final Integer lastActiveMonth =
                Integer.valueOf(DateUtil.getFirstDayOfCurrentMonth(activeMonth).minusMonths(1)
                        .format(DateTimeFormatter.ofPattern("yyyyMM")));

        final List<Long> accountIds =
                currentMonthFeeConfig.stream().map(MonthlyFeeConfigurationDto::getAccountId)
                        .distinct().collect(Collectors.toList());

        final Map<Long, List<MonthlyFeeConfigurationDto>> currentMonthFeeMap =
                currentMonthFeeConfig.stream()
                        .collect(Collectors.groupingBy(MonthlyFeeConfigurationDto::getAccountId));

        //get last month fee config
        final ResponseDto<List<MonthlyFeeConfigurationDto>> responseDto =
                baseFeign.listMonthFeeConfig(
                        QueryMonthFeeConfigVo.builder().accountIds(accountIds)
                                .activeMonth(lastActiveMonth).build());

        CheckResponseUtil.checkResponseData(responseDto);

        final Map<MonthFeeConfigGroup, MonthlyFeeConfigurationDto> map =
                responseDto.getData().stream().collect(Collectors.toMap(
                        v -> (new MonthFeeConfigGroup(v.getAccountId(),
                                v.getAccountProductId(), v.getFeeCode(), v.getCalculationRule())),
                        Function.identity()));

        final ResponseDto<List<MerchantDto>> merchantResponse = baseFeign.queryAllMerchant();
        final List<MerchantDto> merchantList = merchantResponse.getData();

        final List<AccountDto> accountDtoList = statementService.queryAllAccount();

        final Map<Long, LarkBathMessageVo> merchantIdAndBathMessageMap = new HashMap<>();

        // compare current month fee config and last month fee config different
        currentMonthFeeMap.forEach((key, value) -> value.forEach(cmf -> {

            final AccountDto accountDto =
                    accountDtoList.stream().filter(v -> v.getId().equals(key)).findFirst()
                            .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);

            // build map key
            final MonthFeeConfigGroup mapKey = new MonthFeeConfigGroup(cmf.getAccountId(),
                    cmf.getAccountProductId(), cmf.getFeeCode(), cmf.getCalculationRule());

            final MonthlyFeeConfigurationDto dto = map.get(mapKey);

            if (Objects.nonNull(dto)) {
                // if current month fee config not equals last month fee config send lark message
                if (cmf.getFeeOn() != dto.getFeeOn()
                        || cmf.getFeeValueModel() != dto.getFeeValueModel()
                        || cmf.getFeeValue().compareTo(dto.getFeeValue()) != 0) {
                    final LarkBathMessageVo larkBathMessageVo =
                            buildLarkBathMessageVo(merchantIdAndBathMessageMap, merchantList,
                                    accountDto);

                    final String context = "\\n"
                            + accountDto.getTransactionTypeCode() + "-"
                            + dto.getProductCode();

                    larkBathMessageVo.setContent(larkBathMessageVo.getContent() + context);
                }
            }
        }));
        baseFeign.bathSendListLarkMessage(merchantIdAndBathMessageMap.values());
    }

    private LarkBathMessageVo buildLarkBathMessageVo(
            final Map<Long, LarkBathMessageVo> merchantIdAndBathMessageMap,
            final List<MerchantDto> merchantList,
            final AccountDto accountDto) {

        if (!merchantIdAndBathMessageMap.containsKey(accountDto.getMerchantId())) {
            final LarkBathMessageVo larkBathMessageVo = new LarkBathMessageVo();

            larkBathMessageVo.setContent(String.format(
                    "The fees applicable to ** %s-%s ** for this month have changed.",
                    merchantList.stream().filter(m -> m.getId().equals(accountDto.getMerchantId()))
                            .findFirst()
                            .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception)
                            .getCode(), accountDto.getCountryCode().getCode())
            );
            larkBathMessageVo.setReceiveIds(monthlyFeeConfig.getLarkMessage().getReceiveId());
            larkBathMessageVo.setRemindRank(LarkRemindRankEnum.WARN);
            larkBathMessageVo.setTitle("Applicable Fee Gradient Change");

            merchantIdAndBathMessageMap.put(accountDto.getMerchantId(), larkBathMessageVo);
            return larkBathMessageVo;
        }
        return merchantIdAndBathMessageMap.get(accountDto.getMerchantId());
    }

    @AllArgsConstructor
    @Data
    public static class MonthFeeConfigGroup {
        private Long accountId;

        private Long accountProductId;

        @Convert(converter = FeeCodeEnum.Convert.class)
        private FeeCodeEnum feeCode;

        @Convert(converter = CalculationRuleConverter.class)
        private Map<String, String> calculationRule;
    }
}
