package com.liquido.worker.service.calculate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.MonthFxLoseConfigDto;
import com.liquido.base.pojo.vo.ListAccountMonthFxLoseVo;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.core.common.utils.OkHttpClientUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.CalculateConfigBo;
import com.liquido.statement.pojo.bo.ExchangeRateConfig;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.pojo.vo.DailyExchangeRateVo;
import com.liquido.statement.pojo.vo.ListHourlyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateVo;
import com.liquido.statement.pojo.vo.RealTimeExchangeRateVo;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.feign.BaseService;
import com.liquido.worker.feign.StatementService;
import com.liquido.worker.pojo.bo.RealTimeExchangeRateBo;
import com.liquido.worker.pojo.dto.ExchangeRateAuthDto;
import com.liquido.worker.pojo.dto.ExchangeRateDto;
import com.liquido.worker.pojo.dto.FxQuoteDto;
import com.liquido.worker.pojo.mapper.ModelMapper;
import com.liquido.worker.pojo.vo.InitExchangeManualVo;
import com.liquido.worker.pojo.vo.QueryExchangeRateVo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RefreshScope
@RequiredArgsConstructor
public class ExchangeRateManager {

    private final ModelMapper modelMapper;
    private final BaseService baseService;
    private final RedisCacheUtil redisCacheUtil;
    private final StatementService statementService;
    private final LarkRobotMonitor larkRobotMonitor;
    private final WorkerProperties.ExchangeRateProperties exchangeRateProperties;

    private static String buildCacheKey(final LocalDateTime exchangeTime) {
        return String.join(":", Constant.CACHE.EXCHANGE_RATE_HASH,
                exchangeTime.format(LocalDateUtil.FORMAT_YYYYMMDD));
    }

    private static String buildCacheHashKey(final Long accountId,
                                            final LocalDateTime exchangeTime) {
        return String.join(":", String.format("%02d", exchangeTime.getHour()),
                accountId.toString());
    }

    /**
     * Initialization exchange rate
     */
    public void initExchangeRate() {

        // Initialize real-time exchange rates from remote service
        final List<RealTimeExchangeRateVo> realTimeRateList = this.fetchRealTimeExchangeRates();

        // Query all accounts
        final List<AccountDto> accountList = statementService.queryAllAccount();

        // Build account exchange rates
        final List<DailyExchangeRateVo> accountRates =
                this.buildAccountExchangeRates(accountList, realTimeRateList);

        // Save data to statement service
        statementService.saveExchangeRate(LocalDateTimeUtil.nowUtc(), realTimeRateList,
                accountRates);
    }

    private List<RealTimeExchangeRateVo> fetchRealTimeExchangeRates() {
        try {
            return Optional.ofNullable(exchangeRateProperties.getHourlyExchangeRate())
                    .orElse(Collections.emptyList()).stream().distinct()
                    .map(x -> this.getRealTimeExchangeRate(
                            QueryExchangeRateVo.builder().sourceCurrency(x.getSourceCurrency())
                                    .targetCurrency((x.getTargetCurrency())).build()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("init hourly exchange rate error:", e);
            return List.of();
        }
    }

    private List<DailyExchangeRateVo> buildAccountExchangeRates(
            final List<AccountDto> accountList,
            final List<RealTimeExchangeRateVo> realTimeRateList) {

        if (CollectionUtils.isEmpty(accountList) || CollectionUtils.isEmpty(realTimeRateList)) {
            return Collections.emptyList();
        }

        // Build real-time exchange rate map
        final Map<String, RealTimeExchangeRateVo> realTimeRateMap = realTimeRateList.stream()
                .collect(Collectors.toMap(item -> this.realTimeRateKey(item.getSourceCurrency(),
                        item.getTargetCurrency()), Function.identity(), (v1, v2) -> v2));

        final Map<Long, BigDecimal> fxLoseMap = baseService.listAllAccountMonthFxLose(
                        ListAccountMonthFxLoseVo.builder().build()).stream()
                .collect(Collectors.toMap(MonthFxLoseConfigDto::getAccountId,
                        MonthFxLoseConfigDto::getFeeValue, (v1, v2) -> v2));

        final List<DailyExchangeRateVo> dailyExchangeRateList = new ArrayList<>();
        for (AccountDto account : accountList) {
            // Get fx-lose
            final BigDecimal fxLose = fxLoseMap.getOrDefault(account.getId(), BigDecimal.ZERO);
            // Get exchange rate config
            final var exchangeRateList = extractExchangeRatesConfig(account);
            for (ExchangeRateConfig.DailyExchangeRate rate : exchangeRateList) {
                final CurrencyEnum sourceCurrency = rate.getSourceCurrency();
                final CurrencyEnum targetCurrency = rate.getTargetCurrency();
                // Get real-time exchange rate
                final var realTimeExchangeRate =
                        realTimeRateMap.get(this.realTimeRateKey(sourceCurrency, targetCurrency));
                try {
                    // build account exchange rate
                    dailyExchangeRateList.add(buildDailyExchangeRate(account, realTimeExchangeRate,
                            sourceCurrency, targetCurrency, fxLose));
                } catch (Exception e) {
                    log.error("init daily-exchange-rate error. accountId={}",
                            account.getId(), e);
                }
            }
        }
        return dailyExchangeRateList;
    }

    private String realTimeRateKey(final CurrencyEnum source, final CurrencyEnum target) {
        return source.getCode().concat(target.getCode());
    }

    private List<ExchangeRateConfig.DailyExchangeRate> extractExchangeRatesConfig(
            final AccountDto account) {
        return Optional.ofNullable(account.getAccountConfig())
                .map(AccountConfigDto::getExchangeRateConfig)
                .map(ExchangeRateConfig::getDailyExchangeRates).orElse(Collections.emptyList())
                .stream()
                // filter out the configurations that are not enabled
                .filter(ExchangeRateConfig.DailyExchangeRate::isEnabled)
                .collect(Collectors.toList());
    }

    /**
     * get realtime exchangeRate from remote server and save to database
     *
     * @param account      account accountInfo
     * @param rate         exchangeRate
     * @param fromCurrency from currency
     * @param toCurrency   to currency
     * @param fxLose       fx-lose
     * @return rate
     */
    private DailyExchangeRateVo buildDailyExchangeRate(final AccountDto account,
                                                       final RealTimeExchangeRateVo rate,
                                                       final CurrencyEnum fromCurrency,
                                                       final CurrencyEnum toCurrency,
                                                       final BigDecimal fxLose) {

        final BigDecimal exchangeRate = rate.getExchangeRate();
        final BigDecimal merchantFxRate;
        if (TransactionTypeCodeEnum.PAY_IN == account.getTransactionTypeCode()) {
            merchantFxRate = Objects.isNull(exchangeRate) ? BigDecimal.ZERO :
                    exchangeRate.multiply(BigDecimal.ONE.add(fxLose))
                            .setScale(6, RoundingMode.HALF_UP);
        } else {
            // PAY_OUT, MARKET_PLACE
            merchantFxRate = Objects.isNull(exchangeRate) ? BigDecimal.ZERO :
                    AmountUtil.division(exchangeRate, BigDecimal.ONE.add(fxLose), 6);
        }

        /* after init daily exchange rate success, save data to statement-service */
        return DailyExchangeRateVo.builder().merchantId(account.getMerchantId())
                .accountId(account.getId()).sourceCurrency(fromCurrency).targetCurrency(toCurrency)
                .exchangeTime(rate.getExchangeTime().withMinute(0).withSecond(0).withNano(0))
                .exchangeRate(exchangeRate).ratioLose(fxLose).merchantRate(merchantFxRate).build();
    }

    /**
     * Get exchange rate info
     *
     * @param account         accountInfo
     * @param transactionTime transactionTime(UTC0)
     * @param orderCurrency   orderCurrency
     * @return
     */
    public Pair<DailyExchangeRateDto, DailyExchangeRateDto> getExchangeRate(
            final MerchantDto merchantDto,
            final AccountDto account,
            final LocalDateTime transactionTime,
            final CurrencyEnum orderCurrency) {

        // check calculate config value
        final boolean isRealTimeRate =
                Optional.ofNullable(account.getAccountConfig().getConfigData())
                        .map(AccountConfigData::getCalculateConfig)
                        .map(CalculateConfigBo::getRealTimeRate)
                        .orElseThrow(() -> {
                            final String content =
                                    larkRobotMonitor.buildLarkAlarmContent(merchantDto.getCode(),
                                            account.getCountryCode().getCountryName(),
                                            account.getTransactionTypeCode().getCode(),
                                            "Account missing calculate config");

                            larkRobotMonitor.error("Missing Calculate Config", content,
                                    String.format("account id: %s", account.getId()));

                            // warning and set as default value: false
                            return WorkerExceptionCode.GET_EXCHANGE_RATE_FAIL
                                    .exception(orderCurrency);
                        });

        // Use realtime exchange rate
        final LocalDateTime exchangeTime;
        if (isRealTimeRate) {
            exchangeTime = transactionTime.withMinute(0).withSecond(0).withNano(0);
            log.info("Use realtime exchange rate, account = {}, exchangeTime = {}", account,
                    exchangeTime);
        } else {
            /* Non-realtime exchange rate,
             * Get the exchange rate value corresponding to 0 o'clock
             * in the time zone where the account is located;
             *
             * Step1:Convert the transactionTime to 00:00:00 of the corresponding account time zone;
             * Step2:Convert the corresponding time of the account to UTC0 zone time;
             */
            exchangeTime = LocalDateTimeUtil.localToUtc(
                    transactionTime.atZone(Constant.COMMON.ZONE_UTC)
                            .withZoneSameInstant(ZoneId.of(account.getTimezone())).toLocalDateTime()
                            .withHour(0).withMinute(0).withSecond(0).withNano(0),
                    account.getTimezone());
            log.info("Use Non-realtime exchange rate, account = {}, exchangeTime = {}", account,
                    exchangeTime);
        }

        // Check currency, Invalid when there are 3 currency types
        if (Sets.newHashSet(orderCurrency, account.getCurrency(), CurrencyEnum.USD).size() == 3) {
            throw WorkerExceptionCode.GET_EXCHANGE_RATE_FAIL.exception(orderCurrency);
        }

        final DailyExchangeRateDto usdExchangeRate =
                CurrencyEnum.USD == orderCurrency ? DailyExchangeRateDto.builder()
                        .id(0L)
                        .exchangeTime(exchangeTime)
                        .merchantId(account.getMerchantId())
                        .accountId(account.getId())
                        .sourceCurrency(CurrencyEnum.USD)
                        .targetCurrency(CurrencyEnum.USD)
                        .exchangeRate(BigDecimal.ONE)
                        .merchantRate(BigDecimal.ONE)
                        .ratioLose(BigDecimal.ZERO)
                        .build() : this.loadExchangeRate(account, exchangeTime, orderCurrency);

        final DailyExchangeRateDto accountExchangeRate =
                orderCurrency == account.getCurrency() ? DailyExchangeRateDto.builder()
                        .id(0L)
                        .exchangeTime(exchangeTime)
                        .merchantId(account.getMerchantId())
                        .accountId(account.getId())
                        .sourceCurrency(account.getCurrency())
                        .targetCurrency(account.getCurrency())
                        .exchangeRate(BigDecimal.ONE)
                        .merchantRate(BigDecimal.ONE)
                        .ratioLose(BigDecimal.ZERO)
                        .build() : usdExchangeRate;

        // account->order, usd->order
        return Pair.of(accountExchangeRate, usdExchangeRate);
    }

    /**
     * Load daily exchange rate form cache, if not exist reload from database
     *
     * @param account
     * @param exchangeTime
     * @return
     */
    public DailyExchangeRateDto loadExchangeRate(final AccountDto account,
                                                 final LocalDateTime exchangeTime,
                                                 final CurrencyEnum orderCurrency) {
        /* Try to get from redis cache */
        final String cacheKey = buildCacheKey(exchangeTime);
        final String hashKey = buildCacheHashKey(account.getId(), exchangeTime);
        DailyExchangeRateDto dailyExchangeRateDto =
                redisCacheUtil.getCacheMapValue(cacheKey, hashKey);
        if (Objects.nonNull(dailyExchangeRateDto)) {
            return dailyExchangeRateDto;
        }

        /* Try to get exchangeRate from statement-service */
        dailyExchangeRateDto = statementService.queryDailyExchangeRate(
                QueryDailyExchangeRateVo.builder()
                        .merchantId(account.getMerchantId())
                        .accountId(account.getId())
                        .exchangeTime(exchangeTime)
                        .sourceCurrency(CurrencyEnum.USD)
                        .targetCurrency(orderCurrency)
                        .build());
        if (Objects.isNull(dailyExchangeRateDto)) {
            throw WorkerExceptionCode.GET_EXCHANGE_RATE_FAIL.exception(orderCurrency);
        }

        // Set to cache
        redisCacheUtil.setCacheMapValue(cacheKey, hashKey, dailyExchangeRateDto);
        redisCacheUtil.expire(cacheKey, Constant.CACHE.EXCHANGE_RATE_EXPIRE_DAY, TimeUnit.DAYS);

        return dailyExchangeRateDto;
    }

    /**
     * get Real Time Exchange Rates from remote service
     */
    public RealTimeExchangeRateVo getRealTimeExchangeRate(final QueryExchangeRateVo vo) {
        if (Objects.isNull(vo)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        if (Objects.nonNull(vo.getSourceCurrency())
                && vo.getSourceCurrency() == vo.getTargetCurrency()) {
            return RealTimeExchangeRateVo.builder().exchangeRate(BigDecimal.ONE).build();
        }

        // get access token
        final ExchangeRateAuthDto authDto = getAccessToken();

        final Map<String, String> headers = new HashedMap(1);
        headers.put("Authorization", authDto.getTokenType() + " " + authDto.getAccessToken());

        final Map<String, Object> params = new HashedMap(2);
        params.put("sourceCurrency", vo.getSourceCurrency());
        params.put("targetCurrency", vo.getTargetCurrency());

        final ResponseDto<ExchangeRateDto> responseDto =
                OkHttpClientUtil.postWithJson(exchangeRateProperties.getQuoteUrl(), params, headers,
                        new TypeReference<>() {
                        });

        if (Objects.isNull(responseDto) || responseDto.getCode() != 0
                || Objects.isNull(responseDto.getData())) {
            throw WorkerExceptionCode.GET_EXCHANGE_RATE_FAIL.exception(vo.getTargetCurrency());
        }

        /*
           TASK: See A77;
           The time point for obtaining the exchange rate task
           is triggered five times per hour at minutes 58, 59, 0, 1, and 2,
           If the current time is greater than 30 minutes,
           it will be counted as the exchange rate for the next hour
         */
        LocalDateTime exchangeTime = LocalDateTimeUtil.nowUtc();
        if (exchangeTime.getMinute() >= 30) {
            exchangeTime = exchangeTime.plusHours(1);
        }

        return RealTimeExchangeRateVo.builder().sourceCurrency(vo.getSourceCurrency())
                .targetCurrency(vo.getTargetCurrency())
                .exchangeTime(exchangeTime.withMinute(0).withSecond(0).withNano(0))
                .exchangeRate(responseDto.getData().getExchangeRate()).build();
    }

    public ExchangeRateAuthDto getAccessToken() {
        final ExchangeRateAuthDto dto =
                redisCacheUtil.getCacheObject(Constant.CACHE.EXCHANGE_RATE_AUTH_TOKEN);
        if (Objects.nonNull(dto)) {
            return dto;
        }

        try {
            final Map<String, String> authVo = new HashedMap(3);
            authVo.put("client_id", exchangeRateProperties.getClientId());
            authVo.put("client_secret", exchangeRateProperties.getClientSecret());
            authVo.put("grant_type", exchangeRateProperties.getGrantType());

            log.info("get exchange rate access token begin");
            String result =
                    OkHttpClientUtil.postWithForm(exchangeRateProperties.getAuthUrl(), authVo);
            log.info("get exchange rate access token end, result={}", result);
            if (StringUtil.isBlank(result)) {
                log.error("get exchange rate access token fail, result={}", result);
                throw WorkerExceptionCode.GET_EXCHANGE_RATE_TOKEN_FAIL.exception();
            }

            final ExchangeRateAuthDto authDto = JsonUtil.toBean(result, ExchangeRateAuthDto.class);
            redisCacheUtil.setCacheObject(Constant.CACHE.EXCHANGE_RATE_AUTH_TOKEN, authDto,
                    authDto.getExpiresIn(), TimeUnit.SECONDS);

            return authDto;
        } catch (Exception e) {
            log.error("get exchange rate access token error:", e);
            throw WorkerExceptionCode.GET_EXCHANGE_RATE_TOKEN_FAIL.exception();
        }
    }

    public String initExchangeRateManual(final InitExchangeManualVo vo) {
        // Supplement missing hourly exchange rates and return all hourly exchange rates
        final RealTimeExchangeRateBo realTimeRateBo =
                fetchHourlyExchangeRates(vo.getExchangeTime());

        // Query all accounts
        final List<AccountDto> accountList =
                initExchangeRateAccountList(vo, statementService.queryAllAccount());

        // Build account exchange rates
        final List<DailyExchangeRateVo> accountRates =
                this.buildAccountExchangeRates(accountList,
                        realTimeRateBo.getAllRealTimeExchangeRateList(), vo.getExchangeTime());

        // Save data to statement service
        statementService.saveExchangeRate(vo.getExchangeTime(),
                realTimeRateBo.getSupplementaryRealTimeExchangeRateList(), accountRates);

        return String.format("hourly exchange rate: %s, daily exchange rate: %s",
                realTimeRateBo.getSupplementaryRealTimeExchangeRateList().size(),
                accountRates.size());
    }

    private RealTimeExchangeRateBo fetchHourlyExchangeRates(final LocalDateTime exchangeTime) {

        final List<HourlyExchangeRateDto> existHourlyExchangeRateList =
                statementService.queryHourlyExchangeRate(
                        ListHourlyExchangeRateVo.builder().exchangeTime(exchangeTime).build());

        final List<String> existKey = existHourlyExchangeRateList.stream()
                .map(v -> realTimeRateKey(v.getSourceCurrency(), v.getTargetCurrency()))
                .collect(Collectors.toList());

        final List<RealTimeExchangeRateVo> realTimeExchangeRates =
                Optional.ofNullable(exchangeRateProperties.getHourlyExchangeRate())
                        .orElse(Collections.emptyList()).stream().distinct()
                        .filter(v -> !existKey.contains(
                                realTimeRateKey(v.getSourceCurrency(), v.getTargetCurrency())))
                        .map(x -> this.getRealTimeExchangeRateByTime(
                                QueryExchangeRateVo.builder().sourceCurrency(x.getSourceCurrency())
                                        .targetCurrency((x.getTargetCurrency()))
                                        .exchangeTime(exchangeTime).build()))
                        .filter(v -> Objects.nonNull(v.getExchangeRate()))
                        .collect(Collectors.toList());

        log.info("there are {} exchange rate need to init exchange rate",
                realTimeExchangeRates.size());

        final List<RealTimeExchangeRateVo> allRealTimeExchangeRateList = Stream.concat(
                modelMapper.convertRealTimeExchangeRate(existHourlyExchangeRateList).stream(),
                realTimeExchangeRates.stream()).collect(Collectors.toList());

        return RealTimeExchangeRateBo.builder()
                .supplementaryRealTimeExchangeRateList(realTimeExchangeRates)
                .allRealTimeExchangeRateList(allRealTimeExchangeRateList)
                .build();
    }

    public RealTimeExchangeRateVo getRealTimeExchangeRateByTime(final QueryExchangeRateVo vo) {
        if (Objects.isNull(vo)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        if (Objects.isNull(vo.getExchangeTime())) {
            throw CommonExceptionCode.PARAMETER_MISSING.exception("exchangeTime can't be null");
        }

        if (Objects.nonNull(vo.getSourceCurrency())
                && vo.getSourceCurrency() == vo.getTargetCurrency()) {
            return RealTimeExchangeRateVo.builder().exchangeRate(BigDecimal.ONE).build();
        }

        // get access token
        final ExchangeRateAuthDto authDto = getAccessToken();

        final Map<String, String> headers = new HashedMap(1);
        headers.put("Authorization", authDto.getTokenType() + " " + authDto.getAccessToken());

        final Map<String, Object> params = new HashMap<>();
        params.put("sourceCurrency", vo.getSourceCurrency());
        params.put("targetCurrency", vo.getTargetCurrency());

        final Long endTimestamp = LocalDateTimeUtil.utcToInstant(
                vo.getExchangeTime().withMinute(0).withSecond(0).withNano(0));
        final Long startTimestamp = endTimestamp - 300;

        params.put("start", startTimestamp);
        params.put("end", endTimestamp);

        final FxQuoteDto responseDto =
                OkHttpClientUtil.postWithJson(exchangeRateProperties.getRangeUrl(), params, headers,
                        new TypeReference<>() {
                        });

        if (Objects.isNull(responseDto) || responseDto.getCode() != 0
                || Objects.isNull(responseDto.getTimeRangeData())
                || responseDto.getTimeRangeData().isEmpty()) {
            log.error("get exchange rate {} -> {} failed",
                    vo.getSourceCurrency(), vo.getTargetCurrency());
            return RealTimeExchangeRateVo.builder()
                    .exchangeTime(vo.getExchangeTime().withMinute(0).withSecond(0).withNano(0))
                    .targetCurrency(vo.getTargetCurrency()).sourceCurrency(vo.getSourceCurrency())
                    .build();
        }

        return RealTimeExchangeRateVo.builder().sourceCurrency(vo.getSourceCurrency())
                .targetCurrency(vo.getTargetCurrency())
                .exchangeTime(vo.getExchangeTime().withMinute(0).withSecond(0).withNano(0))
                .exchangeRate(responseDto.getTimeRangeData().get(0).getExchangeRate()).build();
    }

    private List<AccountDto> initExchangeRateAccountList(
            final InitExchangeManualVo vo, final List<AccountDto> accountList) {
        if (Objects.isNull(vo.getAccountIdList()) && Objects.isNull(vo.getCountryCode())) {
            return accountList;
        }

        Stream<AccountDto> stream = accountList.stream();

        if (Objects.nonNull(vo.getAccountIdList()) && !vo.getAccountIdList().isEmpty()) {
            stream = stream.filter(v -> vo.getAccountIdList().contains(v.getId()));
        }

        if (Objects.nonNull(vo.getCountryCode())) {
            stream = stream.filter(v -> vo.getCountryCode().equals(v.getCountryCode()));
        }

        return stream.collect(Collectors.toList());
    }

    private List<DailyExchangeRateVo> buildAccountExchangeRates(
            final List<AccountDto> accountList,
            final List<RealTimeExchangeRateVo> realTimeRateList,
            final LocalDateTime exchangeTime) {

        if (CollectionUtils.isEmpty(accountList) || CollectionUtils.isEmpty(realTimeRateList)) {
            return Collections.emptyList();
        }

        final Map<String, RealTimeExchangeRateVo> realTimeRateMap = realTimeRateList.stream()
                .collect(Collectors.toMap(item -> this.realTimeRateKey(item.getSourceCurrency(),
                        item.getTargetCurrency()), Function.identity(), (v1, v2) -> v2));


        final Map<Long, BigDecimal> fxLoseMap = baseService.listAllAccountMonthFxLose(
                        ListAccountMonthFxLoseVo.builder()
                                .activeTime(exchangeTime)
                                .build()).stream()
                .collect(Collectors.toMap(MonthFxLoseConfigDto::getAccountId,
                        MonthFxLoseConfigDto::getFeeValue, (v1, v2) -> v2));

        final List<String> exitKeyList = statementService.queryDailyExchangeRate(
                        accountList.stream().map(AccountDto::getId).collect(Collectors.toList()),
                        exchangeTime, exchangeTime).stream()
                .map(v -> String.format("%s-%s", v.getAccountId(),
                        this.realTimeRateKey(v.getSourceCurrency(), v.getTargetCurrency())))
                .collect(Collectors.toList());

        final List<DailyExchangeRateVo> newDailyExchangeRateList = new ArrayList<>();

        for (AccountDto account : accountList) {
            final BigDecimal fxLose = fxLoseMap.getOrDefault(account.getId(), BigDecimal.ZERO);
            // Get exchange rate config
            final var exchangeRateList = extractExchangeRatesConfig(account);
            for (ExchangeRateConfig.DailyExchangeRate rate : exchangeRateList) {
                final CurrencyEnum sourceCurrency = rate.getSourceCurrency();
                final CurrencyEnum targetCurrency = rate.getTargetCurrency();

                if (exitKeyList.contains(String.format("%s-%s", account.getId(),
                        this.realTimeRateKey(sourceCurrency, targetCurrency)))) {
                    continue;
                }

                // Get real-time exchange rate
                final RealTimeExchangeRateVo realTimeExchangeRate =
                        realTimeRateMap.get(this.realTimeRateKey(sourceCurrency, targetCurrency));

                if (Objects.isNull(realTimeExchangeRate)) {
                    log.error("Can't find {} {} -> {} hourly exchange rate", account.getId(),
                            sourceCurrency, targetCurrency);
                    continue;
                }
                try {
                    // build account exchange rate
                    newDailyExchangeRateList.add(
                            buildDailyExchangeRate(account, realTimeExchangeRate,
                                    sourceCurrency, targetCurrency, fxLose));
                } catch (Exception e) {
                    log.error("init daily-exchange-rate error. accountId={}",
                            account.getId(), e);
                }
            }
        }
        return newDailyExchangeRateList;
    }

}
