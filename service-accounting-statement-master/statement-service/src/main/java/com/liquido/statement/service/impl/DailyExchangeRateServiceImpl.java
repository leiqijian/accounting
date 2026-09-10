package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.Constant;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.AccountInfoBo;
import com.liquido.statement.pojo.bo.CalculateConfigBo;
import com.liquido.statement.pojo.bo.FxRateInitKey;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.pojo.dto.RealTimeExchangeRateDto;
import com.liquido.statement.pojo.entity.DailyExchangeRate;
import com.liquido.statement.pojo.entity.QAccount;
import com.liquido.statement.pojo.entity.QDailyExchangeRate;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.BatchAddExchangeRateVo;
import com.liquido.statement.pojo.vo.DailyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryAccountVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateListVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryHourlyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryLatestExchangeRateVo;
import com.liquido.statement.repository.DailyExchangeRateRepository;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.DailyExchangeRateService;
import com.liquido.statement.service.HourlyExchangeRateService;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyExchangeRateServiceImpl implements DailyExchangeRateService {

    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;
    private final AccountService accountService;
    private final HourlyExchangeRateService hourlyExchangeRateService;
    private final DailyExchangeRateRepository dailyExchangeRateRepository;
    private final RedisCacheUtil redisCacheUtil;

    private static final int BATCH_COUNT = 1000;

    private final LoadingCache<FxRateInitKey, DailyExchangeRateDto> localRateCache =
            Caffeine.newBuilder()
                    // Maximum number of caches
                    .maximumSize(500)
                    // Fixed time expires after last write
                    .expireAfterWrite(4, TimeUnit.HOURS)
                    // If the value in the cache is empty or null, trigger reload from the database
                    .build(this::loadDailyExchangeRate);

    @Override
    public DailyExchangeRateDto queryDailyExchangeRate(final FxRateInitKey key) {
        return localRateCache.get(key);
    }

    /**
     * get account exchange rate
     *
     * @param vo
     *
     * @return
     */
    @Override
    public DailyExchangeRateDto loadDailyExchangeRate(final FxRateInitKey vo) {
        return this.queryDailyExchangeRate(QueryDailyExchangeRateVo.builder()
                .merchantId(vo.getMerchantId())
                .accountId(vo.getAccountId())
                .exchangeTime(vo.getExchangeTime())
                .sourceCurrency(vo.getSourceCurrency())
                .targetCurrency(vo.getTargetCurrency())
                .build());
    }

    @Override
    public void batchSaveExchangeRate(final BatchAddExchangeRateVo rateVo) {

        // save realtime exchange rate
        if (!CollectionUtils.isEmpty(rateVo.getRealTimeExchangeRateList())) {
            hourlyExchangeRateService.batchSaveExchangeRate(rateVo.getRealTimeExchangeRateList());
        }

        // save account exchange rate
        if (!CollectionUtils.isEmpty(rateVo.getAccountExchangeRateList())) {
            this.batchSaveAccountExchangeRate(rateVo.getAccountExchangeRateList());
        }

        /**
         * TASK: See A77 check rate init success
         *            The time point for obtaining the exchange rate task
         *            is triggered five times per hour at minutes 58, 59, 0, 1, and 2,
         *            If the current time is less than 30 minutes,
         *            it will be counted as the exchange rate for the next hour
         */
        if (rateVo.getObtainTime().getMinute() < 30) {
            try {
                // clear entity cache
                entityManager.flush();
                entityManager.clear();

                // re-init exchange rate
                this.checkAccountExchangeRate();
                log.info("check and init account exchange rate, obtainTime={}",
                        rateVo.getObtainTime());
            } catch (Exception e) {
                log.error("check account exchange rate error, obtainTime={}",
                        rateVo.getObtainTime());
            }
        }
    }

    /**
     * TASK: See A77 check rate init success
     * The time point for obtaining the exchange rate task
     * is triggered five times per hour at minutes 58, 59, 0, 1, and 2,
     * If the current time is greater than 30 minutes,
     * it will be counted as the exchange rate for the next hour
     */
    private void checkAccountExchangeRate() {
        List<AccountInfoBo> acountList;
        Long nextAccountId = 0L;
        do {
            acountList = this.batchLoadAccountList(nextAccountId);
            if (!CollectionUtils.isEmpty(acountList)) {
                nextAccountId = Collections.max(acountList.stream()
                        .map(AccountInfoBo::getAccountId).collect(Collectors.toList()));

                this.processAccountExchangeRate(acountList);
            }
        } while (!CollectionUtils.isEmpty(acountList) && acountList.size() >= BATCH_COUNT);
    }

    private void processAccountExchangeRate(final List<AccountInfoBo> acountList) {
        for (final AccountInfoBo account : acountList) {
            try {
                final LocalDateTime exchangeTime =
                        LocalDateTimeUtil.localToUtc(LocalDateTimeUtil.nowUtcZonedDateTime()
                                        .withZoneSameInstant(ZoneId.of(account.getTimezone()))
                                        .toLocalDateTime().withMinute(0).withSecond(0).withNano(0),
                                account.getTimezone());

                if (CurrencyEnum.USD == account.getCurrency()) {
                    continue;
                }
                final DailyExchangeRateDto rateDto = this.queryDailyExchangeRate(
                        QueryDailyExchangeRateVo.builder()
                                .merchantId(account.getMerchantId())
                                .accountId(account.getAccountId())
                                .sourceCurrency(CurrencyEnum.USD)
                                .targetCurrency(account.getCurrency())
                                .exchangeTime(exchangeTime)
                                .build());
                if (Objects.isNull(rateDto)) {
                    // get latest rate as current exchange rate and save to database;
                    final RealTimeExchangeRateDto latestRate =
                            this.queryLatestExchangeRate(QueryLatestExchangeRateVo.builder()
                                    .merchantId(account.getMerchantId())
                                    .accountId(account.getAccountId())
                                    .sourceCurrency(CurrencyEnum.USD)
                                    .targetCurrency(account.getCurrency())
                                    .build());

                    if (Objects.nonNull(latestRate)) {
                        dailyExchangeRateRepository.saveAndFlush(DailyExchangeRate.builder()
                                .id(SnowflakeIdUtil.generate())
                                .merchantId(latestRate.getMerchantId())
                                .accountId(latestRate.getAccountId())
                                .exchangeTime(exchangeTime)
                                .sourceCurrency(latestRate.getSourceCurrency())
                                .targetCurrency(latestRate.getTargetCurrency())
                                .exchangeRate(latestRate.getExchangeRate())
                                .ratioLose(latestRate.getRatioLose())
                                .merchantRate(latestRate.getMerchantRate())
                                .createdTime(LocalDateTimeUtil.nowUtc())
                                .updatedTime(LocalDateTimeUtil.nowUtc())
                                .version(1)
                                .build());
                    }
                }
            } catch (Exception e) {
                log.error("init fx rage fail msg:" + e.getMessage());
            }
        }
    }

    private List<AccountInfoBo> batchLoadAccountList(final Long nextAccountId) {
        QAccount account = QAccount.account;
        final QBean<AccountInfoBo> bean = Projections.fields(AccountInfoBo.class,
                account.id.as("accountId"),
                account.merchantId,
                account.countryCode,
                account.transactionTypeCode,
                account.currency,
                account.timezone);

        return jpaQueryFactory.select(bean).from(account)
                .where(account.id.gt(nextAccountId)
                        .and(account.delFlag.eq(Boolean.FALSE)))
                .orderBy(account.id.asc())
                .limit(BATCH_COUNT)
                .fetch();
    }

    private void batchSaveAccountExchangeRate(final List<DailyExchangeRateVo> rateList) {
        if (CollectionUtils.isEmpty(rateList)) {
            return;
        }
        // Individual saving if abnormal,will not affect the exchange rate saving of other accounts
        for (final DailyExchangeRateVo vo : rateList) {
            try {
                // Individual saving if abnormal,will not affect the exchange rate saving of other accounts
                this.saveAccountExchangeRate(vo);
            } catch (Exception e) {
                log.error("save daily exchange rate error={}", e.getMessage());
            }
        }
    }

    private DailyExchangeRate saveAccountExchangeRate(final DailyExchangeRateVo vo) {
        return dailyExchangeRateRepository.saveAndFlush(DailyExchangeRate.builder()
                .id(SnowflakeIdUtil.generate())
                .merchantId(vo.getMerchantId())
                .accountId(vo.getAccountId())
                .exchangeTime(vo.getExchangeTime().withMinute(0).withSecond(0).withNano(0))
                .sourceCurrency(vo.getSourceCurrency())
                .targetCurrency(vo.getTargetCurrency())
                .exchangeRate(vo.getExchangeRate())
                .ratioLose(vo.getRatioLose())
                .merchantRate(vo.getMerchantRate())
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .version(1)
                .build());

    }

    @Override
    public RealTimeExchangeRateDto queryLatestExchangeRate(final QueryLatestExchangeRateVo vo) {
        final QDailyExchangeRate entity = QDailyExchangeRate.dailyExchangeRate;
        final QBean<RealTimeExchangeRateDto> bean =
                Projections.fields(RealTimeExchangeRateDto.class,
                        entity.id,
                        entity.exchangeTime,
                        entity.merchantId,
                        entity.accountId,
                        entity.sourceCurrency,
                        entity.targetCurrency,
                        entity.exchangeRate,
                        entity.ratioLose,
                        entity.merchantRate);

        return jpaQueryFactory.select(bean)
                .from(entity)
                .where(entity.merchantId.eq(vo.getMerchantId())
                        .and(entity.accountId.eq(vo.getAccountId()))
                        .and(entity.delFlag.eq(Boolean.FALSE))
                        .and(entity.sourceCurrency.eq(vo.getSourceCurrency()))
                        .and(entity.targetCurrency.eq(vo.getTargetCurrency())))
                .orderBy(entity.id.desc())
                .fetchFirst();
    }

    @Override
    public DailyExchangeRateDto queryDailyExchangeRate(final QueryDailyExchangeRateVo vo) {

        final AccountDto accountDto = accountService.getEffectiveAccountInfo(
                QueryAccountVo.builder().id(vo.getAccountId()).build());

        final boolean useRealTimeRate = Optional.ofNullable(accountDto.getAccountConfig())
                .map(AccountConfigDto::getConfigData)
                .map(AccountConfigData::getCalculateConfig)
                .map(CalculateConfigBo::getRealTimeRate).orElse(false);

        final LocalDateTime exchangeTime;
        if (useRealTimeRate) {
            // Use realtime exchange rate
            exchangeTime = vo.getExchangeTime().withMinute(0).withSecond(0).withNano(0);
        } else {
            // Use non-realtime exchange rate
            exchangeTime = LocalDateTimeUtil.localToUtc(vo.getExchangeTime()
                            .atZone(Constant.COMMON.ZONE_UTC)
                            .withZoneSameInstant(ZoneId.of(accountDto.getTimezone()))
                            .toLocalDateTime().withHour(0).withMinute(0).withSecond(0).withNano(0),
                    accountDto.getTimezone());
        }

        final QDailyExchangeRate entity = QDailyExchangeRate.dailyExchangeRate;
        final DailyExchangeRateDto result = jpaQueryFactory.select(
                        Projections.fields(DailyExchangeRateDto.class,
                                entity.id,
                                entity.exchangeTime,
                                entity.merchantId,
                                entity.accountId,
                                entity.sourceCurrency,
                                entity.targetCurrency,
                                entity.exchangeRate,
                                entity.ratioLose,
                                entity.merchantRate))
                .from(entity)
                .where(entity.merchantId.eq(vo.getMerchantId())
                        .and(entity.accountId.eq(vo.getAccountId()))
                        .and(entity.exchangeTime.eq(exchangeTime))
                        .and(entity.sourceCurrency.eq(vo.getSourceCurrency()))
                        .and(entity.targetCurrency.eq(vo.getTargetCurrency())))
                .orderBy(entity.id.desc())
                .fetchOne();

        if (Objects.nonNull(result)) {
            return result;
        }

        //try to re-init history transaction fx-rate
        return this.initDailyHistoryFxRate(accountDto, vo, exchangeTime);
    }

    private DailyExchangeRateDto initDailyHistoryFxRate(
            final AccountDto accountDto,
            final QueryDailyExchangeRateVo vo,
            final LocalDateTime exchangeTime) {

        // auto init daily exchange rate;
        final HourlyExchangeRateDto hourlyRate = hourlyExchangeRateService.queryHourlyExchangeRate(
                QueryHourlyExchangeRateVo.builder()
                        .sourceCurrency(vo.getSourceCurrency())
                        .targetCurrency(vo.getTargetCurrency())
                        .exchangeTime(exchangeTime)
                        .build());

        if (Objects.isNull(hourlyRate)) {
            throw StatementExceptionCode.GET_REALTIME_EXCHANGE_RATE_FAIL.exception();
        }

        final RealTimeExchangeRateDto latestExchangeRate =
                this.queryLatestExchangeRate(QueryLatestExchangeRateVo.builder()
                        .merchantId(accountDto.getMerchantId())
                        .accountId(accountDto.getId())
                        .sourceCurrency(vo.getSourceCurrency())
                        .targetCurrency(vo.getTargetCurrency())
                        .build());
        if (Objects.isNull(latestExchangeRate)) {
            throw StatementExceptionCode.GET_REALTIME_EXCHANGE_RATE_FAIL.exception();
        }

        final BigDecimal exchangeRate = hourlyRate.getExchangeRate();
        final BigDecimal ratioLose = Optional.ofNullable(latestExchangeRate.getRatioLose())
                .orElse(BigDecimal.ZERO);
        final BigDecimal merchantFxRate;
        if (TransactionTypeCodeEnum.PAY_IN == accountDto.getTransactionTypeCode()) {
            merchantFxRate = Objects.isNull(exchangeRate) ? BigDecimal.ZERO :
                    exchangeRate.multiply(BigDecimal.ONE.add(ratioLose))
                            .setScale(6, RoundingMode.HALF_UP);
        } else {
            // PAY_OUT, MARKET_PLACE
            merchantFxRate = Objects.isNull(exchangeRate) ? BigDecimal.ZERO :
                    AmountUtil.division(exchangeRate, BigDecimal.ONE.add(ratioLose), 6);
        }

        try {
            return modelMapper.convertDto(
                    this.saveAccountExchangeRate(
                            DailyExchangeRateVo.builder()
                                    .merchantId(accountDto.getMerchantId())
                                    .accountId(accountDto.getId())
                                    .sourceCurrency(vo.getSourceCurrency())
                                    .targetCurrency(vo.getTargetCurrency())
                                    .exchangeTime(exchangeTime)
                                    .exchangeRate(hourlyRate.getExchangeRate())
                                    .ratioLose(ratioLose)
                                    .merchantRate(merchantFxRate)
                                    .build()));
        } catch (Exception e) {
            log.error("Init Daily History FxRate error, vo:{}", vo, e);
        }

        throw StatementExceptionCode.GET_REALTIME_EXCHANGE_RATE_FAIL.exception();
    }

    @Override
    public List<DailyExchangeRateDto> queryDailyExchangeRateForList(
            final QueryDailyExchangeRateListVo vo
    ) {

        if (vo.getBeginTime().isAfter(vo.getEndTime())) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL
                    .exception("The endTime must be greater than the beginTime");
        }

        final QDailyExchangeRate entity = QDailyExchangeRate.dailyExchangeRate;
        final QBean<DailyExchangeRateDto> bean = Projections.fields(DailyExchangeRateDto.class,
                entity.id,
                entity.exchangeTime,
                entity.merchantId,
                entity.accountId,
                entity.sourceCurrency,
                entity.targetCurrency,
                entity.exchangeRate,
                entity.ratioLose,
                entity.merchantRate);

        BooleanExpression condition =
                entity.exchangeTime.goe(vo.getBeginTime())
                        .and(entity.exchangeTime.loe(vo.getEndTime()))
                        .and(entity.delFlag.eq(Boolean.FALSE));
        if (Objects.nonNull(vo.getAccountIds()) && !vo.getAccountIds().isEmpty()) {
            condition = condition.and(entity.accountId.in(vo.getAccountIds()));
        }

        return jpaQueryFactory.select(bean)
                .from(entity)
                .where(condition)
                .orderBy(entity.id.desc())
                .fetch();
    }

    public List<DailyExchangeRateDto> queryDailyExchangeRateFromCache(
            QueryDailyExchangeRateListVo vo
    ) {
        final String cacheKey = String.format("%s:%s", "DAILY_EXCHANGE_RATE",
                vo.hashCode());

        final List<DailyExchangeRateDto> cacheList = redisCacheUtil.getCacheList(cacheKey);

        if (ObjectUtils.isNotEmpty(cacheList)) {
            return cacheList;
        }
        final List<DailyExchangeRateDto> result = queryDailyExchangeRateForList(vo);

        if (ObjectUtils.isNotEmpty(result)) {
            redisCacheUtil.setCacheList(cacheKey, result);

            redisCacheUtil.expire(cacheKey, 1, TimeUnit.HOURS);

            return result;
        }
        throw StatementExceptionCode.GET_REALTIME_EXCHANGE_RATE_FAIL.exception();
    }
}
