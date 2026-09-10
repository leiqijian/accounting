package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.TransactionSummaryBo;
import com.liquido.statement.pojo.dto.ListTransactionSummaryDto;
import com.liquido.statement.pojo.dto.TransactionSummaryDailyStatisticsDto;
import com.liquido.statement.pojo.dto.TransactionSummaryDto;
import com.liquido.statement.pojo.dto.TransactionSummaryStatisticsDto;
import com.liquido.statement.pojo.entity.QTransactionSummary;
import com.liquido.statement.pojo.entity.TransactionSummary;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.ListHasTransactionAccountVo;
import com.liquido.statement.pojo.vo.ListTransactionSummaryVo;
import com.liquido.statement.pojo.vo.QueryGlobalTransactionVo;
import com.liquido.statement.pojo.vo.TransactionSummaryDailyStatisticsVo;
import com.liquido.statement.pojo.vo.TransactionSummaryStatisticsVo;
import com.liquido.statement.repository.TransactionSummaryRepository;
import com.liquido.statement.service.TransactionSummaryService;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TransactionSummaryServiceImpl implements TransactionSummaryService {
    private final ModelMapper modelMapper;
    private final BaseService baseService;
    private final RedisDistLock redisDistLock;
    private final JPAQueryFactory jpaQueryFactory;
    private final TransactionSummaryRepository transactionSummaryRepository;

    @Override
    public TransactionSummary getOrInitTransactionSummary(
            final Long merchantId,
            final Long accountId,
            final LocalDate transactionDate) {

        final QTransactionSummary entity = QTransactionSummary.transactionSummary;
        final BooleanExpression condition = entity.accountId.eq(accountId)
                .and(entity.transactionDate.eq(transactionDate));

        TransactionSummary summary = jpaQueryFactory.select(entity)
                .from(entity).where(condition).limit(1).fetchOne();

        if (Objects.nonNull(summary)) {
            return summary;
        }

        final String lockVal = DataUtil.getUuid();
        String cacheKey = String.format(CacheConstant.TRANSACTION_SUMMARY_INIT_LOCK,
                accountId.toString(), transactionDate.format(LocalDateUtil.FORMAT_YYYYMMDD));
        try {
            boolean lockSuccess = redisDistLock.getLock(cacheKey, lockVal, 180, TimeUnit.SECONDS);
            if (!lockSuccess) {
                log.error("Transaction summary init fail account={}, transactionDate={}",
                        accountId, transactionDate);
                throw StatementExceptionCode.TRANSACTION_SUMMARY_INIT_FAIL.exception();
            }

            summary = jpaQueryFactory.select(entity).from(entity)
                    .where(condition).limit(1).fetchOne();
            if (Objects.nonNull(summary)) {
                return summary;
            }

            summary = TransactionSummary.builder()
                    .id(SnowflakeIdUtil.generate())
                    .merchantId(merchantId)
                    .accountId(accountId)
                    .transactionDate(transactionDate)
                    .transactionCount(0)
                    .transactionAmount(BigDecimal.ZERO)
                    .transactionVolumeAmount(BigDecimal.ZERO)
                    .settlementAmount(BigDecimal.ZERO)
                    .settlementVolumeAmount(BigDecimal.ZERO)
                    .settlementAmountUsd(BigDecimal.ZERO)
                    .settlementVolumeAmountUsd(BigDecimal.ZERO)
                    .feeAmount(BigDecimal.ZERO)
                    .taxAmount(BigDecimal.ZERO)
                    .createdTime(LocalDateTimeUtil.nowUtc())
                    .updatedTime(LocalDateTimeUtil.nowUtc())
                    .version(1).build();
            transactionSummaryRepository.save(summary);
            return summary;
        } finally {
            boolean res = redisDistLock.unlock(cacheKey, lockVal);
            log.info("transaction summary init info unlock result={}", res);
        }
    }

    @Override
    public void saveOrUpdate(final TransactionSummaryBo summaryBo) {
        final QTransactionSummary entity = QTransactionSummary.transactionSummary;

        final BooleanExpression condition = entity.accountId.eq(summaryBo.getAccountId())
                .and(entity.transactionDate.eq(summaryBo.getTransactionDate()));

        TransactionSummary summary = jpaQueryFactory.select(entity)
                .from(entity).where(condition).limit(1).fetchOne();
        if (Objects.isNull(summary)) {
            summary = getOrInitTransactionSummary(
                    summaryBo.getMerchantId(),
                    summaryBo.getAccountId(),
                    summaryBo.getTransactionDate());
        }

        final long result = jpaQueryFactory.update(entity)
                .set(entity.transactionCount,
                        entity.transactionCount.add(summaryBo.getTransactionCount()))
                .set(entity.transactionAmount,
                        entity.transactionAmount.add(summaryBo.getTransactionAmount()))
                .set(entity.transactionVolumeAmount,
                        entity.transactionVolumeAmount.add(summaryBo.getTransactionVolumeAmount()))
                .set(entity.settlementAmount,
                        entity.settlementAmount.add(summaryBo.getSettlementAmount()))
                .set(entity.settlementVolumeAmount,
                        entity.settlementVolumeAmount.add(summaryBo.getSettlementVolumeAmount()))
                .set(entity.settlementAmountUsd,
                        entity.settlementAmountUsd.add(summaryBo.getSettlementAmountUsd()))
                .set(entity.settlementVolumeAmountUsd, entity.settlementVolumeAmountUsd
                        .add(summaryBo.getSettlementVolumeAmountUsd()))
                .set(entity.feeAmount, entity.feeAmount.add(summaryBo.getFeeAmount()))
                .set(entity.taxAmount, entity.taxAmount.add(summaryBo.getTaxAmount()))
                .set(entity.transactionCurrency, summaryBo.getTransactionCurrency())
                .set(entity.settlementCurrency, summaryBo.getSettlementCurrency())
                .set(entity.version, entity.version.add(1))
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.id.eq(summary.getId())
                        .and(entity.version.eq(summary.getVersion())))
                .execute();

        if (result <= 0) {
            log.error("update transaction summary fail summaryBo={}", summaryBo);
            throw StatementExceptionCode.TRANSACTION_SETTLEMENT_FAIL.exception(
                    JsonUtil.toJson(summaryBo));
        }
    }

    @Override
    public List<TransactionSummaryBo> queryLast2DayTransactionSummary(final Long accountId) {
        final QTransactionSummary entity = QTransactionSummary.transactionSummary;
        final BooleanExpression condition = entity.accountId.eq(accountId);

        final List<TransactionSummary> summaryList = jpaQueryFactory.select(entity)
                .from(entity)
                .where(condition)
                .orderBy(entity.transactionDate.desc())
                .limit(2).fetch();

        if (CollectionUtils.isEmpty(summaryList)) {
            return Collections.emptyList();
        }

        return modelMapper.convertList(summaryList);
    }

    @Override
    public TransactionSummaryDto globalTransactionSummary(final QueryGlobalTransactionVo vo) {
        final QTransactionSummary transactionSummary = QTransactionSummary.transactionSummary;
        final QBean<TransactionSummaryDto> bean =
                Projections.bean(TransactionSummaryDto.class,
                        transactionSummary.settlementAmountUsd.abs().sum()
                                .coalesce(BigDecimal.ZERO)
                                .as("transactionAmountUsd"),
                        transactionSummary.transactionCount.sum().coalesce(0)
                                .as("transactionCount"));

        final List<BooleanExpression> predicates = setInnerMerchantCondition(Lists.newArrayList());
        predicates.add(transactionSummary.transactionDate.between(vo.getStartDate(),
                vo.getEndDate()));

        final TransactionSummaryDto transactionSummaryDto = jpaQueryFactory.select(bean)
                .from(transactionSummary)
                .where(predicates.toArray(new BooleanExpression[] {}))
                .fetchOne();

        Objects.requireNonNull(transactionSummaryDto)
                .setCurrency(CurrencyEnum.USD);

        return transactionSummaryDto;
    }

    @Override
    public TransactionSummaryStatisticsDto statisticsTransactionSummary(
            final TransactionSummaryStatisticsVo vo) {
        final QTransactionSummary transactionSummary = QTransactionSummary.transactionSummary;

        final QBean<TransactionSummaryStatisticsDto> bean = Projections
                .bean(TransactionSummaryStatisticsDto.class,
                        transactionSummary.settlementVolumeAmountUsd.sum()
                                .coalesce(BigDecimal.ZERO).as("settlementVolumeAmountUsd"),
                        transactionSummary.transactionCount.sum()
                                .coalesce(0).as("transactionCount"));

        final List<BooleanExpression> predicates = setInnerMerchantCondition(Lists.newArrayList());

        if (Objects.nonNull(vo.getStartDate())) {
            predicates.add(transactionSummary.transactionDate.goe(vo.getStartDate()));
        }
        if (Objects.nonNull(vo.getEndDate())) {
            predicates.add(transactionSummary.transactionDate.loe(vo.getEndDate()));
        }

        return jpaQueryFactory.select(bean)
                .from(transactionSummary)
                .where(predicates.toArray(new BooleanExpression[] {}))
                .fetchOne();
    }

    @Override
    public List<TransactionSummaryDailyStatisticsDto> statisticsTransactionSummaryDaily(
            final TransactionSummaryDailyStatisticsVo vo) {
        final QTransactionSummary transactionSummary = QTransactionSummary.transactionSummary;

        final QBean<TransactionSummaryDailyStatisticsDto> bean = Projections
                .bean(TransactionSummaryDailyStatisticsDto.class,
                        transactionSummary.transactionDate.as("transactionDate"),
                        transactionSummary.settlementVolumeAmountUsd.sum()
                                .coalesce(BigDecimal.ZERO).as("settlementVolumeAmountUsd"),
                        transactionSummary.transactionCount.sum()
                                .coalesce(0).as("transactionCount"));

        final List<BooleanExpression> predicates = setInnerMerchantCondition(Lists.newArrayList());

        if (Objects.nonNull(vo.getStartDate())) {
            predicates.add(transactionSummary.transactionDate.goe(vo.getStartDate()));
        }
        if (Objects.nonNull(vo.getEndDate())) {
            predicates.add(transactionSummary.transactionDate.loe(vo.getEndDate()));
        }

        return jpaQueryFactory.select(bean)
                .from(transactionSummary)
                .where(predicates.toArray(new BooleanExpression[] {}))
                .groupBy(transactionSummary.transactionDate)
                .fetch();
    }

    @Override
    public Set<Long> listHasTransactionAccountId(final ListHasTransactionAccountVo vo) {
        final QTransactionSummary transactionSummary = QTransactionSummary.transactionSummary;
        final BooleanExpression condition =
                transactionSummary.transactionDate.goe(vo.getStartDate())
                        .and(transactionSummary.transactionDate.loe(vo.getEndDate()));
        return new HashSet<>(jpaQueryFactory.select(transactionSummary.accountId)
                .from(transactionSummary)
                .where(condition)
                .groupBy(transactionSummary.accountId)
                .having(transactionSummary.transactionCount.sum().gt(0L))
                .fetch());
    }

    @Override
    public List<ListTransactionSummaryDto> listTransactionSummary(
            final ListTransactionSummaryVo vo) {
        final QTransactionSummary entity = QTransactionSummary.transactionSummary;
        final BooleanBuilder builder = new BooleanBuilder();

        if (CollectionUtils.isNotEmpty(vo.getAccountIds())) {
            builder.and(entity.accountId.in(vo.getAccountIds()));
        }
        if (Objects.nonNull(vo.getStartDate())) {
            builder.and(entity.transactionDate.goe(vo.getStartDate()));
        }
        if (Objects.nonNull(vo.getEndDate())) {
            builder.and(entity.transactionDate.loe(vo.getEndDate()));
        }

        final List<TransactionSummary> list = jpaQueryFactory.select(entity)
                .from(entity)
                .where(builder)
                .orderBy(entity.transactionDate.desc())
                .fetch();

        return modelMapper.convertTransactionSummaryList(list);
    }

    private List<BooleanExpression> setInnerMerchantCondition(
            final List<BooleanExpression> predicates) {
        final QTransactionSummary transactionSummary = QTransactionSummary.transactionSummary;
        final List<Long> innerMerchantIds = baseService.getInnerMerchant()
                .stream().map(MerchantDto::getId).collect(Collectors.toList());
        if (ObjectUtils.isNotEmpty(innerMerchantIds)) {
            predicates.add(transactionSummary.merchantId.notIn(innerMerchantIds));
        }
        return predicates;
    }
}
