package com.liquido.statement.service.inprogress.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionInProgressStatusEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.SumInProgressAmountBo;
import com.liquido.statement.pojo.bo.TransactionInProgressBo;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.TransactionInProgressDto;
import com.liquido.statement.pojo.entity.AccountInProgress;
import com.liquido.statement.pojo.entity.TransactionInProgress;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.BatchAddTransactionProgressVo;
import com.liquido.statement.pojo.vo.TransactionInProgressVo;
import com.liquido.statement.repository.AccountInProgressRepository;
import com.liquido.statement.repository.TransactionInProgressRepository;
import com.liquido.statement.service.AccountInProgressService;
import com.liquido.statement.service.TransactionProgressService;
import com.liquido.statement.service.inprogress.InProgressService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InProgressServiceImpl implements InProgressService {

    private final AccountInProgressService accountInProgressService;
    private final AccountInProgressRepository accountInProgressRepository;
    private final TransactionProgressService transactionProgressService;
    private final TransactionInProgressRepository transactionInProgressRepository;
    private final ModelMapper modelMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void processing(
            final List<TransactionInProgressVo> transactionInProgressList,
            final AccountDto account) {
        try {
            final List<TransactionInProgressBo> inProgressList = new ArrayList<>();
            final List<TransactionInProgressBo> finalStatusList = new ArrayList<>();

            // Step1: group processing by uniqueId
            log.info("group processing by uniqueId, accountId: {}, transactionInProgress data:{}",
                    account.getId(), transactionInProgressList);
            transactionInProgressList.stream()
                    .collect(Collectors.groupingBy(TransactionInProgressVo::getUniqueId))
                    .forEach((uniqueId, tpData) -> processingByUniqueId(
                            account, uniqueId, tpData, inProgressList, finalStatusList));

            log.info("inProgressData size: {}, finalStatusData size: {}",
                    inProgressList.size(), finalStatusList.size());

            // Step2: save inProgress data
            final List<TransactionInProgressDto> inProgressData =
                    transactionProgressService.batchAddTransactionProgress(
                            new BatchAddTransactionProgressVo(inProgressList));

            // Step3: update finalStatus data
            updateFinalStatusData(inProgressData, finalStatusList);

            // Step4: adjust amounts based on changes
            adjustAmountBasedOnChanges(inProgressList, finalStatusList, account);

        } catch (Exception e) {
            log.error("process inProgress data error, syncId: {}", account.getId(), e);
            throw StatementExceptionCode.IN_PROGRESS_PROCESS_SYNC_FAIL.exception(e);
        }
    }

    private void processingByUniqueId(
            final AccountDto account,
            final String uniqueId,
            final List<TransactionInProgressVo> tpData,
            final List<TransactionInProgressBo> inProgressList,
            final List<TransactionInProgressBo> finalStatusList) {

        // query db data
        log.info("processingByUniqueId, "
                        + "uniqueId: {}, tpData: {}, inProgressList:{}, finalStatusList:{}",
                uniqueId, tpData, inProgressList, finalStatusList);

        final TransactionInProgressDto dbTpData =
                transactionProgressService.queryByUniqueId(uniqueId);

        // if transaction data status is finalStatus and db not exist same uniqueId data, is mean
        // final status transaction is before inProgress status transaction
        if (Objects.isNull(dbTpData)
                && tpData.stream().allMatch(x -> x.getLifecycleStatus().getRank() >
                TransactionStatusEnum.IN_PROGRESS.getRank())) {
            return;
        }

        // if db no exist data, get inProgress data, if exist data, get final status data
        log.info("filter data, dbTpData: {}", dbTpData);
        final List<TransactionInProgressVo> filteredTpData =
                Objects.nonNull(dbTpData)
                        ? tpData.stream().filter(v -> v.getLifecycleStatus().getRank() >
                        dbTpData.getLifecycleStatus().getRank()).collect(Collectors.toList())
                        : tpData;
        if (filteredTpData.isEmpty()) {
            return;
        }

        // sort by lifecycleStatus
        log.info("sort by lifecycleStatus, filteredTpData: {}", filteredTpData);
        filteredTpData.sort(Comparator.comparingInt(tp -> tp.getLifecycleStatus().getRank()));
        final TransactionInProgressVo firstData = filteredTpData.get(0);
        final TransactionInProgressVo finalData = filteredTpData.get(tpData.size() - 1);

        // inProgress data
        log.info("inProgress data, firstData: {}, finalData: {}", firstData, finalData);
        if (firstData.getLifecycleStatus().getRank()
                .equals(TransactionStatusEnum.IN_PROGRESS.getRank())) {
            final TransactionInProgressBo inProgressBo = buildTransactionBo(account, firstData,
                    TransactionInProgressStatusEnum.IN_PROGRESS);
            inProgressList.add(inProgressBo);
            return;
        }

        if (finalData.getLifecycleStatus().getRank() >
                TransactionStatusEnum.IN_PROGRESS.getRank()) {

            final TransactionInProgressBo finalBo = buildTransactionBo(account, finalData,
                    TransactionInProgressStatusEnum.COMPLETED);
            if (Objects.nonNull(dbTpData)) {
                finalBo.setId(dbTpData.getId());
                final boolean isAmountChanged = filteredTpData.stream()
                        .anyMatch(v -> v.getLifecycleStatus() == TransactionStatusEnum.IN_PROGRESS)
                        || dbTpData.getLifecycleStatus() == TransactionStatusEnum.IN_PROGRESS;

                finalBo.setIsChangedAmount(isAmountChanged);
                finalBo.setAmount(dbTpData.getAmount());
                finalBo.setFee(Optional.ofNullable(dbTpData.getFee()).orElse(BigDecimal.ZERO));
                finalBo.setTax(Optional.ofNullable(dbTpData.getTax()).orElse(BigDecimal.ZERO));
                finalBo.setNetAmount(Optional.ofNullable(dbTpData.getNetAmount())
                        .orElse(BigDecimal.ZERO));

                finalStatusList.add(finalBo);
            }

            log.info("finalStatusList: {}", finalStatusList);
        }
    }

    private TransactionInProgressBo buildTransactionBo(
            AccountDto account,
            TransactionInProgressVo source,
            TransactionInProgressStatusEnum status
    ) {
        TransactionInProgressBo bo = modelMapper.convertBo(source);
        bo.setMerchantId(account.getMerchantId());
        bo.setAccountId(account.getId());
        bo.setStatus(status);
        return bo;
    }


    private void updateFinalStatusData(
            final List<TransactionInProgressDto> inProgressData,
            final List<TransactionInProgressBo> finalStatusList
    ) {
        log.info("updateFinalStatusData, inProgressData: {}, finalStatusList: {}",
                inProgressData, finalStatusList);

        if (CollectionUtils.isEmpty(finalStatusList)) {
            return;
        }
        final Map<String, TransactionInProgressDto> inProgressMap = inProgressData.stream()
                .collect(Collectors.toMap(TransactionInProgressDto::getUniqueId, v -> v));

        final Map<Long, TransactionInProgressBo> boFinalStatusMap = finalStatusList.stream()
                .peek(v -> v.setId(Optional.ofNullable(v.getId())
                        .orElseGet(() -> inProgressMap.get(v.getUniqueId()).getId())))
                .collect(Collectors.toMap(TransactionInProgressBo::getId, v -> v));

        log.info("inProgressMap: {}, boFinalStatusMap: {}", inProgressMap, boFinalStatusMap);
        final List<TransactionInProgress> dbInProgressData =
                transactionInProgressRepository.findAllByIdIn(boFinalStatusMap.keySet());

        final LocalDateTime now = LocalDateTime.now();
        dbInProgressData.forEach(v -> {
            final TransactionInProgressBo bo = boFinalStatusMap.get(v.getId());
            v.setTransactionId(bo.getTransactionId());
            v.setStatus(bo.getStatus());
            v.setTransactionStatus(bo.getTransactionStatus());
            v.setDirectionType(bo.getDirectionType());
            v.setLifecycleStatus(bo.getLifecycleStatus());
            v.setUpdatedTime(now);
        });
        log.info("dbInProgressData-2: {}", dbInProgressData);
        transactionInProgressRepository.saveAll(dbInProgressData);
    }

    private void adjustAmountBasedOnChanges(
            final List<TransactionInProgressBo> inProgressData,
            final List<TransactionInProgressBo> finalStatusList,
            final AccountDto account
    ) {
        if (CollectionUtils.isEmpty(inProgressData) && CollectionUtils.isEmpty(finalStatusList)) {
            return;
        }
        Map<CurrencyEnum, SumInProgressAmountBo> currencyInProgressAmountMap =
                inProgressAmountBoToMap(inProgressData);

        Map<CurrencyEnum, SumInProgressAmountBo> currencyFinalStatusAmountMap =
                inProgressAmountBoToMap(finalStatusList.stream()
                        .filter(TransactionInProgressBo::getIsChangedAmount)
                        .collect(Collectors.toList()));

        log.info("currencyInProgressAmountMap: {}, currencyFinalStatusAmountMap: {}",
                currencyInProgressAmountMap, currencyFinalStatusAmountMap);

        final Stream<CurrencyEnum> uniqueCurrencyStream = Stream.concat(
                currencyInProgressAmountMap.keySet().stream(),
                currencyFinalStatusAmountMap.keySet().stream()).distinct();

        final List<AccountInProgress> accountInProgressList = uniqueCurrencyStream.map(currency -> {
            final AccountInProgress dbEntity =
                    accountInProgressService.findOrInitEntity(account, currency);

            dbEntity.setInProgressAmount(dbEntity.getInProgressAmount()
                    .add(calculateAccountInProgressAmount(
                            currencyInProgressAmountMap,
                            currencyFinalStatusAmountMap,
                            currency,
                            SumInProgressAmountBo::getAmount)));
            dbEntity.setFee(dbEntity.getFee()
                    .add(calculateAccountInProgressAmount(
                            currencyInProgressAmountMap,
                            currencyFinalStatusAmountMap,
                            currency,
                            SumInProgressAmountBo::getFee)));
            dbEntity.setTax(dbEntity.getTax()
                    .add(calculateAccountInProgressAmount(
                            currencyInProgressAmountMap,
                            currencyFinalStatusAmountMap,
                            currency,
                            SumInProgressAmountBo::getTax)));
            dbEntity.setNetAmount(dbEntity.getNetAmount()
                    .add(calculateAccountInProgressAmount(
                            currencyInProgressAmountMap,
                            currencyFinalStatusAmountMap,
                            currency,
                            SumInProgressAmountBo::getNetAmount)));

            dbEntity.setUpdatedTime(LocalDateTime.now());
            return dbEntity;
        }).collect(Collectors.toList());

        accountInProgressRepository.saveAll(accountInProgressList);
    }

    private Map<CurrencyEnum, SumInProgressAmountBo> inProgressAmountBoToMap(
            final List<TransactionInProgressBo> inProgressData
    ) {
        return inProgressData.stream()
                .collect(Collectors.toMap(
                        TransactionInProgressBo::getCurrency,
                        data -> {
                            SumInProgressAmountBo bo = new SumInProgressAmountBo();
                            bo.setAmount(data.getAmount());
                            bo.setFee(data.getFee());
                            bo.setTax(data.getTax());
                            bo.setNetAmount(data.getNetAmount());
                            return bo;
                        }, (bo1, bo2) -> {
                            SumInProgressAmountBo merged = new SumInProgressAmountBo();
                            merged.setAmount(bo1.getAmount().add(bo2.getAmount()));
                            merged.setFee(bo1.getFee().add(bo2.getFee()));
                            merged.setTax(bo1.getTax().add(bo2.getTax()));
                            merged.setNetAmount(bo1.getNetAmount().add(bo2.getNetAmount()));
                            return merged;
                        }
                ));
    }

    private BigDecimal calculateAccountInProgressAmount(
            final Map<CurrencyEnum, SumInProgressAmountBo> currencyInProgressAmountMap,
            final Map<CurrencyEnum, SumInProgressAmountBo> currencyFinalStatusAmountMap,
            final CurrencyEnum currency,
            final Function<SumInProgressAmountBo, BigDecimal> function
    ) {
        return Optional.ofNullable(currencyInProgressAmountMap.get(currency))
                .map(function)
                .orElse(BigDecimal.ZERO)
                .subtract(Optional.ofNullable(currencyFinalStatusAmountMap.get(currency))
                        .map(function).orElse(BigDecimal.ZERO));
    }
}
