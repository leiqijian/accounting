package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.enums.AccountingScheduleStateEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VersionEnum;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.pojo.bo.AccountCardScheduleBo;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.DailyExtractableAmountInfo;
import com.liquido.statement.pojo.bo.DailyTransactionMoneyBo;
import com.liquido.statement.pojo.bo.ExtractableAmountBo;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.TransactionMoneyDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountDailyBill;
import com.liquido.statement.pojo.entity.QAccountDailyBill;
import com.liquido.statement.pojo.entity.QTransactionMoney;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.BatchWithdrawalApplyVo;
import com.liquido.statement.pojo.vo.ExtendData;
import com.liquido.statement.pojo.vo.FillFieldTransactionMoneyVo;
import com.liquido.statement.pojo.vo.ListTransactionMoneyVo;
import com.liquido.statement.pojo.vo.QueryAccountingCalendarVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;
import com.liquido.statement.repository.SumAccountScheduleDataRepository;
import com.liquido.statement.repository.TransactionCostRepository;
import com.liquido.statement.repository.TransactionMoneyRepository;
import com.liquido.statement.service.AccountingScheduleService;
import com.liquido.statement.service.TransactionMoneyService;
import com.liquido.statement.service.TransactionUnHoldService;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionMoneyServiceImpl implements TransactionMoneyService {

    private final ModelMapper modelMapper;
    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final TransactionUnHoldService transactionUnHoldService;
    private final TransactionMoneyRepository transactionMoneyRepository;
    private final TransactionCostRepository transactionCostRepository;
    private final AccountingScheduleService accountingScheduleService;
    private final SumAccountScheduleDataRepository sumAccountScheduleDataRepository;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public TransactionMoney save(final TransactionMoney entity) {
        return transactionMoneyRepository.saveAndFlush(entity);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<TransactionMoney> batchSave(List<TransactionMoney> entityList) {
        return transactionMoneyRepository.saveAllAndFlush(entityList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<TransactionMoney> batchSave(
            final List<TransactionMoneyVo> dataList,
            final AccountDailyInitBo dailyInitBo) {

        return transactionMoneyRepository.saveAllAndFlush(
                dataList.stream().map(vo -> transferEntity(vo, dailyInitBo))
                        .collect(Collectors.toList()));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean updateState(
            final Long id,
            final SettleStatusEnum fromState,
            final SettleStatusEnum toState,
            final Integer fromVersion) {

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        return jpaQueryFactory.update(entity)
                .set(entity.settleStatus, toState)
                .set(entity.version, entity.version.add(1))
                .set(entity.settleTime, LocalDateTimeUtil.nowUtc())
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.id.eq(id)
                        .and(entity.settleStatus.eq(fromState))
                        .and(entity.version.eq(fromVersion)))
                .execute() > 0;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean batchUpdateState(final List<Long> idList, final SettleStatusEnum fromState,
            final SettleStatusEnum toState, final Integer fromVersion) {
        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        return jpaQueryFactory.update(entity)
                .set(entity.settleStatus, toState)
                .set(entity.version, entity.version.add(1))
                .set(entity.settleTime, LocalDateTimeUtil.nowUtc())
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.id.in(idList)
                        .and(entity.settleStatus.eq(fromState))
                        .and(entity.version.eq(fromVersion)))
                .execute() > 0;
    }

    @Override
    public TransactionMoney queryOriginalOrderInfo(final Long accountId,
            final String uniqueId,
            final DirectionTypeEnum directionType) {

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        return jpaQueryFactory.select(entity).from(entity)
                .where(entity.uniqueId.eq(uniqueId)
                        .and(entity.accountId.eq(accountId))
                        .and(entity.directionType.eq(directionType)))
                .orderBy(entity.id.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public TransactionMoney queryOriginalOrderInfo(final Long transactionId,
            final DirectionTypeEnum directionType) {
        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        return jpaQueryFactory.select(entity)
                .from(entity)
                .where(entity.transactionId.eq(transactionId)
                        .and(entity.directionType.eq(directionType)))
                .orderBy(entity.id.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public ExtendData queryExtendInfo(final Long transactionId,
            final DirectionTypeEnum directionType) {
        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        return jpaQueryFactory.select(entity.extendData)
                .from(entity)
                .where(entity.transactionId.eq(transactionId)
                        .and(entity.directionType.eq(directionType)))
                .orderBy(entity.id.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public Map<String, String> queryCalculationRule(
            final Long transactionId,
            final DirectionTypeEnum directionType) {

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        return jpaQueryFactory.select(entity.calculationRule)
                .from(entity)
                .where(entity.transactionId.eq(transactionId)
                        .and(entity.directionType.eq(directionType)))
                .orderBy(entity.id.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public List<TransactionMoneyDto> listTransactionMoneyInfo(
            final ListTransactionMoneyVo vo) {

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        return modelMapper.convertBo(jpaQueryFactory
                .select(entity)
                .from(entity)
                .where(entity.merchantId.eq(vo.getMerchantId())
                        .and(entity.uniqueId.eq(vo.getUniqueId())))
                .orderBy(entity.id.asc())
                .fetch());
    }

    @Override
    public boolean checkUnSettleTransactionCount(final Account account,
            final AccountDailyInitBo dailyInitBo) {
        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        final BooleanExpression condition = entity.accountId.eq(account.getId())
                .and(entity.settleStatus.in(SettleStatusEnum.WAITING, SettleStatusEnum.PROCESSING))
                .and(entity.billId.eq(dailyInitBo.getBillId()));

        final TransactionMoney result = jpaQueryFactory.select(entity)
                .from(entity).where(condition).limit(1).fetchOne();

        return Objects.nonNull(result) && result.getId() > 0L;
    }

    @Override
    public DailyTransactionMoneyBo statisticsDailyBill(
            final Account account,
            final AccountDailyInitBo dailyInitBo) {

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        final BooleanExpression condition = entity.accountId.eq(account.getId())
                .and(entity.settleStatus.eq(SettleStatusEnum.SUCCESS))
                .and(entity.billId.eq(dailyInitBo.getBillId()));

        final QBean<DailyTransactionMoneyBo> bean =
                Projections.fields(DailyTransactionMoneyBo.class,
                        (entity.amount.multiply(entity.amountPon)).sum()
                                .coalesce(BigDecimal.ZERO).as("totalTransactionAmount"),
                        (entity.settlementAmountUsd.multiply(entity.amountPon)).sum()
                                .coalesce(BigDecimal.ZERO).as("totalTransactionAmountUsd"),
                        entity.currency.as("transactionCurrency"),

                        (entity.settlementAmount.multiply(entity.amountPon)).sum()
                                .coalesce(BigDecimal.ZERO).as("totalSettlementAmount"),
                        entity.settlementCurrency.as("settlementCurrency"),
                        entity.id.count().coalesce(0L).as("totalCount"));

        final DailyTransactionMoneyBo dailyBo =
                jpaQueryFactory.select(bean).from(entity).where(condition).fetchOne();

        final BigDecimal totalAdditionalCharge =
                this.statisticsDailyAdditionalCharge(account, dailyInitBo);

        dailyBo.setTotalAdditionalCharge(totalAdditionalCharge);

        return dailyBo;
    }

    private BigDecimal statisticsDailyAdditionalCharge(
            final Account account,
            final AccountDailyInitBo dailyInitBo) {

        return Optional.ofNullable(transactionMoneyRepository.sumDailyAdditionalCharge(
                account.getId(), dailyInitBo.getBillId())).orElse(BigDecimal.ZERO);
    }

    private TransactionMoney transferEntity(
            final TransactionMoneyVo vo,
            final AccountDailyInitBo dailyInitBo) {

        return TransactionMoney.builder()
                .id(SnowflakeIdUtil.generate())
                .uniqueId(vo.getUniqueId())
                .transactionId(vo.getTransactionId())
                .merchantId(vo.getMerchantId())
                .subMerchantId(vo.getSubMerchantId())
                .accountId(vo.getAccountId())
                .documentId(StringUtils.defaultIfBlank(vo.getDocumentId(), StringUtils.EMPTY))
                .fxRateId(Optional.ofNullable(vo.getFxRateId()).orElse(0L))
                .vendor(vo.getVendor())
                .transactionTypeCode(vo.getTransactionTypeCode())
                .productCode(vo.getProductCode())
                .directionType(vo.getDirectionType())
                .amount(vo.getAmount())
                .amountPon(vo.getAmountPon().getCode())
                .currency(vo.getCurrency())
                .fxRate(Optional.ofNullable(vo.getFxRate()).orElse(BigDecimal.ONE))
                .fxRateUsd(Optional.ofNullable(vo.getFxUsdRate()).orElse(BigDecimal.ONE))
                .fxLoseUsd(Optional.ofNullable(vo.getFxUsdLose()).orElse(BigDecimal.ZERO))
                .settlementAmount(Optional.ofNullable(vo.getSettlementAmount())
                        .orElse(BigDecimal.ZERO))
                .settlementAmountUsd(Optional.ofNullable(vo.getSettlementAmountUsd())
                        .orElse(BigDecimal.ZERO))
                .settlementCurrency(vo.getSettlementCurrency())
                .settleStatus(SettleStatusEnum.PROCESSING)
                .submitTime(vo.getSubmitTime())
                .transactionTime(vo.getTransactionTime())
                .settleTime(LocalDateTimeUtil.nowUtc())
                .tradingModel(vo.getTradingModel())

                .beCreditedDate(vo.getBeCreditedDate())
                .beCreditedAmount(Optional.ofNullable(vo.getBeCreditedAmount())
                        .orElse(BigDecimal.ZERO))

                .additionalCharge(Optional.ofNullable(vo.getAdditionalCharge())
                        .orElse(Collections.emptyList()))

                .holdStatus(vo.getHoldStatus())
                .installmentFlag(Optional.ofNullable(vo.getInstallmentFlag()).orElse(false))
                .calculationRule(vo.getCalculationRule())
                .createdBy(0L)
                .updatedBy(0L)
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .billId(dailyInitBo.getBillId())
                .version(VersionEnum.LOCKED.getCode())
                .extendData(
                        JsonUtil.toBean(JsonUtil.toJson(vo.getExtendData()), ExtendData.class))
                .build();
    }

    @Override
    public DailyExtractableAmountInfo statisticsDailyExtractableAmount(
            final Account account,
            final AccountDailyInitBo latestDailyInitBo,
            final AccountDailyInitBo currentDailyInitBo,
            final BigDecimal latestAccountBizAmount,
            final BigDecimal latestDailyTransactionOccurredAmount) {

        // statistics payin account extractable amount
        if (TransactionTypeCodeEnum.PAY_IN == account.getTransactionTypeCode()) {
            return this.statisticsDailyPayInAccountExtractableAmount(
                    account, latestDailyInitBo, currentDailyInitBo, latestAccountBizAmount);
        }

        // statistics payout/marketplace account extractable amount
        return this.statisticsDailyPayOutAccountExtractableAmount(account,
                latestAccountBizAmount,
                latestDailyTransactionOccurredAmount);
    }

    private DailyExtractableAmountInfo statisticsDailyPayInAccountExtractableAmount(
            final Account account,
            final AccountDailyInitBo latestDailyInitBo,
            final AccountDailyInitBo currentDailyInitBo,
            final BigDecimal latestAccountBizAmount) {

        // Statistics yesterday total extractable amount
        final Pair<BigDecimal, BigDecimal> latestDailyPair =
                this.statisticsDailyExtractableAmount(account, latestDailyInitBo);

        // Statistics current day total extractable amount
        final Pair<BigDecimal, BigDecimal> curentDailyPair =
                this.statisticsDailyExtractableAmount(account, currentDailyInitBo);

        // Update credit-card accounting plan state after daily-cut success
        accountingScheduleService.updateAccounted(account.getId(),
                currentDailyInitBo.getTransactionDate());

        // Statistics current day total extractable amount
        final BigDecimal latestDailyUnHoldAmount =
                transactionUnHoldService.statisticsDailyUnHoldAmount(account, latestDailyInitBo);

        /** latestDailyOccurredExtractableAmt =
         latestDailyT0 + latestDailyTn + latestDailyBiz + latestDailyUnHold;
         */
        final BigDecimal latestDailyOccurredExtractableAmount = latestDailyPair.getLeft()
                .add(latestDailyPair.getRight())
                .add(latestAccountBizAmount)
                .add(latestDailyUnHoldAmount);

        return DailyExtractableAmountInfo.builder()
                .latestDailyT0ExtractableAmount(latestDailyPair.getLeft())
                .latestDailyTnExtractableAmount(latestDailyPair.getRight())
                .currentDailyT0ExtractableAmount(curentDailyPair.getLeft())
                .currentDailyTnExtractableAmount(curentDailyPair.getRight())
                .latestDailyBizOccurredAmount(latestAccountBizAmount)
                .latestDailyOccurredExtractableAmount(latestDailyOccurredExtractableAmount)
                .latestDailyExtractableEndBalance(account.getLatestDailyExtractableBalance()
                        .add(latestDailyOccurredExtractableAmount))
                .build();
    }

    private DailyExtractableAmountInfo statisticsDailyPayOutAccountExtractableAmount(
            final Account account,
            final BigDecimal latestAccountBizAmount,
            final BigDecimal latestDailyTaskOccurredAmount) {


        /** latestDailyOccurredExtractableAmount =
         latestDailyTaskOccurredAmount + latestAccountBizAmount;
         */
        final BigDecimal latestDailyOccurredExtractableAmount = latestDailyTaskOccurredAmount
                .add(latestAccountBizAmount);

        return DailyExtractableAmountInfo.builder()
                .latestDailyT0ExtractableAmount(BigDecimal.ZERO)
                .latestDailyTnExtractableAmount(BigDecimal.ZERO)
                .currentDailyT0ExtractableAmount(BigDecimal.ZERO)
                .currentDailyTnExtractableAmount(BigDecimal.ZERO)
                .latestDailyBizOccurredAmount(latestAccountBizAmount)
                .latestDailyOccurredExtractableAmount(latestDailyOccurredExtractableAmount)
                .latestDailyExtractableEndBalance(account.getLatestDailyExtractableBalance()
                        .add(latestDailyOccurredExtractableAmount))
                .build();
    }

    /**
     * Statistics Daily Extractable Amount
     *
     * @param account
     * @param dailyInitBo
     * @return Pair.of(dailyT0ExtractableAmount, dailyTnExtractableAmount)
     */
    private Pair<BigDecimal, BigDecimal> statisticsDailyExtractableAmount(
            final Account account,
            final AccountDailyInitBo dailyInitBo) {

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        final BooleanExpression condition = entity.accountId.eq(account.getId())
                .and(entity.beCreditedDate.eq(dailyInitBo.getTransactionDate()))
                .and(entity.holdStatus.in(HoldStatusEnum.NORMAL, HoldStatusEnum.UNHOLD));

        final List<ExtractableAmountBo> dailyTransactionList =
                jpaQueryFactory.select(Projections.fields(ExtractableAmountBo.class,
                                entity.tradingModel,
                                entity.beCreditedAmount.sum()
                                        .coalesce(BigDecimal.ZERO).as("totalAmount")))
                        .from(entity)
                        .where(condition)
                        .groupBy(entity.tradingModel)
                        .fetch();

        // query credit-card installment accounting amount
        final BigDecimal creditCardAccountingAmt =
                Optional.ofNullable(accountingScheduleService.queryPendingAccountingAmount(
                                account.getId(), dailyInitBo.getTransactionDate()))
                        .orElse(BigDecimal.ZERO);


        /*
         * statistics daily total extractable amount
         */
        BigDecimal dailyT0ExtractableAmount = BigDecimal.ZERO;
        BigDecimal dailyTnExtractableAmount = creditCardAccountingAmt;
        for (final ExtractableAmountBo data : dailyTransactionList) {
            if (TradingModelEnum.INSTANT_TRADING.contains(data.getTradingModel())) {
                dailyT0ExtractableAmount = dailyT0ExtractableAmount.add(data.getTotalAmount());
            } else {
                dailyTnExtractableAmount = dailyTnExtractableAmount.add(data.getTotalAmount());
            }
        }

        return Pair.of(dailyT0ExtractableAmount, dailyTnExtractableAmount);
    }

    @Override
    public BigDecimal getPendingBalance(final Account account) {
        if (Objects.isNull(account)
                || TransactionTypeCodeEnum.PAY_IN != account.getTransactionTypeCode()) {
            return BigDecimal.ZERO;
        }
        final AccountDailyBill dailyBill = this.findLatestDailyBill(account.getId());
        final LocalDate nextBillDate =
                Objects.nonNull(dailyBill) ? dailyBill.getBillDate().plusDays(1) :
                        LocalDateTimeUtil.nowUtcZonedDateTime()
                                .withZoneSameInstant(ZoneId.of(account.getTimezone()))
                                .toLocalDate();

        return this.queryPendingBalance(account.getId(), nextBillDate);
    }

    @Override
    public List<TransactionMoney> queryTransactionMoneyList(final BatchWithdrawalApplyVo vo) {

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        BooleanExpression condition = entity.accountId.eq(vo.getAccountId())
                .and(entity.beCreditedDate.eq(vo.getCreditedDate()))
                .and(entity.holdStatus.in(HoldStatusEnum.NORMAL, HoldStatusEnum.UNHOLD));
        if (CollectionUtils.isNotEmpty(vo.getSubMerchantId())) {
            condition = condition.and(entity.subMerchantId.in(vo.getSubMerchantId()));
        }

        return jpaQueryFactory.select(entity)
                .from(entity)
                .where(condition)
                .fetch();
    }

    @Override
    public BigDecimal queryPendingBalance(final Long accountId,
            final LocalDate date) {

        // non-credit-card accounting amount
        final BigDecimal nonCreditCardPendingAmt =
                Optional.ofNullable(transactionMoneyRepository.pendingAmount(accountId, date))
                        .orElse(BigDecimal.ZERO);

        // credit-card installment accounting amount
        final BigDecimal creditCardPendingAmt =
                Optional.ofNullable(accountingScheduleService.queryAccountingAmount(accountId,
                        AccountingScheduleStateEnum.PENDING)).orElse(BigDecimal.ZERO);

        return nonCreditCardPendingAmt.add(creditCardPendingAmt);
    }

    @Override
    public BigDecimal getHoldingBalance(final Long accountId) {
        BigDecimal holdingAmount =
                redisCacheUtil.getCacheObject(CacheConstant.ACCOUNT_HOLDING_KEY + accountId);
        if (Objects.nonNull(holdingAmount)) {
            return holdingAmount;
        }

        holdingAmount = transactionMoneyRepository.holdingAmount(accountId);
        holdingAmount = Objects.nonNull(holdingAmount) ? holdingAmount : BigDecimal.ZERO;
        redisCacheUtil.setCacheObject(CacheConstant.ACCOUNT_HOLDING_KEY + accountId, holdingAmount,
                10, TimeUnit.SECONDS);

        return holdingAmount;
    }

    @Override
    public List<TransactionMoney> queryHoldingOrderByDocumentIds(final Long accountId,
            final Set<String> documentIds) {

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        final BooleanExpression condition = entity.accountId.eq(accountId)
                .and(entity.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_IN))
                .and(entity.documentId.in(documentIds))
                .and(entity.holdStatus.eq(HoldStatusEnum.HOLD));

        return jpaQueryFactory.select(entity).from(entity).where(condition).fetch();
    }

    @Override
    public List<TransactionMoney> queryHoldingOrderByTransactionIds(
            final Long accountId,
            final Set<Long> transactionIds) {

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;

        final List<BooleanExpression> condition = Lists.newArrayList();
        condition.add(entity.accountId.eq(accountId));
        condition.add(entity.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_IN));
        condition.add(entity.holdStatus.eq(HoldStatusEnum.HOLD));
        if (CollectionUtils.isNotEmpty(transactionIds)) {
            condition.add(entity.transactionId.in(transactionIds));
        }

        return jpaQueryFactory.select(entity).from(entity)
                .where(condition.toArray(new BooleanExpression[] {})).fetch();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void cancelHoldingTransaction(final Long accountId, final Set<Long> idList) {
        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        final BooleanExpression condition = entity.accountId.eq(accountId)
                .and(entity.id.in(idList))
                .and(entity.holdStatus.eq(HoldStatusEnum.HOLD));

        jpaQueryFactory.update(entity).set(entity.holdStatus, HoldStatusEnum.UNHOLD)
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(condition)
                .execute();

        redisCacheUtil.deleteObject(CacheConstant.ACCOUNT_HOLDING_KEY + accountId);
    }

    private AccountDailyBill findLatestDailyBill(final Long accountId) {
        final QAccountDailyBill entity = QAccountDailyBill.accountDailyBill;

        final BooleanExpression condition = entity.accountId.eq(accountId)
                .and(entity.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_IN));

        return jpaQueryFactory.select(entity).from(entity).where(condition)
                .orderBy(entity.billDate.desc()).limit(1).fetchOne();
    }

    @Override
    public List<TransactionMoney> queryTransactionMoneyList(final Long startId,
            final Long accountId, final Long billId,
            final Integer limitSize) {
        final QTransactionMoney money = QTransactionMoney.transactionMoney;
        final BooleanExpression condition = money.id.gt(Optional.ofNullable(startId).orElse(0L))
                .and(money.accountId.eq(accountId)).and(money.billId.eq(billId));
        return jpaQueryFactory.select(money).from(money).where(condition).orderBy(money.id.asc())
                .limit(limitSize).fetch();
    }

    public List<TransactionMoneyDto> queryTransactionMoneyByAccountIdSubMerchantIdAndBillId(
            final Collection<Long> accountIds,
            final Set<String> subMerchantIds,
            final Long billId) {

        final QTransactionMoney money = QTransactionMoney.transactionMoney;
        final BooleanExpression condition = money.accountId.in(accountIds)
                .and(money.subMerchantId.in(subMerchantIds))
                .and(money.billId.gt(billId));

        return jpaQueryFactory.select(money).from(money).where(condition).fetch().stream()
                .map(modelMapper::convertBo).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateTransactionMoneyFxUsdRate(final AccountDto account, final Long billId,
            final DailyExchangeRateDto fxRate) {
        // step1 update transaction money fx
        final QTransactionMoney money = QTransactionMoney.transactionMoney;
        jpaQueryFactory.update(money).set(money.fxRateId, fxRate.getId())
                .set(money.fxRateUsd, fxRate.getMerchantRate())
                .set(money.fxLoseUsd, fxRate.getRatioLose())
                .where(money.accountId.eq(account.getId()).and(money.billId.eq(billId))).execute();

        // step1 update transaction cost fx
        transactionCostRepository.updateTransactionFxRate(account.getId(), billId,
                fxRate.getMerchantRate(), fxRate.getRatioLose());

    }

    @Override
    public void fillTransactionMoneyField(final List<FillFieldTransactionMoneyVo> list) {
        log.info("fill transaction money params is->{}", list);

        final List<String> uniqueIds = list.stream().map(FillFieldTransactionMoneyVo::getUniqueId)
                .collect(Collectors.toList());

        final Map<String, TransactionMoney> map =
                transactionMoneyRepository.findByUniqueIdIn(uniqueIds).stream().collect(
                        Collectors.toMap(TransactionMoney::getUniqueId, Function.identity()));

        list.forEach(vo -> {
            final TransactionMoney transactionMoney = map.get(vo.getUniqueId());
            if (Objects.isNull(transactionMoney)) {
                return;
            }
            final ExtendData extendData = transactionMoney.getExtendData();

            Optional.ofNullable(vo.getPayerCity()).ifPresent(extendData::setPayerCity);
            Optional.ofNullable(vo.getTargetName()).ifPresent(extendData::setTargetName);
            Optional.ofNullable(vo.getDocumentId()).ifPresent(transactionMoney::setDocumentId);
        });
        transactionMoneyRepository.saveAll(map.values());
    }


    @Async("reRunTaskExecutor")
    @Override
    public CompletableFuture<List<AccountCardScheduleBo>> runQueryPendAmountTask(
            final QueryAccountingCalendarVo vo, final LocalDate date) {
        try {
            return CompletableFuture.completedFuture(
                    sumAccountScheduleDataRepository.sumAccountScheduleData(
                            vo.getAccountId(), date, vo.getProductCode()));
        } catch (Exception e) {
            log.error("query account schedule data error:", e);
        }
        return CompletableFuture.completedFuture(null);
    }
}
