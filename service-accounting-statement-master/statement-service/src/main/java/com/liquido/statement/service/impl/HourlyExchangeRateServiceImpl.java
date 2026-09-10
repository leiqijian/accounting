package com.liquido.statement.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.pojo.entity.HourlyExchangeRate;
import com.liquido.statement.pojo.entity.QHourlyExchangeRate;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.QueryHourlyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryHourlyLatestExchangeRateVo;
import com.liquido.statement.pojo.vo.RealTimeExchangeRateVo;
import com.liquido.statement.repository.HourlyExchangeRateRepository;
import com.liquido.statement.service.HourlyExchangeRateService;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HourlyExchangeRateServiceImpl implements HourlyExchangeRateService {
    private final JPAQueryFactory jpaQueryFactory;
    private final HourlyExchangeRateRepository repository;
    private final RedisCacheUtil redisCacheUtil;
    private final ModelMapper modelMapper;

    @Override
    public void batchSaveExchangeRate(final List<RealTimeExchangeRateVo> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        try {
            final List<HourlyExchangeRate> rateList =
                    dataList.stream().map(vo -> HourlyExchangeRate.builder()
                            .id(SnowflakeIdUtil.generate())
                            .sourceCurrency(vo.getSourceCurrency())
                            .targetCurrency(vo.getTargetCurrency())
                            .exchangeTime(
                                    vo.getExchangeTime().withMinute(0).withSecond(0).withNano(0))
                            .exchangeRate(vo.getExchangeRate())
                            .version(1)
                            .createdTime(LocalDateTimeUtil.nowUtc())
                            .updatedTime(LocalDateTimeUtil.nowUtc())
                            .remark("")
                            .build()).collect(Collectors.toList());
            repository.saveAllAndFlush(rateList);
        } catch (Exception e) {
            log.error("init RealTime rate fail:{}", e.getMessage());
        }
    }

    /**
     * @param sourceCurrency
     * @param targetCurrency
     * @param exchangeTime   UTC+0, format: yyyy-MM-dd HH:00:00
     * @return
     */
    @Override
    public HourlyExchangeRate queryHourlyExchangeRate(final CurrencyEnum sourceCurrency,
                                                      final CurrencyEnum targetCurrency,
                                                      final LocalDateTime exchangeTime) {

        final QHourlyExchangeRate entity = QHourlyExchangeRate.hourlyExchangeRate;
        final BooleanExpression condition = entity.sourceCurrency.eq(sourceCurrency)
                .and(entity.targetCurrency.eq(targetCurrency))
                .and(entity.exchangeTime.eq(exchangeTime));

        return jpaQueryFactory.select(entity).from(entity).where(condition).fetchOne();
    }

    public HourlyExchangeRateDto queryHourlyExchangeRate(final QueryHourlyExchangeRateVo vo) {

        log.info("query hour exchange rate param={}", vo);
        final String redisKey = String.format(CacheConstant.HOURLY_EXCHANGE_RATE,
                vo.getSourceCurrency(),
                vo.getTargetCurrency(),
                vo.getExchangeTime());

        final HourlyExchangeRateDto dto = redisCacheUtil.getCacheObject(redisKey);
        if (Objects.nonNull(dto)) {
            return dto;
        }

        final QHourlyExchangeRate entity = QHourlyExchangeRate.hourlyExchangeRate;
        final QBean<HourlyExchangeRateDto> bean =
                Projections.fields(HourlyExchangeRateDto.class, entity.id, entity.sourceCurrency,
                        entity.targetCurrency, entity.exchangeTime, entity.exchangeRate);

        // if get realtime now rate fail load prev hour as fx rate and return;
        final BooleanExpression condition = entity.sourceCurrency.eq(vo.getSourceCurrency())
                .and(entity.targetCurrency.eq(vo.getTargetCurrency()))
                .and(entity.exchangeTime.goe(vo.getExchangeTime().minusHours(1)))
                .and(entity.exchangeTime.loe(vo.getExchangeTime()));

        return Optional.ofNullable(jpaQueryFactory.select(bean)
                        .from(entity)
                        .where(condition)
                        .orderBy(entity.id.desc())
                        .limit(1)
                        .fetchOne())
                .map(data -> {
                    redisCacheUtil.setCacheObject(redisKey, data, 1, TimeUnit.HOURS);
                    return data;
                }).orElseThrow(StatementExceptionCode.GET_REALTIME_EXCHANGE_RATE_FAIL::exception);
    }

    @Override
    public List<HourlyExchangeRateDto> queryHourlyExchangeRate(final LocalDateTime exchangeTime) {
        final QHourlyExchangeRate entity = QHourlyExchangeRate.hourlyExchangeRate;
        final BooleanExpression condition = entity.exchangeTime.eq(exchangeTime);
        return modelMapper.convertDto(
                jpaQueryFactory.select(entity).from(entity).where(condition).fetch());
    }

    @Override
    public HourlyExchangeRateDto queryLatestHourlyExchangeRate(
            final QueryHourlyLatestExchangeRateVo vo) {
        final QHourlyExchangeRate entity = QHourlyExchangeRate.hourlyExchangeRate;

        BooleanExpression condition = entity.delFlag.eq(Boolean.FALSE)
                .and(entity.sourceCurrency.eq(vo.getSourceCurrency()))
                .and(entity.targetCurrency.eq(vo.getTargetCurrency()));

        if (Objects.nonNull(vo.getExchangeTime())) {
            condition = condition.and(entity.exchangeTime.eq(vo.getExchangeTime()));

        }

        final QBean<HourlyExchangeRateDto> bean =
                Projections.fields(HourlyExchangeRateDto.class,
                        entity.id,
                        entity.exchangeTime,
                        entity.sourceCurrency,
                        entity.targetCurrency,
                        entity.exchangeRate);

        return jpaQueryFactory.select(bean)
                .from(entity).where(condition).orderBy(entity.id.desc()).fetchFirst();
    }

}
