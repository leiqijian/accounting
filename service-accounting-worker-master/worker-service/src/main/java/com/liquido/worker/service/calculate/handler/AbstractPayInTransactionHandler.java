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
import com.liquido.base.enums.CardTypeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.pojo.dto.AccountProductDto;
import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.dto.CardCostConfigDto;
import com.liquido.base.pojo.dto.CardCostConfigurationDto;
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
import com.liquido.worker.common.utils.WorkdayUtil;
import com.liquido.worker.enums.CalculationTaskStateEnum;
import com.liquido.worker.enums.TaskLogResultEnum;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.feign.BaseService;
import com.liquido.worker.feign.StatementService;
import com.liquido.worker.pojo.bo.AccountingScheduleBo;
import com.liquido.worker.pojo.bo.AdditionalChargeBo;
import com.liquido.worker.pojo.bo.MonthlyFeeConfigurationBo;
import com.liquido.worker.pojo.bo.PreCalculateConfigBo;
import com.liquido.worker.pojo.bo.PreCalculateFeeBo;
import com.liquido.worker.pojo.bo.TransactionFeeBo;
import com.liquido.worker.pojo.bo.TransactionMoneyBo;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.entity.TaskLogFeeCalculation;
import com.liquido.worker.pojo.mapper.ModelMapper;
import com.liquido.worker.pojo.vo.ApmCostConfigVo;
import com.liquido.worker.pojo.vo.CardCostConfigVo;
import com.liquido.worker.service.TaskFeeCalculationService;
import com.liquido.worker.service.TaskHoldMonitorService;
import com.liquido.worker.service.TaskLogFeeCalculationService;
import com.liquido.worker.service.calculate.CalculateManager;
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
 * Abstract PayIn TransactionHandler
 */
@Slf4j
@Service
@RequiredArgsConstructor
public abstract class AbstractPayInTransactionHandler implements TransactionStrategy {

    @Resource
    private WorkerProperties workerProperties;
    @Resource
    private BaseService baseService;
    @Resource
    private StatementService statementService;
    @Resource
    private ExchangeRateManager exchangeRateManager;
    @Resource
    private TaskHoldMonitorService taskHoldMonitorService;
    @Resource
    private TransactionCostService transactionCostService;
    @Resource
    private TaskFeeCalculationService taskFeeCalculationService;
    @Resource
    private TaskLogFeeCalculationService taskLogFeeCalculationService;
    @Resource
    private TransactionExtraIncomeService transactionExtraIncomeService;
    @Resource
    private ModelMapper modelMapper;

    private static final List<DirectionTypeEnum> REFUND_CHARGE_BACK_TYPES =
            List.of(DirectionTypeEnum.REFUND, DirectionTypeEnum.CHARGE_BACK);

    private static final List<DirectionTypeEnum> NON_SETTLED_TYPES =
            List.of(DirectionTypeEnum.REFUND, DirectionTypeEnum.CHARGE_BACK,
                    DirectionTypeEnum.CHARGE_BACK_REJECTED);


    /**
     * batch task fee trial calculate
     */
    public List<PreCalculateFeeBo> batchFeeTrialCalculate(
            final List<TaskFeeCalculation> taskOrders
    ) {

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
     *
     * @param requestId     requestId
     * @param taskOrderList taskOrderList
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchProcessCalculate(
            final String requestId,
            final List<TaskFeeCalculation> taskOrderList
    ) {
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
        watch.start("payIn batch calculation fee");
        for (final Map.Entry<String, List<TaskFeeCalculation>> entryMap : groupTask.entrySet()) {
            for (final TaskFeeCalculation taskOrder : entryMap.getValue()) {
                final PreCalculateConfigBo configBo = this.preProcessCalculate(taskOrder);
                final TransactionMoneyBo resultBo = this.processingCalculate(taskOrder, configBo);
                orderList.add(resultBo);
            }
        }

        watch.stop();
        final long calculationConsuming = watch.getLastTaskTimeMillis();
        log.info("Batch Process calculation fee ts={}ms", calculationConsuming);

        watch.start("payIn batch process settlement");
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
     * @param taskOrder taskOrder
     *
     * @return PreCalculateConfigBo
     */
    protected PreCalculateConfigBo preProcessCalculate(final TaskFeeCalculation taskOrder) {
        log.info("Payin task preCalculate begin. taskOrder={}", taskOrder);

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

        /* STEP7: get transaction cost config */
        final Pair<ApmCostConfigurationDto, CardCostConfigurationDto> costConfig =
                this.getCostConfigs(taskOrder, merchantDto, accountInfo);

        /* STEP8: get extra income config from base-service; */
        final List<ExtraIncomeConfigurationDto> extraIncomeConfig =
                transactionExtraIncomeService.queryExtraIncomeConfig(accountInfo, taskOrder);

        /* STEP9: get workday config from base-service; */
        final List<WorkingDayDto> workdayList = baseService.getWorkingDay(
                transactionDate.getYear(), accountInfo.getCountryCode());

        log.info("Payin task preCalculate end. ts={}ms, taskOrderId={}",
                (System.currentTimeMillis() - begin), taskOrder.getId());

        /* STEP10: encapsulated preCalculateConfig and return; */
        return PreCalculateConfigBo.builder()
                .transactionDate(transactionDate)
                .merchantInfo(merchantDto)
                .accountInfo(accountInfo)
                .accountProduct(accountProduct)
                .exchangeRate(exchangeRatePair)
                .calculationRule(feeConfigBo.getCalculationRuleMap())
                .feeConfigList(feeConfigBo.getFeeConfigList())
                .workdayList(workdayList)
                .apmCostConfig(costConfig.getLeft())
                .cardCostConfig(costConfig.getRight())
                .extraIncomeConfig(extraIncomeConfig)
                .build();
    }


    private LocalDateTime getTimeToGetExchangeRate(
            final AccountDto account,
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

    private Pair<ApmCostConfigurationDto, CardCostConfigurationDto> getCostConfigs(
            final TaskFeeCalculation task,
            final MerchantDto merchantDto,
            final AccountDto accountInfo) {

        ApmCostConfigurationDto apmCostConfig = ApmCostConfigurationDto.builder()
                .costConfigList(Collections.emptyList())
                .build();
        CardCostConfigurationDto cardCostConfig = CardCostConfigurationDto.builder()
                .cardCostConfigList(Collections.emptyList()).build();

        // if DirectionType is "CHARGE_BACK_REJECTED" No need to calculate costs;
        if (List.of(DirectionTypeEnum.CHARGE_BACK_REJECTED).contains(task.getDirectionType())) {
            return Pair.of(apmCostConfig, cardCostConfig);
        }

        // when transaction card-type is credit-card or debit-card
        if (ProductCodeEnum.CARD == task.getProductCode()) {
            cardCostConfig = baseService.getCardCostConfig(CardCostConfigVo.builder()
                    .transactionId(task.getId())
                    .merchantCode(merchantDto.getCode())
                    .accountId(accountInfo.getId())
                    .country(accountInfo.getCountryCode())
                    .transactionType(accountInfo.getTransactionTypeCode())
                    .productCode(task.getProductCode())
                    .directionType(task.getDirectionType())
                    .vendor(task.getVendor())
                    .cardType(CalculateManager.getCardType(task))
                    .cardGroup(CalculateManager.getCardBrand(task))
                    .installment(CalculateManager.getCardInstallments(task))
                    .build());

            // if order is refund or charge_back exclude shopify cost fee
            if (REFUND_CHARGE_BACK_TYPES.contains(task.getDirectionType())) {
                this.excludeShopifyFeeRules(cardCostConfig);
            }
        } else {
            // non-card transaction
            apmCostConfig = baseService.getApmCostConfig(ApmCostConfigVo.builder()
                    .accountId(accountInfo.getId())
                    .country(accountInfo.getCountryCode())
                    .merchantCode(task.getMerchantCode())
                    .transactionType(accountInfo.getTransactionTypeCode())
                    .directionType(task.getDirectionType())
                    .vendor(task.getVendor())
                    .productCode(task.getProductCode())
                    .build());
        }

        return Pair.of(apmCostConfig, cardCostConfig);
    }

    private void excludeShopifyFeeRules(
            final CardCostConfigurationDto cardCostConfig) {

        if (Objects.nonNull(cardCostConfig)
                && CollectionUtils.isNotEmpty(cardCostConfig.getCardCostConfigList())) {

            final List<CardCostConfigDto> cardCostConfigList =
                    cardCostConfig.getCardCostConfigList().stream()
                            .filter(item -> !StringUtils.equalsIgnoreCase("SHOPIFY_FEE",
                                    item.getFeeName()))
                            .collect(Collectors.toList());
            // exclude shopify fee rules
            cardCostConfig.setCardCostConfigList(cardCostConfigList);
        }
    }

    /**
     * process fee calculate
     *
     * @param taskOrder taskOrder
     * @param preConfig preConfig
     *
     * @return TransactionMoneyBo
     */
    protected TransactionMoneyBo processingCalculate(
            final TaskFeeCalculation taskOrder,
            final PreCalculateConfigBo preConfig) {

        log.info("Task calculate begin. taskOrder={},accountInfo={}, feeConfig={},"
                        + "accountProduct={},exchangeRate={},apmCostConfig={},"
                        + "cardCostConfig={},extraIncomeConfig={},workdayList size={}",
                taskOrder, preConfig.getAccountInfo(), preConfig.getFeeConfigList(),
                preConfig.getAccountProduct(), preConfig.getExchangeRate(),
                preConfig.getApmCostConfig(), preConfig.getCardCostConfig(),
                preConfig.getExtraIncomeConfig(),
                Optional.ofNullable(preConfig.getWorkdayList()).map(List::size).orElse(0));

        final long begin = System.currentTimeMillis();
        final AccountDto accountInfo = preConfig.getAccountInfo();
        final List<MonthlyFeeConfigurationDto> feeConfigList = preConfig.getFeeConfigList();
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
        transactionMoney.setCalculationRule(preConfig.getCalculationRule());

        // Setting PAYIN beCreditedDate and beCreditedAmount
        this.settingBeCreditedRules(transactionMoney, taskOrder, preConfig);

        // build additionalCharge;
        this.buildAdditionalCharge(taskOrder, transactionMoney, merchantRateInfo);

        final BigDecimal totalAdditionalAmount = transactionMoney.getAdditionalCharge().stream()
                .filter(ac -> Objects.nonNull(ac.getAmount()))
                .map(ac -> ac.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        transactionMoney.setBeCreditedAmount(
                settlementAmount.multiply(businessStrategy.getAmountPon().getCode())
                        .add(totalAdditionalAmount));

        /* STEP2.1: Monitor transactions for over limit */
        final boolean holding = taskHoldMonitorService.checkOverLimit(
                taskOrder, preConfig, settlementAmount);
        transactionMoney.setHoldStatus(holding ? HoldStatusEnum.HOLD : HoldStatusEnum.NORMAL);

        /* STEP2.2: No fees and
            build accounting schedule list(use credit-card and installment transactions)
         */
        this.buildAccountingScheduleList(taskOrder, transactionMoney, preConfig);

        /* STEP2.3: Get account product fee config rule,
         * if fee config is empty return transactionMoney
         */
        if (CollectionUtils.isEmpty(feeConfigList)) {
            return transactionMoney;
        }

        /* STEP3: Deal calculate transaction fee */
        final List<TransactionFeeBo> transactionFeeList = this.calculateTransactionFee(accountInfo,
                taskOrder, settlementAmount, businessStrategy, usdDailyRateInfo, feeConfigList);
        transactionMoney.setTransactionFeeList(transactionFeeList);

        final BigDecimal totalFeeAmount = transactionFeeList.stream()
                .filter(TransactionFeeBo::getInstantFlag)
                .reduce(BigDecimal.ZERO, (x, y) ->
                                x.add(y.getSettlementAmount().multiply(y.getAmountPon().getCode())),
                        BigDecimal::add);

        transactionMoney.setBeCreditedAmount(
                transactionMoney.getBeCreditedAmount().add(totalFeeAmount));
        /*
          STEP4: Has fees and
          build accounting schedule list(use credit-card and installment transactions)
         */
        this.buildAccountingScheduleList(taskOrder, transactionMoney, preConfig);

        /*
         * TODO build
         * BigDecimal settlementAmount;        // 结算总额
         * BigDecimal settlementMarginAmount;  // 结算保证金部分(延迟入账)
         * BigDecimal settlementReleaseAmount; // 保证金释放金额
         * BigDecimal marginRate;              // 保证金比例(如 0.2)
         */

        /* STEP5: Deal calculate transaction cost */
        transactionMoney.setTransactionCostList(transactionCostService.calculateTransactionCost(
                transactionMoney, preConfig, usdDailyRateInfo));

        /* STEP6: Deal calculate transaction extra fee */
        transactionMoney.setTransactionExtraFeeList(transactionExtraIncomeService
                .calculateExtraIncome(transactionMoney, usdDailyRateInfo,
                        preConfig.getExtraIncomeConfig()));

        log.info("Task calculated end. ts={}ms, taskOrderId={}, transactionMoney={}",
                (System.currentTimeMillis() - begin), taskOrder.getId(), transactionMoney);

        return transactionMoney;
    }

    private void buildAdditionalCharge(
            final TaskFeeCalculation taskOrder,
            final TransactionMoneyBo transactionMoney,
            final DailyExchangeRateDto merchantRateInfo) {

        final List<AdditionalChargeBo> additionalChargeList = Lists.newArrayList();
        final BigDecimal paidAmount = Optional.ofNullable(taskOrder.getOthers().get("paidAmount"))
                .map(JsonNode::decimalValue)
                .orElse(BigDecimal.ZERO);
        if (paidAmount.compareTo(BigDecimal.ZERO) > 0
                && paidAmount.compareTo(taskOrder.getAmount()) > 0) {

            // convert to settlement currency at last
            final BigDecimal penaltyInterest = AmountUtil.division(
                    paidAmount.subtract(taskOrder.getAmount()),
                    merchantRateInfo.getMerchantRate(),
                    0).multiply(transactionMoney.getAmountPon().getCode());

            additionalChargeList.add(AdditionalChargeBo.builder()
                    .amount(penaltyInterest)
                    .type("PENALTY_INTEREST")
                    .remark("PenaltyInterest = PaidAmount - Amount")
                    .build());
        }

        transactionMoney.setAdditionalCharge(additionalChargeList);
    }

    /**
     * credit-card transaction installment accounting schedule
     *
     * @param taskOrder
     * @param transactionMoney
     * @param preConfig
     */
    private void buildAccountingScheduleList(
            final TaskFeeCalculation taskOrder,
            final TransactionMoneyBo transactionMoney,
            final PreCalculateConfigBo preConfig) {

        final List<AccountingScheduleBo> scheduleList = Lists.newArrayList();

        boolean accountingScheduleSwitch = Optional.ofNullable(
                        workerProperties.getAccountingSchedule())
                .map(WorkerProperties.AccountingSchedule::getEnable).orElse(false);

        final List<Long> scheduleAaccountList = Optional.ofNullable(
                        workerProperties.getAccountingSchedule())
                .map(WorkerProperties.AccountingSchedule::getAccountList)
                .orElse(Lists.newArrayList());

        if (!accountingScheduleSwitch) {
            transactionMoney.setAccountingScheduleList(scheduleList);
            return;
        }

        if (!scheduleAaccountList.contains(preConfig.getAccountInfo().getId())) {
            transactionMoney.setAccountingScheduleList(scheduleList);
            return;
        }

        //Non-Settled state transactions
        if (DirectionTypeEnum.SETTLED != taskOrder.getDirectionType()) {
            transactionMoney.setAccountingScheduleList(scheduleList);
            return;
        }

        //Non-credit card transactions and non-installment transactions
        if (!(ProductCodeEnum.CARD == taskOrder.getProductCode()
                && CardTypeEnum.CREDIT_CARD == CalculateManager.getCardType(taskOrder))) {
            transactionMoney.setAccountingScheduleList(scheduleList);
            return;
        }

        final int totalInstallment = CalculateManager.getCardInstallments(taskOrder);
        if (totalInstallment <= 1) {
            transactionMoney.setAccountingScheduleList(scheduleList);
            return;
        }

        transactionMoney.setInstallmentFlag(true);

        final BigDecimal totalFeeAmount =
                Optional.ofNullable(transactionMoney.getTransactionFeeList().stream()
                                .filter(TransactionFeeBo::getInstantFlag)
                                .filter(x -> FeeGroupEnum.TRANSACTION_FEE == x.getFeeGroup())
                                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y.getSettlementAmount()
                                        .multiply(y.getAmountPon().getCode())), BigDecimal::add))
                        .orElse(BigDecimal.ZERO);

        final BigDecimal totalTaxAmount =
                Optional.ofNullable(transactionMoney.getTransactionFeeList().stream()
                                .filter(TransactionFeeBo::getInstantFlag)
                                .filter(x -> FeeGroupEnum.TRANSACTION_FEE != x.getFeeGroup())
                                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y.getSettlementAmount()
                                        .multiply(y.getAmountPon().getCode())), BigDecimal::add))
                        .orElse(BigDecimal.ZERO);

        final LocalDate transactionDate = transactionMoney.getTransactionDate();
        final BigDecimal totalAccountingAmount = transactionMoney.getSettlementAmount()
                .add(totalTaxAmount).add(totalFeeAmount);

        // account amount divide installment
        final BigDecimal singleInstallAmount = totalAccountingAmount.divide(
                new BigDecimal(totalInstallment), 0, RoundingMode.HALF_UP);
        final BigDecimal roundingDifferenceAmount = totalAccountingAmount
                .subtract(singleInstallAmount.multiply(new BigDecimal(totalInstallment))
                        .setScale(0, RoundingMode.HALF_UP));

        // settlement amount divide installment
        final BigDecimal singleInstallSettleAmount = transactionMoney.getSettlementAmount()
                .divide(new BigDecimal(totalInstallment), 0, RoundingMode.HALF_UP);
        final BigDecimal roundingDifferenceSettleAmount = transactionMoney.getSettlementAmount()
                .subtract(singleInstallSettleAmount.multiply(new BigDecimal(totalInstallment))
                        .setScale(0, RoundingMode.HALF_UP));

        // fee amount divide installment
        final BigDecimal singleInstallFeeAmount = totalFeeAmount.divide(
                new BigDecimal(totalInstallment), 0, RoundingMode.HALF_UP);
        final BigDecimal roundingDifferenceFeeAmount = totalFeeAmount
                .subtract(singleInstallFeeAmount
                        .multiply(new BigDecimal(totalInstallment))
                        .setScale(0, RoundingMode.HALF_UP));

        // tax amount divide installment
        final BigDecimal singleInstallTaxAmount = totalTaxAmount.divide(
                new BigDecimal(totalInstallment), 0, RoundingMode.HALF_UP);
        final BigDecimal roundingDifferenceTaxAmount = totalTaxAmount
                .subtract(singleInstallTaxAmount.multiply(new BigDecimal(totalInstallment))
                        .setScale(0, RoundingMode.HALF_UP));

        for (int i = 1; i <= totalInstallment; i++) {

            final LocalDate accountingDate = WorkdayUtil.getInstallmentWorkDay(
                    transactionDate, i, 31, preConfig.getWorkdayList());

            BigDecimal settlementAmount = singleInstallSettleAmount;
            BigDecimal accountingAmount = singleInstallAmount;
            BigDecimal feeAmount = singleInstallFeeAmount;
            BigDecimal taxAmount = singleInstallTaxAmount;

            if (i == 1) {
                transactionMoney.setBeCreditedDate(accountingDate);
            }

            // The last installment will supplement the rounding difference amount
            if (i == totalInstallment) {
                settlementAmount = singleInstallSettleAmount.add(roundingDifferenceSettleAmount);
                accountingAmount = singleInstallAmount.add(roundingDifferenceAmount);
                feeAmount = singleInstallFeeAmount.add(roundingDifferenceFeeAmount);
                taxAmount = singleInstallTaxAmount.add(roundingDifferenceTaxAmount);
            }
            scheduleList.add(AccountingScheduleBo.builder()
                    .currentInstallment(i)
                    .totalInstallment(totalInstallment)
                    .accountingDate(accountingDate)
                    .settlementAmount(settlementAmount)
                    .accountingAmount(accountingAmount)
                    .feeAmount(feeAmount)
                    .taxAmount(taxAmount)
                    .cardType(CalculateManager.getCardType(taskOrder).getCode())
                    .cardBrand(CalculateManager.getCardBrand(taskOrder).getCode())
                    .build());
        }

        transactionMoney.setAccountingScheduleList(scheduleList);
        transactionMoney.setBeCreditedAmount(BigDecimal.ZERO);
    }


    private List<TransactionFeeBo> calculateTransactionFee(
            final AccountDto accountInfo,
            final TaskFeeCalculation taskOrder,
            final BigDecimal settlementAmount,
            final BusinessStrategyEnum businessStrategy,
            final DailyExchangeRateDto usdDailyRateInfo,
            final List<MonthlyFeeConfigurationDto> feeConfigList
    ) {

        /* STEP1: Deal calculate transaction-fee. amount-unit: cent */
        final List<TransactionFeeBo> transactionFeeList = this.buildTransactionFee(
                taskOrder, accountInfo, settlementAmount, feeConfigList, usdDailyRateInfo);

        /* STEP2: Deal calculate anticipation-fee, just for credit card installment business */
        final List<TransactionFeeBo> anticipationFees = this.buildAnticipationFee(
                taskOrder, accountInfo, settlementAmount, feeConfigList, usdDailyRateInfo);
        transactionFeeList.addAll(anticipationFees);

        /* STEP3: Deal calculate tax-fee. E.g:(Tax-fee ......)*/
        final List<TransactionFeeBo> otherFees = this.buildTransactionTaxFee(taskOrder, accountInfo,
                settlementAmount, transactionFeeList, feeConfigList, usdDailyRateInfo);
        transactionFeeList.addAll(otherFees);

        /* STEP4: Set fees pon(positive or negative)*/
        transactionFeeList.forEach(feeBo -> feeBo.setAmountPon(businessStrategy.getFeePon()));

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
     * @param requestId requestId
     * @param orderList orderList
     */
    protected ResponseDto<Void> batchProcessStatement(
            final String requestId,
            final List<TransactionMoneyBo> orderList
    ) {

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
    protected void postBatchProcessCalculate(
            final String requestId,
            final List<TransactionMoneyBo> orderList,
            final ResponseDto<Void> settlementResult,
            final LocalDateTime executeStartTime,
            final long calculationConsuming,
            final long settlementConsuming
    ) {

        TaskLogResultEnum taskState = TaskLogResultEnum.FAILED;
        if (Objects.nonNull(settlementResult) && settlementResult.isSuccess()) {
            taskState = TaskLogResultEnum.SUCCESS;

            taskFeeCalculationService.batchHoldingTask(orderList.stream()
                    .filter(item -> HoldStatusEnum.HOLD == item.getHoldStatus())
                    .map(TransactionMoneyBo::getTransactionId)
                    .collect(Collectors.toList()));

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
            final Pair<DailyExchangeRateDto, DailyExchangeRateDto> exchangeRate
    ) {

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
                .cardGroup(CreditCardGroupCodeEnum.parse(taskOrder.getSubProductCode()))
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
            final DailyExchangeRateDto usdDailyRateInfo
    ) {

        final List<MonthlyFeeConfigurationDto> feeConfigList = feeConfigurationList.stream()
                .filter(item -> FeeTypeCodeEnum.TRANSACTION_FEE == item.getFeeTypeCode())
                .collect(Collectors.toList());

        final List<TransactionFeeBo> transactionFeeList = Lists.newArrayList();
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
     * Deal calculate transaction-fee. amount-unit: cent
     * Formula: anticipation_fee = amount / periods * anticipation_ratio * (1+ periods)/2 * periods
     */
    private List<TransactionFeeBo> buildAnticipationFee(
            final TaskFeeCalculation taskOrder,
            final AccountDto accountInfo,
            final BigDecimal settlementAmount,
            final List<MonthlyFeeConfigurationDto> feeConfigurationList,
            final DailyExchangeRateDto usdDailyRateInfo
    ) {

        final List<TransactionFeeBo> transactionFeeList = Lists.newArrayList();
        final int installmentPeriod = CalculateManager.getCardInstallments(taskOrder);
        final List<MonthlyFeeConfigurationDto> feeConfigList = feeConfigurationList.stream()
                .filter(item -> FeeTypeCodeEnum.ANTICIPATION_FEE == item.getFeeTypeCode())
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(feeConfigList) || installmentPeriod <= 0) {
            log.info("anticipation-fee not configured, period={} taskId={}",
                    installmentPeriod, taskOrder.getId());
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

                // feeAmount = amount / periods * anticipation_ratio * (1+ periods)/2 * periods
                final BigDecimal periods = new BigDecimal(installmentPeriod);
                feeAmount = AmountUtil.division(taskOrder.getAmount(), periods, 6)
                        .multiply(feeConfig.getFeeValue())
                        .multiply(AmountUtil.division(BigDecimal.ONE.add(periods), Constant.TWO, 6))
                        .multiply(periods).setScale(6, RoundingMode.HALF_UP);
                if (feeConfig.getAccountCurrency() == accountInfo.getCurrency()) {
                    feeAmount = AmountUtil.division(settlementAmount, periods, 6)
                            .multiply(feeConfig.getFeeValue())
                            .multiply(AmountUtil.division(BigDecimal.ONE.add(periods),
                                    Constant.TWO, 6))
                            .multiply(periods).setScale(6, RoundingMode.HALF_UP);
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
            final DailyExchangeRateDto usdDailyRateInfo
    ) {

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

    /**
     * Calculate order tobe credited date
     *
     * @param taskOrder transactionMoney
     * @param taskOrder taskOrder
     * @param preConfig preConfig
     *
     * @return LocalDate LocalDate
     */
    private void settingBeCreditedRules(
            final TransactionMoneyBo transactionMoney,
            final TaskFeeCalculation taskOrder,
            final PreCalculateConfigBo preConfig
    ) {

        final TradingModelEnum tradingModel = Optional.ofNullable(preConfig.getAccountProduct())
                .map(AccountProductDto::getTradingModel).orElse(null);
        if (Objects.isNull(tradingModel)) {
            throw WorkerExceptionCode.TRADING_MODEL_UNDEFINED.exception();
        }

        /* D+0 or T+0 settlement */
        if (TradingModelEnum.INSTANT_TRADING.contains(tradingModel)
                || NON_SETTLED_TYPES.contains(taskOrder.getDirectionType())) {
            transactionMoney.setTradingModel(TradingModelEnum.D0);
            transactionMoney.setBeCreditedDate(preConfig.getTransactionDate());

            return;
        }

        /* D+n or T+n settlement */
        transactionMoney.setTradingModel(tradingModel);
        transactionMoney.setBeCreditedDate(WorkdayUtil.getNextWorkDay(
                preConfig.getTransactionDate(), tradingModel, preConfig.getWorkdayList()));
    }
}
