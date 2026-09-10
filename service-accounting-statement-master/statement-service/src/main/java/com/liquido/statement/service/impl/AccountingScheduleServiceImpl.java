package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.enums.AccountingScheduleStateEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.statement.common.properties.StatementProperties;
import com.liquido.statement.pojo.bo.AccountBo;
import com.liquido.statement.pojo.bo.AccountCardScheduleBo;
import com.liquido.statement.pojo.bo.SummaryCalendarInfoBo;
import com.liquido.statement.pojo.dto.AccountingCalendarDto;
import com.liquido.statement.pojo.entity.AccountingSchedule;
import com.liquido.statement.pojo.entity.QAccountingSchedule;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.AccountingScheduleVo;
import com.liquido.statement.pojo.vo.QueryAccountingCalendarVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;
import com.liquido.statement.repository.AccountRepository;
import com.liquido.statement.repository.AccountingScheduleRepository;
import com.liquido.statement.service.AccountingScheduleService;
import com.liquido.statement.service.TransactionMoneyService;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Slf4j
@Service
@AllArgsConstructor
public class AccountingScheduleServiceImpl implements AccountingScheduleService {

    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final AccountRepository accountRepository;
    private final AccountingScheduleRepository accountingScheduleRepository;
    private final StatementProperties.Calendar calendar;


    @Autowired
    @Lazy
    private TransactionMoneyService transactionMoneyService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<AccountingSchedule> batchSave(final List<TransactionMoneyVo> orderList) {

        final List<TransactionMoneyVo> installmentOrderList = orderList.stream()
                .filter(x -> TransactionTypeCodeEnum.PAY_IN == x.getTransactionTypeCode()
                        && ProductCodeEnum.CARD == x.getProductCode()
                        && CollectionUtils.isNotEmpty(x.getAccountingScheduleList()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(installmentOrderList)) {
            return Lists.newArrayList();
        }

        final List<AccountingSchedule> dataList = Lists.newArrayList();
        for (final TransactionMoneyVo order : installmentOrderList) {
            for (final AccountingScheduleVo plan : order.getAccountingScheduleList()) {

                dataList.add(AccountingSchedule.builder()
                        .id(SnowflakeIdUtil.generate())
                        .merchantId(order.getMerchantId())
                        .accountId(order.getAccountId())
                        .transactionId(order.getTransactionId())
                        .uniqueId(order.getUniqueId())
                        .transactionDate(order.getTransactionDate())
                        .productCode(order.getProductCode())
                        .accountingDate(plan.getAccountingDate())
                        .settlementAmount(plan.getSettlementAmount())
                        .accountingAmount(plan.getAccountingAmount())
                        .feeAmount(plan.getFeeAmount())
                        .taxAmount(plan.getTaxAmount())
                        .currency(order.getCurrency())
                        .currentInstallment(plan.getCurrentInstallment())
                        .totalInstallment(plan.getTotalInstallment())
                        .cardBrand(plan.getCardBrand())
                        .cardType(plan.getCardType())
                        .state(AccountingScheduleStateEnum.PENDING)
                        .createdTime(LocalDateTimeUtil.nowUtc())
                        .updatedTime(LocalDateTimeUtil.nowUtc())
                        .version(1)
                        .delFlag(Boolean.FALSE)
                        .remark("")
                        .build());
            }
        }

        return accountingScheduleRepository.saveAllAndFlush(dataList);
    }

    @Override
    public BigDecimal queryPendingAccountingAmount(final Long accountId,
                                                   final LocalDate accountingDate) {

        final QAccountingSchedule entity = QAccountingSchedule.accountingSchedule;
        return jpaQueryFactory.select(entity.accountingAmount.sum()
                        .coalesce(BigDecimal.ZERO).as("accountingAmount"))
                .from(entity)
                .where(entity.accountId.eq(accountId)
                        .and(entity.state.eq(AccountingScheduleStateEnum.PENDING))
                        .and(entity.accountingDate.eq(accountingDate)))
                .fetchOne();
    }

    @Override
    public BigDecimal queryAccountingAmount(final Long accountId,
                                            final AccountingScheduleStateEnum state) {

        final QAccountingSchedule entity = QAccountingSchedule.accountingSchedule;
        return jpaQueryFactory.select(entity.accountingAmount.sum()
                        .coalesce(BigDecimal.ZERO).as("accountingAmount"))
                .from(entity)
                .where(entity.accountId.eq(accountId)
                        .and(entity.state.eq(state)))
                .fetchOne();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateAccounted(final Long accountId,
                                final LocalDate accountingDate) {

        final QAccountingSchedule entity = QAccountingSchedule.accountingSchedule;
        jpaQueryFactory.update(entity)
                .set(entity.state, AccountingScheduleStateEnum.ACCOUNTED)
                .set(entity.version, entity.version.add(1))
                .set(entity.actualAccountingTime, LocalDateTimeUtil.nowUtc())
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.accountId.eq(accountId)
                        .and(entity.accountingDate.eq(accountingDate))
                        .and(entity.state.eq(AccountingScheduleStateEnum.PENDING)))
                .execute();
    }

    @Override
    public List<AccountingCalendarDto> queryAccountingCalendarList(
            final QueryAccountingCalendarVo vo) {

        if (vo.getEndDate().isBefore(vo.getStartDate()) ||
                ChronoUnit.MONTHS.between(vo.getStartDate(), vo.getEndDate()) >
                        Optional.ofNullable(calendar.getMaxMonthInterval()).orElse(2)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL.exception(
                    "wrong time range selected");
        }

        final LocalDate beginDate = LocalDateUtil.firstDayOfMonth(vo.getStartDate());
        final LocalDate endDate = LocalDateUtil.lastDayOfMonth(vo.getEndDate());

        final AccountBo accountInfo = modelMapper.convertBo(
                accountRepository.findByIdAndAndDelFlagIn(vo.getAccountId(),
                        List.of(Boolean.TRUE, Boolean.FALSE)));
        if (Objects.isNull(accountInfo)) {
            throw CommonExceptionCode.DATA_NOT_FOUND.exception();
        }
        log.info("query account calendar list beginDate={}, endDate={}", beginDate, endDate);

        final List<AccountCardScheduleBo> dataList =
                loadAccountCalendarData(vo, beginDate, endDate);

        return buildAccountingCalendarResult(processSummaryOriginalData(dataList), accountInfo,
                beginDate, endDate);
    }

    private List<SummaryCalendarInfoBo> processSummaryOriginalData(
            final List<AccountCardScheduleBo> dataList) {

        final Map<String, List<AccountCardScheduleBo>> map =
                dataList.stream().collect(Collectors.groupingBy(v -> String.valueOf(
                        String.format("%s_%s", v.getAccountingDate(), v.getProductCode()))));

        final List<SummaryCalendarInfoBo> list = new ArrayList<>();

        for (final Map.Entry<String, List<AccountCardScheduleBo>> entry : map.entrySet()) {
            final String[] arr = entry.getKey().split("_");

            final LocalDate localDate = LocalDateUtil.formatToLocalDate(arr[0]);
            final SummaryCalendarInfoBo calendarInfoDto =
                    SummaryCalendarInfoBo.builder().productCode(ProductCodeEnum.parse(arr[1]))
                            .accountDate(localDate).build();

            BigDecimal settleAmount = BigDecimal.ZERO;
            BigDecimal accountingAmount = BigDecimal.ZERO;
            BigDecimal holdAmount = BigDecimal.ZERO;
            BigDecimal feeAmount = BigDecimal.ZERO;

            for (final AccountCardScheduleBo bo : entry.getValue()) {
                settleAmount = settleAmount.add(bo.getSettlementAmount());
                feeAmount = feeAmount.add(
                        Optional.ofNullable(bo.getFeeAmount()).orElse(BigDecimal.ZERO));
                accountingAmount = accountingAmount.add(bo.getAccountingAmount());
                holdAmount = holdAmount.add(HoldStatusEnum.HOLD == bo.getHoldStatus() ?
                                bo.getAccountingAmount() : BigDecimal.ZERO)
                        .add(AccountingScheduleStateEnum.HOLDING ==
                                bo.getAccountingScheduleState() ?
                                bo.getAccountingAmount() : BigDecimal.ZERO);
            }

            calendarInfoDto.setSettlementAmount(settleAmount);
            calendarInfoDto.setAccountAmount(accountingAmount);
            calendarInfoDto.setHoldAmount(holdAmount);
            calendarInfoDto.setFeeAmount(feeAmount);

            list.add(calendarInfoDto);
        }
        return list;
    }

    private List<AccountingCalendarDto> buildAccountingCalendarResult(
            final List<SummaryCalendarInfoBo> dataList,
            final AccountBo accountInfo,
            final LocalDate beginDate, final LocalDate endDate) {

        // grouping by date
        final Map<LocalDate, List<SummaryCalendarInfoBo>> calendarMap = dataList.stream()
                .collect(Collectors.groupingBy(SummaryCalendarInfoBo::getAccountDate));

        final Map<LocalDate, BigDecimal> settleAmountMap =
                sumAmount(dataList, SummaryCalendarInfoBo::getSettlementAmount);

        final Map<LocalDate, BigDecimal> accountAmountMap =
                sumAmount(dataList, SummaryCalendarInfoBo::getAccountAmount);

        final Map<LocalDate, BigDecimal> feeAmountMap =
                sumAmount(dataList, SummaryCalendarInfoBo::getFeeAmount);

        final List<AccountingCalendarDto> resultList = Lists.newArrayList();
        LocalDate tmpDate = beginDate;

        // warp result data
        while (!tmpDate.isAfter(endDate)) {
            final AccountingCalendarDto dto = AccountingCalendarDto.builder()
                    .accountDate(tmpDate)
                    .settleAmount(settleAmountMap.getOrDefault(tmpDate, BigDecimal.ZERO))
                    .accountAmount(accountAmountMap.getOrDefault(tmpDate, BigDecimal.ZERO))
                    .feeAmount(feeAmountMap.getOrDefault(tmpDate, BigDecimal.ZERO))
                    .currency(accountInfo.getCurrency())
                    .details(buildDetails(calendarMap.getOrDefault(tmpDate, Lists.newArrayList())))
                    .build();

            resultList.add(dto);
            tmpDate = tmpDate.plusDays(1);
        }

        // sort by date
        resultList.sort(Comparator.comparing(AccountingCalendarDto::getAccountDate));
        return resultList;
    }

    private List<AccountCardScheduleBo> loadAccountCalendarData(final QueryAccountingCalendarVo vo,
                                                                final LocalDate beginDate,
                                                                final LocalDate endDate) {

        final StopWatch stopWatch = new StopWatch("start load account calendar Data");
        stopWatch.start();
        // query installment transaction info
        final List<AccountCardScheduleBo> installmentList =
                queryCardScheduleList(vo, beginDate, endDate)
                        .stream().filter(v -> Objects.nonNull(v.getAccountingDate()))
                        .collect(Collectors.toList());

        log.info("build card schedule list size={}", installmentList.size());
        final List<CompletableFuture<List<AccountCardScheduleBo>>> futureList =
                Lists.newArrayList();

        LocalDate date = beginDate;
        while (!date.isAfter(endDate)) {
            final CompletableFuture<List<AccountCardScheduleBo>>
                    completableFuture = transactionMoneyService.runQueryPendAmountTask(vo, date);
            futureList.add(completableFuture);
            date = date.plusDays(1);
        }

        CompletableFuture.allOf(
                futureList.toArray(new CompletableFuture[futureList.size()])).join();

        final List<AccountCardScheduleBo> normalTransactionList =
                futureList.stream().filter(Objects::nonNull).flatMap(v -> v.join().stream())
                        .collect(Collectors.toList());

        stopWatch.stop();

        log.info("query schedule data time={}, schedule size={}", stopWatch.getLastTaskTimeMillis(),
                normalTransactionList.size());

        normalTransactionList.addAll(installmentList);

        return normalTransactionList;
    }

    private Map<LocalDate, BigDecimal> sumAmount(final List<SummaryCalendarInfoBo> dataList,
                                                 final Function<SummaryCalendarInfoBo, BigDecimal> function) {

        return dataList.stream()
                .filter(data -> Objects.nonNull(data.getSettlementAmount())
                        && Objects.nonNull(data.getAccountAmount()))
                .collect(Collectors.groupingBy(SummaryCalendarInfoBo::getAccountDate,
                        Collectors.reducing(BigDecimal.ZERO, function, BigDecimal::add)));
    }

    private List<AccountCardScheduleBo> queryCardScheduleList(
            final QueryAccountingCalendarVo vo,
            final LocalDate beginDate,
            final LocalDate endDate) {

        final QAccountingSchedule entity = QAccountingSchedule.accountingSchedule;
        final QBean<AccountCardScheduleBo> bean =
                Projections.fields(AccountCardScheduleBo.class,
                        entity.settlementAmount.sum().coalesce(BigDecimal.ZERO)
                                .as("settlementAmount"),
                        entity.accountingAmount.sum().coalesce(BigDecimal.ZERO)
                                .as("accountingAmount"),
                        entity.feeAmount.sum().coalesce(BigDecimal.ZERO)
                                .add(entity.taxAmount.sum().coalesce(BigDecimal.ZERO))
                                .as("feeAmount"),
                        entity.currency.as("accountingCurrency"),
                        entity.state.as("accountingScheduleState"),
                        entity.productCode.as("productCode"),
                        entity.accountingDate);

        BooleanExpression condition = entity.merchantId.eq(vo.getMerchantId())
                .and(entity.accountId.eq(vo.getAccountId()))
                .and(entity.accountingDate.goe(beginDate))
                .and(entity.accountingDate.loe(endDate));

        if (Objects.nonNull(vo.getProductCode())) {
            condition = condition.and(entity.productCode.eq(vo.getProductCode()));
        }

        return jpaQueryFactory.select(bean)
                .from(entity)
                .where(condition)
                .orderBy(entity.accountingDate.asc())
                .groupBy(entity.accountingDate, entity.state, entity.productCode)
                .fetch();
    }


    private static List<AccountingCalendarDto.DetailDto> buildDetails(
            final List<SummaryCalendarInfoBo> details) {

        if (CollectionUtils.isEmpty(details)) {
            return Lists.newArrayList();
        }
        return details.stream().map(entry -> AccountingCalendarDto.DetailDto.builder()
                .productCode(entry.getProductCode())
                .settlementAmount(entry.getSettlementAmount())
                .accountAmount(entry.getAccountAmount())
                .holdAmount(entry.getHoldAmount())
                .feeAmount(entry.getFeeAmount())
                .build()).collect(Collectors.toList());
    }

}
