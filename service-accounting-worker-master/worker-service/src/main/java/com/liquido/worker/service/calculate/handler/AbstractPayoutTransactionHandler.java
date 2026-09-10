package com.liquido.worker.service.calculate.handler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.AccountProductDto;
import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;
import com.liquido.base.pojo.dto.WorkingDayDto;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.CalculateConfigBo;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.enums.CalculationTaskStateEnum;
import com.liquido.worker.enums.TaskLogResultEnum;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.feign.BaseService;
import com.liquido.worker.feign.StatementService;
import com.liquido.worker.pojo.bo.MonthlyFeeConfigurationBo;
import com.liquido.worker.pojo.bo.PreCalculateConfigBo;
import com.liquido.worker.pojo.bo.PreCalculateFeeBo;
import com.liquido.worker.pojo.bo.TransactionFeeBo;
import com.liquido.worker.pojo.bo.TransactionMoneyBo;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.entity.TaskLogFeeCalculation;
import com.liquido.worker.pojo.mapper.ModelMapper;
import com.liquido.worker.pojo.vo.ApmCostConfigVo;
import com.liquido.worker.service.TaskFeeCalculationService;
import com.liquido.worker.service.TaskLogFeeCalculationService;
import com.liquido.worker.service.calculate.ExchangeRateManager;
import com.liquido.worker.service.calculate.TransactionCostService;
import com.liquido.worker.service.calculate.TransactionExtraIncomeService;
import com.liquido.worker.service.calculate.TransactionStrategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

/**
 * Abstract Payout TransactionHandler
 */
@Slf4j
@Service
@RequiredArgsConstructor
public abstract class AbstractPayoutTransactionHandler implements TransactionStrategy {
    @Resource
    private BaseService baseService;
    @Resource
    private StatementService statementService;
    @Resource
    private ExchangeRateManager exchangeRateManager;
    @Resource
    private TransactionCostService transactionCostService;
    @Resource
    private TaskFeeCalculationService taskFeeCalculationService;
    @Resource
    private TaskLogFeeCalculationService taskLogFeeCalculationService;
    @Resource
    private TransactionExtraIncomeService transactionExtraIncomeService;
    @Resource
    private WorkerProperties.CoRejectedOrderProperties coRejectedOrderProperties;
    @Resource
    private ModelMapper modelMapper;

    /**
     * batch task fee trial calculate
     */
    public List<PreCalculateFeeBo> batchFeeTrialCalculate(
            final List<TaskFeeCalculation> taskOrders) {

        /* STEP1: get merchant-info from base-service; */
        final MerchantDto merchantDto =
                baseService.getMerchantByCode(taskOrders.get(0).getMerchantCode());

        final List<PreCalculateFeeBo> list = new ArrayList<>();

        /* STEP2: get merchant-account from statement-service; */
        final AccountDto accountInfo = statementService.queryMerchantAccount(merchantDto.getId(),
                taskOrders.get(0).getCountryCode(),
                taskOrders.get(0).getTransactionTypeCode());

        for (final TaskFeeCalculation taskOrder : taskOrders) {

            final LocalDate transactionDate = LocalDateTimeUtil.utcToLocal(
                    taskOrder.getTransactionTime(),
                    ZoneId.of(accountInfo.getTimezone())).toLocalDate();

            /* STEP1: get fee config from base-service; */
            final MonthlyFeeConfigurationBo feeConfigBo =
                    baseService.getMonthlyProductFeeConfig(accountInfo, transactionDate,
                            taskOrder);

            /* STEP2: currency conversion if required; */
            final Pair<DailyExchangeRateDto, DailyExchangeRateDto> exchangeRatePair =
                    exchangeRateManager.getExchangeRate(merchantDto, accountInfo,
                            getTimeToGetExchangeRate(accountInfo, taskOrder),
                            taskOrder.getCurrency());

            /* STEP3: Deal currency conversion; Formula: tradeAmount / merchantRate */
            final BigDecimal settlementAmount = AmountUtil.division(taskOrder.getAmount(),
                    exchangeRatePair.getLeft().getMerchantRate(), 0);

            final BusinessStrategyEnum businessStrategy = BusinessStrategyEnum.parse(
                    taskOrder.getTransactionTypeCode(), taskOrder.getDirectionType());

            /* STEP4: calculate fee and tax  */
            final List<TransactionFeeBo> transactionFeeBos =
                    this.calculateTransactionFee(accountInfo, taskOrder,
                            settlementAmount, businessStrategy, exchangeRatePair.getLeft(),
                            feeConfigBo.getFeeConfigList());

            final PreCalculateFeeBo preCalculateFeeBo =
                    modelMapper.convertPreCalculateFeeBo(taskOrder, settlementAmount,
                            exchangeRatePair.getRight().getMerchantRate(), transactionFeeBos);

            list.add(preCalculateFeeBo);
        }
        return list;
    }

    /**
     * batch task operation
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchProcessCalculate(final String requestId,
                                      final List<TaskFeeCalculation> taskOrderList) {
        log.info("Batch Process Calculate begin, requestId={}, count={}", requestId,
                taskOrderList.size());

        if (CollectionUtils.isEmpty(taskOrderList)) {
            return;
        }

        // Use the same exchange rate configuration for data in the same hour
        final Map<String, List<TaskFeeCalculation>> groupTask =
                taskOrderList.stream().collect(Collectors.groupingBy(
                        item -> item.getTransactionTime().format(Constant.GROUP_BY_HOUR)));

        final LocalDateTime executeStartTime = LocalDateTimeUtil.nowUtc();
        final List<TransactionMoneyBo> orderList = Lists.newArrayList();

        final StopWatch watch = new StopWatch();
        watch.start("payout batch calculation fee");
        for (final Map.Entry<String, List<TaskFeeCalculation>> entryMap : groupTask.entrySet()) {
            for (final TaskFeeCalculation taskOrder : entryMap.getValue()) {
                final PreCalculateConfigBo configBo = this.preProcessCalculate(taskOrder);
                final TransactionMoneyBo resultBo = this.processingCalculate(taskOrder, configBo);
                if (DirectionTypeEnum.REJECTED_DEBIT.equals(taskOrder.getDirectionType())
                        && resultBo.getIsRejectedDebit()
                        && CollectionUtils.isEmpty(resultBo.getTransactionFeeList())) {
                    continue;
                }
                orderList.add(resultBo);
            }
        }

        watch.stop();
        final long calculationConsuming = watch.getLastTaskTimeMillis();
        log.info("Batch Process calculation fee ts={}ms", calculationConsuming);

        watch.start("payout batch process settlement");
        final ResponseDto<Void> settleResult = this.batchProcessStatement(requestId, orderList);
        watch.stop();
        final long settlementConsuming = watch.getLastTaskTimeMillis();
        log.info("Batch Process settlement ts={}ms", settlementConsuming);

        watch.start("marketPlace post batch process calculate");
        this.postBatchProcessCalculate(requestId, orderList, settleResult,
                executeStartTime, calculationConsuming, settlementConsuming);
        watch.stop();

        log.info("Batch Process Calculate end, total-ts={}ms, size={}, traceWatch={}",
                watch.getTotalTimeMillis(), taskOrderList.size(), watch.prettyPrint());
    }

    /**
     * load fee config, merchant info, account info ... before calculate
     *
     * @param taskOrder
     *
     * @return
     */
    protected PreCalculateConfigBo preProcessCalculate(final TaskFeeCalculation taskOrder) {
        log.info("Payout task preCalculate begin. taskOrder={}", taskOrder);

        final long begin = System.currentTimeMillis();
        /* STEP1: get merchant-info from base-service; */
        final MerchantDto merchantDto = baseService.getMerchantByCode(taskOrder.getMerchantCode());

        /* STEP2: get merchant-account from statement-service; */
        final AccountDto accountInfo = statementService.queryMerchantAccount(merchantDto.getId(),
                taskOrder.getCountryCode(),
                taskOrder.getTransactionTypeCode());

        /* STEP3: get account-product config from base-service; */
        final AccountProductDto accountProduct = baseService.loadAccountProductConfig(
                accountInfo.getId(), taskOrder.getProductCode());

        /* STEP4: transactionDate is UTC+0 time, convert to merchant-account timezone;*/
        final LocalDate transactionDate = LocalDateTimeUtil.utcToLocal(
                taskOrder.getTransactionTime(), ZoneId.of(accountInfo.getTimezone())).toLocalDate();

        /* STEP5: get fee config from base-service; */
        final MonthlyFeeConfigurationBo feeConfigBo =
                baseService.getMonthlyProductFeeConfig(accountInfo, transactionDate, taskOrder);

        /* STEP6: currency conversion if required; */
        final Pair<DailyExchangeRateDto, DailyExchangeRateDto> exchangeRatePair =
                exchangeRateManager.getExchangeRate(merchantDto, accountInfo,
                        getTimeToGetExchangeRate(accountInfo, taskOrder), taskOrder.getCurrency());

        /* STEP7: get apm transaction cost config */
        final ApmCostConfigurationDto apmCostConfig =
                this.getApmCostConfig(taskOrder, accountInfo);

        /* STEP8: get extra income config from base-service; */
        final List<ExtraIncomeConfigurationDto> extraIncomeConfig =
                transactionExtraIncomeService.queryExtraIncomeConfig(accountInfo, taskOrder);

        /* STEP9: get workday config from base-service; */
        final List<WorkingDayDto> workdayList = Lists.newArrayList();

        log.info("Payout task preCalculate end. ts={}ms, taskOrderId={}",
                (System.currentTimeMillis() - begin), taskOrder.getId());

        /* STEP10: encapsulated preCalculateConfig and return; */
        return PreCalculateConfigBo.builder()
                .transactionDate(transactionDate)
                .merchantInfo(merchantDto)
                .accountInfo(accountInfo)
                .accountProduct(accountProduct)
                .exchangeRate(exchangeRatePair)
                .feeConfigList(feeConfigBo.getFeeConfigList())
                .workdayList(workdayList)
                .calculationRule(feeConfigBo.getCalculationRuleMap())
                .apmCostConfig(apmCostConfig)
                .extraIncomeConfig(extraIncomeConfig)
                .build();
    }

    private LocalDateTime getTimeToGetExchangeRate(final AccountDto account,
                                                   final TaskFeeCalculation taskOrder) {
        final boolean getRateByCreateTime =
                Optional.ofNullable(account.getAccountConfig().getConfigData())
                        .map(AccountConfigData::getCalculateConfig)
                        .map(CalculateConfigBo::getGetRateByCreateTime)
                        .orElse(Boolean.FALSE);
        if (getRateByCreateTime) {
            return taskOrder.getCreatedTime();
        }
        return taskOrder.getTransactionTime();
    }

    private ApmCostConfigurationDto getApmCostConfig(
            final TaskFeeCalculation taskOrder,
            final AccountDto accountInfo) {

        // if DirectionType is REJECTED No need to calculate fee;
        if (DirectionTypeEnum.REJECTED == taskOrder.getDirectionType()) {
            return ApmCostConfigurationDto.builder()
                    .costConfigList(Collections.emptyList())
                    .build();
        }

        return baseService.getApmCostConfig(ApmCostConfigVo.builder()
                .accountId(accountInfo.getId())
                .country(accountInfo.getCountryCode())
                .merchantCode(taskOrder.getMerchantCode())
                .transactionType(accountInfo.getTransactionTypeCode())
                .directionType(taskOrder.getDirectionType())
                .vendor(taskOrder.getVendor())
                .productCode(taskOrder.getProductCode())
                .build());
    }

    /**
     * process fee calculate
     *
     * @param taskOrder taskOrder
     * @param preConfig preConfig
     *
     * @return TransactionMoneyBo
     */
    protected TransactionMoneyBo processingCalculate(final TaskFeeCalculation taskOrder,
                                                     final PreCalculateConfigBo preConfig) {
        log.info("Task calculate begin. taskOrder={}, preConfig={}", taskOrder, preConfig);

        final long begin = System.currentTimeMillis();
        final AccountDto accountInfo = preConfig.getAccountInfo();
        final List<MonthlyFeeConfigurationDto> monthlyFeeConfigList = preConfig.getFeeConfigList();
        final DailyExchangeRateDto merchantRateInfo = preConfig.getExchangeRate().getLeft();
        final DailyExchangeRateDto usdDailyRateInfo = preConfig.getExchangeRate().getRight();
        final BusinessStrategyEnum businessStrategy = BusinessStrategyEnum.parse(
                taskOrder.getTransactionTypeCode(), taskOrder.getDirectionType());

        /* STEP1: Deal currency conversion; Formula: tradeAmount / merchantRate */
        final BigDecimal settlementAmount = AmountUtil.division(taskOrder.getAmount(),
                merchantRateInfo.getMerchantRate(), 0);

        // Exchange of non US dollar order amount(tips: use for daily report)
        final BigDecimal settlementAmountUsd = CurrencyEnum.USD == taskOrder.getCurrency()
                ? taskOrder.getAmount() : AmountUtil.division(taskOrder.getAmount(),
                usdDailyRateInfo.getMerchantRate(), 0);

        /* STEP2.0: Deal calculate transaction-money. amount-unit: cent */
        final TransactionMoneyBo transactionMoney = this.buildTransactionMoney(taskOrder,
                accountInfo, settlementAmount, settlementAmountUsd, preConfig.getExchangeRate());

        transactionMoney.setBusinessStrategy(businessStrategy);
        transactionMoney.setAmountPon(businessStrategy.getAmountPon());
        transactionMoney.setTransactionDate(preConfig.getTransactionDate());
        transactionMoney.setTransactionFeeList(Collections.emptyList());
        transactionMoney.setTradingModel(preConfig.getAccountProduct().getTradingModel());
        transactionMoney.setBeCreditedDate(preConfig.getTransactionDate());
        transactionMoney.setCalculationRule(preConfig.getCalculationRule());
        transactionMoney.setBeCreditedAmount(BigDecimal.ZERO);

        // mark whether this order is REJECTED_DEBIT
        transactionMoney.setIsRejectedDebit(false);
        if (CountryCodeEnum.CO.equals(taskOrder.getCountryCode())
                && TransactionTypeCodeEnum.PAY_OUT.equals(taskOrder.getTransactionTypeCode())
                && List.of(DirectionTypeEnum.REJECTED, DirectionTypeEnum.REJECTED_DEBIT)
                .contains(taskOrder.getDirectionType())) {

            final boolean timeLimitCondition =
                    LocalDateTimeUtil.utcToInstant(LocalDateTimeUtil.nowUtc())
                            >= coRejectedOrderProperties.getEnableTimestamp();
            final boolean specifiedMerchantCondition =
                    coRejectedOrderProperties.getSpecifiedMerchants()
                            .contains(taskOrder.getMerchantCode());

            final String errorCode =
                    Optional.ofNullable(taskOrder.getOthers()).map(v -> v.get("transferStatusCode"))
                            .map(JsonNode::asText).orElse(null);
            if ((!coRejectedOrderProperties.getDebitExcludeErrorCode().contains(errorCode))
                    && (timeLimitCondition || specifiedMerchantCondition)) {
                transactionMoney.setIsRejectedDebit(true);
            }
        }

        /* STEP2.1: Get account product fee config rule,
         * if fee config is empty return transactionMoney
         */
        if (CollectionUtils.isEmpty(monthlyFeeConfigList)) {
            return transactionMoney;
        }

        /* STEP3: Deal calculate transaction fee*/
        transactionMoney.setTransactionFeeList(this.calculateTransactionFee(accountInfo, taskOrder,
                settlementAmount, businessStrategy, usdDailyRateInfo, monthlyFeeConfigList));

        /* STEP4: Deal calculate transaction cost */
        transactionMoney.setTransactionCostList(transactionCostService.calculateTransactionCost(
                transactionMoney, preConfig, usdDailyRateInfo));

        /* STEP5: Deal calculate transaction extra income */
        transactionMoney.setTransactionExtraFeeList(transactionExtraIncomeService
                .calculateExtraIncome(transactionMoney, usdDailyRateInfo,
                        preConfig.getExtraIncomeConfig()));

        log.info("Task calculated end. ts={}ms, taskOrderId={}, transactionMoney={}",
                (System.currentTimeMillis() - begin), taskOrder.getId(), transactionMoney);

        return transactionMoney;
    }

    private List<TransactionFeeBo> calculateTransactionFee(
            final AccountDto accountInfo,
            final TaskFeeCalculation taskOrder,
            final BigDecimal settlementAmount,
            final BusinessStrategyEnum businessStrategy,
            final DailyExchangeRateDto usdDailyRateInfo,
            final List<MonthlyFeeConfigurationDto> feeConfigList) {

        /* STEP1: Deal calculate transaction-fee. amount-unit: cent */
        final List<TransactionFeeBo> transactionFeeList = this.buildTransactionFee(
                taskOrder, accountInfo, settlementAmount, feeConfigList, usdDailyRateInfo);

        /* STEP2: Deal calculate tax-fees. E.g:(Tax-fee ......)*/
        final List<TransactionFeeBo> otherFees = this.buildTransactionTaxFee(taskOrder,
                accountInfo, settlementAmount, transactionFeeList, feeConfigList, usdDailyRateInfo);
        transactionFeeList.addAll(otherFees);

        /* STEP3: Set fees pon(positive or negative)*/
        for (final TransactionFeeBo feeBo : transactionFeeList) {
            feeBo.setAmountPon(businessStrategy.getFeePon());
        }

        if (feeConfigList.size() != transactionFeeList.size()) {
            log.error("The number of settlement fee records does not match the configured nums, "
                            + "feeConfigSize={}, reeRecordSize={}, accountId={}, uniqueId={}",
                    feeConfigList.size(), transactionFeeList.size(), accountInfo.getId(),
                    taskOrder.getUniqueId());
            throw WorkerExceptionCode.FEE_CALCULATION_RECORDS_NOT_MATCH.exception();
        }

        return transactionFeeList;
    }

    /**
     * batch send fee data to statement-service
     *
     * @param requestId
     * @param orderList
     */
    protected ResponseDto<Void> batchProcessStatement(
            final String requestId,
            final List<TransactionMoneyBo> orderList) {

        return statementService.batchSettlementTradeOrder(requestId, orderList);
    }

    /**
     * post batch process calculate
     *
     * @param requestId
     * @param orderList
     * @param settlementResult
     * @param executeStartTime
     * @param calculationConsuming
     * @param settlementConsuming
     */
    protected void postBatchProcessCalculate(final String requestId,
                                             final List<TransactionMoneyBo> orderList,
                                             final ResponseDto<Void> settlementResult,
                                             final LocalDateTime executeStartTime,
                                             final long calculationConsuming,
                                             final long settlementConsuming) {

        TaskLogResultEnum taskState = TaskLogResultEnum.FAILED;
        if (Objects.nonNull(settlementResult) && settlementResult.isSuccess()) {
            taskState = TaskLogResultEnum.SUCCESS;

            taskFeeCalculationService.batchUnLockTask(orderList.stream()
                            .map(TransactionMoneyBo::getTransactionId)
                            .collect(Collectors.toList()),
                    CalculationTaskStateEnum.SUCCESS);
        }

        final TaskLogFeeCalculation logTask = new TaskLogFeeCalculation();
        logTask.setRequestId(requestId);
        logTask.setTaskId(0L);
        logTask.setStartTime(executeStartTime);
        logTask.setEndTime(LocalDateTimeUtil.nowUtc());

        logTask.setBatchCount(orderList.size());
        logTask.setCalculateConsuming(calculationConsuming);
        logTask.setSettlementConsuming(settlementConsuming);
        logTask.setTotalConsuming(
                Duration.between(logTask.getStartTime(), logTask.getEndTime()).toMillis());

        logTask.setTaskResult(taskState);
        logTask.setRequestData(JsonUtil.toJson(orderList));
        logTask.setResponseData(JsonUtil.toJson(settlementResult));
        logTask.setCreatedTime(LocalDateTimeUtil.nowUtc());
        taskLogFeeCalculationService.saveLog(logTask);

        log.info("post batch process calculate end: requestId={}", requestId);
    }

    private TransactionMoneyBo buildTransactionMoney(
            final TaskFeeCalculation taskOrder,
            final AccountDto accountInfo,
            final BigDecimal settlementAmount,
            final BigDecimal settlementAmountUsd,
            final Pair<DailyExchangeRateDto, DailyExchangeRateDto> exchangeRate) {

        final DailyExchangeRateDto merchantRateInfo = exchangeRate.getLeft();
        final DailyExchangeRateDto usdDailyRateInfo = exchangeRate.getRight();

        return TransactionMoneyBo.builder()
                .transactionId(taskOrder.getId())
                .uniqueId(taskOrder.getUniqueId())
                .referenceId(StringUtils.defaultIfBlank(taskOrder.getMerchantReference(), ""))
                .merchantId(accountInfo.getMerchantId())
                .subMerchantId(taskOrder.getSubMerchantId())
                .accountId(accountInfo.getId())
                .documentId(StringUtils.defaultIfBlank(taskOrder.getDocumentId(), ""))
                .fxRateId(Optional.ofNullable(usdDailyRateInfo.getId()).orElse(0L))
                .accountTimeZone(accountInfo.getTimezone())
                .countryCode(taskOrder.getCountryCode())
                .merchantCode(taskOrder.getMerchantCode())
                .transactionTypeCode(taskOrder.getTransactionTypeCode())
                .productCode(taskOrder.getProductCode())
                .cardGroup(CreditCardGroupCodeEnum.DEFAULT)
                .vendor(taskOrder.getVendor())
                .amount(taskOrder.getAmount())
                .currency(taskOrder.getCurrency())
                .fxRate(merchantRateInfo.getMerchantRate())
                .settlementAmount(settlementAmount)
                .settlementAmountUsd(settlementAmountUsd)
                .fxUsdRate(Optional.ofNullable(usdDailyRateInfo.getMerchantRate())
                        .orElse(BigDecimal.ONE))
                .fxUsdLose(Optional.ofNullable(usdDailyRateInfo.getRatioLose())
                        .orElse(BigDecimal.ZERO))
                .settlementCurrency(accountInfo.getCurrency())
                .holdStatus(HoldStatusEnum.NORMAL)
                .directionType(taskOrder.getDirectionType())
                .transactionTime(taskOrder.getTransactionTime())
                .submitTime(LocalDateTimeUtil.instantToUtc(taskOrder.getSubmitTimestamp()))
                .calculateTime(LocalDateTimeUtil.nowUtc())
                .extendData(taskOrder.getOthers())
                .build();
    }

    /**
     * Deal calculate transaction-fee. amount-unit: cent
     */
    private List<TransactionFeeBo> buildTransactionFee(
            final TaskFeeCalculation taskOrder,
            final AccountDto accountInfo,
            final BigDecimal settlementAmount,
            final List<MonthlyFeeConfigurationDto> feeConfigurationList,
            final DailyExchangeRateDto usdDailyRateInfo) {

        final List<TransactionFeeBo> transactionFeeList = Lists.newArrayList();
        final List<MonthlyFeeConfigurationDto> feeConfigList = feeConfigurationList.stream()
                .filter(item -> FeeTypeCodeEnum.TRANSACTION_FEE == item.getFeeTypeCode())
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(feeConfigList)) {
            log.info("transaction-fee not configured, taskId={}", taskOrder.getId());
            return transactionFeeList;
        }

        for (final MonthlyFeeConfigurationDto feeConfig : feeConfigList) {
            if (feeConfig.getInstantFlag()
                    && feeConfig.getAccountCurrency() != accountInfo.getCurrency()) {
                throw WorkerExceptionCode.FEE_CONFIG_INVALID.exception(taskOrder.getId());
            }

            final TransactionFeeBo transactionFee = new TransactionFeeBo();
            transactionFee.setFeeConfigurationId(feeConfig.getId());
            transactionFee.setFeeName(feeConfig.getFeeName());
            transactionFee.setFeeTypeCode(feeConfig.getFeeTypeCode());
            transactionFee.setFeeGroup(feeConfig.getFeeGroup());
            transactionFee.setFeeOn(feeConfig.getFeeOn());
            transactionFee.setFeeValueModel(feeConfig.getFeeValueModel());
            transactionFee.setSettlementCurrency(feeConfig.getAccountCurrency());

            /* Use fixed amount fee rule(FeeValueModelEnum.FIXED)*/
            BigDecimal feeAmount = feeConfig.getFeeValue();
            /* Use fixed amount fee rule(FeeValueModelEnum.PERCENTAGE)*/
            if (FeeValueModelEnum.PERCENTAGE == feeConfig.getFeeValueModel()) {

                feeAmount = taskOrder.getAmount().multiply(feeConfig.getFeeValue());
                if (feeConfig.getAccountCurrency() == accountInfo.getCurrency()) {
                    feeAmount = settlementAmount.multiply(feeConfig.getFeeValue());
                }

                /* When feeAmount <= minFeeAmount */
                if (feeAmount.compareTo(feeConfig.getMinFeeAmount()) <= 0) {
                    feeAmount = feeConfig.getMinFeeAmount();
                }
                /* When feeAmount >= maxFeeAmount */
                if (feeAmount.compareTo(feeConfig.getMaxFeeAmount()) >= 0) {
                    feeAmount = feeConfig.getMaxFeeAmount();
                }
            }

            final BigDecimal settlementFeeAmount = feeAmount.setScale(0, RoundingMode.HALF_UP);
            final BigDecimal feeAmountUsd =
                    CurrencyEnum.USD == transactionFee.getSettlementCurrency()
                            ? settlementFeeAmount : AmountUtil
                            .division(settlementFeeAmount, usdDailyRateInfo.getMerchantRate(), 6);

            transactionFee.setCalculateAmount(feeAmount.setScale(6, RoundingMode.HALF_UP));
            transactionFee.setSettlementAmount(settlementFeeAmount);
            transactionFee.setSettlementAmountUsd(feeAmountUsd);
            transactionFee.setInstantFlag(feeConfig.getInstantFlag());
            transactionFeeList.add(transactionFee);
        }

        return transactionFeeList;
    }

    /**
     * Deal calculate tax-fee. E.g:(Tax-fee ......)
     */
    private List<TransactionFeeBo> buildTransactionTaxFee(
            final TaskFeeCalculation taskOrder,
            final AccountDto accountInfo,
            final BigDecimal settlementAmount,
            final List<TransactionFeeBo> transactionFees,
            final List<MonthlyFeeConfigurationDto> feeConfigurationList,
            final DailyExchangeRateDto usdDailyRateInfo) {

        final List<TransactionFeeBo> transactionFeeList = Lists.newArrayList();

        // Other fee has excluded the TRANSACTION_FEE and ANTICIPATION_FEE
        final List<MonthlyFeeConfigurationDto> feeConfigList = feeConfigurationList.stream()
                .filter(item -> FeeTypeCodeEnum.TAX == item.getFeeTypeCode())
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(feeConfigList)) {
            return Collections.emptyList();
        }

        for (final MonthlyFeeConfigurationDto feeConfig : feeConfigList) {
            if (feeConfig.getInstantFlag()
                    && feeConfig.getAccountCurrency() != accountInfo.getCurrency()) {
                throw WorkerExceptionCode.FEE_CONFIG_INVALID.exception(taskOrder.getId());
            }

            final TransactionFeeBo transactionFee = new TransactionFeeBo();
            transactionFee.setFeeConfigurationId(feeConfig.getId());
            transactionFee.setFeeName(feeConfig.getFeeName());
            transactionFee.setFeeTypeCode(feeConfig.getFeeTypeCode());
            transactionFee.setFeeGroup(feeConfig.getFeeGroup());
            transactionFee.setInstantFlag(feeConfig.getInstantFlag());
            transactionFee.setFeeOn(feeConfig.getFeeOn());
            transactionFee.setFeeValueModel(feeConfig.getFeeValueModel());
            transactionFee.setSettlementCurrency(feeConfig.getAccountCurrency());

            /* Use fixed fee model(FeeValueModelEnum.FIXED)*/
            BigDecimal feeAmount = feeConfig.getFeeValue();
            /* Use percentage fee model(FeeValueModelEnum.PERCENTAGE)*/
            if (FeeValueModelEnum.PERCENTAGE == feeConfig.getFeeValueModel()) {
                /* Use before settlement amount rule(FeeOnEnum.AMOUNT)*/
                feeAmount = taskOrder.getAmount().multiply(feeConfig.getFeeValue());
                if (feeConfig.getAccountCurrency() == accountInfo.getCurrency()) {
                    feeAmount = settlementAmount.multiply(feeConfig.getFeeValue());
                }

                /* Use after settlement fee rule(FeeOnEnum.FEE)*/
                if (FeeOnEnum.FEE == feeConfig.getFeeOn()) {
                    BigDecimal transactionFeeAmount = BigDecimal.ZERO;
                    if (CollectionUtils.isNotEmpty(transactionFees)) {
                        for (final TransactionFeeBo transFee : transactionFees) {
                            transactionFeeAmount = transactionFeeAmount.add(Optional.ofNullable(
                                    transFee.getCalculateAmount()).orElse(BigDecimal.ZERO));
                        }

                        transactionFee.setSettlementCurrency(transactionFees.get(0)
                                .getSettlementCurrency());
                    }

                    feeAmount = transactionFeeAmount.multiply(feeConfig.getFeeValue());
                }

                /* When feeAmount <= minFeeAmount */
                if (feeAmount.compareTo(feeConfig.getMinFeeAmount()) <= 0) {
                    feeAmount = feeConfig.getMinFeeAmount();
                }
                /* When feeAmount >= maxFeeAmount */
                if (feeAmount.compareTo(feeConfig.getMaxFeeAmount()) >= 0) {
                    feeAmount = feeConfig.getMaxFeeAmount();
                }
            }

            final BigDecimal settlementFeeAmount = feeAmount.setScale(0, RoundingMode.HALF_UP);
            final BigDecimal feeAmountUsd =
                    CurrencyEnum.USD == transactionFee.getSettlementCurrency()
                            ? settlementFeeAmount : AmountUtil
                            .division(settlementFeeAmount, usdDailyRateInfo.getMerchantRate(), 6);

            transactionFee.setCalculateAmount(feeAmount.setScale(6, RoundingMode.HALF_UP));
            transactionFee.setSettlementAmount(settlementFeeAmount);
            transactionFee.setSettlementAmountUsd(feeAmountUsd);
            transactionFee.setInstantFlag(feeConfig.getInstantFlag());
            transactionFeeList.add(transactionFee);
        }

        return transactionFeeList;
    }
}
