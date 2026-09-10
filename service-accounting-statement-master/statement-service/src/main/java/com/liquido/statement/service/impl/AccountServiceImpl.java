package com.liquido.statement.service.impl;

import static com.liquido.statement.pojo.entity.QAccount.account;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;

import com.liquido.base.enums.BizFinanceTypeEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.common.properties.StatementProperties;
import com.liquido.statement.enums.SwitchEnum;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountBo;
import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.AccountStatementBizBo;
import com.liquido.statement.pojo.bo.CalculateInProgressesAmountBo;
import com.liquido.statement.pojo.bo.DailyBillSummaryBo;
import com.liquido.statement.pojo.bo.DailyCutAccountBo;
import com.liquido.statement.pojo.bo.DailyExtractableAmountInfo;
import com.liquido.statement.pojo.bo.DepositConfigBo;
import com.liquido.statement.pojo.bo.ExchangeRateConfig;
import com.liquido.statement.pojo.bo.MerchantAccountsBo;
import com.liquido.statement.pojo.bo.TransactionSummaryBo;
import com.liquido.statement.pojo.dto.AccountBalanceDto;
import com.liquido.statement.pojo.dto.AccountBasicInfoDto;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.AccountDailyBillDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.AccountExtractableBalanceDto;
import com.liquido.statement.pojo.dto.ApprovalApplyDto;
import com.liquido.statement.pojo.dto.CountryDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.pojo.dto.ListAccountBalanceDto;
import com.liquido.statement.pojo.dto.MerchantAccountsDto;
import com.liquido.statement.pojo.dto.MerchantBalanceDto;
import com.liquido.statement.pojo.dto.PageAccountDto;
import com.liquido.statement.pojo.dto.QueryAccountTransferAmountDto;
import com.liquido.statement.pojo.dto.QueryAccountUploadTradeDataDto;
import com.liquido.statement.pojo.dto.RecalculationSwitchDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountInProgress;
import com.liquido.statement.pojo.entity.AccountStatementBiz;
import com.liquido.statement.pojo.entity.QAccount;
import com.liquido.statement.pojo.entity.QTransactionMoney;
import com.liquido.statement.pojo.entity.TransactionBiz;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.ApprovalApplyVo;
import com.liquido.statement.pojo.vo.ApprovalBatchApplyVo;
import com.liquido.statement.pojo.vo.ApprovalRollBackVo;
import com.liquido.statement.pojo.vo.BatchAddAccountVo;
import com.liquido.statement.pojo.vo.BatchQueryAccountBalanceVo;
import com.liquido.statement.pojo.vo.BillRecalculationVo;
import com.liquido.statement.pojo.vo.EditAccountVo;
import com.liquido.statement.pojo.vo.ListAccountBalanceVo;
import com.liquido.statement.pojo.vo.ListAccountConfigVo;
import com.liquido.statement.pojo.vo.ListAccountVo;
import com.liquido.statement.pojo.vo.PageAccountVo;
import com.liquido.statement.pojo.vo.QueryAccountExtractableBalanceVo;
import com.liquido.statement.pojo.vo.QueryAccountInfoVo;
import com.liquido.statement.pojo.vo.QueryAccountTransferAmountVo;
import com.liquido.statement.pojo.vo.QueryAccountUploadTradeDataVo;
import com.liquido.statement.pojo.vo.QueryAccountVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateListVo;
import com.liquido.statement.pojo.vo.QueryHisAccountDailyBillVo;
import com.liquido.statement.pojo.vo.QueryHourlyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryMerchantBalanceInfoVo;
import com.liquido.statement.pojo.vo.QueryMerchantCountryVo;
import com.liquido.statement.pojo.vo.QuerySubtractInProgressAmountAccountVo;
import com.liquido.statement.pojo.vo.QueryUniqueAccountVo;
import com.liquido.statement.repository.AccountRepository;
import com.liquido.statement.service.AccountConfigService;
import com.liquido.statement.service.AccountDailyBillService;
import com.liquido.statement.service.AccountDailyInitService;
import com.liquido.statement.service.AccountInProgressService;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.AccountStatementBizService;
import com.liquido.statement.service.AccountStatementService;
import com.liquido.statement.service.BalanceSnapshotService;
import com.liquido.statement.service.DailyExchangeRateService;
import com.liquido.statement.service.HourlyExchangeRateService;
import com.liquido.statement.service.TransactionMoneyService;
import com.liquido.statement.service.TransactionSummaryService;
import com.liquido.statement.service.monitor.lark.LarkRobotMonitor;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphUtils;
import com.github.wenhao.jpa.Specifications;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final ModelMapper modelMapper;
    private final BaseService baseService;
    private final EntityManager entityManager;
    private final RedisDistLock redisDistLock;
    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final LarkRobotMonitor larkRobotMonitor;
    private final AccountRepository accountRepository;
    private final AccountConfigService accountConfigService;
    private final BalanceSnapshotService balanceSnapshotService;
    private final AccountDailyInitService accountDailyInitService;
    private final AccountDailyBillService accountDailyBillService;
    private final AccountStatementService accountStatementService;
    private final TransactionMoneyService transactionMoneyService;
    private final AccountInProgressService accountInProgressService;
    private final TransactionSummaryService transactionSummaryService;
    private final HourlyExchangeRateService hourlyExchangeRateService;
    private final AccountStatementBizService accountStatementBizService;
    private final StatementProperties.ApprovalProperties approvalProperties;


    @Qualifier("balanceExecutor")
    private final Executor balanceExecutor;

    @Lazy
    @Autowired
    private DailyExchangeRateService dailyExchangeRateService;

    @Override
    public List<AccountDto> batchAddAccount(final BatchAddAccountVo vo) {

        // check merchant info
        baseService.getMerchantById(vo.getMerchantId());

        final List<Account> accounts = vo.getListVo().stream().map(v -> {
            final Account account = BeanCopierUtil.copyProperties(v, Account.class);
            account.setMerchantId(vo.getMerchantId());
            account.setLatestDailyBalance(BigDecimal.ZERO);
            account.setSubTotalAmount(BigDecimal.ZERO);
            account.setSubTotalCount(0L);
            account.setLatestDailyExtractableBalance(BigDecimal.ZERO);
            account.setExtractableBalance(BigDecimal.ZERO);
            account.setFrozenAmount(BigDecimal.ZERO);
            account.setExchangeAmount(BigDecimal.ZERO);
            final LocalDateTime now = LocalDateTimeUtil.nowUtc();
            account.setCreatedTime(now);
            account.setUpdatedTime(now);
            account.setVersion(1L);
            return account;
        }).collect(Collectors.toList());

        return toDto(accountRepository.saveAll(accounts));
    }

    @Override
    public void update(final Long id, final EditAccountVo vo) {
        final Account bean = requireOne(id);
        bean.setTimezone(vo.getTimezone());
        bean.setTimezoneName(vo.getTimezoneName());
        bean.setHoldingLimit(vo.getHoldingLimit());
        bean.setUpdatedTime(LocalDateTimeUtil.nowUtc());
        accountRepository.save(bean);
    }

    @Override
    public List<AccountBasicInfoDto> queryAllAccountBasicInfo() {
        return BeanCopierUtil.copyPropertyList(accountRepository.findAll(),
                AccountBasicInfoDto.class);
    }

    @Override
    public List<AccountDto> queryAllAccount() {
        return toDto(accountRepository.findAll((root, query, builder) -> null,
                EntityGraphUtils.fromName("accountConfig", false)));
    }

    @Override
    public AccountDto queryAccountByIdentifier(
            final String accountIdentifier,
            final String merchantCode,
            final CountryCodeEnum countryCode,
            final TransactionTypeCodeEnum transactionTypeCode
    ) {
        try {
            // Cache lookup
            log.info("queryAccountByIdentifier: accountIdentifier={}", accountIdentifier);
            AccountDto accountDto = redisCacheUtil.getCacheMapValue(
                    CacheConstant.ACCOUNT_INFO_HASH, accountIdentifier);

            log.info("queryAccountByIdentifier: cache accountDto={}", accountDto);
            if (Objects.nonNull(accountDto)) {
                return accountDto;
            }

            // Retrieve db account
            log.info("queryAccountByIdentifier: accountIdentifier={}, "
                            + "merchantCode={}, countryCode={}, transactionTypeCode={}",
                    accountIdentifier, merchantCode, countryCode, transactionTypeCode);

            final MerchantDto merchant = baseService.getMerchantByCode(merchantCode);
            log.info("queryAccountByIdentifier: merchant={}", merchant);

            accountDto = accountRepository.findOne(Specifications.<Account>and()
                                    .eq("merchantId", merchant.getId())
                                    .eq("countryCode", countryCode)
                                    .eq("transactionTypeCode", transactionTypeCode).build()
                            , EntityGraphUtils.fromName("accountConfig", false))
                    .map(this::toDto)
                    .orElseThrow(
                            () -> StatementExceptionCode.ACCOUNT_NOT_FOUND_BY_ACCOUNT_IDENTIFIER
                                    .exception(accountIdentifier));
            log.info("queryAccountByIdentifier: accountDto={}", accountDto);

            // Cache set
            log.info("queryAccountByIdentifier: set cache accountIdentifier={}", accountIdentifier);
            redisCacheUtil.setCacheMapValue(
                    CacheConstant.ACCOUNT_INFO_HASH, accountIdentifier, accountDto);
            log.info("queryAccountByIdentifier: set cache success accountIdentifier={}",
                    accountIdentifier);
            return accountDto;
        } catch (Exception e) {
            log.error("queryAccountByIdentifier error: {}", e.getMessage());
            throw StatementExceptionCode.ACCOUNT_NOT_FOUND_BY_ACCOUNT_IDENTIFIER.exception(
                    accountIdentifier);
        }
    }

    @Override
    public List<AccountDto> listAccount(final ListAccountVo vo) {
        return toDto(accountRepository.findAll(getSpecification(vo),
                EntityGraphUtils.fromName("accountConfig", false)));
    }

    public Specification<Account> getSpecification(final ListAccountVo vo) {
        return Specifications.<Account>and().eq(Objects.nonNull(vo.getId()), "id", vo.getId())
                .in(CollectionUtils.isNotEmpty(vo.getIds()), "id",
                        ListUtils.emptyIfNull(vo.getIds()).toArray())
                .eq(Objects.nonNull(vo.getMerchantId()), "merchantId", vo.getMerchantId())
                .in(CollectionUtils.isNotEmpty(vo.getMerchantIds()), "merchantId",
                        ListUtils.emptyIfNull(vo.getMerchantIds()).toArray())
                .eq(Objects.nonNull(vo.getCountryCode()), "countryCode", vo.getCountryCode())
                .eq(Objects.nonNull(vo.getTransactionTypeCode()), "transactionTypeCode",
                        vo.getTransactionTypeCode())
                .eq(Objects.nonNull(vo.getCurrency()), "currency", vo.getCurrency())
                .eq(Objects.nonNull(vo.getTimezone()), "timezone", vo.getTimezone())
                .like(Objects.nonNull(vo.getTimezoneName()), "timezoneName", vo.getTimezoneName())
                .build();
    }

    @Override
    public void saveBalanceSnapshot() {
        final long begin = System.currentTimeMillis();
        log.info("process save balance snapshot begin");
        try {
            final QAccount account = QAccount.account;
            final List<Account> accountList = jpaQueryFactory.select(account)
                    .from(account)
                    .setHint("javax.persistence.fetchgraph",
                            entityManager.getEntityGraph("accountConfig"))
                    .orderBy((account.latestDailyBalance.add(account.subTotalAmount)).desc())
                    .fetch();

            if (CollectionUtils.isEmpty(accountList)) {
                return;
            }

            final List<CompletableFuture<String>> futureList = accountList.stream()
                    .map(acc -> CompletableFuture.supplyAsync(() ->
                            processAccountBalanceSnapshot(acc), balanceExecutor))
                    .collect(Collectors.toList());

            // Waiting for all asynchronous tasks to complete
            CompletableFuture.allOf(
                    futureList.toArray(new CompletableFuture[accountList.size()])).join();

            // Collect all results
            final List<String> resultList = futureList.stream()
                    .map(CompletableFuture::join)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(resultList)) {
                larkRobotMonitor.error("Save Balance Snapshot Error",
                        StringUtils.join(resultList, "\n\n"), "");
            }
        } finally {
            log.info("process save balance snapshot end ts:{}ms",
                    System.currentTimeMillis() - begin);
        }
    }

    private String processAccountBalanceSnapshot(final Account acc) {
        final long begin = System.currentTimeMillis();
        try {
            final String lockVal = DataUtil.getUuid();
            if (!redisDistLock.tryLock(
                    String.format(CacheConstant.ACCOUNT_BALANCE_SNAPSHOT, acc.getId()),
                    lockVal, 10, TimeUnit.SECONDS)) {
                return "";
            }

            balanceSnapshotService.saveOrUpdate(this.mapAccountToPageDto(acc));
            return "";
        } catch (Exception e) {
            log.error("save balance snapshot error: {}, accountId={}",
                    e.getMessage(), acc.getId(), e);
            return String.format("save balance snapshot error \\n\\n"
                            + "**MerchantId:** %s\\n"
                            + "**Country:** %s\\n"
                            + "**Transaction Type:** %s\\n"
                            + "**Error message:** %s\\n",
                    acc.getMerchantId(),
                    acc.getCountryCode().getCode(),
                    acc.getTransactionTypeCode().getCode(),
                    e.getMessage());
        } finally {
            log.info("process a account balance snapshot end ts:{}ms",
                    System.currentTimeMillis() - begin);
        }
    }


    @Override
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
        dto.setId(account.getId());

        return dto;
    }

    @Override
    public AccountDto getEffectiveAccountInfo(final QueryAccountVo vo) {
        return toDto(this.getById(vo.getId()));
    }

    @Override
    public AccountBo findById(final Long id) {
        return modelMapper.convertBo(getById(id));
    }

    @Override
    public AccountBo findAccountInfoById(final Long accountId) {
        return modelMapper.convertBo(accountRepository.findByIdAndAndDelFlagIn(
                accountId, List.of(Boolean.TRUE, Boolean.FALSE)));
    }

    @Override
    public Account getById(final Long id) {
        return accountRepository.findById(id, EntityGraphUtils.fromName("accountConfig", false))
                .orElseThrow(StatementExceptionCode.UNKNOWN_ACCOUNT::exception);
    }

    @Override
    public List<Account> findByIds(final Collection<Long> ids) {
        return StreamSupport.stream(accountRepository.findAllById(
                        new HashSet<>(ids),
                        EntityGraphUtils.fromName("accountConfig", false)).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyCutAccountBo> findAllAccountForDailyCut() {
        final QBean<DailyCutAccountBo> bean = Projections.fields(DailyCutAccountBo.class,
                account.id.as("accountId"),
                account.timezone,
                account.countryCode,
                account.transactionTypeCode);

        return jpaQueryFactory.select(bean)
                .from(account)
                .where(account.delFlag.eq(Boolean.FALSE))
                .fetch();
    }

    @Override
    public AccountDto queryByMerchantIdAndAccountId(final QueryAccountInfoVo vo) {
        return toDto(this.findByMerchantIdAndAccountId(vo));
    }

    @Override
    public Account findByMerchantIdAndAccountId(final QueryAccountInfoVo vo) {
        final Account account = requireOne(vo.getAccountId());
        if (!account.getMerchantId().equals(vo.getMerchantId())) {
            throw StatementExceptionCode.UNKNOWN_ACCOUNT.exception();
        }
        return account;
    }

    @Override
    public void setRecalculationSwitch(final BillRecalculationVo vo) {
        if (CollectionUtils.isEmpty(vo.getAccountIdList())) {
            return;
        }
        final Map<String, SwitchEnum> map = Maps.newHashMap();
        for (final Long accountId : vo.getAccountIdList()) {
            map.put(accountId.toString(), vo.getSwitchState());
        }
        redisCacheUtil.setCacheMap(CacheConstant.BILL_RECALCULATION_SWITCH, map);
    }

    @Override
    public RecalculationSwitchDto getRecalculationSwitch(final Long accountId) {
        final RecalculationSwitchDto dto =
                redisCacheUtil.getCacheMapValue(CacheConstant.BILL_RECALCULATION_SWITCH,
                        accountId.toString());
        if (Objects.nonNull(dto)) {
            return dto;
        }
        return RecalculationSwitchDto.builder().switchState(SwitchEnum.OFF).build();
    }

    @Override
    public AccountBalanceDto queryAccountBalanceInfo(final QueryAccountInfoVo vo) {

        // Check recalculation switch state
        final RecalculationSwitchDto switchDto = this.getRecalculationSwitch(vo.getAccountId());
        if (Objects.nonNull(switchDto) && SwitchEnum.ON == switchDto.getSwitchState()) {
            throw StatementExceptionCode.TRANSACTION_RECALCULATION.exception();
        }

        final Account account = this.findByMerchantIdAndAccountId(vo);
        final AccountBalanceDto infoDto = new AccountBalanceDto();
        infoDto.setUtcTime(LocalDateTimeUtil.nowUtc());
        infoDto.setAccountId(vo.getAccountId());
        infoDto.setTransactionTypeCode(account.getTransactionTypeCode());
        infoDto.setCurrency(account.getCurrency());

        // The payin account
        if (account.getTransactionTypeCode() == TransactionTypeCodeEnum.PAY_IN) {
            // accountTotalBalance = dailyBalance + subTotalAmount
            infoDto.setTotalBalance(
                    account.getLatestDailyBalance().add(account.getSubTotalAmount()));

            infoDto.setExtractableAmount(
                    account.getExtractableBalance().compareTo(BigDecimal.ZERO) <= 0
                            ? BigDecimal.ZERO : account.getExtractableBalance());

            infoDto.setPendingAmount(transactionMoneyService.getPendingBalance(account));
            infoDto.setHoldingAmount(transactionMoneyService.getHoldingBalance(account.getId()));
        } else {
            infoDto.setTotalBalance(account.getExtractableBalance());
        }
        return infoDto;
    }

    /**
     * Merchant applies for withdrawal account(PAY-IN、PAY-OUT) withdrawal/exchange apply
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ApprovalApplyDto approvalApply(final ApprovalApplyVo vo) {

        /* data pre-check repeat request (prevent duplicate submissions) */
        this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER, vo.getRequestId(), 7,
                TimeUnit.DAYS);

        if (Objects.isNull(vo.getAmount()) || vo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL.exception("applyAmount illegal");
        }

        final Account account = this.findByMerchantIdAndAccountId(
                QueryAccountInfoVo.builder().merchantId(vo.getMerchantId())
                        .accountId(vo.getAccountId()).build());

        final CalculateInProgressesAmountBo bo =
                queryAccountInProgressAndCalculateInProgressesAmount(account.getId(),
                        account.getCurrency(),
                        account.getCountryCode().getCurrency());

        final BigDecimal withdrawalAmount = account.getExtractableBalance()
                .subtract(Optional.ofNullable(bo)
                        .map(CalculateInProgressesAmountBo::getTotalInProgressAmount)
                        .orElse(BigDecimal.ZERO));

        final boolean overWithdrawalAmountFlag = getOverWithdrawalFlag(vo.getAccountId());
        // overWithdrawalAmountFlag true,  withdrawalAmount= current balance - ongoing withdrawal
        if (overWithdrawalAmountFlag
                && account.getLatestDailyBalance().add(account.getSubTotalAmount())
                .subtract(account.getFrozenAmount().subtract(withdrawalAmount))
                .compareTo(vo.getAmount()) < 0) {

            throw StatementExceptionCode.INSUFFICIENT_EXTRACTABLE_BALANCE.exception(
                    AmountUtil.centToYuan(account.getExtractableBalance()), account.getCurrency());
        }

        // overWithdrawalAmountFlag false,  withdrawalAmount= withdrawal amount
        if (!overWithdrawalAmountFlag && withdrawalAmount.compareTo(vo.getAmount()) < 0) {
            throw StatementExceptionCode.INSUFFICIENT_EXTRACTABLE_BALANCE.exception(
                    AmountUtil.centToYuan(account.getExtractableBalance()), account.getCurrency());
        }

        final String lockVal = DataUtil.getUuid();
        final String lockKey = CacheConstant.ACCOUNT_WITHDRAWAL_APPLY_LOCK + account.getId();
        try {
            if (!redisDistLock.tryLock(lockKey, lockVal, 2, TimeUnit.HOURS)) {
                log.error("get and locked account fail: accountId={}", account.getId());
                throw StatementExceptionCode.GET_ACCOUNT_LOCKED_FAIL.exception();
            }

            // process frozen extractable balance
            if (!this.frozenExtractableBalance(account, vo.getBizTypeCode(), vo.getAmount())) {
                throw StatementExceptionCode.ACCOUNT_FROZEN_EXTRACTABLE_BALANCE_FAIL.exception();
            }

            final AccountStatementBiz order = accountStatementBizService.saveAccountStatementBiz(
                    this.buildApplyAccountStatementBiz(account, vo));

            return ApprovalApplyDto.builder().transactionId(order.getTransactionId()).build();
        } finally {
            redisDistLock.unlock(lockKey, lockVal);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchWithdrawalFrozenAmount(final ApprovalBatchApplyVo vo) {

        log.info("batch frozen amount begin, batchId: {}, frozen amount: {}, count: {}",
                vo.getBatchId(), vo.getTotalAmount(), vo.getDataList().size());

        if (vo.getDataList().stream().anyMatch(v -> Optional.ofNullable(v.getAmount())
                .orElse(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) <= 0)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL.exception("applyAmount illegal");
        }

        final Account account = this.findByMerchantIdAndAccountId(
                QueryAccountInfoVo.builder().merchantId(vo.getMerchantId())
                        .accountId(vo.getAccountId()).build());

        final CalculateInProgressesAmountBo bo =
                queryAccountInProgressAndCalculateInProgressesAmount(account.getId(),
                        account.getCurrency(),
                        account.getCountryCode().getCurrency());

        final BigDecimal withdrawalAmount = account.getExtractableBalance()
                .subtract(Optional.ofNullable(bo)
                        .map(CalculateInProgressesAmountBo::getTotalInProgressAmount)
                        .orElse(BigDecimal.ZERO));

        final boolean overWithdrawalAmountFlag = getOverWithdrawalFlag(vo.getAccountId());
        // overWithdrawalAmountFlag true,  withdrawalAmount= current balance - ongoing withdrawal
        if (overWithdrawalAmountFlag
                && account.getLatestDailyBalance().add(account.getSubTotalAmount())
                .subtract(account.getFrozenAmount().subtract(withdrawalAmount))
                .compareTo(vo.getTotalAmount()) < 0) {

            throw StatementExceptionCode.INSUFFICIENT_EXTRACTABLE_BALANCE.exception(
                    AmountUtil.centToYuan(account.getExtractableBalance()), account.getCurrency());
        }

        // overWithdrawalAmountFlag false,  withdrawalAmount= withdrawal amount
        if (!overWithdrawalAmountFlag && withdrawalAmount.compareTo(vo.getTotalAmount()) < 0) {
            throw StatementExceptionCode.INSUFFICIENT_EXTRACTABLE_BALANCE.exception(
                    AmountUtil.centToYuan(account.getExtractableBalance()), account.getCurrency());
        }

        final String lockVal = DataUtil.getUuid();
        final String lockKey = CacheConstant.ACCOUNT_WITHDRAWAL_APPLY_LOCK + account.getId();
        try {
            if (!redisDistLock.tryLock(lockKey, lockVal, 2, TimeUnit.HOURS)) {
                log.error("get and locked account fail: accountId={}", account.getId());
                throw StatementExceptionCode.GET_ACCOUNT_LOCKED_FAIL.exception();
            }

            // process frozen extractable balance
            if (!this.frozenExtractableBalance(account, vo.getBizTypeCode(), vo.getTotalAmount())) {
                throw StatementExceptionCode.ACCOUNT_FROZEN_EXTRACTABLE_BALANCE_FAIL.exception();
            }

            accountStatementBizService.saveAccountStatementBiz(
                    this.batchBuildApplyAccountStatementBiz(account, vo));
        } finally {
            redisDistLock.unlock(lockKey, lockVal);
        }
    }

    private AccountStatementBizBo buildApplyAccountStatementBiz(
            final Account account,
            final ApprovalApplyVo biz
    ) {
        final LocalDate billDate = LocalDateTimeUtil.nowUtcZonedDateTime()
                .withZoneSameInstant(ZoneId.of(account.getTimezone())).toLocalDate();
        final AccountDailyInitBo billBo =
                accountDailyInitService.getDailyBillInitInfo(account.getId(), billDate);

        return AccountStatementBizBo.builder()
                .requestId(biz.getRequestId())
                .transactionId(SnowflakeIdUtil.generate())
                .merchantId(biz.getMerchantId())
                .subMerchantId(StringUtils.defaultIfBlank(biz.getSubMerchantId(), ""))
                .accountId(biz.getAccountId())
                .billId(billBo.getBillId())
                .businessType(biz.getBizTypeCode())
                .financeType(BizFinanceTypeEnum.PRE_FREEZING)
                .transactionTime(LocalDateTimeUtil.nowUtc())
                .extractableAmount(biz.getAmount().negate())
                .frozenAmount(BusinessTypeEnum.TRANSFER_OUT == biz.getBizTypeCode()
                        ? biz.getAmount() : BigDecimal.ZERO)
                .exchangeAmount(BusinessTypeEnum.EXCHANGE == biz.getBizTypeCode()
                        ? biz.getAmount() : BigDecimal.ZERO)
                .currency(account.getCurrency())
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(0L).updatedBy(0L).version(1)
                .delFlag(Boolean.FALSE)
                .remark("Approval Apply:" + biz.getBizTypeCode().getCode())
                .build();
    }

    private List<AccountStatementBizBo> batchBuildApplyAccountStatementBiz(
            final Account account, final ApprovalBatchApplyVo biz) {

        final LocalDate billDate = LocalDateTimeUtil.nowUtcZonedDateTime()
                .withZoneSameInstant(ZoneId.of(account.getTimezone())).toLocalDate();
        final AccountDailyInitBo billBo =
                accountDailyInitService.getDailyBillInitInfo(account.getId(), billDate);

        return biz.getDataList().stream()
                .map(v -> AccountStatementBizBo.builder()
                        .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                        .transactionId(v.getTransactionId())
                        .merchantId(biz.getMerchantId())
                        .accountId(biz.getAccountId())
                        .billId(billBo.getBillId())
                        .businessType(biz.getBizTypeCode())
                        .financeType(BizFinanceTypeEnum.PRE_FREEZING)
                        .transactionTime(LocalDateTimeUtil.nowUtc())
                        .extractableAmount(v.getAmount().negate()).frozenAmount(
                                BusinessTypeEnum.TRANSFER_OUT == biz.getBizTypeCode()
                                        ? v.getAmount() : BigDecimal.ZERO).exchangeAmount(
                                BusinessTypeEnum.EXCHANGE == biz.getBizTypeCode() ? v.getAmount() :
                                        BigDecimal.ZERO).currency(account.getCurrency())
                        .createdTime(LocalDateTimeUtil.nowUtc())
                        .updatedTime(LocalDateTimeUtil.nowUtc())
                        .createdBy(0L).updatedBy(0L).version(1).delFlag(Boolean.FALSE)
                        .remark("Approval Apply:" + biz.getBizTypeCode().getCode()).build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void approvalRollBack(final ApprovalRollBackVo vo) {

        // TODO Rollback order by transactionId required;

        /* data pre-check repeat request (prevent duplicate submissions) */
        this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER, vo.getRequestId(), 7,
                TimeUnit.DAYS);

        if (Objects.isNull(vo.getAmount()) || vo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL.exception("applyAmount illegal");
        }

        final Account account = this.findByMerchantIdAndAccountId(
                QueryAccountInfoVo.builder().merchantId(vo.getMerchantId())
                        .accountId(vo.getAccountId()).build());

        final String lockVal = DataUtil.getUuid();
        final String lockKey = CacheConstant.ACCOUNT_WITHDRAWAL_APPLY_LOCK + account.getId();
        try {
            if (!redisDistLock.tryLock(lockKey, lockVal, 2, TimeUnit.HOURS)) {
                log.error("get and locked account fail: accountId={}", account.getId());
                throw StatementExceptionCode.GET_ACCOUNT_LOCKED_FAIL.exception();
            }

            // process unfrozen extractable amount
            if (!this.unfrozeExtractableBalance(vo.getBizTypeCode(), account, vo.getAmount())) {
                throw StatementExceptionCode.ACCOUNT_UNFROZEN_EXTRACTABLE_BALANCE_FAIL.exception();
            }

            accountStatementBizService.saveAccountStatementBiz(
                    this.buildRollBackAccountStatementBiz(account, vo));

        } finally {
            redisDistLock.unlock(lockKey, lockVal);
        }
    }

    private AccountStatementBizBo buildRollBackAccountStatementBiz(
            final Account account,
            final ApprovalRollBackVo biz
    ) {
        final LocalDate billDate = LocalDateTimeUtil.nowUtcZonedDateTime()
                .withZoneSameInstant(ZoneId.of(account.getTimezone())).toLocalDate();
        final AccountDailyInitBo billBo =
                accountDailyInitService.getDailyBillInitInfo(account.getId(), billDate);

        return AccountStatementBizBo.builder()
                .requestId(biz.getRequestId())
                .transactionId(biz.getTransactionId())
                .merchantId(biz.getMerchantId())
                .subMerchantId(StringUtils.defaultIfBlank(biz.getSubMerchantId(), ""))
                .accountId(biz.getAccountId())
                .billId(billBo.getBillId())
                .businessType(biz.getBizTypeCode())
                .financeType(BizFinanceTypeEnum.UNFREEZE)
                .transactionTime(LocalDateTimeUtil.nowUtc())
                .extractableAmount(biz.getAmount())
                .frozenAmount(BusinessTypeEnum.TRANSFER_OUT == biz.getBizTypeCode()
                        ? biz.getAmount().negate() : BigDecimal.ZERO)
                .exchangeAmount(BusinessTypeEnum.EXCHANGE == biz.getBizTypeCode()
                        ? biz.getAmount().negate() : BigDecimal.ZERO)
                .currency(account.getCurrency())
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(0L)
                .updatedBy(0L)
                .version(1)
                .delFlag(Boolean.FALSE)
                .remark("Approval RollBack:" + biz.getBizTypeCode().getCode()).build();
    }

    @Override
    public AccountExtractableBalanceDto queryAccountExtractableBalance(
            final QueryAccountExtractableBalanceVo vo
    ) {
        final Account accountInfo = jpaQueryFactory.select(account)
                .from(account)
                .where(account.merchantId.eq(vo.getMerchantId())
                        .and(account.countryCode.eq(vo.getCountryCode()))
                        .and(account.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_IN)))
                .setHint("javax.persistence.fetchgraph",
                        entityManager.getEntityGraph("accountConfig"))
                .limit(1)
                .fetchOne();
        if (Objects.isNull(accountInfo)) {
            throw StatementExceptionCode.UNKNOWN_ACCOUNT.exception();
        }

        return AccountExtractableBalanceDto.builder().merchantId(accountInfo.getMerchantId())
                .accountId(accountInfo.getId()).countryCode(accountInfo.getCountryCode())
                .transactionTypeCode(accountInfo.getTransactionTypeCode())
                .latestDailyBalance(accountInfo.getLatestDailyBalance())
                .subTotalAmount(accountInfo.getSubTotalAmount()).totalBalance(
                        accountInfo.getLatestDailyBalance().add(accountInfo.getSubTotalAmount()))
                .extractableBalance(accountInfo.getExtractableBalance())
                .frozenAmount(accountInfo.getFrozenAmount()).currency(accountInfo.getCurrency())
                .build();
    }

    @Override
    public List<CountryDto> findAccountCountryCode(final Long merchantId) {
        final List<Account> accountList = accountRepository.findByMerchantId(merchantId);
        return accountList.stream().distinct()
                .map(x -> CountryDto.builder().code(x.getCountryCode().getCode())
                        .name(x.getCountryCode().getCountryName()).timezone(x.getTimezone())
                        .timezoneName(x.getTimezoneName()).currency(x.getCurrency()).build())
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto queryUniqueAccount(final QueryUniqueAccountVo vo) {
        final Account accountInfo = jpaQueryFactory.select(account)
                .from(QAccount.account)
                .where(account.merchantId.eq(vo.getMerchantId())
                        .and(account.countryCode.eq(vo.getCountryCode()))
                        .and(account.transactionTypeCode.eq(vo.getTransactionTypeCode())))
                .setHint("javax.persistence.fetchgraph",
                        entityManager.getEntityGraph("accountConfig"))
                .fetchOne();
        return Optional.ofNullable(accountInfo).map(this::toDto).orElse(null);
    }

    @Override
    public boolean processAccountDailyCut(final DailyBillSummaryBo summary) {

        final String lockVal = DataUtil.getUuid();
        final Long accountId = summary.getAccount().getId();
        try {
            /* Step1: Get account Lock
             * (Mainly to get the latest version information);
             */
            final Account account = this.getAccountLocked(accountId, lockVal);
            final DailyExtractableAmountInfo deaInfo = summary.getDailyExtractableAmountInfo();

            /* Step2: update account balance */
            final QAccount entity = QAccount.account;
            final long result = jpaQueryFactory.update(entity)

                    /* transaction account bill's book */
                    .set(entity.latestDailyBalance,
                            entity.latestDailyBalance.add(summary.getLatestDailyOccurredAmount()))
                    .set(entity.subTotalAmount,
                            entity.subTotalAmount.subtract(summary.getLatestDailyOccurredAmount()))
                    .set(entity.subTotalCount,
                            entity.subTotalCount.subtract(summary.getLatestDailyOccurredCount()))

                    /* extractable balance bill's book */
                    .set(entity.extractableBalance,
                            entity.extractableBalance.add(Optional.ofNullable(
                                            deaInfo.getCurrentDailyTnExtractableAmount())
                                    .orElse(BigDecimal.ZERO)))
                    .set(entity.latestDailyExtractableBalance,
                            entity.latestDailyExtractableBalance.add(Optional.ofNullable(
                                            deaInfo.getLatestDailyOccurredExtractableAmount())
                                    .orElse(BigDecimal.ZERO)))

                    .set(entity.version, entity.version.add(1))
                    .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                    .where(entity.id.eq(account.getId())
                            .and(entity.version.eq(account.getVersion()))).execute();

            log.info("Execute update account after daily-cut completed. accountId={}, result={}",
                    account.getId(), (result > 0));

            return result > 0;
        } finally {
            this.releaseAccountLock(accountId, lockVal);
        }
    }

    @Override
    public List<AccountBalanceDto> findAccountBalanceInfo(final QueryMerchantCountryVo vo) {
        final Long merchantId = vo.getMerchantId();

        // search balance from db
        final Specification<Account> specification = Specifications.<Account>and()
                .eq("merchantId", merchantId)
                .eq("countryCode", vo.getCountryCode())
                .eq(Objects.nonNull(vo.getTransactionTypeCode()), "transactionTypeCode",
                        vo.getTransactionTypeCode()).build();

        final List<Account> accountList = accountRepository.findAll(specification);

        final LocalDateTime utcTime = LocalDateTimeUtil.nowUtc();
        final List<AccountBalanceDto> balanceDtoList = Lists.newArrayList();
        for (final Account account : accountList) {
            final AccountBalanceDto infoDto = new AccountBalanceDto();
            infoDto.setAccountId(account.getId());
            infoDto.setTransactionTypeCode(account.getTransactionTypeCode());

            infoDto.setExtractableAmount(Objects.nonNull(account.getExtractableBalance())
                    ? account.getExtractableBalance() : BigDecimal.ZERO);

            // payin:dailyBalance + subTotalAmount  payout、marketplace:extractableBalance
            infoDto.setTotalBalance(
                    account.getTransactionTypeCode() == TransactionTypeCodeEnum.PAY_IN
                            ? account.getLatestDailyBalance().add(account.getSubTotalAmount()) :
                            infoDto.getExtractableAmount());

            infoDto.setFrozenAmount(Objects.nonNull(account.getFrozenAmount())
                    ? account.getFrozenAmount() : BigDecimal.ZERO);

            infoDto.setExchangeAmount(Objects.nonNull(account.getExchangeAmount())
                    ? account.getExchangeAmount() : BigDecimal.ZERO);

            infoDto.setCurrency(account.getCurrency());
            infoDto.setUtcTime(utcTime);

            final List<TransactionSummaryBo> transactionSummary =
                    transactionSummaryService.queryLast2DayTransactionSummary(account.getId());

            final LocalDate nowDay = LocalDateTimeUtil.nowUtcZonedDateTime()
                    .withZoneSameInstant(ZoneId.of(account.getTimezone())).toLocalDate();

            // today transaction summary
            final TransactionSummaryBo todaySummary = transactionSummary.stream()
                    .filter(item -> nowDay.equals(item.getTransactionDate())).findAny()
                    .orElse(null);
            // yesterday transaction summary
            final TransactionSummaryBo yesterdaySummary = transactionSummary.stream()
                    .filter(item -> nowDay.minusDays(1).equals(item.getTransactionDate())).findAny()
                    .orElse(null);

            infoDto.setRealTimeTransactionsCount(
                    Objects.nonNull(todaySummary) ? todaySummary.getTransactionCount() : 0L);

            infoDto.setRealTimeTransactionsAmount(Objects.nonNull(todaySummary)
                    ? todaySummary.getSettlementAmount().add(todaySummary.getFeeAmount())
                    .add(todaySummary.getTaxAmount()) : BigDecimal.ZERO);

            infoDto.setYesterdayTransactionsCount(Objects.nonNull(yesterdaySummary)
                    ? yesterdaySummary.getTransactionCount() : 0L);

            infoDto.setYesterdayTransactionsAmount(Objects.nonNull(yesterdaySummary)
                    ? yesterdaySummary.getSettlementAmount().add(yesterdaySummary.getFeeAmount())
                    .add(yesterdaySummary.getTaxAmount()) : BigDecimal.ZERO);

            final AccountDailyBillDto latestDailyBill = this.getLatestAccountDailyBill(account);
            infoDto.setYesterdayBalance(
                    Objects.nonNull(latestDailyBill) ? latestDailyBill.getEndBalance() :
                            BigDecimal.ZERO);

            infoDto.setTodayDate(nowDay);
            infoDto.setLatestBillDate(nowDay.minusDays(1));

            final AccountBalanceDto.PendBusiness pend = new AccountBalanceDto.PendBusiness();
            pend.setExchangeAmount(
                    Optional.ofNullable(account.getExchangeAmount()).orElse(BigDecimal.ZERO));

            pend.setFrozenAmount(
                    Optional.ofNullable(account.getFrozenAmount()).orElse(BigDecimal.ZERO));

            final CalculateInProgressesAmountBo bo =
                    queryAccountInProgressAndCalculateInProgressesAmount(account.getId(),
                            account.getCurrency(),
                            account.getCountryCode().getCurrency());

            Optional.ofNullable(bo).map(CalculateInProgressesAmountBo::getTotalInProgressAmount)
                    .ifPresent(pend::setInProgressAmount);

            infoDto.setPendBusiness(pend);
            balanceDtoList.add(infoDto);
        }

        if (Objects.nonNull(vo.getTransactionTypeCode())) {
            return balanceDtoList.stream()
                    .filter(x -> x.getTransactionTypeCode().equals(vo.getTransactionTypeCode()))
                    .collect(Collectors.toList());
        }
        return balanceDtoList;
    }

    @Override
    public List<ListAccountBalanceDto> listAccountBalance(final ListAccountBalanceVo vo) {

        final List<Account> accountList =
                accountRepository.findByMerchantIdAndCountryCode(vo.getMerchantId(),
                        vo.getCountryCode());

        return queryAccountBalance(accountList);
    }

    private List<ListAccountBalanceDto> queryAccountUploadTradeData(
            final List<Account> accountList
    ) {
        if (CollectionUtils.isEmpty(accountList)) {
            return Lists.newArrayList();
        }
        log.info("Query account size={}", accountList.size());
        final LocalDateTime utcTime = LocalDateTimeUtil.nowUtc();

        final List<AccountConfigDto> accountConfigList = accountConfigService.listAccountConfig(
                ListAccountConfigVo.builder()
                        .ids(accountList.stream().map(Account::getAccountConfigId).collect(
                                Collectors.toList())).build());

        final List<AccountInProgress> accountInProgresses =
                accountInProgressService.findAllByAccountIdIn(
                        accountList.stream().map(Account::getId).collect(Collectors.toList()));

        return accountList.stream().map(account -> {
            try {
                List<AccountInProgress> targetAccountInProgress = accountInProgresses.stream()
                        .filter(inProgress -> inProgress.getAccountId().equals(account.getId()))
                        .collect(Collectors.toList());

                final ListAccountBalanceDto dto = ListAccountBalanceDto.builder()
                        .utcTime(utcTime)
                        .merchantId(account.getMerchantId())
                        .accountId(account.getId())
                        .countryCode(account.getCountryCode())
                        .transactionTypeCode(account.getTransactionTypeCode())
                        .currency(account.getCurrency())
                        .totalBalance(
                                account.getLatestDailyBalance().add(account.getSubTotalAmount()))
                        .build();

                final ListAccountBalanceDto.PendBusiness pend =
                        new ListAccountBalanceDto.PendBusiness();

                final ListAccountBalanceDto.InProgress inProgress =
                        new ListAccountBalanceDto.InProgress();

                pend.setExchangeAmount(
                        Optional.ofNullable(account.getExchangeAmount()).orElse(BigDecimal.ZERO));

                pend.setFrozenAmount(
                        Optional.ofNullable(account.getFrozenAmount()).orElse(BigDecimal.ZERO));

                //deposit amount
                final AccountConfigDto accountConfigDto = accountConfigList.stream()
                        .filter(config -> config.getId().equals(account.getAccountConfigId()))
                        .findFirst().orElse(null);

                final BigDecimal depositAmount = Optional.ofNullable(accountConfigDto)
                        .map(AccountConfigDto::getConfigData)
                        .map(AccountConfigData::getDepositConfig)
                        .map(DepositConfigBo::getAmount)
                        .orElse(BigDecimal.ZERO);

                final BigDecimal legalHoldAmount = Optional.ofNullable(accountConfigDto)
                        .map(AccountConfigDto::getConfigData)
                        .map(AccountConfigData::getDepositConfig)
                        .map(DepositConfigBo::getLegalHoldAmount)
                        .orElse(BigDecimal.ZERO);

                pend.setDepositAmount(depositAmount);

                pend.setLegalHoldAmount(legalHoldAmount);

                final CalculateInProgressesAmountBo bo =
                        calculateInProgressesAmount(targetAccountInProgress,
                                account.getCurrency(),
                                account.getCountryCode().getCurrency());

                if (Objects.nonNull(bo)) {
                    pend.setInProgressAmount(bo.getTotalInProgressAmount());
                    inProgress.setUsdInProgressAmount(bo.getUsdTotalInProgressAmount());
                    inProgress.setCountryCurrencyInProgressAmount(
                            bo.getCountryCurrencyTotalInProgressAmount());
                    inProgress.setInProgressAmount(bo.getTotalInProgressAmount());
                }
                dto.setPendingAmount(pend.getExchangeAmount().add(pend.getFrozenAmount())
                        .add(Optional.ofNullable(pend.getInProgressAmount())
                                .orElse(BigDecimal.ZERO)));

                dto.setInProgress(inProgress);
                dto.setPendBusiness(pend);

                dto.setAvailable((Objects.nonNull(account.getExtractableBalance())
                        ? account.getExtractableBalance() : BigDecimal.ZERO)
                        .subtract(Optional.ofNullable(dto.getPendBusiness().getInProgressAmount())
                                .orElse(BigDecimal.ZERO))
                        .subtract(depositAmount)
                        .subtract(legalHoldAmount));

                return dto;
            } catch (Exception e) {
                log.error("Query account balance error:", e);
                final MerchantDto merchant = baseService.getMerchantById(account.getMerchantId());

                final String msgContent =
                        String.format("Push account balance to trade-service fail\\n\\n"
                                        + "**Merchant:** %s\\n"
                                        + "**Country:** %s\\n"
                                        + "**Transaction Type:** %s\\n",
                                merchant.getCode(),
                                account.getCountryCode().getCode(),
                                account.getTransactionTypeCode().getCode());

                larkRobotMonitor.error("Push Account Balance Error", msgContent,
                        "AccountId=" + account.getId());
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }


    private List<ListAccountBalanceDto> queryAccountBalance(final List<Account> accountList) {
        try {
            log.info("query account size={}", accountList.size());
            final LocalDateTime utcTime = LocalDateTimeUtil.nowUtc();

            final List<AccountConfigDto> accountConfigList = accountConfigService.listAccountConfig(
                    ListAccountConfigVo.builder()
                            .ids(accountList.stream().map(Account::getAccountConfigId).collect(
                                    Collectors.toList())).build());
            return accountList.stream()
                    .map(account -> buildAccountBalanceInfo(account, utcTime, accountConfigList))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("query account balance error,", e);
            throw StatementExceptionCode.GET_ACCOUNT_BALANCE_ERROR.exception();
        }
    }

    private ListAccountBalanceDto buildAccountBalanceInfo(
            final Account account,
            final LocalDateTime utcTime,
            final List<AccountConfigDto> accountConfigList
    ) {
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

        // margin amount
        final BigDecimal depositAmount = Optional.ofNullable(accountConfigDto)
                .map(AccountConfigDto::getConfigData)
                .map(AccountConfigData::getDepositConfig)
                .map(t -> {
                    final BigDecimal amount =
                            Optional.ofNullable(t.getAmount()).orElse(BigDecimal.ZERO);

                    final BigDecimal legalHoldAmount =
                            Optional.ofNullable(t.getLegalHoldAmount()).orElse(BigDecimal.ZERO);
                    return amount.add(legalHoldAmount);

                }).orElse(BigDecimal.ZERO);

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
            final Account account
    ) {
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
            final ListAccountBalanceDto.InProgress inProgress
    ) {
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
            final CurrencyEnum countryCurrency
    ) {
        final List<AccountInProgress> accountInProgresses =
                accountInProgressService.findAllByAccountId(accountId);
        return calculateInProgressesAmount(accountInProgresses, accountCurrency, countryCurrency);
    }

    private CalculateInProgressesAmountBo calculateInProgressesAmount(
            final List<AccountInProgress> accountInProgresses,
            final CurrencyEnum accountCurrency,
            final CurrencyEnum countryCurrency
    ) {
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


    @Override
    public QueryAccountUploadTradeDataDto queryUploadTradeData(
            final QueryAccountUploadTradeDataVo vo
    ) {
        final List<Account> accountList =
                accountRepository.findAllByMerchantIdIn(vo.getMerchantIds());

        final List<DailyExchangeRateDto> dailyExchangeRateDtoList =
                dailyExchangeRateService.queryDailyExchangeRateFromCache(
                        QueryDailyExchangeRateListVo.builder()
                                .beginTime(vo.getLocalDateTime())
                                .endTime(vo.getLocalDateTime())
                                .accountIds(accountList.stream()
                                        .map(Account::getId)
                                        .collect(Collectors.toList())).build());

        final List<ListAccountBalanceDto> accountBalanceList =
                queryAccountUploadTradeData(accountList);

        return QueryAccountUploadTradeDataDto.builder()
                .accountBalanceList(accountBalanceList)
                .dailyExchangeRateList(dailyExchangeRateDtoList)
                .build();
    }

    private AccountDailyBillDto getLatestAccountDailyBill(final Account account) {
        final LocalDate yesterday = LocalDateTimeUtil.nowUtcZonedDateTime()
                .withZoneSameInstant(ZoneId.of(account.getTimezone())).toLocalDate()
                .minusDays(1);

        AccountDailyBillDto latestDailyBill = accountDailyBillService.queryHisAccountDailyBill(
                QueryHisAccountDailyBillVo.builder().accountId(account.getId())
                        .billDate(yesterday)
                        .build());

        if (Objects.isNull(latestDailyBill)) {
            latestDailyBill =
                    accountDailyBillService.queryLatestAccountDailyBill(account.getId());
        }

        return latestDailyBill;
    }

    @Override
    public Account getAccountLocked(final Long accountId, final String lockVal) {

        this.tryLockAccount(accountId, lockVal);

        final Account account = requireOne(accountId);
        if (Objects.isNull(account) || account.getDelFlag()) {
            log.warn("get account fail unknown account: accountId={}, account={}", accountId,
                    account);
            this.releaseAccountLock(accountId, lockVal);
            throw StatementExceptionCode.UNKNOWN_ACCOUNT.exception();
        }

        return account;
    }

    @Override
    public void tryLockAccount(final Long accountId, final String lockVal) {

        if (Objects.isNull(accountId) || accountId <= 0 || StringUtil.isBlank(lockVal)) {
            log.warn("get account fail accountId or requestId is illegal : accountId={}, "
                    + "lockVal={}", accountId, lockVal);
            throw StatementExceptionCode.GET_ACCOUNT_LOCKED_FAIL.exception();
        }

        final boolean isLocked =
                redisDistLock.tryLock(CacheConstant.ACCOUNT_LOCK_KEY + accountId, lockVal, 30,
                        TimeUnit.MINUTES);
        if (!isLocked) {
            log.warn("get and locked account fail: accountId={}, lockVal={}", accountId,
                    lockVal);
            throw StatementExceptionCode.GET_ACCOUNT_LOCKED_FAIL.exception();
        }
    }

    @Override
    public boolean releaseAccountLock(final Long accountId, final String lockVal) {
        return redisDistLock.unlock(CacheConstant.ACCOUNT_LOCK_KEY + accountId, lockVal);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean increaseAccountBalance(
            final Account account,
            final BigDecimal occurredAmount,
            final BigDecimal extractableAmount,
            final int totalCount
    ) {
        log.info(
                "begin increase account balance accountId={}, occurredAmount={}, totalCount={}",
                account.getId(), occurredAmount, totalCount);

        final QAccount entity = QAccount.account;
        final boolean result = jpaQueryFactory.update(entity)
                .set(entity.extractableBalance,
                        entity.extractableBalance.add(extractableAmount))
                .set(entity.subTotalAmount, entity.subTotalAmount.add(occurredAmount))
                .set(entity.subTotalCount, entity.subTotalCount.add(totalCount))
                .set(entity.version, entity.version.add(1))
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.id.eq(account.getId())
                        .and(entity.version.eq(account.getVersion())))
                .execute() > 0;

        log.info("end increase account balance accountId={} result={}", account.getId(),
                result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean reduceAccountBalance(
            final Account account,
            final BigDecimal occurredAmount,
            final int totalCount
    ) {
        log.info("begin reduce account balance accountId={}, amount={}", account.getId(),
                occurredAmount);
        final QAccount entity = QAccount.account;
        final boolean result = jpaQueryFactory.update(entity)
                .set(entity.extractableBalance,
                        entity.extractableBalance.subtract(occurredAmount))
                .set(entity.subTotalAmount, entity.subTotalAmount.subtract(occurredAmount))
                .set(entity.subTotalCount, entity.subTotalCount.add(1))
                .set(entity.version, entity.version.add(1))
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.id.eq(account.getId())
                        .and(entity.version.eq(account.getVersion())))
                .execute() > 0;

        log.info("end reduce account accountId={} result={}", account.getId(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean increaseExtractableBalance(final Account account, final BigDecimal amount) {
        log.info("begin increase extractable amount accountId={}, amount={}", account.getId(),
                amount);
        final QAccount entity = QAccount.account;
        final boolean result = jpaQueryFactory.update(entity)
                .set(entity.extractableBalance, entity.extractableBalance.add(amount))
                .set(entity.version, entity.version.add(1))
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.id.eq(account.getId())
                        .and(entity.version.eq(account.getVersion())))
                .execute() > 0;

        log.info("end increase extractable amount accountId={} result={}", account.getId(),
                result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean reduceFrozenAmount(
            final BusinessTypeEnum type, final Account account,
            final BigDecimal amount
    ) {
        log.info("reduce frozen amount accountId={}, amount={}", account.getId(), amount);
        final QAccount entity = QAccount.account;
        if (BusinessTypeEnum.TRANSFER_OUT == type) {
            return jpaQueryFactory.update(entity)
                    .set(entity.frozenAmount, entity.frozenAmount.subtract(amount))
                    .set(entity.subTotalAmount, entity.subTotalAmount.subtract(amount))
                    .set(entity.subTotalCount, entity.subTotalCount.add(1))
                    .set(entity.version, entity.version.add(1))
                    .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                    .where(entity.id.eq(account.getId())
                            .and(entity.version.eq(account.getVersion()))
                            .and(entity.frozenAmount.subtract(amount).goe(BigDecimal.ZERO)))
                    .execute() > 0;
        }

        if (BusinessTypeEnum.EXCHANGE == type) {
            return jpaQueryFactory.update(entity)
                    .set(entity.exchangeAmount, entity.exchangeAmount.subtract(amount))
                    .set(entity.subTotalAmount, entity.subTotalAmount.subtract(amount))
                    .set(entity.subTotalCount, entity.subTotalCount.add(1))
                    .set(entity.version, entity.version.add(1))
                    .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                    .where(entity.id.eq(account.getId())
                            .and(entity.version.eq(account.getVersion()))
                            .and(entity.exchangeAmount.subtract(amount).goe(BigDecimal.ZERO)))
                    .execute() > 0;
        }
        throw StatementExceptionCode.ACCOUNT_REDUCE_FROZEN_AMOUNT_FAIL.exception();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean unfrozenAmount(
            final Account account,
            final BigDecimal unfrozenAmount,
            final Integer approvedCount,
            final BigDecimal approvedAmount,
            final BigDecimal rejectedAmount
    ) {
        log.info("unfrozen amount accountId={}, unfrozenAmount={}, "
                        + "unfrozenAmount={}, unfrozenAmount={},",
                account.getId(), unfrozenAmount, approvedAmount, rejectedAmount);
        final QAccount entity = QAccount.account;
        return jpaQueryFactory.update(entity)
                .set(entity.frozenAmount, entity.frozenAmount.subtract(unfrozenAmount))
                .set(entity.subTotalAmount, entity.subTotalAmount.subtract(approvedAmount))
                .set(entity.extractableBalance, entity.extractableBalance.add(rejectedAmount))
                .set(entity.subTotalCount, entity.subTotalCount.add(approvedCount))
                .set(entity.version, entity.version.add(1))
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.id.eq(account.getId())
                        .and(entity.version.eq(account.getVersion()))
                        .and(entity.frozenAmount.subtract(unfrozenAmount).goe(BigDecimal.ZERO)))
                .execute() > 0;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean frozenExtractableBalance(
            final Account account,
            final BusinessTypeEnum type,
            final BigDecimal amount
    ) {
        log.info("frozen extractable balance type={}. accountId={}, amount={}",
                type, account.getId(), amount);

        final boolean overWithdrawalAmountFlag = getOverWithdrawalFlag(account.getId());
        if ((account.getLatestDailyBalance().add(account.getSubTotalAmount())
                .subtract(account.getFrozenAmount())).subtract(account.getExchangeAmount())
                .compareTo(amount) < 0 || (!overWithdrawalAmountFlag
                && account.getExtractableBalance().compareTo(amount) < 0)) {
            return false;
        }

        final QAccount entity = QAccount.account;
        BooleanExpression condition = entity.id.eq(account.getId())
                .and(entity.version.eq(account.getVersion()));

        if (!overWithdrawalAmountFlag) {
            condition = condition.and(entity.extractableBalance.subtract(amount)
                    .goe(BigDecimal.ZERO));
        } else {
            condition = condition.and(entity.latestDailyBalance.add(entity.subTotalAmount)
                    .subtract(entity.frozenAmount).subtract(entity.exchangeAmount)
                    .subtract(amount)
                    .goe(BigDecimal.ZERO));
        }

        if (BusinessTypeEnum.TRANSFER_OUT == type) {
            return jpaQueryFactory.update(entity)
                    .set(entity.extractableBalance, entity.extractableBalance.subtract(amount))
                    .set(entity.frozenAmount, entity.frozenAmount.add(amount))
                    .set(entity.version, entity.version.add(1))
                    .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                    .where(condition)
                    .execute() > 0;
        }
        if (BusinessTypeEnum.EXCHANGE == type) {
            return jpaQueryFactory.update(entity)
                    .set(entity.extractableBalance, entity.extractableBalance.subtract(amount))
                    .set(entity.exchangeAmount, entity.exchangeAmount.add(amount))
                    .set(entity.version, entity.version.add(1))
                    .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                    .where(condition)
                    .execute() > 0;
        }

        throw StatementExceptionCode.ACCOUNT_FROZEN_EXTRACTABLE_BALANCE_FAIL.exception();
    }

    @Override
    public boolean unfrozeExtractableBalance(
            final BusinessTypeEnum type,
            final Account account,
            final BigDecimal amount
    ) {
        log.info("unfrozen extractable balance type={}, accountId={}, amount={}", type,
                account.getId(), amount);

        final QAccount entity = QAccount.account;
        if (BusinessTypeEnum.TRANSFER_OUT == type) {
            return jpaQueryFactory.update(entity)
                    .set(entity.frozenAmount, entity.frozenAmount.subtract(amount))
                    .set(entity.extractableBalance, entity.extractableBalance.add(amount))
                    .set(entity.version, entity.version.add(1))
                    .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                    .where(entity.id.eq(account.getId())
                            .and(entity.version.eq(account.getVersion()))
                            .and(entity.frozenAmount.subtract(amount).goe(BigDecimal.ZERO)))
                    .execute() > 0;
        }
        if (BusinessTypeEnum.EXCHANGE == type) {
            return jpaQueryFactory.update(entity)
                    .set(entity.exchangeAmount, entity.exchangeAmount.subtract(amount))
                    .set(entity.extractableBalance, entity.extractableBalance.add(amount))
                    .set(entity.version, entity.version.add(1))
                    .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                    .where(entity.id.eq(account.getId())
                            .and(entity.version.eq(account.getVersion()))
                            .and(entity.exchangeAmount.subtract(amount).goe(BigDecimal.ZERO)))
                    .execute() > 0;
        }

        throw StatementExceptionCode.ACCOUNT_UNFROZEN_EXTRACTABLE_BALANCE_FAIL.exception();
    }

    private Boolean getOverWithdrawalFlag(final Long accountId) {
        final StatementProperties.ApprovalBizTransferOut approvalBizTransferOut =
                approvalProperties.getBizTransferOut();
        return approvalBizTransferOut.getAccountIds().contains(accountId)
                && approvalBizTransferOut.getOverWithdrawalAmount();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reduceAccountBalanceForFundCollection(
            final Account account,
            final TransactionBiz transferOut
    ) {

        final BigDecimal settleAmount = transferOut.getSettlementAmount();
        final BigDecimal startBalance =
                account.getLatestDailyBalance().add(account.getSubTotalAmount());
        final BigDecimal endBalance = startBalance.subtract(settleAmount);

        /* Step1: update account */
        if (!this.reduceFrozenAmount(transferOut.getBusinessType(), account, settleAmount)) {
            throw StatementExceptionCode.ACCOUNT_BALANCE_CHANGE_FAILED.exception();
        }

        /* Step2: save account statement flow */
        accountStatementService.saveTransactionBizAccountFlow(transferOut, settleAmount,
                startBalance, endBalance);
    }

    @Override
    public List<AccountDto> queryAllEnableHoldingAccount() {
        return toDto(jpaQueryFactory.select(account)
                .from(account)
                .where(account.holdingLimit.gt(BigDecimal.ZERO)
                        .and(account.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_IN)
                                .and(account.delFlag.eq(Boolean.FALSE))))
                .setHint("javax.persistence.fetchgraph",
                        entityManager.getEntityGraph("accountConfig"))
                .fetch());
    }

    /**
     * query after settle account  merchant balance
     * totalBalance: PAY-IN balance +PAY-OUT balance  last_daily_balance+subtotal_amount
     * withdrawableAmount: PAY-IN extractableBalance +PAY-OUT balance
     *
     * @param vo vo
     */
    @Override
    public MerchantBalanceDto queryMerchantBalanceInfo(final QueryMerchantBalanceInfoVo vo) {

        final List<Account> accountList =
                accountRepository.findByMerchantIdAndCountryCode(vo.getMerchantId(),
                        vo.getCountryCode());

        if (ObjectUtils.isEmpty(accountList)) {
            throw CommonExceptionCode.DATA_NOT_FOUND.exception();
        }

        if (accountList.stream().map(Account::getCurrency).collect(Collectors.toSet()).size() > 1) {
            throw StatementExceptionCode.ACCOUNT_CURRENCY_INCONSISTENT.exception();
        }

        final MerchantBalanceDto dto = MerchantBalanceDto.builder().build();

        BigDecimal totalBalance = BigDecimal.ZERO;
        BigDecimal available = BigDecimal.ZERO;
        BigDecimal unavailableBalance = BigDecimal.ZERO;
        BigDecimal pendingBalance = BigDecimal.ZERO;
        final LocalDateTime utcTime = LocalDateTimeUtil.nowUtc();

        final List<AccountConfigDto> accountConfigList = accountConfigService.listAccountConfig(
                ListAccountConfigVo.builder()
                        .ids(accountList.stream().map(Account::getAccountConfigId).collect(
                                Collectors.toList())).build());
        for (final Account account : accountList) {
            // Check recalculation switch state
            final RecalculationSwitchDto switchDto =
                    this.getRecalculationSwitch(account.getId());
            if (Objects.nonNull(switchDto) && SwitchEnum.ON == switchDto.getSwitchState()) {
                throw StatementExceptionCode.TRANSACTION_RECALCULATION.exception();
            }
            final ListAccountBalanceDto accountBalanceDto =
                    buildAccountBalanceInfo(account, utcTime, accountConfigList);

            totalBalance = totalBalance.add(accountBalanceDto.getTotalBalance());
            available = available.add(accountBalanceDto.getAvailable());
            unavailableBalance = unavailableBalance.add(accountBalanceDto.getUnavailable());
            pendingBalance = pendingBalance.add(accountBalanceDto.getPendingAmount());
        }

        dto.setTotalBalance(totalBalance);
        dto.setExtractableAmount(available);
        dto.setUnavailableAmount(unavailableBalance);
        dto.setPendingAmount(pendingBalance);
        dto.setCurrency(accountList.get(0).getCurrency());

        return dto;
    }

    @Override
    public QueryAccountTransferAmountDto queryAccountTransferAmount(
            final QueryAccountTransferAmountVo vo
    ) {
        // query account latestDailyExtractableBalance
        final Account account = requireOne(vo.getAccountId());

        // query be_credited_amount where beCreditedDate equals merchant localDate
        final LocalDate localDate = LocalDateTimeUtil.nowUtcZonedDateTime()
                .withZoneSameInstant(ZoneId.of(account.getTimezone())).toLocalDate();

        final QTransactionMoney entity = QTransactionMoney.transactionMoney;
        final BooleanExpression condition =
                entity.accountId.eq(vo.getAccountId()).and(entity.beCreditedDate.eq(localDate))
                        .and(entity.tradingModel.ne(TradingModelEnum.D0))
                        .and(entity.holdStatus.eq(HoldStatusEnum.NORMAL));

        final BigDecimal creditedAmount = jpaQueryFactory.select(
                        entity.beCreditedAmount.sum().coalesce(BigDecimal.ZERO))
                .from(entity)
                .where(condition)
                .fetchOne();

        // query extract_amount from account_statement_biz
        final AccountDailyInitBo dailyBillInitInfo =
                accountDailyInitService.getDailyBillInitInfo(vo.getAccountId(), localDate);

        final BigDecimal latestAccountStatementBizAmount =
                accountStatementBizService.statisticsDailyBill(account, dailyBillInitInfo);


        BigDecimal transferAmount =
                Optional.ofNullable(account.getLatestDailyExtractableBalance())
                        .orElse(BigDecimal.ZERO).add(creditedAmount)
                        .add(latestAccountStatementBizAmount);

        transferAmount =
                transferAmount.compareTo(BigDecimal.ZERO) >= 0 ? transferAmount :
                        BigDecimal.ZERO;

        return QueryAccountTransferAmountDto.builder().transferAmount(transferAmount)
                .accountId(vo.getAccountId()).currency(account.getCurrency()).build();
    }

    private Account requireOne(final Long id) {
        return accountRepository.findById(id, EntityGraphUtils.fromName("accountConfig", false))
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
    }

    private AccountDto toDto(final Account original) {
        return setOverWithdrawalAmount(modelMapper.convert(original), approvalProperties);
    }

    private List<AccountDto> toDto(final List<Account> originalList) {
        return originalList.stream().map(this::toDto).collect(Collectors.toList());
    }

    private AccountDto setOverWithdrawalAmount(
            final AccountDto accountDto,
            final StatementProperties.ApprovalProperties approvalProperties
    ) {
        final StatementProperties.ApprovalBizTransferOut approvalBizTransferOut =
                approvalProperties.getBizTransferOut();
        accountDto.setOverWithdrawalAmount(
                approvalBizTransferOut.getAccountIds().contains(accountDto.getId())
                        && approvalBizTransferOut.getOverWithdrawalAmount()
        );
        return accountDto;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public AccountDto querySubtractInProgressAmountAccountInfo(
            final QuerySubtractInProgressAmountAccountVo vo
    ) {
        final Specification<Account> specification = Specifications.<Account>and()
                .eq("merchantId", vo.getMerchantId())
                .eq(Objects.nonNull(vo.getAccountId()), "id", vo.getAccountId())
                .eq(Objects.nonNull(vo.getCountryCode()), "countryCode", vo.getCountryCode())
                .eq(Objects.nonNull(vo.getTransactionTypeCode()), "transactionTypeCode",
                        vo.getTransactionTypeCode())
                .build();

        final Account account = accountRepository.findOne(specification,
                        EntityGraphUtils.fromName("accountConfig", false))
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);

        final List<AccountConfigDto> accountConfigList = accountConfigService.listAccountConfig(
                ListAccountConfigVo.builder().ids(List.of(account.getAccountConfigId())).build());

        final ListAccountBalanceDto accountBalanceDto =
                buildAccountBalanceInfo(account, LocalDateTimeUtil.nowUtc(), accountConfigList);

        final AccountDto dto = modelMapper.convert(account);
        dto.setExtractableBalance(accountBalanceDto.getAvailable());
        dto.setUnavailableAmount(accountBalanceDto.getUnavailable());
        dto.setPendingAmount(accountBalanceDto.getPendingAmount());
        return dto;
    }


    @Override
    public List<AccountDto> listSubtractInProgressAmountAccountInfo(final ListAccountVo vo) {
        final List<Account> dtoList = accountRepository.findAll(getSpecification(vo),
                EntityGraphUtils.fromName("accountConfig", false));

        final List<AccountConfigDto> accountConfigList = accountConfigService.listAccountConfig(
                ListAccountConfigVo.builder()
                        .ids(dtoList.stream().map(Account::getAccountConfigId).collect(
                                Collectors.toList())).build());

        return dtoList.stream().map(v -> {

            final ListAccountBalanceDto accountBalanceDto =
                    buildAccountBalanceInfo(v, LocalDateTimeUtil.nowUtc(), accountConfigList);

            final AccountDto dto = modelMapper.convert(v);
            dto.setExtractableBalance(accountBalanceDto.getAvailable());
            return dto;

        }).collect(Collectors.toList());
    }

    @Override
    public List<MerchantAccountsDto> queryMerchantAccounts() {

        final List<Tuple> dataList = accountRepository.queryAccountGroup();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        final List<MerchantAccountsBo> listBo = Lists.newArrayList();
        for (final Tuple data : dataList) {
            listBo.add(MerchantAccountsBo.builder()
                    .merchantId(data.get("merchantId", BigInteger.class).longValue())
                    .countryCode(data.get("countryCode", String.class))
                    .transactionTypeList(Lists.newArrayList(
                            data.get("transactionTypes", String.class).split(",")))
                    .build());
        }

        final Map<Long, List<MerchantAccountsBo>> dataMap =
                listBo.stream().collect(Collectors.groupingBy(MerchantAccountsBo::getMerchantId));

        final List<MerchantAccountsDto> resultList = Lists.newArrayList();
        for (final Map.Entry<Long, List<MerchantAccountsBo>> entry : dataMap.entrySet()) {

            final Map<String, List<String>> countryMap = entry.getValue().stream()
                    .collect(Collectors.toMap(MerchantAccountsBo::getCountryCode,
                            MerchantAccountsBo::getTransactionTypeList));

            resultList.add(MerchantAccountsDto.builder()
                    .merchantId(entry.getKey())
                    .merchantCode(baseService.getMerchantById(entry.getKey()).getCode())
                    .country(countryMap)
                    .build());
        }

        return resultList;
    }

    @Override
    public List<ListAccountBalanceDto> listAccountBalance(final BatchQueryAccountBalanceVo vo) {
        log.info("batch query balance detail by merchantIds, size: {}", vo.getMerchantIds().size());
        final List<Account> accountList =
                accountRepository.findAllByMerchantIdIn(vo.getMerchantIds());
        return queryAccountBalance(accountList);
    }

}
