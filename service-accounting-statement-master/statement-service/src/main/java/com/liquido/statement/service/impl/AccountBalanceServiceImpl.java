package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.CalculateInProgressesAmountBo;
import com.liquido.statement.pojo.bo.DepositConfigBo;
import com.liquido.statement.pojo.bo.ExchangeRateConfig;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.pojo.dto.ListAccountBalanceDto;
import com.liquido.statement.pojo.dto.PageAccountDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountInProgress;
import com.liquido.statement.pojo.entity.QAccount;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.ListAccountConfigVo;
import com.liquido.statement.pojo.vo.PageAccountVo;
import com.liquido.statement.pojo.vo.QueryHourlyExchangeRateVo;
import com.liquido.statement.service.AccountConfigService;
import com.liquido.statement.service.AccountInProgressService;
import com.liquido.statement.service.HourlyExchangeRateService;
import com.liquido.statement.service.TransactionMoneyService;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountBalanceServiceImpl {


    private final ModelMapper modelMapper;
    private final BaseService baseService;
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;
    private final TransactionMoneyService transactionMoneyService;
    private final AccountInProgressService accountInProgressService;
    private final HourlyExchangeRateService hourlyExchangeRateService;
    private final AccountConfigService accountConfigService;

    public PageVo<PageAccountDto> pageAccount(final PageAccountVo vo) {

        final Long merchantId = Optional.ofNullable(vo.getMerchantCode())
                .map(baseService::getMerchantByCode)
                .map(MerchantDto::getId).orElse(null);

        final QAccount account = QAccount.account;
        final List<BooleanExpression> condition = Lists.newArrayList();
        if (Objects.nonNull(merchantId)) {
            condition.add(account.merchantId.eq(merchantId));
        }
        if (Objects.nonNull(vo.getCountryCode())) {
            condition.add(account.countryCode.eq(vo.getCountryCode()));
        }
        if (Objects.nonNull(vo.getTransactionTypeCode())) {
            condition.add(account.transactionTypeCode.eq(vo.getTransactionTypeCode()));
        }

        final BooleanExpression[] conditionArray = condition.toArray(new BooleanExpression[] {});
        final long count = Optional.ofNullable(jpaQueryFactory.select(account.id.count())
                .from(account)
                .where(conditionArray)
                .fetchOne()).orElse(0L);

        if (count <= 0) {
            return new PageVo<>(vo.getPageNo(), vo.getPageSize(), count, Collections.emptyList());
        }

        final List<Account> resultList = jpaQueryFactory.select(account)
                .from(account)
                .where(conditionArray)
                .setHint("javax.persistence.fetchgraph",
                        entityManager.getEntityGraph("accountConfig"))
                .orderBy((account.latestDailyBalance.add(account.subTotalAmount)).desc())
                .offset(vo.getOffset()).limit(vo.getPageSize())
                .fetch();

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(), count,
                resultList.stream().map(this::mapAccountToPageDto).collect(Collectors.toList()));
    }


    private PageAccountDto mapAccountToPageDto(final Account account) {
        final PageAccountDto dto = modelMapper.convertPage(account);

        Optional.ofNullable(baseService.getMerchantById(account.getMerchantId()))
                .ifPresent(info -> {
                    dto.setMerchantCode(info.getCode());
                    dto.setMerchantName(info.getName());
                });

        Optional.ofNullable(dto.getAccountConfigDto().getExchangeRateConfig())
                .ifPresent(exchangeRateConfig -> dto.setExchangeRateCurrency(
                        exchangeRateConfig.getDailyExchangeRates().stream()
                                .filter(ExchangeRateConfig.DailyExchangeRate::isEnabled)
                                .map(ExchangeRateConfig.DailyExchangeRate::getSourceCurrency)
                                .collect(Collectors.toList())));

        final List<AccountConfigDto> accountConfigList = accountConfigService.listAccountConfig(
                ListAccountConfigVo.builder().ids(List.of(account.getAccountConfigId())).build());

        final ListAccountBalanceDto accountBalanceDto =
                buildAccountBalanceInfo(account, LocalDateTime.now(), accountConfigList);

        dto.setAvailableAmount(accountBalanceDto.getAvailable());
        dto.setPendingAmount(accountBalanceDto.getPendingAmount());
        dto.setUnavailableAmount(accountBalanceDto.getUnavailable());
        dto.setHoldingAmount(
                Optional.ofNullable(accountBalanceDto.getHoldingAmount()).orElse(BigDecimal.ZERO));

        return dto;
    }


    private ListAccountBalanceDto buildAccountBalanceInfo(
            final Account account,
            final LocalDateTime utcTime,
            final List<AccountConfigDto> accountConfigList) {

        final ListAccountBalanceDto dto = ListAccountBalanceDto.builder()
                .utcTime(utcTime)
                .merchantId(account.getMerchantId())
                .accountId(account.getId())
                .countryCode(account.getCountryCode())
                .transactionTypeCode(account.getTransactionTypeCode())
                .currency(account.getCurrency()).build();

        dto.setTotalBalance(
                account.getLatestDailyBalance().add(account.getSubTotalAmount()));

        dto.setUnavailable(transactionMoneyService.getPendingBalance(account));

        if (TransactionTypeCodeEnum.PAY_IN == account.getTransactionTypeCode()) {
            final BigDecimal holdingBalance =
                    transactionMoneyService.getHoldingBalance(account.getId());
            if (holdingBalance.compareTo(BigDecimal.ZERO) > 0) {
                dto.setHoldingAmount(holdingBalance);
            }
        }
        // pend amount
        this.buildAccountPendBusiness(dto, account);

        //deposit amount
        final AccountConfigDto accountConfigDto = accountConfigList.stream()
                .filter(config -> config.getId().equals(account.getAccountConfigId()))
                .findFirst().orElse(null);

        final BigDecimal depositAmount = Optional.ofNullable(accountConfigDto)
                .map(AccountConfigDto::getConfigData)
                .map(AccountConfigData::getDepositConfig)
                .map(DepositConfigBo::getAmount)
                .orElse(BigDecimal.ZERO);

        dto.setAvailable((Objects.nonNull(account.getExtractableBalance())
                ? account.getExtractableBalance() : BigDecimal.ZERO)
                .subtract(Optional.ofNullable(dto.getPendBusiness().getInProgressAmount())
                        .orElse(BigDecimal.ZERO))
                .subtract(depositAmount));
        return dto;
    }

    /**
     * PAY_IN: pending Amount = frozenAmount + exchangeAmount
     * PAY_OUT: pending Amount = frozenAmount + exchangeAmount + inProgressAmount
     * unit:cent
     */
    private void buildAccountPendBusiness(
            final ListAccountBalanceDto dto,
            final Account account) {

        final ListAccountBalanceDto.PendBusiness pend =
                new ListAccountBalanceDto.PendBusiness();

        final ListAccountBalanceDto.InProgress inProgress =
                new ListAccountBalanceDto.InProgress();

        pend.setExchangeAmount(
                Optional.ofNullable(account.getExchangeAmount()).orElse(BigDecimal.ZERO));

        pend.setFrozenAmount(
                Optional.ofNullable(account.getFrozenAmount()).orElse(BigDecimal.ZERO));

        this.calculateInProgressesAmountAndSet(account, pend, inProgress);

        dto.setPendingAmount(pend.getExchangeAmount().add(pend.getFrozenAmount())
                .add(Optional.ofNullable(pend.getInProgressAmount()).orElse(BigDecimal.ZERO)));

        dto.setInProgress(inProgress);
        dto.setPendBusiness(pend);
    }

    private void calculateInProgressesAmountAndSet(
            final Account account,
            final ListAccountBalanceDto.PendBusiness pend,
            final ListAccountBalanceDto.InProgress inProgress) {

        final CalculateInProgressesAmountBo bo =
                queryAccountInProgressAndCalculateInProgressesAmount(account.getId(),
                        account.getCurrency(),
                        account.getCountryCode().getCurrency());
        if (Objects.nonNull(bo)) {
            pend.setInProgressAmount(bo.getTotalInProgressAmount());

            inProgress.setUsdInProgressAmount(bo.getUsdTotalInProgressAmount());
            inProgress.setCountryCurrencyInProgressAmount(
                    bo.getCountryCurrencyTotalInProgressAmount());
            inProgress.setInProgressAmount(bo.getTotalInProgressAmount());
        }
    }

    private CalculateInProgressesAmountBo queryAccountInProgressAndCalculateInProgressesAmount(
            final Long accountId,
            final CurrencyEnum accountCurrency,
            final CurrencyEnum countryCurrency) {

        final List<AccountInProgress> accountInProgresses =
                accountInProgressService.findAllByAccountId(accountId);
        return calculateInProgressesAmount(accountInProgresses, accountCurrency, countryCurrency);
    }

    private CalculateInProgressesAmountBo calculateInProgressesAmount(
            final List<AccountInProgress> accountInProgresses,
            final CurrencyEnum accountCurrency,
            final CurrencyEnum countryCurrency) {

        if (ObjectUtils.isEmpty(accountInProgresses)) {
            return null;
        }
        BigDecimal accountInProgressAmount = BigDecimal.ZERO;
        BigDecimal usdInProgressAmount = BigDecimal.ZERO;
        BigDecimal countryCurrencyInProgressAmount = BigDecimal.ZERO;
        BigDecimal exchangeRate = BigDecimal.ONE;

        // if account settle accountCurrency not equals transaction accountCurrency query rate
        for (final AccountInProgress v : accountInProgresses) {
            log.info("query account_in_progress id={}", v.getId());

            if (Sets.newHashSet(CurrencyEnum.USD, v.getInProgressCurrency(), accountCurrency)
                    .size() == 3) {
                throw StatementExceptionCode.INCONSISTENT_CURRENCY_TYPES.exception();
            }
            final HourlyExchangeRateDto dto = Stream.of(accountCurrency, v.getInProgressCurrency())
                    .filter(currency -> CurrencyEnum.USD != currency)
                    .findFirst()
                    .map(targetCurrency -> hourlyExchangeRateService.queryHourlyExchangeRate(
                            QueryHourlyExchangeRateVo.builder()
                                    .sourceCurrency(CurrencyEnum.USD)
                                    .targetCurrency(targetCurrency)
                                    .exchangeTime(LocalDateTimeUtil.nowUtc()
                                            .withMinute(0).withSecond(0).withNano(0)).build()))
                    .orElse(HourlyExchangeRateDto.builder()
                            .sourceCurrency(CurrencyEnum.USD)
                            .targetCurrency(CurrencyEnum.USD)
                            .exchangeTime(LocalDateTimeUtil.nowUtc())
                            .exchangeRate(BigDecimal.ONE)
                            .build());

            log.info("calculate account inprogress amount hourly exchange rate result={}", dto);

            //1. account currency =usd, inProgress currency=usd;  usdAmount=inAmount+ fee+ tax, accountAmount= inProgressAmount + fee + tax
            //2. account currency =usd, inProgress currency!=usd; usdAmount=inAmount/rate + fee + tax, accountAmount=inAmount/rate + fee + tax
            //3. account currency!=usd, inProgress currency=usd;  usdAmount=inAmount + fee/rate + tax/rate, accountAmount=inAmount*rate + fee +tax,
            //4. account currency!=usd, inProgress currency!=usd; usdAmount=inAmount/rate+fee/rate+ tax/rate, accountAmount=inAmount + fee + tax
            // fee and tax currency is account currency
            final BigDecimal inProgressAmount = v.getInProgressAmount();
            final BigDecimal fee = v.getFee();
            final BigDecimal tax = v.getTax();
            BigDecimal usdAmount;
            BigDecimal accountCurrencyAmount;

            final BigDecimal total = inProgressAmount.add(fee).add(tax);
            if (CurrencyEnum.USD == accountCurrency) {
                if (CurrencyEnum.USD != v.getInProgressCurrency()) {
                    usdAmount =
                            inProgressAmount.divide(dto.getExchangeRate(), 6, RoundingMode.HALF_UP)
                                    .add(fee).add(tax);
                } else {
                    usdAmount = total;
                }
                accountCurrencyAmount = usdAmount;
            } else {
                if (CurrencyEnum.USD == v.getInProgressCurrency()) {
                    accountCurrencyAmount =
                            inProgressAmount.multiply(dto.getExchangeRate()).add(fee).add(tax);
                    usdAmount = inProgressAmount.add(
                                    fee.divide(dto.getExchangeRate(), 6, RoundingMode.HALF_UP))
                            .add(tax.divide(dto.getExchangeRate(), 6, RoundingMode.HALF_UP));
                } else {
                    accountCurrencyAmount = total;
                    usdAmount =
                            inProgressAmount.divide(dto.getExchangeRate(), 6, RoundingMode.HALF_UP)
                                    .add(fee.divide(dto.getExchangeRate(), 6, RoundingMode.HALF_UP))
                                    .add(tax.divide(dto.getExchangeRate(), 6,
                                            RoundingMode.HALF_UP));
                }
            }

            accountInProgressAmount = accountInProgressAmount.add(accountCurrencyAmount);
            usdInProgressAmount = usdInProgressAmount.add(usdAmount);

            // country currency,account currency,inProgress currency,USD currency total not more than 2 kind
            // 1. country currency =inProgress currency= account currency,countryCurrencyAmount= inProgressAmount +fee +tax ;
            // 2. country currency =inProgress currency= USD currency && country currency !=account currency  countryCurrencyAmount= inProgressAmount + fee/rate + tax/rate
            // 3. country currency =inProgress currency != account currency && USD currency =account currency  countryCurrencyAmount= inProgressAmount + fee*rate + tax*rate
            if (countryCurrency == accountCurrency) {
                countryCurrencyInProgressAmount =
                        countryCurrencyInProgressAmount.add(accountInProgressAmount);
            } else {
                if (CurrencyEnum.USD != v.getInProgressCurrency()) {
                    countryCurrencyInProgressAmount = countryCurrencyInProgressAmount.add(
                            inProgressAmount.add(fee.multiply(dto.getExchangeRate()))
                                    .add(tax.multiply(dto.getExchangeRate())));
                } else {
                    countryCurrencyInProgressAmount =
                            countryCurrencyInProgressAmount.add(inProgressAmount
                                    .add(AmountUtil.division(fee, dto.getExchangeRate(), 6))
                                    .add(AmountUtil.division(tax, dto.getExchangeRate(), 6)));
                }
            }
        }
        return CalculateInProgressesAmountBo.builder()
                .usdTotalInProgressAmount(usdInProgressAmount.setScale(0, RoundingMode.HALF_UP))
                .totalInProgressAmount(accountInProgressAmount.setScale(0, RoundingMode.HALF_UP))
                .countryCurrencyTotalInProgressAmount(
                        countryCurrencyInProgressAmount.setScale(0, RoundingMode.HALF_UP))
                .accountCurrencyRealRate(exchangeRate)
                .build();
    }

}
