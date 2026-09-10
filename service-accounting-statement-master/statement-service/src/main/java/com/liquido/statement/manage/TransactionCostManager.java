package com.liquido.statement.manage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.liquido.base.BaseApis;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.dto.CardCostConfigurationDto;
import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.base.pojo.vo.QueryExtraIncomeConfigurationVo;
import com.liquido.core.common.thread.CommonThreadMdcDecorator;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountBo;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.FxRateInitKey;
import com.liquido.statement.pojo.bo.RecalculateCostBo;
import com.liquido.statement.pojo.bo.RecalculateCostData;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.entity.AccountDailyBill;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.BatchRecalculateCostVo;
import com.liquido.statement.pojo.vo.BatchSyncCostBillVo;
import com.liquido.statement.pojo.vo.SyncTransactionCostVo;
import com.liquido.statement.pojo.vo.SyncTransactionFxRateVo;
import com.liquido.statement.service.AccountDailyBillService;
import com.liquido.statement.service.AccountDailyInitService;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.DailyExchangeRateService;
import com.liquido.statement.service.TransactionCostService;
import com.liquido.statement.service.TransactionMoneyService;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class TransactionCostManager {
    private final BaseService baseService;
    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final TransactionCostService transactionCostService;
    private final AccountDailyBillService accountDailyBillService;
    private final AccountDailyInitService accountDailyInitService;
    private final TransactionMoneyService transactionMoneyService;
    private final DailyExchangeRateService dailyExchangeRateService;
    private final BaseApis.BaseFeign baseFeign;

    @Value("${statement.cost.recalculate.batch-load-size:2000}")
    private int batchLoadSize;
    @Value("${statement.cost.recalculate.batch-process-size:100}")
    private int batchProcessSize;

    public static final ConcurrentHashMap<String, ApmCostConfigurationDto>
            cacheApmConfigMap = new ConcurrentHashMap();

    public static final ConcurrentHashMap<String, CardCostConfigurationDto>
            cacheCardConfigMap = new ConcurrentHashMap();

    public static final ConcurrentHashMap<String, List<ExtraIncomeConfigurationDto>>
            cacheExtraFeeConfigMap = new ConcurrentHashMap<>();

    @Async("reRunTaskExecutor")
    public void syncTransactionCostData(final SyncTransactionCostVo vo) {
        final long begin = System.currentTimeMillis();
        log.info("begin sync cost data ...");

        vo.setBatchSize(Optional.ofNullable(vo.getBatchSize()).orElse(batchLoadSize));
        final List<Long> accountIdList = Lists.newArrayList(vo.getAccountIds());
        Collections.sort(accountIdList);
        for (final Long accountId : accountIdList) {
            LocalDate executeDate = vo.getBeginDate();
            while (executeDate.isBefore(vo.getEndDate().plusDays(1))) {
                final AccountDailyInitBo dailyBill =
                        accountDailyInitService.getDailyBillInitInfo(accountId, executeDate);

                // Both account switching and daily switching require resetting the startId
                vo.setStartId(0L);
                if (Objects.nonNull(dailyBill)) {
                    List<TransactionMoney> orderList;
                    do {
                        final long start = System.currentTimeMillis();
                        log.info("batch sync cost begin....");
                        final long queryBegin = System.currentTimeMillis();
                        // load transaction money data by after transaction money id
                        orderList = transactionMoneyService.queryTransactionMoneyList(
                                vo.getStartId(), accountId,
                                dailyBill.getBillId(), vo.getBatchSize());
                        final long queryEnd = System.currentTimeMillis();
                        log.info("batch query transaction money end accountId={}, size={},"
                                + " ts={}ms", accountId, orderList.size(), queryEnd - queryBegin);

                        if (CollectionUtils.isNotEmpty(orderList)) {
                            vo.setStartId(orderList.stream()
                                    .max(Comparator.comparingLong(TransactionMoney::getId))
                                    .get().getId());

                            final List<CompletableFuture<Boolean>> futureList =
                                    Lists.newArrayList();
                            // Multithreaded batch processing
                            final List<List<TransactionMoney>> subCostList =
                                    Lists.partition(orderList, batchProcessSize);
                            // process sync transaction cost;
                            try {
                                for (final List<TransactionMoney> batchList : subCostList) {
                                    futureList.add(
                                            transactionCostService.processTransactionCostData(
                                                    batchList));
                                }
                            } catch (Exception e) {
                                log.error("process sync cost error:{}", e.getMessage(), e);
                            }

                            /* Wait until they are all done */
                            CompletableFuture.allOf(futureList.toArray(
                                    new CompletableFuture[futureList.size()])).join();
                        }

                        log.info("batch processing cost end accountId={}, size={}, executeDate={},"
                                        + " qs={}ms, ts={}ms",
                                accountId, orderList.size(), executeDate, queryEnd - queryBegin,
                                System.currentTimeMillis() - start);
                    } while (orderList.size() > 0 && orderList.size() >= vo.getBatchSize());
                }

                // process next day
                executeDate = executeDate.plusDays(1);
            }
        }
        log.info("end sync cost data, ts={}ms", System.currentTimeMillis() - begin);
    }

    @Async("reRunTaskExecutor")
    public void batchRecalculateTransactionCost(final BatchRecalculateCostVo vo) {
        final long begin = System.currentTimeMillis();
        log.info("begin batch recalculate cost");

        final List<AccountDailyInitBo> dailyBillList =
                accountDailyInitService.queryDailyBillInitList(vo.getAccountIdList(),
                        vo.getBeginDate(), vo.getEndDate());
        if (CollectionUtils.isEmpty(dailyBillList)) {
            log.info("daily bill is empty skip recalculate cost");
            return;
        }

        final Map<Long, List<AccountDailyInitBo>> accountGroup = dailyBillList.stream()
                .collect(Collectors.groupingBy(AccountDailyInitBo::getAccountId));

        final ThreadPoolTaskExecutor executorPool = new ThreadPoolTaskExecutor();
        executorPool.setCorePoolSize(2);
        executorPool.setMaxPoolSize(4);
        executorPool.setQueueCapacity(400);
        executorPool.setKeepAliveSeconds(60);
        executorPool.setThreadNamePrefix("rerun-cost-pool-");
        executorPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executorPool.setTaskDecorator(new CommonThreadMdcDecorator());
        executorPool.setWaitForTasksToCompleteOnShutdown(true);
        executorPool.initialize();

        for (final Map.Entry<Long, List<AccountDailyInitBo>> entry : accountGroup.entrySet()) {

            final Long accountId = entry.getKey();
            final List<AccountDailyInitBo> billList = entry.getValue();
            billList.sort(Comparator.comparing(AccountDailyInitBo::getBillId));

            final CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                final long start = System.currentTimeMillis();
                for (final AccountDailyInitBo dailyBill : billList) {
                    try {
                        final long dailyStart = System.currentTimeMillis();
                        this.recalculateTransactionCost(RecalculateCostBo.builder()
                                .costId(0L)
                                .accountId(accountId)
                                .billId(dailyBill.getBillId())
                                .transactionDate(dailyBill.getTransactionDate())
                                .bathSize(Optional.ofNullable(vo.getBatchSize())
                                        .orElse(batchLoadSize))
                                .activeVersion(Optional.ofNullable(vo.getActiveVersion())
                                        .orElse(Integer.parseInt(dailyBill.getTransactionDate()
                                                .format(LocalDateUtil.FORMAT_YYYYMM))))
                                .productCodes(Optional.ofNullable(vo.getProductCodes())
                                        .orElse(Collections.emptySet()))
                                .build());

                        log.info("daily recalculate cost end accountId={}, date={}, ts={}ms",
                                accountId, dailyBill.getTransactionDate(),
                                System.currentTimeMillis() - dailyStart);
                    } catch (Exception e) {
                        log.error("recalculate cost fail accountId={}, vo={}", accountId, vo, e);
                    }
                }

                log.info("account recalculate cost end accountId={}, ts={}",
                        accountId, System.currentTimeMillis() - start);

            }, executorPool);

            future.join();
        }

        // clear cache after completed;
        cacheApmConfigMap.clear();
        cacheCardConfigMap.clear();
        cacheExtraFeeConfigMap.clear();
        log.info("end batch recalculate transaction cost, ts={}ms",
                System.currentTimeMillis() - begin);
    }

    private void recalculateTransactionCost(final RecalculateCostBo recalculateCostBo) {

        final ApmCostConfigurationDto apmCostConfig =
                this.getApmCostConfig(recalculateCostBo.getActiveVersion());

        final CardCostConfigurationDto cardCostConfig = this.getCardCostConfig();

        final List<ExtraIncomeConfigurationDto> extraIncomeConfig =
                this.getExtraFeeConfig(recalculateCostBo.getAccountId());

        final long beginTs = System.currentTimeMillis();
        log.info("begin recalculate transaction cost accountId={}, date={}",
                recalculateCostBo.getAccountId(), recalculateCostBo.getTransactionDate());

        int totalCount = 0;
        int batchCount;
        do {
            final long batchTs = System.currentTimeMillis();
            final List<RecalculateCostData> batchCostList
                    = transactionCostService.batchLoadTransactionCost(recalculateCostBo);
            batchCount = CollectionUtils.isEmpty(batchCostList) ? 0 : batchCostList.size();
            log.info("batch loading cost list accountId={}, date={}, count={}, ts={}ms",
                    recalculateCostBo.getAccountId(), recalculateCostBo.getTransactionDate(),
                    batchCount, System.currentTimeMillis() - batchTs);

            if (batchCount > 0) {
                final List<CompletableFuture<Boolean>> futureList = Lists.newArrayList();
                totalCount += batchCount;
                // reset maxOrderId
                recalculateCostBo.setCostId(batchCostList.stream()
                        .max(Comparator.comparingLong(RecalculateCostData::getId)).get().getId());

                // Multithreaded batch processing
                final List<List<RecalculateCostData>> subCostList =
                        Lists.partition(batchCostList, batchProcessSize);
                for (final List<RecalculateCostData> costList : subCostList) {
                    futureList.add(transactionCostService.doRecalculateCost(
                            costList,
                            recalculateCostBo,
                            apmCostConfig,
                            cardCostConfig,
                            extraIncomeConfig));
                }

                /* Wait until they are all done */
                CompletableFuture.allOf(
                        futureList.toArray(new CompletableFuture[futureList.size()])).join();

                log.info("process recalculate transaction cost end accountId={}, date={}, "
                                + "batchCount={}, ts={}ms", recalculateCostBo.getAccountId(),
                        recalculateCostBo.getTransactionDate(), batchCount,
                        System.currentTimeMillis() - batchTs);
            }
        } while (batchCount >= recalculateCostBo.getBathSize());
        log.info("end recalculate transaction cost accountId={}, date={}, "
                        + "totalCount={}, ts={}ms", recalculateCostBo.getAccountId(),
                recalculateCostBo.getTransactionDate(), totalCount,
                System.currentTimeMillis() - beginTs);
    }

    @Async("reRunTaskExecutor")
    public void syncTransactionFxRateData(final SyncTransactionFxRateVo vo) {
        final long begin = System.currentTimeMillis();
        log.info("begin sync fx data ...");

        // load transaction money data by after transaction money id
        final List<AccountDto> accountList =
                modelMapper.convert(accountService.findByIds(vo.getAccountIds()));
        for (final AccountDto account : accountList) {

            final long accBegin = System.currentTimeMillis();
            final Long accountId = account.getId();
            LocalDate executeDate = vo.getBeginDate();
            while (executeDate.isBefore(vo.getEndDate().plusDays(1))) {
                final long tsBegin = System.currentTimeMillis();
                final AccountDailyBill dailyBill =
                        accountDailyBillService.findAccountBillByBillDay(accountId, executeDate);
                if (Objects.nonNull(dailyBill)) {
                    final LocalDateTime exchangeTime = LocalDateTimeUtil.localToUtc(
                            dailyBill.getBillDate().atTime(0, 0, 0), account.getTimezone());

                    final DailyExchangeRateDto fxConfig =
                            Optional.ofNullable(dailyExchangeRateService.loadDailyExchangeRate(
                                    FxRateInitKey.builder()
                                            .merchantId(account.getMerchantId())
                                            .accountId(account.getId())
                                            .exchangeTime(exchangeTime)
                                            .build())
                            ).orElse(DailyExchangeRateDto.builder()
                                    .id(0L)
                                    .accountId(accountId)
                                    .merchantId(account.getMerchantId())
                                    .sourceCurrency(CurrencyEnum.USD)
                                    .targetCurrency(CurrencyEnum.USD)
                                    .exchangeTime(exchangeTime)
                                    .exchangeRate(BigDecimal.ZERO)
                                    .merchantRate(BigDecimal.ONE)
                                    .ratioLose(BigDecimal.ZERO)
                                    .build());

                    transactionMoneyService.updateTransactionMoneyFxUsdRate(
                            account, dailyBill.getId(), fxConfig);
                }

                log.info("daily sync fx data end, accountId ={}, date={}, ts={}ms",
                        account.getId(), executeDate, System.currentTimeMillis() - tsBegin);

                // process next day
                executeDate = executeDate.plusDays(1);
            }

            log.info("account sync fx data end, accountId ={} ts={}ms",
                    account.getId(), System.currentTimeMillis() - accBegin);
        }
        log.info("process sync fx data end, ts={}ms", System.currentTimeMillis() - begin);
    }

    public void syncCostBillId(final BatchSyncCostBillVo vo) {
        final List<AccountDailyInitBo> billList = accountDailyInitService.queryDailyBillInitList(
                vo.getAccountIdList(), vo.getBeginDate(), vo.getEndDate());

        for (final AccountDailyInitBo bo : billList) {
            try {
                long begin = System.currentTimeMillis();
                log.info("sync cost bill id begin dailyBill={}", bo);
                transactionCostService.syncCostBillId(bo.getAccountId(), bo.getBillId());
                log.info("sync cost bill id end ms={}, dailyBill={}",
                        System.currentTimeMillis() - begin, bo);
            } catch (Exception e) {
                log.error("sync billId from transaction.money");
            }
        }
    }

    private ApmCostConfigurationDto getApmCostConfig(final Integer activeVersion) {

        final String cacheKey = "ALL";
        ApmCostConfigurationDto costConfig = cacheApmConfigMap.get(cacheKey);
        if (Objects.nonNull(costConfig)
                && Objects.nonNull(costConfig.getCostConfigList())) {
            return costConfig;
        }

        costConfig = baseService.loadApmCostConfig(activeVersion);

        cacheApmConfigMap.put(cacheKey, costConfig);
        return costConfig;
    }

    private CardCostConfigurationDto getCardCostConfig() {
        final String cacheKey = "ALL";
        CardCostConfigurationDto costConfig = cacheCardConfigMap.get(cacheKey);
        if (Objects.nonNull(costConfig)
                && Objects.nonNull(costConfig.getCardCostConfigList())) {
            return costConfig;
        }

        costConfig = baseService.getCardCostConfig();
        cacheCardConfigMap.put(cacheKey, costConfig);
        return costConfig;
    }

    private List<ExtraIncomeConfigurationDto> getExtraFeeConfig(
            final Long accountId) {

        final String cacheKey = accountId.toString();
        final List<ExtraIncomeConfigurationDto> cacheList = cacheExtraFeeConfigMap.get(cacheKey);
        if (Objects.nonNull(cacheList)) {
            return cacheList;
        }

        final AccountBo accountBo = accountService.findAccountInfoById(accountId);
        if (Objects.isNull(accountBo)) {
            return Collections.emptyList();
        }

        final ResponseDto<List<ExtraIncomeConfigurationDto>> resp =
                baseFeign.queryExtraIncomeConfiguration(QueryExtraIncomeConfigurationVo.builder()
                        .accountId(accountId)
                        .countryCode(accountBo.getCountryCode())
                        .transactionTypeCode(accountBo.getTransactionTypeCode())
                        .build());

        CheckResponseUtil.checkResponse(resp);
        cacheExtraFeeConfigMap.put(cacheKey, resp.getData());
        return resp.getData();
    }
}
