package com.liquido.statement.service.dailycut.handler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.DailyBillSummaryBo;
import com.liquido.statement.pojo.bo.DailyCutSuccessBo;
import com.liquido.statement.pojo.bo.DailyExtractableAmountInfo;
import com.liquido.statement.pojo.bo.DailyTransactionBizBo;
import com.liquido.statement.pojo.bo.DailyTransactionFeeBo;
import com.liquido.statement.pojo.bo.DailyTransactionMoneyBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountDailyBill;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.service.AccountDailyInitService;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.AccountStatementBizService;
import com.liquido.statement.service.TransactionBizService;
import com.liquido.statement.service.TransactionFeeService;
import com.liquido.statement.service.TransactionMoneyService;
import com.liquido.statement.service.dailycut.AccountDailyCutService;
import com.liquido.statement.service.dailycut.DailyCutHandlerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

/**
 * formula: amount = amount - (tradeAmount * fx) - fee
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.AbstractClassWithoutAbstractMethod"})
public abstract class AbstractAccountDailyCutHandler implements DailyCutHandlerService {

    @Resource
    private ModelMapper modelMapper;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private RedisCacheUtil redisCacheUtil;
    @Resource
    private AccountService accountService;
    @Resource
    private TransactionBizService transactionBizService;
    @Resource
    private TransactionFeeService transactionFeeService;
    @Resource
    private TransactionMoneyService transactionMoneyService;
    @Resource
    private AccountDailyInitService accountDailyInitService;
    @Resource
    private AccountDailyCutService accountDailyCutService;
    @Resource
    private AccountStatementBizService accountStatementBizService;

    /**
     * Async execute account daily cut
     *
     * @param accountId
     * @param billDate
     *
     * @return
     */
    @Override
    @Async("dailyCutTaskExecutor")
    public CompletableFuture<DailyCutSuccessBo> asyncHandleDailyCut(
            final Long accountId,
            final LocalDate billDate) {
        try {
            return CompletableFuture.completedFuture(
                    this.syncHandleDailyCut(accountId, billDate));
        } catch (Exception e) {
            log.error("account process daily cut error:", e);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * sync execute account daily cut
     *
     * @param accountId
     * @param billDate
     *
     * @return
     */
    @Override
    public DailyCutSuccessBo syncHandleDailyCut(
            final Long accountId,
            final LocalDate billDate) {

        final String lockKey = CacheConstant.buildDailyCutLockKey(accountId);
        final String lockVal = DataUtil.getUuid();
        try {

            /* Step1: Get daily cut lock */
            if (!redisDistLock.tryLock(lockKey, lockVal, 4, TimeUnit.HOURS)) {
                log.warn("accountId[{}] is processing daily cut now", accountId);
                throw StatementExceptionCode.ACCOUNT_DAILY_CUT_FAILED.exception();
            }

            // reload the real-time account info after locked success;
            final Account account = accountService.getById(accountId);

            final String billDateStr = billDate.format(LocalDateUtil.FORMAT_DATE);
            log.info("daily-cut handle account dailyCut begin accountId={}, billDate={}",
                    account.getId(), billDateStr);

            final StopWatch watch = new StopWatch(String.format("RunDailyCut:%s:%s",
                    account.getId(), billDateStr));

            final AccountDailyInitBo dailyInitBo = accountDailyInitService.getDailyBillInitInfo(
                    account.getId(), billDate);

            /* Step2: check exists unsettle transaction records */
            watch.start("daily-cut pre-check");
            if (transactionMoneyService.checkUnSettleTransactionCount(account, dailyInitBo)) {
                log.warn(">>>> merchantId=[{}], accountId=[{}],"
                                + " There are still pending transaction records at date={},"
                                + " Billing date cut off", account.getMerchantId(), account.getId(),
                        billDate.format(LocalDateUtil.FORMAT_DATE));
                // send alert notification
                throw StatementExceptionCode.ACCOUNT_DAILY_CUT_FAILED.exception();
            }
            watch.stop();

            /* Step3: Statistic daily bill summary */
            watch.start("daily-cut statistic daily bill summary");
            final DailyBillSummaryBo billSummary = this.statisticDailyBill(account, dailyInitBo);
            watch.stop();
            log.info("daily-cut statistic daily bill summary ts={}ms, accountId={}, billDate={}, "
                            + "billSummary={}", watch.getLastTaskTimeMillis(), account.getId(),
                    billDateStr, billSummary);

            /* Step4: Execute account dailyCut */
            watch.start("daily-cut execute account dailyCut");
            final AccountDailyBill accountDailyBill =
                    accountDailyCutService.executeAccountDailyCut(billSummary);
            watch.stop();
            log.info("daily-cut execute account dailyCut end ts={}ms, account={}, billDate={},",
                    watch.getTotalTimeMillis(), account.getId(), billDateStr);
            log.info("daily-cut handle account dailyCut end log-trace={}", watch.prettyPrint());

            /* Step5: After process account daily cut completed,  save to cache */
            final String cacheKey = buildDailyCutCacheKey(account.getId(), billDate);
            redisCacheUtil.setCacheMapValue(CacheConstant.COMPLETED_DAILY_CUT, cacheKey,
                    accountDailyBill.getId());

            return DailyCutSuccessBo.builder()
                    .billId(accountDailyBill.getId())
                    .accountId(accountDailyBill.getAccountId())
                    .merchantId(accountDailyBill.getMerchantId())
                    .timezone(accountDailyBill.getTimezone())
                    .billDate(accountDailyBill.getBillDate())
                    .countryCode(accountDailyBill.getCountryCode())
                    .transactionTypeCode(accountDailyBill.getTransactionTypeCode())
                    .dailyExtractableAmountInfo(billSummary.getDailyExtractableAmountInfo())
                    //.latestDailyExtractableEndBalance(accountDailyBill.getExtractableBalance())
                    .latestDailyTransactionVolume(billSummary.getLatestDailyTransactionVolume())
                    .latestDailyOccurredAmount(billSummary.getLatestDailyOccurredAmount())
                    .latestDailyOccurredCount(billSummary.getLatestDailyOccurredCount())
                    .dailyEndBalance(accountDailyBill.getEndBalance())
                    .transactionCount(accountDailyBill.getTransactionCount())
                    .currency(account.getCurrency())
                    .build();
        } finally {
            redisDistLock.unlock(lockKey, lockVal);
        }
    }

    private static String buildDailyCutCacheKey(
            final Long accountId,
            final LocalDate billDate) {

        return String.join(":", accountId.toString(),
                billDate.format(LocalDateUtil.FORMAT_YYYYMMDD));
    }

    protected DailyBillSummaryBo statisticDailyBill(
            final Account account,
            final AccountDailyInitBo latestDailyInitBo) {

        final AccountDailyInitBo currentDailyInitBo =
                accountDailyInitService.getDailyBillInitInfo(account.getId(),
                        latestDailyInitBo.getTransactionDate().plusDays(1));

        /* Step1: Statistic Daily(yesterday: 00:00:00～23:59:59) Transaction Money Order*/
        final DailyTransactionMoneyBo latestTransactionMoney =
                transactionMoneyService.statisticsDailyBill(account, latestDailyInitBo);

        /* Step2: Statistic Daily(yesterday: 00:00:00～23:59:59) Transaction Fee Order */
        final List<DailyTransactionFeeBo> latestTransactionFeeList =
                transactionFeeService.statisticsDailyBill(account, latestDailyInitBo);
        // MutableTriple Left:transactionFee, Middle:taxFee, Right:otherFee;
        final MutableTriple<BigDecimal, BigDecimal, BigDecimal> latestFeeAmount =
                this.analyzeInstantSettlementFees(latestTransactionFeeList);

        /* Step3: Statistic Daily(yesterday: 00:00:00～23:59:59) TransactionBiz Order */
        final List<DailyTransactionBizBo> latestBizOrderList =
                transactionBizService.statisticsDailyBill(account, latestDailyInitBo);

        final BigDecimal latestAccountStatementBizAmount =
                accountStatementBizService.statisticsDailyBill(account, latestDailyInitBo);

        final Map<BusinessTypeEnum, DailyTransactionBizBo> bizMap = latestBizOrderList
                .stream().collect(Collectors.toMap(DailyTransactionBizBo::getBusinessType,
                        Function.identity()));

        final DailyTransactionBizBo topupOrder = bizMap.get(BusinessTypeEnum.TOPUP);
        final DailyTransactionBizBo refundOrder = bizMap.get(BusinessTypeEnum.REFUND);
        final DailyTransactionBizBo exchangeOrder = bizMap.get(BusinessTypeEnum.EXCHANGE);
        final DailyTransactionBizBo transferOutOrder = bizMap.get(BusinessTypeEnum.TRANSFER_OUT);
        final DailyTransactionBizBo adjustmentOrder = bizMap.get(BusinessTypeEnum.ADJUSTMENT);

        transferOutOrder.setTotalAmount(transferOutOrder.getTotalAmount()
                .add(exchangeOrder.getTotalAmount()));
        transferOutOrder.setTotalCount(transferOutOrder.getTotalCount()
                + exchangeOrder.getTotalCount());
        transferOutOrder.setTotalFee(transferOutOrder.getTotalFee()
                .add(exchangeOrder.getTotalFee()));
        transferOutOrder.setTotalTax(transferOutOrder.getTotalTax()
                .add(exchangeOrder.getTotalTax()));

        /* Step:4 Calculate daily total task occurred amount */
        final BigDecimal latestDailyTransactionOccurredAmount = BigDecimal.ZERO
                /* normal transactions */
                .add(latestTransactionMoney.getTotalSettlementAmount())
                .add(latestTransactionMoney.getTotalAdditionalCharge())
                .add(latestFeeAmount.getLeft())
                .add(latestFeeAmount.getMiddle())
                .add(latestFeeAmount.getRight());

        /* Step:5 Calculate daily total biz occurred amount */
        final BigDecimal latestDailyTransactionBizOccurredAmount = BigDecimal.ZERO
                /* topup transactions */
                .add(topupOrder.getTotalAmount())
                .add(topupOrder.getTotalFee())
                .add(topupOrder.getTotalTax())

                /* transfer-out transactions */
                .add(transferOutOrder.getTotalAmount())

                /* refund transactions */
                .add(refundOrder.getTotalAmount())
                .add(refundOrder.getTotalFee())
                .add(refundOrder.getTotalTax())

                /* adjustment transactions */
                .add(adjustmentOrder.getTotalAmount());

        // Sum latest daily total occurredAmount
        final BigDecimal latestDailyTotalOccurredAmount = latestDailyTransactionOccurredAmount
                .add(latestDailyTransactionBizOccurredAmount);

        // Step:6 Sum total transaction count
        final Long latestDailyOccurredCount = latestTransactionMoney.getTotalCount()
                + topupOrder.getTotalCount()
                + transferOutOrder.getTotalCount()
                + refundOrder.getTotalCount()
                + adjustmentOrder.getTotalCount();

        // Step:7 Sum yesterday's total trading volume
        final BigDecimal latestDailyTransactionVolume =
                latestTransactionMoney.getTotalSettlementAmount()
                        .add(latestFeeAmount.getLeft())
                        .add(latestFeeAmount.getMiddle())
                        .add(latestFeeAmount.getRight());

        /* Step8: Statistic daily extractable totalAmount */
        final DailyExtractableAmountInfo dailyExtractableAmountInfo =
                transactionMoneyService.statisticsDailyExtractableAmount(
                        account,
                        latestDailyInitBo,
                        currentDailyInitBo,
                        latestAccountStatementBizAmount,
                        latestDailyTransactionOccurredAmount);

        return DailyBillSummaryBo.builder()
                .account(modelMapper.convertBo(account))
                .dailyInitInfo(latestDailyInitBo)
                .transactionMoney(latestTransactionMoney)
                .transactionFeeList(latestTransactionFeeList)
                .transactionBizMap(bizMap)
                .latestDailyTransactionVolume(latestDailyTransactionVolume)
                .latestDailyOccurredAmount(latestDailyTotalOccurredAmount)
                .latestDailyOccurredCount(latestDailyOccurredCount)
                .dailyExtractableAmountInfo(dailyExtractableAmountInfo)
                .build();
    }

    /**
     * Analyze instant settlement fees, classify by feeType
     *
     * @param feeAmountList
     *
     * @return fees MutableTriple(transactionFee, transactionTax, otherFee)
     */
    private MutableTriple<BigDecimal, BigDecimal, BigDecimal> analyzeInstantSettlementFees(
            final List<DailyTransactionFeeBo> feeAmountList) {

        if (CollectionUtils.isEmpty(feeAmountList)) {
            return MutableTriple.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        // Sum transactionFee
        BigDecimal transactionFee = BigDecimal.ZERO;
        // Sum taxFee
        BigDecimal taxFee = BigDecimal.ZERO;
        // Sum otherFee (Fees other than transactionFee, taxFee) eg. TRANSFER_FEE, REFUND_FEE ...
        BigDecimal otherFee = BigDecimal.ZERO;

        for (final DailyTransactionFeeBo fee : feeAmountList) {
            if (Boolean.TRUE.equals(fee.getInstantFlag())
                    && Objects.nonNull(fee.getTotalAmount())) {

                if (FeeGroupEnum.TRANSACTION_FEE == fee.getFeeGroup()) {
                    transactionFee = transactionFee.add(fee.getTotalAmount());
                } else if (FeeGroupEnum.TAX == fee.getFeeGroup()) {
                    taxFee = taxFee.add(fee.getTotalAmount());
                } else {
                    otherFee = otherFee.add(fee.getTotalAmount());
                }
            }
        }

        return MutableTriple.of(transactionFee, taxFee, otherFee);
    }
}
