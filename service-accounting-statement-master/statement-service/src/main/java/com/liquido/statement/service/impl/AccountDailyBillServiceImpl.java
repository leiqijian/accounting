package com.liquido.statement.service.impl;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.core.mvc.enums.SortTypeEnum;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountDailyTransactionBo;
import com.liquido.statement.pojo.bo.DailyCutSuccessBo;
import com.liquido.statement.pojo.dto.AccountDailyBillDto;
import com.liquido.statement.pojo.dto.AccountDailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.CountryDailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.DailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.DailyTransactionChartDto;
import com.liquido.statement.pojo.dto.EachMerchantDailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.HistoryDailyBillStatisticsDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountDailyBill;
import com.liquido.statement.pojo.entity.QAccount;
import com.liquido.statement.pojo.entity.QAccountDailyBill;
import com.liquido.statement.pojo.entity.QTransactionMoney;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.BatchQueryHisAccountDailyBillVo;
import com.liquido.statement.pojo.vo.CreateDailyBillVo;
import com.liquido.statement.pojo.vo.DailyBillStatisticsVo;
import com.liquido.statement.pojo.vo.FixAccountDailyBillVo;
import com.liquido.statement.pojo.vo.ListHasTransactionAccountVo;
import com.liquido.statement.pojo.vo.QueryAccountIdsDateVo;
import com.liquido.statement.pojo.vo.QueryCountryDailyBillStatisticsVo;
import com.liquido.statement.pojo.vo.QueryDailyBillVo;
import com.liquido.statement.pojo.vo.QueryDailyTransactionChartVo;
import com.liquido.statement.pojo.vo.QueryGlobalTransactionVo;
import com.liquido.statement.pojo.vo.QueryHisAccountDailyBillVo;
import com.liquido.statement.pojo.vo.QueryMonthlyBillVo;
import com.liquido.statement.pojo.vo.QueryPageAccountDailyVo;
import com.liquido.statement.repository.AccountDailyBillRepository;
import com.liquido.statement.service.AccountDailyBillService;
import com.liquido.statement.service.dailycut.DailyCutService;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.AvoidReassigningLoopVariables"})
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AccountDailyBillServiceImpl implements AccountDailyBillService {

    private final ModelMapper modelMapper;
    private final BaseService baseService;
    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final DailyCutService dailyCutService;
    private final AccountDailyBillRepository accountDailyBillRepository;

    @Async
    @Override
    public void generateAccountDailyBill(final CreateDailyBillVo vo) {
        final List<AccountDailyBill> billList = findAllById(vo.getBillIdList());
        if (CollectionUtils.isEmpty(billList)) {
            return;
        }

        final List<DailyCutSuccessBo> dataList = billList.stream().map(item ->
                DailyCutSuccessBo.builder()
                        .billId(item.getId())
                        .accountId(item.getAccountId())
                        .merchantId(item.getMerchantId())
                        .timezone(item.getTimezone())
                        .billDate(item.getBillDate())
                        .countryCode(item.getCountryCode())
                        .transactionTypeCode(item.getTransactionTypeCode())
                        .build()).collect(Collectors.toList());

        dailyCutService.publishDailyCutSuccessEvent(dataList, Boolean.FALSE);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public AccountDailyBill saveAccountDailyBill(final AccountDailyBill accountDailyBill) {
        log.info("save account daily bill, accountDailyBill={}", accountDailyBill);
        final AccountDailyBill existDailyBill =
                accountDailyBillRepository.findAccountDailyBillByAccountIdAndBillDate(
                        accountDailyBill.getAccountId(), accountDailyBill.getBillDate());

        if (Objects.nonNull(existDailyBill)) {
            accountDailyBill.setId(existDailyBill.getId());
            accountDailyBill.setAccountId(accountDailyBill.getAccountId());
            accountDailyBill.setVersion(existDailyBill.getVersion() + 1);
            accountDailyBill.setUpdatedTime(LocalDateTimeUtil.nowUtc());
        }

        return accountDailyBillRepository.saveAndFlush(accountDailyBill);
    }

    @Override
    public AccountDailyBillDto queryHisAccountDailyBill(final QueryHisAccountDailyBillVo vo) {
        final String cacheKey = String.format(CacheConstant.ACCOUNT_DAILY_BILL_KEY,
                vo.getBillDate().format(LocalDateUtil.FORMAT_DATE));

        AccountDailyBillDto dto = redisCacheUtil.getCacheMapValue(cacheKey,
                vo.getAccountId().toString());
        if (Objects.nonNull(dto)) {
            return dto;
        }

        final AccountDailyBill bill = accountDailyBillRepository.findOne(
                Specifications.<AccountDailyBill>and().eq("accountId", vo.getAccountId())
                        .eq("billDate", vo.getBillDate()).build()).orElse(null);

        if (Objects.isNull(bill)) {
            return null;
        }

        dto = modelMapper.convertBo(bill);
        redisCacheUtil.setCacheMapValue(cacheKey, vo.getAccountId().toString(), dto);
        redisCacheUtil.expire(cacheKey, 3, TimeUnit.DAYS);

        return dto;
    }

    @Override
    public List<AccountDailyBillDto> batchQueryHisAccountDailyBill(
            final BatchQueryHisAccountDailyBillVo vo) {
        final String cacheKey = String.format(CacheConstant.ACCOUNT_DAILY_BILL_KEY,
                vo.getBillDate().format(LocalDateUtil.FORMAT_DATE));

        // Get data from cache
        final List<AccountDailyBillDto> dailyBillList = redisCacheUtil.getMultiCacheMapValue(
                cacheKey, vo.getAccountIds().stream().collect(Collectors.toSet()));

        final Set<Long> existAccountIds = dailyBillList.stream()
                .map(AccountDailyBillDto::getAccountId).collect(Collectors.toSet());

        final Set<Long> needReloadAccounts = Sets.difference(vo.getAccountIds(), existAccountIds);
        // If all data exists in cache, return it directly
        if (CollectionUtils.isEmpty(needReloadAccounts)) {
            return dailyBillList;
        }

        // Query data from the database
        final List<AccountDailyBill> entityList =
                accountDailyBillRepository.findAll(Specifications.<AccountDailyBill>and()
                        .in(CollectionUtils.isNotEmpty(needReloadAccounts), "accountId",
                                needReloadAccounts.toArray())
                        .eq("billDate", vo.getBillDate())
                        .build());

        if (CollectionUtils.isNotEmpty(entityList)) {
            final List<AccountDailyBillDto> dataList = entityList.stream()
                    .map(modelMapper::convertBo).collect(Collectors.toList());
            final Map<Long, AccountDailyBillDto> dataMap = dataList.stream()
                    .collect(Collectors.toMap(AccountDailyBillDto::getAccountId,
                            Function.identity(), (x, y) -> y));

            dailyBillList.addAll(dataList);
            redisCacheUtil.setCacheMap(cacheKey, dataMap);
        }

        redisCacheUtil.expire(cacheKey, 2, TimeUnit.DAYS);
        return dailyBillList;
    }

    @Override
    public AccountDailyBill findById(final Long id) {
        return accountDailyBillRepository.findById(id)
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
    }

    @Override
    public List<AccountDailyBill> findAllById(final List<Long> ids) {
        return accountDailyBillRepository.findAllById(ids);
    }

    @Override
    public AccountDailyBillDto queryLatestAccountDailyBill(final Long accountId) {
        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;
        final AccountDailyBill dailyBill = jpaQueryFactory.select(entity)
                .from(entity)
                .where(entity.accountId.eq(accountId))
                .orderBy(entity.billDate.desc())
                .limit(1)
                .fetchOne();

        if (Objects.nonNull(dailyBill)) {
            return this.convertDto(dailyBill);
        }

        return null;
    }

    @Override
    public List<AccountDailyBillDto> findAccountMonthlyBill(final QueryMonthlyBillVo vo) {
        final List<AccountDailyBill> accountDailyBillList =
                accountDailyBillRepository.findAccountDailyBillByAccountIdAndBillMonth(
                        vo.getAccountId(), vo.getBillMonth());

        return modelMapper.convertDailyBillList(accountDailyBillList);
    }

    @Override
    public List<AccountDailyBillDto> findAccountDailyBill(final QueryDailyBillVo vo) {
        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;
        BooleanExpression condition = entity.merchantId.eq(vo.getMerchantId())
                .and(entity.countryCode.eq(vo.getCountryCode()));

        if (Objects.nonNull(vo.getTransactionTypeCode())) {
            condition = condition.and(entity.transactionTypeCode.eq(vo.getTransactionTypeCode()));
        }
        if (Objects.nonNull(vo.getStartDate())) {
            condition = condition.and(entity.billDate.goe(vo.getStartDate()));
        }
        if (Objects.nonNull(vo.getEndDate())) {
            condition = condition.and(entity.billDate.loe(vo.getEndDate()));
        }

        final List<AccountDailyBill> dailyBillList = jpaQueryFactory.select(entity)
                .from(entity)
                .where(condition)
                .orderBy(entity.billDate.desc())
                .fetch();

        return modelMapper.convertDailyBillList(dailyBillList);
    }

    @Override
    public AccountDailyBill findAccountBillByBillDay(final Long accountId,
                                                     final LocalDate billDate) {
        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;
        final BooleanExpression condition = entity.accountId.eq(accountId)
                .and(entity.billDate.eq(billDate));

        return jpaQueryFactory.select(entity).from(entity).where(condition).fetchOne();
    }

    @Override
    public List<DailyTransactionChartDto> findDailyTransactionChart(
            final QueryDailyTransactionChartVo vo) {

        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;
        BooleanExpression condition = entity.merchantId.eq(vo.getMerchantId())
                .and(entity.countryCode.eq(vo.getCountryCode()))
                .and(entity.billDate.goe(vo.getStartDate()))
                .and(entity.billDate.loe(vo.getEndDate()));

        final List<AccountDailyBill> dailyBillList =
                jpaQueryFactory.select(entity).from(entity).where(condition).fetch();

        if (CollectionUtils.isEmpty(dailyBillList)) {
            return Collections.emptyList();
        }

        return dailyBillList.stream().map(bill -> DailyTransactionChartDto.builder()
                        .accountId(bill.getAccountId())
                        .billDate(bill.getBillDate())
                        .transactionTypeCode(bill.getTransactionTypeCode())
                        .transactionCount(bill.getTransactionCount())
                        .transactionAmount(bill.getTransactionAmount())
                        .transactionCurrency(bill.getTransactionCurrency())
                        .settlementAmount(bill.getSettlementAmount())
                        .settlementCurrency(bill.getSettlementCurrency())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @SneakyThrows
    public PageVo<AccountDailyBillDto> pageAccountDaily(final QueryPageAccountDailyVo vo) {

        final PredicateBuilder<AccountDailyBill> spec = Specifications.and();
        spec.ge(Objects.nonNull(vo.getStartDate()), "billDate", vo.getStartDate());
        spec.le(Objects.nonNull(vo.getEndDate()), "billDate", vo.getEndDate());

        if (Objects.nonNull(vo.getMerchantId()) && Objects.nonNull(vo.getCountryCode())
                && Objects.nonNull(vo.getTransactionTypeCode())) {
            // query account
            final AccountDto account = this.findAccountDto(vo.getMerchantId(),
                    vo.getCountryCode(), vo.getTransactionTypeCode());
            if (Objects.isNull(account)) {
                return PageVo.buildEmptyPage(vo.getPageSize());
            }
            spec.eq("accountId", account.getId());
            spec.eq("countryCode", account.getCountryCode());
            spec.eq("transactionTypeCode", account.getTransactionTypeCode());
        }
        spec.eq(Objects.nonNull(vo.getAccountId()), "accountId", vo.getAccountId());

        Sort sort = Sort.by(Sort.Order.desc("billDate"));
        if (StringUtils.isNotBlank(vo.getSortField())) {
            if (SortTypeEnum.DESC == vo.getSortType()) {
                sort = Sort.by(Sort.Order.desc(vo.getSortField().trim()));
            } else {
                sort = Sort.by(Sort.Order.asc(vo.getSortField().trim()));
            }
        }

        final Page<AccountDailyBill> page = accountDailyBillRepository.findAll(spec.build(),
                PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(), sort));

        if (page.getTotalElements() <= 0) {
            return PageVo.buildEmptyPage(vo.getPageSize());
        }

        final List<AccountDailyBillDto> resultList = page.stream()
                .map(modelMapper::convertBo).collect(Collectors.toList());

        final AccountDailyBillDto subTotal = this.subtotalStatistics(vo);

        if (Objects.nonNull(subTotal)) {
            subTotal.setStartBalance(accountDailyBillRepository.findAll(spec.build(),
                    Sort.by(Sort.Order.asc("billDate"))).get(0).getStartBalance());
            subTotal.setEndBalance(accountDailyBillRepository.findAll(spec.build(),
                    Sort.by(Sort.Order.desc("billDate"))).get(0).getEndBalance());
        }

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(), page.getTotalElements(),
                resultList, subTotal);
    }

    private AccountDailyBillDto subtotalStatistics(final QueryPageAccountDailyVo vo) {
        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;
        final QBean<AccountDailyBillDto> bean = Projections.bean(AccountDailyBillDto.class,
                entity.topupCount.sum().coalesce(0L).as("topupCount"),
                entity.topupAmount.sum().coalesce(BigDecimal.ZERO).as("topupAmount"),
                entity.topupFee.sum().coalesce(BigDecimal.ZERO).as("topupFee"),
                entity.topupTax.sum().coalesce(BigDecimal.ZERO).as("topupTax"),

                entity.transactionCount.sum().coalesce(0L).as("transactionCount"),
                entity.transactionAmount.sum().coalesce(BigDecimal.ZERO).as("transactionAmount"),
                entity.settlementAmount.add(entity.additionalCharge)
                        .sum().coalesce(BigDecimal.ZERO).as("settlementAmount"),
                entity.transactionAmountUsd.sum().coalesce(BigDecimal.ZERO)
                        .as("transactionAmountUsd"),

                entity.calculateFee.sum().coalesce(BigDecimal.ZERO).as("calculateFee"),
                entity.calculateTax.sum().coalesce(BigDecimal.ZERO).as("calculateTax"),

                entity.transactionFee.sum().coalesce(BigDecimal.ZERO).as("transactionFee"),
                entity.transactionTax.sum().coalesce(BigDecimal.ZERO).as("transactionTax"),

                entity.calculateFee2.sum().coalesce(BigDecimal.ZERO).as("calculateFee2"),
                entity.calculateTax2.sum().coalesce(BigDecimal.ZERO).as("calculateTax2"),

                entity.transactionFee2.sum().coalesce(BigDecimal.ZERO).as("transactionFee2"),
                entity.transactionFee2.sum().coalesce(BigDecimal.ZERO).as("transactionFee2"),

                entity.transferCount.sum().coalesce(0L).as("transferCount"),
                entity.transferAmount.sum().coalesce(BigDecimal.ZERO).as("transferAmount"),
                entity.transferFee.sum().coalesce(BigDecimal.ZERO).as("transferFee"),
                entity.transferTax.sum().coalesce(BigDecimal.ZERO).as("transferTax"),

                entity.refundCount.sum().coalesce(0L).as("refundCount"),
                entity.refundAmount.sum().coalesce(BigDecimal.ZERO).as("refundAmount"),
                entity.refundFee.sum().coalesce(BigDecimal.ZERO).as("refundFee"),
                entity.refundTax.sum().coalesce(BigDecimal.ZERO).as("refundTax"),

                entity.adjustmentCount.sum().coalesce(0L).as("adjustmentCount"),
                entity.adjustmentAmount.sum().coalesce(BigDecimal.ZERO).as("adjustmentAmount"));

        final AccountDto account = this.findAccountDto(vo.getMerchantId(),
                vo.getCountryCode(), vo.getTransactionTypeCode());
        if (Objects.isNull(account)) {
            return null;
        }

        BooleanExpression condition = entity.accountId.eq(account.getId())
                .and(entity.countryCode.eq(account.getCountryCode()))
                .and(entity.transactionTypeCode.eq(account.getTransactionTypeCode()));

        if (Objects.nonNull(vo.getStartDate())) {
            condition = condition.and(entity.billDate.goe(vo.getStartDate()));
        }
        if (Objects.nonNull(vo.getEndDate())) {
            condition = condition.and(entity.billDate.loe(vo.getEndDate()));
        }

        if (Objects.nonNull(vo.getAccountId()) && vo.getAccountId() > 0L) {
            condition = condition.and(entity.accountId.eq(vo.getAccountId()));
        }

        return jpaQueryFactory.select(bean).from(entity).where(condition).fetchOne();

    }

    @Override
    public HistoryDailyBillStatisticsDto statisticsHistoryDailyBill() {
        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;
        final QBean<HistoryDailyBillStatisticsDto> bean =
                Projections.bean(HistoryDailyBillStatisticsDto.class,
                        entity.transactionAmountUsd.abs().sum()
                                .coalesce(BigDecimal.ZERO).as("transactionAmountUsd"),
                        entity.transactionCount.sum().coalesce(0L).as("transactionCount"));

        final List<BooleanExpression> predicates = setInnerMerchantCondition(Lists.newArrayList());
        final HistoryDailyBillStatisticsDto statisticsDto = jpaQueryFactory.select(bean)
                .from(entity).where(predicates.toArray(new BooleanExpression[] {}))
                .fetchOne();
        Objects.requireNonNull(statisticsDto)
                .setTransactionAmountUsd(statisticsDto.getTransactionAmountUsd());
        statisticsDto.setCurrency(CurrencyEnum.USD);
        return statisticsDto;
    }

    @Override
    public List<AccountDailyBillDto> listAccountDailyBillByIdsDate(final QueryAccountIdsDateVo vo) {
        final PredicateBuilder<AccountDailyBill> spec = Specifications.and();
        spec.ge("billDate", vo.getBillDate());
        spec.in(CollectionUtils.isNotEmpty(vo.getAccountIds()),
                "accountId", ListUtils.emptyIfNull(vo.getAccountIds()).toArray());
        final List<AccountDailyBill> accountDailyBillList =
                accountDailyBillRepository.findAll(spec.build());

        return modelMapper.convertDailyBillList(accountDailyBillList);
    }

    @Override
    public DailyBillStatisticsDto statisticsDailyBill(DailyBillStatisticsVo vo) {
        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;

        final List<BooleanExpression> predicates = setInnerMerchantCondition(Lists.newArrayList());
        predicates.add(entity.billDate.goe(vo.getStartDate()));
        predicates.add(entity.billDate.loe(vo.getEndDate()));

        final DailyBillStatisticsDto dailyBillStatisticsDto = jpaQueryFactory
                .select(Projections.fields(DailyBillStatisticsDto.class,
                        entity.transactionAmountUsd.abs().sum()
                                .coalesce(BigDecimal.ZERO).as("transactionAmountUsd"),
                        entity.transactionCount.sum().coalesce(0L).as("transactionCount")))
                .from(entity)
                .where(predicates.toArray(new BooleanExpression[] {}))
                .fetchOne();
        Objects.requireNonNull(dailyBillStatisticsDto).setCurrency(CurrencyEnum.USD);
        return dailyBillStatisticsDto;
    }

    @Override
    public List<AccountDailyBillStatisticsDto> dailyStatisticsByDate(final LocalDate startDate,
                                                                     final LocalDate endDate) {
        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;
        final QBean<AccountDailyBillStatisticsDto> bean =
                Projections.bean(AccountDailyBillStatisticsDto.class, entity.billDate,
                        entity.transactionAmountUsd.abs().sum()
                                .coalesce(BigDecimal.ZERO).as("transactionAmountUsd"),
                        entity.transactionCount.sum().coalesce(0L).as("transactionCount"));

        final List<BooleanExpression> predicates = setInnerMerchantCondition(Lists.newArrayList());
        if (Objects.nonNull(startDate)) {
            predicates.add(entity.billDate.goe(startDate));
        }

        if (Objects.nonNull(endDate)) {
            predicates.add(entity.billDate.loe(endDate));
        }

        return jpaQueryFactory.select(bean)
                .from(entity)
                .where(predicates.toArray(new BooleanExpression[] {}))
                .groupBy(entity.billDate).fetch();
    }

    @Override
    public List<EachMerchantDailyBillStatisticsDto> eachMerchantDailyStatisticsByDate(
            final LocalDate startDate, final LocalDate endDate) {
        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;
        final QBean<EachMerchantDailyBillStatisticsDto> bean =
                Projections.bean(EachMerchantDailyBillStatisticsDto.class,
                        entity.billDate,
                        entity.merchantId,
                        entity.countryCode,
                        entity.transactionAmountUsd.abs().sum()
                                .coalesce(BigDecimal.ZERO).as("transactionAmountUsd"),
                        entity.transactionCount.sum().coalesce(0L).as("transactionCount"));

        final BooleanExpression condition = entity.billDate.goe(startDate)
                .and(entity.billDate.loe(endDate));

        return jpaQueryFactory.select(bean)
                .from(entity)
                .where(condition)
                .groupBy(entity.billDate,
                        entity.merchantId,
                        entity.countryCode)
                .fetch();
    }

    @Override
    public List<CountryDailyBillStatisticsDto> countryDailyBillStatistics(
            final QueryCountryDailyBillStatisticsVo vo) {
        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;

        final QBean<CountryDailyBillStatisticsDto> bean =
                Projections.bean(CountryDailyBillStatisticsDto.class,
                        entity.billDate,
                        entity.transactionAmountUsd.abs().sum()
                                .coalesce(BigDecimal.ZERO).as("transactionAmountUsd"),
                        entity.transactionCount.sum().coalesce(0L).as("transactionCount"));

        final List<BooleanExpression> predicates = setInnerMerchantCondition(Lists.newArrayList());
        predicates.add(entity.billDate.goe(vo.getStartDate()));
        predicates.add(entity.billDate.loe(vo.getEndDate()));
        predicates.add(entity.countryCode.eq(vo.getCountryCode()));
        predicates.add(entity.transactionTypeCode.eq(vo.getTransactionTypeCode()));

        final List<CountryDailyBillStatisticsDto> dataList = jpaQueryFactory.select(bean)
                .from(entity)
                .where(predicates.toArray(new BooleanExpression[] {}))
                .groupBy(entity.billDate,
                        entity.countryCode)
                .orderBy(entity.billDate.asc())
                .fetch();

        dataList.forEach(item -> item.setCurrency(CurrencyEnum.USD));

        return dataList;
    }

    private AccountDailyBillDto convertDto(final AccountDailyBill entity) {
        return modelMapper.convertBo(entity);
    }

    @Override
    public List<DailyBillStatisticsDto> globalDailyBillList(
            final QueryGlobalTransactionVo vo) {

        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;
        final QBean<DailyBillStatisticsDto> bean =
                Projections.bean(DailyBillStatisticsDto.class,
                        entity.billDate,
                        entity.transactionAmountUsd.abs().sum()
                                .coalesce(BigDecimal.ZERO).as("transactionAmountUsd"),
                        entity.transactionCount.sum().coalesce(0L)
                                .as("transactionCount"));

        final List<BooleanExpression> predicates = setInnerMerchantCondition(Lists.newArrayList());
        predicates.add(entity.billDate.between(vo.getStartDate(), vo.getEndDate()));

        final List<DailyBillStatisticsDto> list = jpaQueryFactory.select(bean)
                .from(entity)
                .where(predicates.toArray(new BooleanExpression[] {}))
                .groupBy(entity.billDate)
                .orderBy(entity.billDate.asc())
                .fetch();

        list.forEach(item -> item.setCurrency(CurrencyEnum.USD));

        return list;
    }

    @Override
    public Set<Long> listHasTransactionAccount(final ListHasTransactionAccountVo vo) {
        final QAccountDailyBill accountDailyBill = QAccountDailyBill.accountDailyBill;
        final BooleanExpression condition =
                accountDailyBill.billDate.goe(vo.getStartDate())
                        .and(accountDailyBill.billDate.loe(vo.getEndDate()));
        return new HashSet<>(jpaQueryFactory.select(accountDailyBill.accountId)
                .from(accountDailyBill)
                .where(condition)
                .groupBy(accountDailyBill.accountId)
                .having(accountDailyBill.transactionCount.sum().gt(0L))
                .fetch());
    }

    private List<BooleanExpression> setInnerMerchantCondition(
            final List<BooleanExpression> predicates) {

        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;

        final List<Long> innerMerchantIds = baseService.getInnerMerchant()
                .stream().map(MerchantDto::getId).collect(Collectors.toList());
        if (ObjectUtils.isNotEmpty(innerMerchantIds)) {
            predicates.add(entity.merchantId.notIn(innerMerchantIds));
        }

        return predicates;
    }

    @Override
    public void fixHistoryAccountDailyBill(final FixAccountDailyBillVo vo) {
        LocalDate executeDate = vo.getBeginDate();
        List<AccountDailyBillDto> billList;
        do {
            billList = this.loadAccountDailyBill(executeDate);
            log.info("begin fix account daily bill, executeDate={}, billSize={}",
                    executeDate, billList.size());

            for (final AccountDailyBillDto dto : billList) {
                if (Objects.nonNull(dto)
                        && Objects.nonNull(dto.getAccountId())
                        && Objects.nonNull(dto.getId())) {

                    this.getAndFixDailyBill(dto.getAccountId(), dto.getId());
                }
            }

            executeDate = executeDate.plusDays(1);
        } while (vo.getEndDate().isAfter(executeDate));

        log.info("end fix account daily bill, executeDate={}", executeDate);
    }

    private void getAndFixDailyBill(final Long accountId, final Long billId) {
        try {
            final QAccount account = QAccount.account;
            final QTransactionMoney money = QTransactionMoney.transactionMoney;
            final BooleanExpression condition = money.accountId.eq(accountId)
                    .and(money.billId.eq(billId));

            final QBean<AccountDailyTransactionBo> bean =
                    Projections.fields(AccountDailyTransactionBo.class,
                            money.billId.as("billId"),
                            (money.amount.multiply(money.amountPon)).sum()
                                    .coalesce(BigDecimal.ZERO).as("totalTransactionAmount"),
                            money.currency.as("transactionCurrency"),
                            account.currency.as("settlementCurrency"));

            final AccountDailyTransactionBo bo = jpaQueryFactory.select(bean)
                    .from(money)
                    .leftJoin(account).on(money.accountId.eq(account.id))
                    .where(condition)
                    .fetchOne();

            if (Objects.nonNull(bo) && Objects.nonNull(bo.getBillId()) && bo.getBillId() > 0) {
                final QAccountDailyBill bill = QAccountDailyBill.accountDailyBill;
                jpaQueryFactory.update(bill)
                        .set(bill.transactionAmount, bo.getTotalTransactionAmount())
                        .set(bill.transactionCurrency, bo.getTransactionCurrency())
                        .set(bill.settlementCurrency, bo.getSettlementCurrency())
                        .where(bill.id.eq(bo.getBillId()))
                        .execute();
            }
        } catch (Exception e) {
            log.error("GetAndFixDailyBill error", e);
        }
    }

    private List<AccountDailyBillDto> loadAccountDailyBill(final LocalDate billDate) {
        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;
        final BooleanExpression condition = entity.billDate.eq(billDate)
                .and(entity.transactionCount.gt(0L));
        final QBean<AccountDailyBillDto> bean =
                Projections.fields(AccountDailyBillDto.class,
                        entity.id,
                        entity.accountId,
                        entity.billDate);
        return jpaQueryFactory.select(bean).from(entity).where(condition).fetch();
    }

    private AccountDto findAccountDto(final Long merchantId,
                                      final CountryCodeEnum country,
                                      final TransactionTypeCodeEnum transactionType) {
        if (Objects.isNull(merchantId)
                || Objects.isNull(country)
                || Objects.isNull(transactionType)) {
            return null;
        }

        final QAccount entity = QAccount.account;
        final BooleanExpression condition = entity.merchantId.eq(merchantId)
                .and(entity.countryCode.eq(country))
                .and(entity.transactionTypeCode.eq(transactionType));

        final Account account = jpaQueryFactory.select(entity)
                .from(entity)
                .where(condition)
                .fetchOne();

        return Objects.nonNull(account) ? modelMapper.convert(account) : null;
    }

}
