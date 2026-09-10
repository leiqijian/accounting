package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.enums.SortTypeEnum;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.SubAccountTransactionMoneyBo;
import com.liquido.statement.pojo.bo.SumTransactionInProgressNetAmountBo;
import com.liquido.statement.pojo.dto.AccountDailyBillDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.PageSubAccountDto;
import com.liquido.statement.pojo.dto.SubAccountDto;
import com.liquido.statement.pojo.dto.TransactionMoneyDto;
import com.liquido.statement.pojo.entity.AccountStatement;
import com.liquido.statement.pojo.entity.SubAccount;
import com.liquido.statement.pojo.entity.TransactionInProgress;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.BatchAddSubAccountVo;
import com.liquido.statement.pojo.vo.ListAccountVo;
import com.liquido.statement.pojo.vo.ListSubAccountVo;
import com.liquido.statement.pojo.vo.PageSubAccountVo;
import com.liquido.statement.pojo.vo.QuerySubAccountVo;
import com.liquido.statement.repository.AccountStatementRepository;
import com.liquido.statement.repository.SubAccountRepository;
import com.liquido.statement.service.AccountDailyBillService;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.SubAccountDailyBillService;
import com.liquido.statement.service.SubAccountService;
import com.liquido.statement.service.TransactionMoneyService;
import com.liquido.statement.service.TransactionProgressService;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SubAccountServiceImpl implements SubAccountService {

    private final SubAccountRepository subAccountRepository;
    private final ModelMapper modelMapper;
    private final BaseService baseService;
    private final AccountService accountService;
    private final TransactionProgressService transactionProgressService;
    @Autowired
    @Lazy
    private SubAccountDailyBillService subAccountDailyBillService;

    private final AccountDailyBillService accountDailyBillService;

    private final TransactionMoneyService transactionMoneyService;

    private final AccountStatementRepository accountStatementRepository;


    @Override
    public SubAccountDto querySubAccount(final QuerySubAccountVo vo) {
        return subAccountRepository.findOne(
                        Specifications.<SubAccount>and().eq("merchantId", vo.getMerchantId())
                                .eq("subMerchantId", vo.getSubMerchantId())
                                .eq("countryCode", vo.getCountryCode()).build())
                .map(this::buildSubAccountDto)
                .orElseThrow(StatementExceptionCode.FIND_SUB_ACCOUNT_FAIL_PARAM_ERROR::exception);
    }

    @Override
    public List<SubAccountDto> listSubAccount(final ListSubAccountVo vo) {
        final PredicateBuilder<SubAccount> predicateBuilder =
                Specifications.<SubAccount>and().eq("merchantId", vo.getMerchantId())
                        .eq(Objects.nonNull(vo.getCountryCode()), "countryCode",
                                vo.getCountryCode());

        if (ObjectUtils.isNotEmpty(vo.getSubMerchantIds())) {
            predicateBuilder.in("subMerchantId", vo.getSubMerchantIds().toArray());
        }
        final List<SubAccount> subAccountList =
                subAccountRepository.findAll(predicateBuilder.build());

        final Set<String> subMerchantIds = subAccountList.stream().map(SubAccount::getSubMerchantId)
                .collect(Collectors.toSet());

        final Map<String, SubMerchantDto> subMerchantMap =
                baseService.querySubMerchantIdByMerchantId(vo.getMerchantId(), subMerchantIds)
                        .stream()
                        .collect(Collectors.toMap(SubMerchantDto::getSubMerchantId, obj -> obj));

        final List<SubAccountDto> list = subAccountList.stream().map(v -> {
            final SubAccountDto dto = modelMapper.convert(v);
            final SubMerchantDto subMerchantDto = subMerchantMap.get(v.getSubMerchantId());
            dto.setSubMerchantName(
                    Optional.ofNullable(subMerchantDto).map(SubMerchantDto::getCommercialName)
                            .orElse(""));
            return dto;
        }).collect(Collectors.toList());

        Lists.partition(list, 100).forEach(this::sumSubAccountPendAndSettlementAmount);

        return list;
    }

    @Override
    public PageVo<PageSubAccountDto> pageSubAccount(final PageSubAccountVo vo) {

        final Specification<SubAccount> spec =
                Specifications.<SubAccount>and().eq("merchantId", vo.getMerchantId())
                        .eq("countryCode", vo.getCountryCode())
                        .eq(Objects.nonNull(vo.getSubMerchantId()), "subMerchantId",
                                vo.getSubMerchantId()).build();

        final Page<SubAccountDto> page = subAccountRepository.findAll(spec,
                        PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(), Sort.by(Direction.valueOf(
                                        Optional.ofNullable(vo.getSortType()).orElse(SortTypeEnum.DESC).getCode()),
                                Optional.ofNullable(vo.getSortField()).orElse("id"))))
                .map(modelMapper::convert);

        if (ObjectUtils.isEmpty(page.getContent())) {
            return PageVo.buildEmptyPage(vo.getPageSize());
        }

        final Set<String> subMerchantIds =
                page.getContent().stream().map(SubAccountDto::getSubMerchantId)
                        .collect(Collectors.toSet());

        final Map<String, SubMerchantDto> subMerchantMap =
                baseService.querySubMerchantIdByMerchantId(vo.getMerchantId(), subMerchantIds)
                        .stream()
                        .collect(Collectors.toMap(SubMerchantDto::getSubMerchantId, obj -> obj));

        sumSubAccountPendAndSettlementAmount(page.getContent());

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(), page.getTotalElements(),
                page.getContent().stream().map(x -> {
                    final PageSubAccountDto bo = new PageSubAccountDto();
                    BeanCopierUtil.copyProperties(x, bo);
                    bo.setSubMerchantName(
                            Optional.ofNullable(subMerchantMap.get(bo.getSubMerchantId()))
                                    .map(SubMerchantDto::getCommercialName).orElse(""));
                    bo.setDate(LocalDateTimeUtil.utcToLocal(LocalDateTimeUtil.nowUtc(),
                            x.getTimezone()).toLocalDate().minusDays(1L));
                    return bo;
                }).collect(Collectors.toList()));
    }


    private SubAccountDto buildSubAccountDto(final SubAccount subAccount) {
        final String subMerchantName =
                findSubMerchantNameBySubMerchantId(subAccount.getMerchantId(),
                        subAccount.getSubMerchantId());
        final SubAccountDto subAccountDto = modelMapper.convert(subAccount);
        subAccountDto.setSubMerchantName(subMerchantName);
        sumSubAccountPendAndSettlementAmount(List.of(subAccountDto));
        return subAccountDto;
    }

    private String findSubMerchantNameBySubMerchantId(final Long merchantId,
            final String subMerchantId) {

        return baseService.querySubMerchantIdByMerchantId(merchantId,
                        Sets.newHashSet((subMerchantId))).stream().findFirst()
                .map(SubMerchantDto::getCommercialName).orElse("");
    }


    @Override
    public void batchAddSubAccount(final BatchAddSubAccountVo vo) {
        final MerchantDto merchantDto = baseService.getMerchantByCode(vo.getMerchantCode());

        final List<AccountDto> accountDtoList = accountService.listAccount(
                ListAccountVo.builder().merchantId(merchantDto.getId()).build());

        final Map<CountryCodeEnum, List<AccountDto>> countryAccountMap =
                accountDtoList.stream().collect(Collectors.groupingBy(AccountDto::getCountryCode));

        final List<SubMerchantDto> subMerchantDtos =
                baseService.querySubMerchantIdByMerchantId(merchantDto.getId(),
                        vo.getSubMerchantIds());

        log.info("batchAdd subAccount merchantId={},subMerchant size={}", merchantDto.getId(),
                subMerchantDtos.size());

        List<SubAccount> subAccountList = subMerchantDtos.stream()
                .flatMap(subMerchant -> countryAccountMap.entrySet().stream().map(entry -> {
                    final CountryCodeEnum country = entry.getKey();
                    final List<AccountDto> accountDtos = entry.getValue();
                    AccountDto accountDto = accountDtos.stream().findFirst().orElseThrow();
                    return SubAccount.builder().merchantId(merchantDto.getId())
                            .subMerchantId(subMerchant.getSubMerchantId()).accountIds(
                                    accountDtos.stream().map(AccountDto::getId)
                                            .collect(Collectors.toList())).countryCode(country)
                            .balance(BigDecimal.ZERO).extractableBalance(BigDecimal.ZERO)
                            .currency(accountDto.getCurrency()).timezone(accountDto.getTimezone())
                            .timezoneName(accountDto.getTimezoneName()).build();
                })).collect(Collectors.toList());

        subAccountRepository.saveAll(subAccountList);
    }


    private void sumSubAccountPendAndSettlementAmount(final List<SubAccountDto> subAccountDto) {
        if (ObjectUtils.isEmpty(subAccountDto)) {
            return;
        }
        final Map<String, List<SubAccountDto>> timezoneMap =
                subAccountDto.stream().collect(Collectors.groupingBy(SubAccountDto::getTimezone));

        timezoneMap.forEach((timezone, timezoneSubAccounts) -> {

            log.info("timezone={},subAccount={}", timezone, timezoneSubAccounts);

            final Set<Long> accountIds = new HashSet<>();
            final Set<String> subMerchantIds = new HashSet<>();

            //1.query transaction money and sum availableBalance balance and unAvailableBalance
            for (final SubAccountDto accountDto : timezoneSubAccounts) {
                accountIds.addAll(accountDto.getAccountIds());
                subMerchantIds.add(accountDto.getSubMerchantId());
            }

            log.info("set accountIds={},set subMerchantId={}", accountIds, subMerchantIds);
            final SubAccountTransactionMoneyBo bo =
                    querySubAccountTransactionMoney(accountIds, subMerchantIds, timezone);

            final Map<String, Map<Long, List<TransactionMoneyDto>>> transactionMoneyMap =
                    bo.getTransactionMoneyList().stream()
                            .collect(Collectors.groupingBy(
                                    TransactionMoneyDto::getSubMerchantId,
                                    Collectors.groupingBy(TransactionMoneyDto::getAccountId)
                            ));

            final LocalDate accountLocalDate = bo.getLocalDate();

            final Map<Long, Map<DirectionTypeEnum, AccountStatement>> accountStatementMap =
                    bo.getAccountStatementList().stream()
                            .collect(Collectors.groupingBy(
                                    AccountStatement::getTransactionId,
                                    Collectors.toMap(AccountStatement::getDirectionType,
                                            Function.identity())));

            // 2. query in-progress data
            final List<SumTransactionInProgressNetAmountBo> list =
                    querySubAccountInProgressMoney(accountIds, subMerchantIds);

            log.info("load in-progress data accountIds={}, subMerchantId={},data={}", accountIds,
                    subMerchantIds, list);

            final Map<String, Map<Long, List<SumTransactionInProgressNetAmountBo>>>
                    inProgressMap = list.stream().collect(Collectors.groupingBy(
                    SumTransactionInProgressNetAmountBo::getSubMerchantId,
                    Collectors.groupingBy(
                            SumTransactionInProgressNetAmountBo::getAccountId)
            ));

            timezoneSubAccounts.forEach(subAccount -> {

                BigDecimal availableBalance = BigDecimal.ZERO;
                BigDecimal unavailableBalance = BigDecimal.ZERO;
                BigDecimal inProgressBalance = BigDecimal.ZERO;
                BigDecimal holdBalance = BigDecimal.ZERO;

                for (final Long accountId : subAccount.getAccountIds()) {
                    final List<TransactionMoneyDto> transactionMoneys = transactionMoneyMap
                            .getOrDefault(subAccount.getSubMerchantId(), Map.of())
                            .getOrDefault(accountId, List.of());

                    final List<SumTransactionInProgressNetAmountBo> inProgressList = inProgressMap
                            .getOrDefault(subAccount.getSubMerchantId(), Map.of())
                            .getOrDefault(accountId, List.of());

                    inProgressBalance = inProgressBalance.add(inProgressList.stream()
                            .map(SumTransactionInProgressNetAmountBo::getNetAmount)
                            .reduce(BigDecimal::add)
                            .orElse(BigDecimal.ZERO));

                    for (final TransactionMoneyDto transactionMoney : transactionMoneys) {
                        if (transactionMoney.getBeCreditedDate().equals(accountLocalDate)) {
                            if (TransactionTypeCodeEnum.PAY_IN ==
                                    transactionMoney.getTransactionTypeCode()) {
                                availableBalance = availableBalance.add(
                                        transactionMoney.getBeCreditedAmount()
                                                .multiply(transactionMoney.getAmountPon()));
                            } else {
                                final AccountStatement accountStatement = accountStatementMap
                                        .getOrDefault(transactionMoney.getTransactionId(), Map.of())
                                        .get(transactionMoney.getDirectionType());

                                availableBalance =
                                        availableBalance.add(Optional.ofNullable(accountStatement)
                                                .map(a -> a.getAmountPon().getCode()
                                                        .multiply(a.getAmount()))
                                                .orElse(BigDecimal.ZERO));
                            }
                        }
                        if (transactionMoney.getBeCreditedDate().isAfter(accountLocalDate)) {
                            unavailableBalance = unavailableBalance.add(
                                    transactionMoney.getBeCreditedAmount()
                                            .multiply(transactionMoney.getAmountPon()));
                        }
                        if (HoldStatusEnum.HOLD == transactionMoney.getHoldStatus()) {
                            holdBalance = holdBalance.add(transactionMoney.getBeCreditedAmount());
                        }
                    }
                }
                subAccount.setAvailableBalance(
                        availableBalance.add(subAccount.getExtractableBalance())
                                .subtract(inProgressBalance));
                subAccount.setUnavailableBalance(unavailableBalance);
                subAccount.setPendingBalance(inProgressBalance);
                subAccount.setHoldBalance(holdBalance);
                subAccount.setBalance((subAccount.getAvailableBalance())
                        .add(subAccount.getUnavailableBalance())
                        .add(subAccount.getPendingBalance())
                        .add(holdBalance));
            });
        });
    }


    private SubAccountTransactionMoneyBo querySubAccountTransactionMoney(
            final Set<Long> accountIds,
            final Set<String> subMerchantIds, final String timezone) {

        final AccountDailyBillDto accountDailyBillDto =
                accountIds.stream().map(accountDailyBillService::queryLatestAccountDailyBill)
                        .filter(Objects::nonNull)
                        .max(Comparator.comparing(AccountDailyBillDto::getId))
                        .orElse(AccountDailyBillDto.builder().id(0L)
                                .billDate(LocalDateTimeUtil.nowUtcToLocal(timezone).toLocalDate()
                                        .minusDays(1))
                                .build());

        log.info("load latest account daily bill accountId={},data={}", accountIds,
                accountDailyBillDto);

        final Long billId = accountDailyBillDto.getId();
        final LocalDate accountLocalDate = accountDailyBillDto.getBillDate().plusDays(1);

        final List<TransactionMoneyDto> transactionMonies =
                transactionMoneyService.queryTransactionMoneyByAccountIdSubMerchantIdAndBillId(
                        accountIds, subMerchantIds, billId);

        log.info(
                "load transaction money billId={},accountIds={},subMerchantIds={} data size ={}",
                billId, accountIds, subMerchantIds, transactionMonies.size());

        final SubAccountTransactionMoneyBo bo = SubAccountTransactionMoneyBo.builder()
                .localDate(accountLocalDate)
                .transactionMoneyList(transactionMonies)
                .accountStatementList(new ArrayList<>())
                .build();

        // payout beCreditedAmount is 0 ,need to account statement get amount replace payout availableAmount
        final List<Long> payoutTransactionIds = transactionMonies.stream()
                .filter(t -> TransactionTypeCodeEnum.PAY_OUT == t.getTransactionTypeCode())
                .map(TransactionMoneyDto::getTransactionId).collect(Collectors.toList());

        if (ObjectUtils.isNotEmpty(payoutTransactionIds)) {
            final List<AccountStatement> accountStatements =
                    accountStatementRepository.findAllByTransactionIdIn(payoutTransactionIds);
            bo.setAccountStatementList(accountStatements);
        }

        return bo;
    }

    private List<SumTransactionInProgressNetAmountBo> querySubAccountInProgressMoney(
            final Set<Long> accountIds,
            final Set<String> subMerchantIds) {

        final TransactionInProgress inProgress =
                transactionProgressService.findFirstInProgressDataByAccountAndSubMerchant(
                        accountIds, subMerchantIds);
        log.info(
                "load in-progress data accountIds={},subMerchantIds={} data ={}",
                accountIds, subMerchantIds, inProgress);

        if (Objects.isNull(inProgress)) {
            return Collections.emptyList();
        }
        return transactionProgressService.sumSubAccountPendAndSettlementAmount(
                accountIds, subMerchantIds, inProgress);

    }
}
