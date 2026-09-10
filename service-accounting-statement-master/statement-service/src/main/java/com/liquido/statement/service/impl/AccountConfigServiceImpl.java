package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.PageUtil;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.BalanceAlarmConfigBo;
import com.liquido.statement.pojo.bo.DepositConfigBo;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.AccountConfigInfoDto;
import com.liquido.statement.pojo.dto.BalanceAlarmConfigDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountConfig;
import com.liquido.statement.pojo.entity.QAccount;
import com.liquido.statement.pojo.entity.QAccountConfig;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.AccountConfigVo;
import com.liquido.statement.pojo.vo.BatchAddAccountConfigVo;
import com.liquido.statement.pojo.vo.ListAccountConfigVo;
import com.liquido.statement.pojo.vo.PageAccountConfigVo;
import com.liquido.statement.pojo.vo.QueryAccountConfigVo;
import com.liquido.statement.pojo.vo.UpdateAccountDepositVo;
import com.liquido.statement.pojo.vo.UpdateBalanceAlarmConfigVo;
import com.liquido.statement.repository.AccountConfigRepository;
import com.liquido.statement.repository.AccountRepository;
import com.liquido.statement.service.AccountConfigService;

import com.github.wenhao.jpa.Specifications;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AccountConfigServiceImpl implements AccountConfigService {
    private final JPAQueryFactory jpaQueryFactory;
    private final AccountConfigRepository accountConfigRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AccountConfigDto> batchAddAccountConfig(final BatchAddAccountConfigVo vo) {

        // check merchant and account info
        final Set<Long> accountIds = accountRepository.findByMerchantId(vo.getMerchantId())
                .stream().map(Account::getId).collect(Collectors.toSet());

        final List<AccountConfig> accountConfigs = vo.getListVo().stream().map(x -> {
            // Check if all account IDs are valid
            if (!accountIds.contains(x.getAccountId())) {
                throw StatementExceptionCode.MERCHANT_AND_ACCOUNT_NO_PATTERN.exception();
            }
            AccountConfig accountConfig = new AccountConfig();
            accountConfig.setMerchantId(vo.getMerchantId());
            accountConfig.setExchangeRateConfig(x.getExchangeRateConfig());
            accountConfig.setConfigData(x.getAccountConfigData());
            final LocalDateTime now = LocalDateTimeUtil.nowUtc();
            accountConfig.setUpdatedTime(now);
            accountConfig.setCreatedTime(now);
            accountConfig.setVersion(1L);
            return accountConfig;
        }).collect(Collectors.toList());

        return toDto(accountConfigRepository.saveAll(accountConfigs));
    }

    @Override
    public List<AccountConfigDto> queryAllAccountConfig() {
        return toDto(accountConfigRepository.findAll());
    }

    @Override
    public List<AccountConfigDto> listAccountConfig(final ListAccountConfigVo vo) {
        return toDto(accountConfigRepository.findAll(getSpecification(vo)));
    }

    @Override
    public PageVo<AccountConfigDto> pageAccountConfig(final PageAccountConfigVo vo) {
        Page<AccountConfig> page = accountConfigRepository.findAll(
                getSpecification(BeanCopierUtil.copyProperties(vo, ListAccountConfigVo.class)),
                PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(),
                        Sort.by(Sort.Order.desc("createdTime"))));
        return PageUtil.buildPage(page, vo, AccountConfigDto.class);
    }

    @Override
    public AccountConfigDto getEffectiveAccountConfigInfo(final QueryAccountConfigVo vo) {
        final QAccountConfig accountConfig = QAccountConfig.accountConfig;
        final QAccount account = QAccount.account;

        BooleanExpression condition = account.id.eq(vo.getAccountId());
        if (Objects.nonNull(vo.getMerchantId())) {
            condition = condition.and(account.merchantId.eq(vo.getMerchantId()))
                    .and(accountConfig.merchantId.eq(vo.getMerchantId()));
        }

        return toDto(jpaQueryFactory.select(accountConfig).from(accountConfig)
                .leftJoin(account).on(accountConfig.id.eq(account.accountConfigId))
                .where(condition)
                .fetchOne());
    }

    @Override
    public List<BalanceAlarmConfigDto> queryBalanceAlarmConfig(final AccountConfigVo vo) {
        final QAccount account = QAccount.account;
        final QAccountConfig accountConfig = QAccountConfig.accountConfig;

        final QBean<AccountConfigInfoDto> bean = Projections.fields(AccountConfigInfoDto.class,
                account.merchantId,
                account.id.as("accountId"),
                account.transactionTypeCode,
                account.countryCode,
                account.currency,
                accountConfig.configData);

        final List<AccountConfigInfoDto> configList = jpaQueryFactory.select(bean)
                .from(account)
                .leftJoin(accountConfig).on(account.accountConfigId.eq(accountConfig.id)
                        .and(account.delFlag.eq(Boolean.FALSE))
                        .and(accountConfig.delFlag.eq(Boolean.FALSE)))
                .where(account.merchantId.eq(vo.getMerchantId()))
                .fetch();

        final List<BalanceAlarmConfigDto> resultList = Lists.newArrayList();

        for (final AccountConfigInfoDto dto : configList) {
            final AccountConfigData configData = Optional.ofNullable(dto.getConfigData())
                    .orElse(new AccountConfigData());

            // set default BalanceAlarm config
            configData.setBalanceAlarm(
                    Optional.ofNullable(configData.getBalanceAlarm())
                            .orElse(BalanceAlarmConfigBo.builder()
                                    .flag(Boolean.FALSE)
                                    .amountLimit(BigDecimal.ZERO)
                                    .currencyEnum(dto.getCurrency())
                                    .email(List.of())
                                    .build()));

            resultList.add(BalanceAlarmConfigDto.builder()
                    .merchantId(dto.getMerchantId())
                    .accountId(dto.getAccountId())
                    .countryCode(dto.getCountryCode())
                    .currency(dto.getCurrency())
                    .transactionTypeCode(dto.getTransactionTypeCode())
                    .balanceAlarmConfig(configData.getBalanceAlarm())
                    .build());
        }
        return resultList;
    }

    @Override
    public void updateAccountBalanceAlarmConfig(final UpdateBalanceAlarmConfigVo vo) {
        final Account account = accountRepository.findById(vo.getAccountId())
                .orElseThrow(StatementExceptionCode.UNKNOWN_ACCOUNT::exception);

        final QAccountConfig entity = QAccountConfig.accountConfig;
        AccountConfig accountConfig = jpaQueryFactory.select(entity)
                .from(entity)
                .where(entity.merchantId.eq(vo.getMerchantId())
                        .and(entity.id.eq(account.getAccountConfigId())))
                .fetchOne();

        Integer alarmCycleHours = 8;
        // Save if not exist
        if (Objects.isNull(accountConfig)) {
            vo.getBalanceAlarmConfig().setAlarmCycleHours(alarmCycleHours);
            accountConfig = AccountConfig.builder()
                    .merchantId(vo.getMerchantId())
                    .configData(AccountConfigData.builder()
                            .balanceAlarm(vo.getBalanceAlarmConfig())
                            .build())
                    .build();

            accountConfigRepository.save(accountConfig);
            return;
        }

        // Update config
        if (Objects.nonNull(accountConfig.getConfigData())
                && Objects.nonNull(accountConfig.getConfigData().getBalanceAlarm())
                && Objects.nonNull(
                accountConfig.getConfigData().getBalanceAlarm().getAlarmCycleHours())) {
            alarmCycleHours =
                    accountConfig.getConfigData().getBalanceAlarm().getAlarmCycleHours();
        }

        vo.getBalanceAlarmConfig().setAlarmCycleHours(alarmCycleHours);
        accountConfig.getConfigData().setBalanceAlarm(vo.getBalanceAlarmConfig());

        accountConfigRepository.save(accountConfig);
    }

    @Override
    public void updateAccountDepositConfig(final UpdateAccountDepositVo vo) {

        if (Objects.isNull(vo.getAmount()) && Objects.isNull(vo.getLegalHoldAmount())) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL.exception(
                    "amount or legalHoldAmount is null");
        }

        final QAccount entity = QAccount.account;
        final BooleanExpression condition = entity.merchantId.eq(vo.getMerchantId())
                .and(entity.id.eq(vo.getAccountId()));

        final Account account = jpaQueryFactory.select(entity)
                .from(entity)
                .where(condition)
                .fetchOne();

        if (Objects.isNull(account)) {
            throw StatementExceptionCode.ACCOUNT_NOT_FOUND.exception();
        }
        final AccountConfig accountConfig =
                Optional.ofNullable(account.getAccountConfig()).orElse(AccountConfig.builder()
                        .merchantId(vo.getMerchantId()).build());

        AccountConfigData configData = Optional.ofNullable(accountConfig.getConfigData())
                .orElseGet(AccountConfigData::new);

        Optional.ofNullable(configData.getDepositConfig())
                .ifPresentOrElse(t ->
                        {
                            Optional.ofNullable(vo.getAmount()).ifPresent(t::setAmount);
                            Optional.ofNullable(vo.getLegalHoldAmount()).ifPresent(t::setLegalHoldAmount);
                        },
                        () -> {
                            final DepositConfigBo bo = DepositConfigBo.builder()
                                    .currency(account.getCurrency())
                                    .build();
                            Optional.ofNullable(vo.getAmount()).ifPresent(bo::setAmount);
                            Optional.ofNullable(vo.getLegalHoldAmount())
                                    .ifPresent(bo::setLegalHoldAmount);

                            accountConfig.setConfigData(
                                    AccountConfigData.builder().depositConfig(bo).build());
                        });

        accountConfigRepository.save(accountConfig);
    }

    private Specification<AccountConfig> getSpecification(final ListAccountConfigVo vo) {
        return Specifications.<AccountConfig>and()
                .eq(Objects.nonNull(vo.getMerchantId()), "merchantId", vo.getMerchantId())
                .eq(Objects.nonNull(vo.getId()), "id", vo.getId())
                .in(CollectionUtils.isNotEmpty(vo.getIds()), "id", ListUtils
                        .emptyIfNull(vo.getIds()).toArray())
                .build();
    }

    private AccountConfigDto toDto(final AccountConfig original) {
        return modelMapper.convert(original);
    }

    private List<AccountConfigDto> toDto(final List<AccountConfig> originalList) {
        return originalList.stream().map(this::toDto).collect(Collectors.toList());
    }

}
