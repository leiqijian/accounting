package com.liquido.transaction.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.ListAccountBalanceDto;
import com.liquido.statement.pojo.dto.ListAccountBalanceDto.PendBusiness;
import com.liquido.statement.pojo.dto.QueryAccountUploadTradeDataDto;
import com.liquido.statement.pojo.vo.QueryAccountUploadTradeDataVo;
import com.liquido.transaction.common.properties.TradeProperties;
import com.liquido.transaction.feign.BaseService;
import com.liquido.transaction.feign.TradeMerchantServiceFeign;
import com.liquido.transaction.pojo.bo.PreProcessUploadAccountBalanceToTradeBo;
import com.liquido.transaction.pojo.vo.UpdateMerchantBalanceVo;
import com.liquido.transaction.service.AccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class AccountServiceImpl implements AccountService {

    private final BaseService baseService;
    private final StatementApis.StatementFeign statementFeign;
    private final TradeMerchantServiceFeign tradeMerchantServiceFeign;
    private final TradeProperties tradeProperties;

    @Lazy
    @Autowired
    private final AccountServiceImpl accountService;

    public List<UpdateMerchantBalanceVo.MerchantBalanceVo> uploadBalanceToTrade() {

        final StopWatch watch = new StopWatch("upload balance");
        watch.start("pre process upload data");
        // step1: pre data(account balance data; exchangeRate; hourly real rate)
        final PreProcessUploadAccountBalanceToTradeBo bo = preProcessUploadAccountBalanceToTrade();
        watch.stop();

        watch.start("process upload data");
        final Map<Long, MerchantDto> merchantMap = bo.getMerchantMap();
        final Map<String, DailyExchangeRateDto> rateMap = bo.getRateMap();
        final Map<String, List<ListAccountBalanceDto>> mergerAccountMap = bo.getMergerAccountMap();
        final List<UpdateMerchantBalanceVo.MerchantBalanceVo> result = new ArrayList<>();

        mergerAccountMap.forEach((key, value) -> {
            final MerchantDto merchant = merchantMap.get(Long.valueOf(key.split("_")[0]));
            final Map<String, UpdateMerchantBalanceVo.MerchantBalanceVo> map = new HashMap<>();
            final List<UpdateMerchantBalanceVo.MerchantBalanceVo> merchantBalanceList =
                    value.stream().map(account -> {
                        final DailyExchangeRateDto rate = rateMap.get(
                                String.format("%s_%s_%s", account.getAccountId(),
                                        CurrencyEnum.USD.getCode(),
                                        account.getCountryCode().getCurrency().getCode()));

                        if (!checkData(rate, account)) {
                            return null;
                        }
                        // step2: build merchant balance pojo
                        return buildMerchantBalanceVo(merchant, account, rate, map);
                    }).filter(Objects::nonNull).collect(Collectors.toList());

            // step3: post process merchantBalanceList,
            // get merger account balance data from map replace old account data
            this.postProcess(merchantBalanceList, map);
            result.addAll(merchantBalanceList);
        });

        watch.stop();
        // step4: upload merchant balance info to trade
        watch.start("upload data to trace");
        this.uploadMerchantBalance(result);
        watch.stop();

        log.info("upload transaction data completed:{}", watch.prettyPrint());

        return result;
    }

    private PreProcessUploadAccountBalanceToTradeBo preProcessUploadAccountBalanceToTrade() {

        final StopWatch watch = new StopWatch("pre process worker begin");
        watch.start("query all merchant");

        final List<MerchantDto> merchantList = baseService.queryAllMerchant().stream()
                .filter(v -> StringUtils.isNotBlank(v.getUuid())).collect(Collectors.toList());

        watch.stop();
        final Map<Long, MerchantDto> merchantMap = merchantList.stream()
                .collect(Collectors.toMap(MerchantDto::getId, Function.identity()));

        log.info("upload balance to trade merchant list size={}", merchantList.size());

        final LocalDateTime time =
                LocalDateTimeUtil.nowUtc().withMinute(0).withSecond(0).withNano(0);

        final ArrayList<ListAccountBalanceDto> listAccountBalanceList = new ArrayList<>();
        final ArrayList<DailyExchangeRateDto> dailyExchangeRateList = new ArrayList<>();
        watch.start("query account upload trade data");

        final List<CompletableFuture<QueryAccountUploadTradeDataDto>> futureList =
                ListUtils.partition(merchantList, 100).stream()
                        .map(part -> accountService.runQueryPushTraceDataTask(part, time))
                        .collect(Collectors.toList());

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();

        final List<QueryAccountUploadTradeDataDto> list = futureList.stream().map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                log.error("run query push trace dataTask error", e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        watch.stop();

        list.forEach(v -> {
            listAccountBalanceList.addAll(v.getAccountBalanceList());

            dailyExchangeRateList.addAll(v.getDailyExchangeRateList());
        });

        // prepare merchant under same country
        final Map<String, List<ListAccountBalanceDto>> mergerAccountMap =
                listAccountBalanceList.stream().collect(Collectors.groupingBy(
                        v -> String.format("%s_%s", v.getMerchantId(),
                                v.getCountryCode().getCode())));

        // prepare account rate info
        final Map<String, DailyExchangeRateDto> rateMap = dailyExchangeRateList.stream().collect(
                Collectors.toMap(v -> String.format("%s_%s_%s", v.getAccountId(),
                                v.getSourceCurrency().getCode(), v.getTargetCurrency().getCode()),
                        Function.identity()));

        final StringBuilder detail = new StringBuilder();
        for (final StopWatch.TaskInfo taskInfo : watch.getTaskInfo()) {
            detail.append(
                    String.format("[%s: %dms] ", taskInfo.getTaskName(), taskInfo.getTimeMillis()));
        }

        log.info("pre process worker completed, ts={}ms, traceWatch={}", watch.getTotalTimeMillis(),
                detail);

        return PreProcessUploadAccountBalanceToTradeBo.builder()
                .accountBalanceList(listAccountBalanceList)
                .dailyExchangeRateList(dailyExchangeRateList).mergerAccountMap(mergerAccountMap)
                .rateMap(rateMap).merchantMap(merchantMap).build();
    }

    @Async("taskExecutor")
    public CompletableFuture<QueryAccountUploadTradeDataDto> runQueryPushTraceDataTask(
            final List<MerchantDto> merchants, final LocalDateTime time) {
        try {
            ResponseDto<QueryAccountUploadTradeDataDto> responseDto =
                    statementFeign.queryAccountUploadTradeData(
                            QueryAccountUploadTradeDataVo.builder()
                                    .merchantIds(merchants.stream().map(MerchantDto::getId)
                                            .collect(Collectors.toList()))
                                    .localDateTime(time)
                                    .build());

            CheckResponseUtil.checkResponseData(responseDto);
            return CompletableFuture.completedFuture(responseDto.getData());

        } catch (Exception e) {
            log.error("query account schedule data error:", e);
            return CompletableFuture.completedFuture(null);
        }
    }

    private boolean checkData(
            final DailyExchangeRateDto rate,
            final ListAccountBalanceDto account
    ) {
        if (Objects.isNull(rate)) {
            return false;
        }
        return List.of(TransactionTypeCodeEnum.PAY_OUT, TransactionTypeCodeEnum.PAY_IN)
                .contains(account.getTransactionTypeCode());
    }

    private UpdateMerchantBalanceVo.MerchantBalanceVo buildMerchantBalanceVo(
            final MerchantDto merchant, final ListAccountBalanceDto accountBalance,
            final DailyExchangeRateDto rate,
            final Map<String, UpdateMerchantBalanceVo.MerchantBalanceVo> map
    ) {
        final String transactionType =
                TransactionTypeCodeEnum.PAY_OUT == accountBalance.getTransactionTypeCode()
                        ? "PAYOUT" : "PAYIN";

        final Currency currency =
                Currency.getInstance(accountBalance.getCountryCode().getCurrency().getCode());

        final UpdateMerchantBalanceVo.InProgressBalanceVo inProgressBalanceVo =
                buildInProgressVo(accountBalance, rate, currency);

        final UpdateMerchantBalanceVo.MerchantBalanceVo vo =
                UpdateMerchantBalanceVo.MerchantBalanceVo.builder()
                        .merchantUuid(UUID.fromString(merchant.getUuid()))
                        .merchantCode(merchant.getCode())
                        .countryCode(accountBalance.getCountryCode().getCode())
                        .mergerAccount(merchant.getMergerAccount())
                        .transactionTypeCode(accountBalance.getTransactionTypeCode())
                        .transactionType(transactionType).inProgressBalance(inProgressBalanceVo)
                        .balanceCurrency(currency).availableBalanceCurrency(currency).build();

        final BigDecimal totalBalance = accountBalance.getTotalBalance();
        final BigDecimal available = accountBalance.getAvailable();

        if (CurrencyEnum.USD == accountBalance.getCurrency()) {
            vo.setUsdBalance(totalBalance.longValue());
            vo.setBalance(totalBalance.multiply(rate.getMerchantRate()).longValue());
            vo.setUsdAvailableBalance(available.longValue());
            vo.setAvailableBalance(available.multiply(rate.getMerchantRate()).longValue());
            vo.setDepositBalance(
                    Optional.ofNullable(accountBalance.getPendBusiness().getDepositAmount())
                            .orElse(BigDecimal.ZERO).multiply(rate.getMerchantRate()).longValue());
            vo.setUsdDepositBalance(
                    Optional.ofNullable(accountBalance.getPendBusiness().getDepositAmount())
                            .orElse(BigDecimal.ZERO).longValue());
            vo.setLegalHoldAmount(
                    Optional.ofNullable(accountBalance.getPendBusiness().getLegalHoldAmount())
                            .orElse(BigDecimal.ZERO).multiply(rate.getMerchantRate()).longValue());
            vo.setUsdLegalHoldAmount(
                    Optional.ofNullable(accountBalance.getPendBusiness().getLegalHoldAmount())
                            .orElse(BigDecimal.ZERO).longValue());

        } else {
            vo.setBalance(totalBalance.longValue());
            vo.setUsdBalance(
                    totalBalance.divide(rate.getMerchantRate(), RoundingMode.HALF_UP).longValue());
            vo.setAvailableBalance(available.longValue());
            vo.setUsdAvailableBalance(
                    available.divide(rate.getMerchantRate(), RoundingMode.HALF_UP).longValue());
            vo.setDepositBalance(
                    Optional.ofNullable(accountBalance.getPendBusiness().getDepositAmount())
                            .orElse(BigDecimal.ZERO).longValue());
            vo.setUsdDepositBalance(
                    Optional.ofNullable(accountBalance.getPendBusiness().getDepositAmount())
                            .orElse(BigDecimal.ZERO)
                            .divide(rate.getMerchantRate(), RoundingMode.HALF_UP).longValue());
            vo.setLegalHoldAmount(
                    Optional.ofNullable(accountBalance.getPendBusiness().getLegalHoldAmount())
                            .orElse(BigDecimal.ZERO).longValue());
            vo.setUsdLegalHoldAmount(
                    Optional.ofNullable(accountBalance.getPendBusiness().getLegalHoldAmount())
                            .orElse(BigDecimal.ZERO)
                            .divide(rate.getMerchantRate(), RoundingMode.HALF_UP).longValue());
        }

        vo.setTotalInProgressBalance(
                inProgressBalanceVo.getBalance() + inProgressBalanceVo.getWithdrawingBalance()
                        + inProgressBalanceVo.getExchangingBalance());

        vo.setUsdTotalInProgressBalance(inProgressBalanceVo.getUsdBalance()
                + inProgressBalanceVo.getUsdWithdrawingBalance()
                + inProgressBalanceVo.getUsdExchangingBalance());

        //if mergerAccount=true, put merger account balance info to map
        if (Optional.ofNullable(merchant.getMergerAccount()).orElse(false)
                && (ObjectUtils.isEmpty(merchant.getNotSupportMergerCountry())
                || !merchant.getNotSupportMergerCountry()
                .contains(accountBalance.getCountryCode()))) {
            if (!map.containsKey(merchant.getCode())) {
                map.put(merchant.getCode(), vo);
            } else {
                this.processMergerAccountBalanceInfo(map.get(merchant.getCode()), vo);
            }
        }
        return vo;
    }

    private void processMergerAccountBalanceInfo(
            final UpdateMerchantBalanceVo.MerchantBalanceVo oldVo,
            final UpdateMerchantBalanceVo.MerchantBalanceVo vo
    ) {
        final UpdateMerchantBalanceVo.InProgressBalanceVo oldInProgress =
                oldVo.getInProgressBalance();

        final UpdateMerchantBalanceVo.InProgressBalanceVo inProgress = vo.getInProgressBalance();

        // sum inProgress amount
        final long inProgressBalance = oldInProgress.getBalance() + inProgress.getBalance();
        final long usdInProgressBalance =
                oldInProgress.getUsdBalance() + inProgress.getUsdBalance();

        final long withdrawingBalance =
                oldInProgress.getWithdrawingBalance() + inProgress.getWithdrawingBalance();
        final long usdWithdrawingBalance =
                oldInProgress.getUsdWithdrawingBalance() + inProgress.getUsdWithdrawingBalance();

        final long exchangingBalance =
                oldInProgress.getExchangingBalance() + inProgress.getExchangingBalance();
        final long usdExchangingBalance =
                oldInProgress.getUsdExchangingBalance() + inProgress.getUsdExchangingBalance();

        // sum balance and availableAmount
        final long totalBalance = oldVo.getBalance() + vo.getBalance();
        final long totalUsdBalance = oldVo.getUsdBalance() + vo.getUsdBalance();
        final long totalAvailable = oldVo.getAvailableBalance() + vo.getAvailableBalance();
        final long totalUsdAvailable = oldVo.getUsdAvailableBalance() + vo.getUsdAvailableBalance();
        final long depositBalance = oldVo.getDepositBalance() + vo.getDepositBalance();
        final long usdDepositBalance = oldVo.getUsdDepositBalance() + vo.getUsdDepositBalance();

        final long totalInProgressBalance =
                oldVo.getTotalInProgressBalance() + vo.getTotalInProgressBalance();
        final long totalUsdInProgressBalance =
                oldVo.getUsdTotalInProgressBalance() + vo.getUsdTotalInProgressBalance();

        oldVo.setBalance(totalBalance);
        oldVo.setUsdBalance(totalUsdBalance);
        oldVo.setAvailableBalance(totalAvailable);
        oldVo.setUsdAvailableBalance(totalUsdAvailable);
        oldVo.setDepositBalance(depositBalance);
        oldVo.setUsdDepositBalance(usdDepositBalance);
        oldVo.setTotalInProgressBalance(totalInProgressBalance);
        oldVo.setUsdTotalInProgressBalance(totalUsdInProgressBalance);

        oldInProgress.setBalance(inProgressBalance);
        oldInProgress.setUsdBalance(usdInProgressBalance);
        oldInProgress.setWithdrawingBalance(withdrawingBalance);
        oldInProgress.setUsdWithdrawingBalance(usdWithdrawingBalance);
        oldInProgress.setExchangingBalance(exchangingBalance);
        oldInProgress.setUsdExchangingBalance(usdExchangingBalance);
    }

    private UpdateMerchantBalanceVo.InProgressBalanceVo buildInProgressVo(
            final ListAccountBalanceDto accountBalance, final DailyExchangeRateDto rate,
            final Currency currency
    ) {
        final PendBusiness pendBusiness = accountBalance.getPendBusiness();

        final BigDecimal inProgressAmount =
                Optional.ofNullable(pendBusiness.getInProgressAmount()).orElse(BigDecimal.ZERO);
        final BigDecimal exchangingAmount = pendBusiness.getExchangeAmount();
        final BigDecimal withdrawingAmount = pendBusiness.getFrozenAmount();

        long countryCurrencyAmount;
        long usdInProgressAmount;
        long countryCurrencyWithdrawingBalance;
        long usdWithdrawingBalance;
        long countryCurrencyExchangingBalance;
        long usdExchangingBalance;

        if (CurrencyEnum.USD == accountBalance.getCurrency()) {
            countryCurrencyAmount = inProgressAmount.multiply(rate.getMerchantRate()).longValue();
            usdInProgressAmount = inProgressAmount.longValue();

            countryCurrencyWithdrawingBalance =
                    withdrawingAmount.multiply(rate.getMerchantRate()).longValue();
            usdWithdrawingBalance = withdrawingAmount.longValue();

            countryCurrencyExchangingBalance =
                    exchangingAmount.multiply(rate.getMerchantRate()).longValue();
            usdExchangingBalance = exchangingAmount.longValue();
        } else {
            countryCurrencyAmount = inProgressAmount.longValue();
            usdInProgressAmount =
                    inProgressAmount.divide(rate.getMerchantRate(), RoundingMode.HALF_UP)
                            .longValue();

            countryCurrencyWithdrawingBalance = withdrawingAmount.longValue();
            usdWithdrawingBalance =
                    withdrawingAmount.divide(rate.getMerchantRate(), RoundingMode.HALF_UP)
                            .longValue();

            countryCurrencyExchangingBalance = exchangingAmount.longValue();
            usdExchangingBalance =
                    exchangingAmount.divide(rate.getMerchantRate(), RoundingMode.HALF_UP)
                            .longValue();
        }
        return UpdateMerchantBalanceVo.InProgressBalanceVo.builder()
                .balance(countryCurrencyAmount)
                .usdBalance(usdInProgressAmount)
                .balanceCurrency(currency)
                .withdrawingBalance(countryCurrencyWithdrawingBalance)
                .usdWithdrawingBalance(usdWithdrawingBalance)
                .exchangingBalance(countryCurrencyExchangingBalance)
                .usdExchangingBalance(usdExchangingBalance)
                .refundBalance(0L)
                .usdRefundBalance(0L)
                .refundBalanceCurrency(currency)
                .chargebackBalance(0L)
                .usdChargebackBalance(0L)
                .chargebackBalanceCurrency(currency)
                .build();
    }

    private void postProcess(
            final List<UpdateMerchantBalanceVo.MerchantBalanceVo> list,
            final Map<String, UpdateMerchantBalanceVo.MerchantBalanceVo> map
    ) {
        list.forEach(t -> {
            if (map.containsKey(t.getMerchantCode())) {
                final UpdateMerchantBalanceVo.MerchantBalanceVo vo = map.get(t.getMerchantCode());
                t.setBalance(vo.getBalance());
                t.setUsdBalance(vo.getUsdBalance());
                t.setAvailableBalance(vo.getAvailableBalance());
                t.setUsdAvailableBalance(vo.getUsdAvailableBalance());
                t.setTotalInProgressBalance(vo.getTotalInProgressBalance());
                t.setUsdTotalInProgressBalance(vo.getUsdTotalInProgressBalance());
                t.setDepositBalance(vo.getDepositBalance());
                t.setUsdDepositBalance(vo.getUsdDepositBalance());

                final UpdateMerchantBalanceVo.InProgressBalanceVo inProgressBalance =
                        t.getInProgressBalance();

                inProgressBalance.setBalance(vo.getInProgressBalance().getBalance());
                inProgressBalance.setUsdBalance(vo.getInProgressBalance().getUsdBalance());
                inProgressBalance.setWithdrawingBalance(
                        vo.getInProgressBalance().getWithdrawingBalance());
                inProgressBalance.setUsdWithdrawingBalance(
                        vo.getInProgressBalance().getUsdWithdrawingBalance());
                inProgressBalance.setExchangingBalance(
                        vo.getInProgressBalance().getExchangingBalance());
                inProgressBalance.setUsdExchangingBalance(
                        vo.getInProgressBalance().getUsdExchangingBalance());
            }
        });
    }

    private void uploadMerchantBalance(List<UpdateMerchantBalanceVo.MerchantBalanceVo> vs) {
        final Map<String, TradeProperties.MerchantServiceEnvProperties> merchantServiceEnvs =
                tradeProperties.getMerchantService();
        vs.stream().collect(Collectors.groupingBy(
                        UpdateMerchantBalanceVo.MerchantBalanceVo::getTransactionTypeCode))
                .entrySet().parallelStream().forEach(x -> {

                    if (!merchantServiceEnvs.containsKey(x.getKey().getCode())) {
                        return;
                    }

                    final TradeProperties.MerchantServiceEnvProperties properties =
                            merchantServiceEnvs.get(x.getKey().getCode());
                    if (!Optional.ofNullable(properties.getEnable()).orElse(false)) {
                        return;
                    }

                    tradeMerchantServiceFeign.updateMerchantBalance(x.getKey(), false,
                            new UpdateMerchantBalanceVo(vs));

                });
    }
}
