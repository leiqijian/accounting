package com.liquido.worker.service.sync.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.LockModeType;

import com.liquido.base.BaseApis;
import com.liquido.base.constant.dynamic.DynamicConstant;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DictionaryTypeEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionDataSourceEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.base.pojo.dto.AccountProductDto;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.exception.ApplicationException;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.vo.TransactionInProgressVo;
import com.liquido.worker.aws.sqs.msg.ServiceFeeCalculationSyncMsg;
import com.liquido.worker.aws.sqs.publish.ServiceFeeCalculationSyncPublisher;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.properties.LarkProperties;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.enums.CalculationTaskStateEnum;
import com.liquido.worker.enums.CalculationTaskTypeEnum;
import com.liquido.worker.events.TaskFeeCalculationEvent;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.feign.DataWarehouseFeign;
import com.liquido.worker.pojo.bo.DwPage;
import com.liquido.worker.pojo.bo.DwResponse;
import com.liquido.worker.pojo.bo.MerchantAccountBo;
import com.liquido.worker.pojo.bo.TaskFeeCalculationSettleBo;
import com.liquido.worker.pojo.dto.DwSyncTransactionDto;
import com.liquido.worker.pojo.dto.PreCalculateFeeDto;
import com.liquido.worker.pojo.dto.TransactionFeeDto;
import com.liquido.worker.pojo.entity.QTaskFeeCalculation;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.vo.SyncRerunTaskFeeCalculationVo;
import com.liquido.worker.pojo.vo.SyncTaskFeeCalculationVo;
import com.liquido.worker.repository.TaskFeeCalculationRepository;
import com.liquido.worker.service.MerchantService;
import com.liquido.worker.service.calculate.TaskTransactionService;
import com.liquido.worker.service.sync.TransactionSyncService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
public class TransactionSyncServiceImpl implements TransactionSyncService {

    private final BaseApis.BaseFeign baseFeign;
    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final ApplicationEventPublisher publisher;
    private final DataWarehouseFeign dataWarehouseFeign;
    private final TaskTransactionService taskTransactionService;
    private final TaskFeeCalculationRepository taskFeeCalculationRepository;
    private final WorkerProperties.DataWarehouseProperties dwProperties;
    private final WorkerProperties.FeeCalculationProperties feeCalculationProperties;
    private final WorkerProperties.CoRejectedOrderProperties coRejectedOrderProperties;
    private final LarkProperties.MonitorProperties monitorProperties;
    private final ServiceFeeCalculationSyncPublisher serviceFeeCalculationSyncPublisher;
    private final MerchantService merchantService;
    private final LarkRobotMonitor larkRobotMonitor;
    private final StatementApis statementApis;

    @Lazy
    @Autowired
    private TransactionSyncService transactionSyncService;

    @Override
    public void sync(final TransactionTypeCodeEnum typeCodeEnum) {

        final Long extension = Optional.ofNullable(dwProperties.getRequestWindowExtension())
                .orElse(0L);

        final QTaskFeeCalculation qTaskFeeCalculation = QTaskFeeCalculation.taskFeeCalculation;
        final TaskFeeCalculation finalData = jpaQueryFactory.select(qTaskFeeCalculation)
                .from(qTaskFeeCalculation)
                .where(qTaskFeeCalculation.transactionTypeCode.eq(typeCodeEnum))
                .orderBy(qTaskFeeCalculation.eventTimestamp.desc())
                .fetchFirst();

        final Long timeSpace = Optional.ofNullable(dwProperties.getTimeSpace()).orElse(1800L);
        final Long to = Instant.now().getEpochSecond();
        final Long from = Optional.ofNullable(finalData).map(TaskFeeCalculation::getEventTimestamp)
                .filter(v -> v.compareTo(to) <= 0 && v.compareTo(to - timeSpace) >= 0)
                .orElseGet(() -> to - timeSpace);

        log.info("fee calculation sync, init type: {} from: {}, init to: {}, extension: {}",
                typeCodeEnum.getCode(), from, to, extension);

        sync(new SyncTaskFeeCalculationVo(typeCodeEnum, from, to, List.of(), extension, false));
    }

    @SneakyThrows
    @Override
    public void sync(final SyncTaskFeeCalculationVo vo) {

        if (Objects.isNull(vo.getExtension())) {
            vo.setExtension(dwProperties.getRequestWindowExtension());
        }
        final List<String> excludeAccount = feeCalculationProperties.getSyncExcludeAccount();

        final Long maxGetValueInterval = dwProperties.getMaxGetValueInterval();
        int cycles = Long.valueOf((vo.getTo() - vo.getFrom()) / maxGetValueInterval).intValue();
        log.info("fee calculation sync start , type: {}, from: {}, to: {}, "
                        + "maxGetValueInterval: {}, cycles: {}", vo.getTypeCodeEnum().getCode(),
                vo.getFrom(), vo.getTo(), maxGetValueInterval, cycles);

        for (int i = 0; i <= cycles; i++) {
            long realFrom = vo.getFrom() + (i * maxGetValueInterval);
            long realTo = i == cycles ? vo.getTo() : realFrom + maxGetValueInterval;

            // Step1: Get the data from the data warehouse
            final DwResponse<DwPage<DwSyncTransactionDto>> res = Optional.ofNullable(
                            vo.getUseFinalStatusTime()).filter(v -> v)
                    .map(v -> // Use final status time
                            dataWarehouseFeign.getTransactionData(
                                    vo.getTypeCodeEnum(), false,
                                    realFrom - vo.getExtension(), realTo))
                    .orElseGet(() -> // Use event time
                            dataWarehouseFeign.getTransactionDataByObjectIdTime(
                                    vo.getTypeCodeEnum(), false,
                                    realFrom - vo.getExtension(), realTo));
            if (!res.isSuccess()) {
                log.error("fee calculation sync, data warehouse visit failed, repeat later, "
                                + "type: {}, from: {}, to: {} , cycle times: {} res: {}",
                        vo.getTypeCodeEnum().getCode(), realFrom - vo.getExtension(),
                        realTo, i, res);

                larkRobotMonitor.error("Data Warehouse Sync Error", String.format(
                                "Data warehouse sync use param from '%s' and to '%s', %s sync error.",
                                realFrom - vo.getExtension(),
                                realTo, vo.getTypeCodeEnum().getCode()),
                        String.format("Response: %s", res));
                return;
            }

            log.info("fee calculation sync from data warehouse , "
                            + "type: {}, from: {}, to: {}, cycle times: {}, count: {}",
                    vo.getTypeCodeEnum().getCode(), realFrom - vo.getExtension(),
                    realTo, i, res.getData().getResults().size());

            // Step2: Filter the data and publish
            final List<DwSyncTransactionDto> data =
                    res.getData().getResults().stream().filter(x -> {

                        final String key =
                                buildAccountIdentifier(x.getMerchantCode(), x.getCountry(),
                                        x.getTransactionType());

                        final MerchantAccountBo merchantAccountInfo =
                                merchantService.getMerchantAccountInfo(key);

                        if (!checkMerchantAccount(merchantAccountInfo, x)) {
                            return false;
                        }

                        if (!checkProduct(merchantAccountInfo, x)) {
                            return false;
                        }

                        // test data is not calculate
                        if (Optional.ofNullable(x.getFlags()).orElse(false)) {
                            return false;
                        }

                        // when the same unique_id data is refunded status, not calculate
                        if (TransactionStatusEnum.INITIAL_STATUS.getCode()
                                .equals(x.getTransactionStatus())) {
                            return false;
                        }

                        if (Objects.nonNull(vo.getFilters()) && !vo.getFilters().isEmpty()) {
                            return vo.getFilters().stream().anyMatch(f -> f.equals(key));
                        }

                        // exclude account
                        return excludeAccount.stream().noneMatch(f -> f.equals(key));
                    }).collect(Collectors.toList());

            // Step3: Get the refund/charge back/reject data to single publish
            final List<DwSyncTransactionDto> singleData = Lists.newArrayList();
            final List<DwSyncTransactionDto> otherData = Lists.newArrayList();
            data.forEach(v -> {
                if (DirectionTypeEnum.parse(v.getDirectionCode()).getRank().compareTo(2) == 0) {
                    singleData.add(v);
                } else {
                    otherData.add(v);
                }
            });

            // Publish
            singleData.forEach(v -> serviceFeeCalculationSyncPublisher.publish(
                    new ServiceFeeCalculationSyncMsg(SnowflakeIdUtil.generate(), List.of(v))));

            // Publish
            Lists.partition(otherData, feeCalculationProperties.getBatchQuantity())
                    .forEach(listV -> serviceFeeCalculationSyncPublisher.publish(
                            new ServiceFeeCalculationSyncMsg(SnowflakeIdUtil.generate(), listV)));

            Thread.sleep(feeCalculationProperties.getSyncDelayMs());
        }
    }

    @Override
    public void syncHandle(final List<DwSyncTransactionDto> bos) {

        // Step1: Get the old data
        log.info("fee calculation sync handle, get old data by unique ids, size: {}", bos.size());
        final Map<String, TaskFeeCalculation> dbDataMap = getOldDataByUniqueIds(bos);

        // Step2: Handle the syncData
        log.info("fee calculation sync handle, handle sync data, size: {}", bos.size());
        final List<String> waitSettleData = Lists.newArrayList();
        final Map<String, PreCalculateFeeDto> preCalculateFeeDtoMap = new HashMap<>();
        for (final DwSyncTransactionDto syncData : bos) {

            // exclude account
            log.info("fee calculation sync handle, exclude account, syncData: {}", syncData);
            if (Optional.ofNullable(feeCalculationProperties.getSyncExcludeAccount())
                    .orElse(List.of()).contains(buildAccountIdentifier(
                            syncData.getMerchantCode(),
                            syncData.getCountry(),
                            syncData.getTransactionType()))) {
                continue;
            }

            // initialize data
            log.info("fee calculation sync handle, initialize data, syncData: {}", syncData);
            TaskFeeCalculation dbData = Optional.ofNullable(dbDataMap.get(syncData.getUniqueId()))
                    .orElse(buildDefaultTaskFeeCalculation(
                            DirectionTypeEnum.parse(syncData.getDirectionCode())));

            // fixing duplicate unique IDs among different merchants
            log.info("fee calculation sync handle, fixing "
                            + "duplicate unique IDs, dbData: {}, syncData: {}",
                    dbData, syncData);
            dbData = changeUniqueId(dbDataMap, dbData, syncData);

            // update shown information
            log.info("fee calculation sync handle, update shown "
                            + "information, dbData: {}, syncData: {}",
                    dbData, syncData);
            transactionSyncService.updateShownInfo(dbData, syncData);

            // allow in_progress -> rejected to charge a fee
            allowRejectedDebit(dbData, syncData);

            // update calculate information
            log.info("fee calculation sync handle, update calculate information, "
                            + "waitSettleData: {}, dbData: {}, syncData: {}",
                    waitSettleData, dbData, syncData);
            updateCalculateInfo(waitSettleData, dbData, syncData);

            // pre calculate fee for in_progress amount
            if (dbData.getLifecycleStatus().getRank()
                    .compareTo(TransactionStatusEnum.IN_PROGRESS.getRank()) >= 0) {
                try {
                    log.info("fee calculation sync handle, "
                                    + "pre calculate fee for in_progress amount, uniqueId: {}",
                            dbData.getUniqueId());
                    putInProgressData(dbData, preCalculateFeeDtoMap);
                } catch (ApplicationException e) {
                    if (WorkerExceptionCode.LOAD_FEE_CONFIG_FAILURE.getCode().equals(e.getCode())) {
                        log.error("fee calculation sync handle, " +
                                        "pre calculate fee for in_progress amount failure, " +
                                        "cause load fee config failure, uniqueId: {}",
                                syncData.getUniqueId(), e);
                        waitSettleData.remove(dbData.getUniqueId());
                        final String info = String.format(
                                "Merchant Code: %s\\nCountry: %s\\nTransaction Type: %s\\n" +
                                        "Product: %s\\nCurrency: %s\\nUniqueId: %s\\nException: %s",
                                syncData.getMerchantCode(), syncData.getCountry(),
                                syncData.getTransactionType(), syncData.getProductCode(),
                                syncData.getCurrency(), syncData.getUniqueId(), e.getMessage());
                        larkRobotMonitor.error("Data Warehouse Sync Warn", info,
                                "pre calculate fee but load fee config failure, it will be ignore");
                        continue;
                    }
                    throw e;
                }
            }

            // publish refund
            log.info("fee calculation sync handle, publish refund, dbData: {}, syncData: {}",
                    dbData, syncData);
            publishRefund(dbData, syncData);

            dbDataMap.put(dbData.getUniqueId(), dbData);
        }
        log.info("fee calculation sync handle, handle sync data end, size: {}", bos.size());

        // save or update
        log.info("fee calculation sync handle, save or update, size: {}", dbDataMap.size());
        final List<TaskFeeCalculation> dbDataList =
                taskFeeCalculationRepository.saveAll(dbDataMap.values());

        // publish in progress data
        final List<PreCalculateFeeDto> preCalculateFeeDtoList =
                new ArrayList<>(preCalculateFeeDtoMap.values());
        log.info("fee calculation sync handle, publish in progress data, size: {}",
                preCalculateFeeDtoList.size());
        statementApis.syncTransactionInProgress(entityToVo(preCalculateFeeDtoList));

        // publish settle data
        log.info("fee calculation sync handle, publish settle data, size: {}", dbDataList.size());
        publishSettle(dbDataList.stream()
                .filter(v -> !feeCalculationProperties.getOnlyUpdate()
                        && waitSettleData.contains(v.getUniqueId()))
                .collect(Collectors.toList()));
    }

    private void putInProgressData(
            final TaskFeeCalculation dbData,
            final Map<String, PreCalculateFeeDto> preCalculateFeeDtoMap) {

        final PreCalculateFeeDto preCalculateFeeDto =
                taskTransactionService.feeTrialCalculate(Lists.newArrayList(dbData)).get(0);

        final PreCalculateFeeDto mapData = preCalculateFeeDtoMap.get(dbData.getUniqueId());
        if (mapData == null || preCalculateFeeDto.getLifecycleStatus().getRank()
                .compareTo(mapData.getLifecycleStatus().getRank()) > 0) {
            preCalculateFeeDtoMap.put(dbData.getUniqueId(), preCalculateFeeDto);
        }
    }

    @Override
    public void syncRerun(final SyncRerunTaskFeeCalculationVo vo) {
        final MerchantAccountBo merchantAccountBo = merchantService.getMerchantAccountInfo(
                buildAccountIdentifier(
                        vo.getMerchantCode(),
                        vo.getCountryCode().getCode(),
                        vo.getTransactionTypeCode().getCode())
        );

        final List<DirectionTypeEnum> SETTLED_REFUND_TYPES =
                List.of(DirectionTypeEnum.SETTLED, DirectionTypeEnum.REFUND);
        final List<DirectionTypeEnum> REJECTED_CHARGE_BACK_TYPES =
                List.of(DirectionTypeEnum.REJECTED, DirectionTypeEnum.CHARGE_BACK);

        final QTaskFeeCalculation entity = QTaskFeeCalculation.taskFeeCalculation;
        int index = 0;
        List<TaskFeeCalculation> oldData;
        do {
            oldData = jpaQueryFactory.select(entity).from(entity)
                    .where(entity.transactionTimestamp.goe(vo.getFrom())
                            .and(entity.transactionTimestamp.lt(vo.getTo()))
                            .and(entity.merchantCode.eq(vo.getMerchantCode()))
                            .and(entity.countryCode.eq(vo.getCountryCode()))
                            .and(entity.transactionTypeCode.eq(vo.getTransactionTypeCode()))
                            .and(entity.transactionStatus.eq(TransactionStatusEnum.SETTLED)))
                    .orderBy(entity.transactionTimestamp.asc())
                    .offset(index)
                    .limit(Constant.COMMON.BATCH_SYNC_SIZE)
                    .fetch();

            ListUtils.partition(oldData, feeCalculationProperties.getBatchQuantity()).forEach(x -> {

                final List<Long> settleIds = Lists.newArrayList();
                final List<Long> secondCalculateIds = Lists.newArrayList();


                for (final TaskFeeCalculation task : x) {
                    final Long nodeTimestamp = task.getCalculationNode().stream()
                            .filter(c -> DirectionTypeEnum.SETTLED == c.getDirectionType())
                            .findFirst().map(TaskFeeCalculation.CalculationNode::getTimeStamp)
                            .orElse(0L);
                    if (SETTLED_REFUND_TYPES.contains(task.getDirectionType())) {
                        task.setTaskStatus(CalculationTaskStateEnum.WAITING);
                        task.setVersion(task.getVersion() + 1);
                        settleIds.add(task.getId());
                    }

                    if (REJECTED_CHARGE_BACK_TYPES.contains(task.getDirectionType())
                            && nodeTimestamp.compareTo(vo.getFrom()) < 0) {
                        task.setTaskStatus(CalculationTaskStateEnum.WAITING);
                        task.setVersion(task.getVersion() + 1);
                        settleIds.add(task.getId());
                    }

                    if (REJECTED_CHARGE_BACK_TYPES.contains(task.getDirectionType())
                            && nodeTimestamp.compareTo(vo.getFrom()) >= 0) {
                        task.setTaskStatus(CalculationTaskStateEnum.WAITING);
                        task.setDirectionType(DirectionTypeEnum.SETTLED);
                        task.setTransactionTimestamp(nodeTimestamp);
                        task.setTransactionTime(LocalDateTimeUtil.instantToUtc(nodeTimestamp));
                        task.setVersion(task.getVersion() + 1);
                        settleIds.add(task.getId());
                        secondCalculateIds.add(task.getId());
                    }
                }

                taskFeeCalculationRepository.saveAllAndFlush(x);

                x.stream().filter(t -> settleIds.contains(t.getId()))
                        .collect(Collectors.groupingBy(v -> TaskFeeCalculationSettleBo.builder()
                                .merchantCode(v.getMerchantCode())
                                .countryCode(v.getCountryCode())
                                .transactionTypeCode(v.getTransactionTypeCode())
                                .isSettleFlg(
                                        TransactionStatusEnum.SETTLED == v.getTransactionStatus()
                                                &&
                                                DirectionTypeEnum.SETTLED == v.getDirectionType())
                                .date(LocalDateTimeUtil.utcToLocal(v.getTransactionTime(),
                                                merchantAccountBo.getTimezone())
                                        .format(LocalDateUtil.FORMAT_YYYYMMDD))
                                .build()))
                        .forEach((key, value) -> {
                            key.setSyncId(SnowflakeIdUtil.generate());
                            key.setRetryId(SnowflakeIdUtil.generate());
                            key.setTaskIdList(value.stream().map(TaskFeeCalculation::getId)
                                    .collect(Collectors.toList()));
                            publisher.publishEvent(
                                    new TaskFeeCalculationEvent.FeePublishSqsEvent(key));
                        });

                publisher.publishEvent(new TaskFeeCalculationEvent.FeeRerunSecondCalculateEvent(
                        vo.getMerchantCode(), vo.getCountryCode(), vo.getTransactionTypeCode(),
                        secondCalculateIds));

            });
            index += Constant.COMMON.BATCH_SYNC_SIZE;
        } while (oldData.size() >= Constant.COMMON.BATCH_SYNC_SIZE);
    }

    private Map<String, TaskFeeCalculation> getOldDataByUniqueIds(
            final List<DwSyncTransactionDto> bos
    ) {
        final QTaskFeeCalculation entity = QTaskFeeCalculation.taskFeeCalculation;
        return jpaQueryFactory.select(entity)
                .from(entity)
                .where(entity.uniqueId.in(bos.stream().map(DwSyncTransactionDto::getUniqueId)
                        .distinct().collect(Collectors.toList())))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch()
                .stream()
                .collect(Collectors.toMap(TaskFeeCalculation::getUniqueId, Function.identity()));
    }

    private TaskFeeCalculation changeUniqueId(
            final Map<String, TaskFeeCalculation> dataMap,
            final TaskFeeCalculation dbData,
            final DwSyncTransactionDto syncData
    ) {
        // check whether different Merchant do not have the same unique id.
        if (checkUniqueId(dbData, syncData)) {
            return dbData;
        }

        // otherwise, set a new unique id.
        final String newUniqueId =
                String.format("%s_%s", syncData.getUniqueId(), syncData.getMerchantCode());
        syncData.setUniqueId(newUniqueId);

        if (dataMap.containsKey(newUniqueId)) {
            return dataMap.get(newUniqueId);
        }

        final QTaskFeeCalculation entity = QTaskFeeCalculation.taskFeeCalculation;
        final TaskFeeCalculation newDbData = jpaQueryFactory.select(entity)
                .from(entity)
                .where(entity.uniqueId.eq(newUniqueId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();

        return Optional.ofNullable(newDbData).orElse(buildDefaultTaskFeeCalculation(
                DirectionTypeEnum.parse(syncData.getDirectionCode())));
    }

    private TaskFeeCalculation buildDefaultTaskFeeCalculation(
            final DirectionTypeEnum directionType
    ) {

        return TaskFeeCalculation.builder()
                .id(SnowflakeIdUtil.generate())
                .taskType(CalculationTaskTypeEnum.UNREPEATABLE)
                .taskStatus(CalculationTaskStateEnum.WAITING)
                .transactionStatus(TransactionStatusEnum.INITIAL_STATUS)
                .directionType(DirectionTypeEnum.REFUND.equals(directionType)
                        ? directionType : DirectionTypeEnum.SETTLED)
                .transactionTimestamp(0L)
                .lifecycleStatus(TransactionStatusEnum.INITIAL_STATUS)
                .lifecycleTimestamp(0L)
                .holdStatus(HoldStatusEnum.NORMAL)
                .version(0)
                .delFlag(false)
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .build();
    }

    public void updateShownInfo(
            final TaskFeeCalculation dbData,
            final DwSyncTransactionDto syncData
    ) {

        if (!isUpdateShownInfo(dbData, syncData)) {
            return;
        }

        // Pre Handle
        final ObjectNode others = Optional.ofNullable(dbData.getOthers())
                .orElse(new ObjectNode(JsonNodeFactory.withExactBigDecimals(true)))
                .setAll(Optional.ofNullable(syncData.getOthers())
                        .orElse(new ObjectNode(JsonNodeFactory.withExactBigDecimals(true))));
        final String vendor = Optional.ofNullable(syncData.getVendor())
                .filter(StringUtils::isNotBlank)
                .map(String::toUpperCase)
                .map(String::trim)
                .orElse("");
        final VendorCodeEnum vendorCode = DynamicConstant
                .parseIfNotBlank(VendorCodeEnum.class, vendor, vendor, vendor);

        // Check
        if (!checkVendor(vendorCode, syncData)) {
            throw CommonExceptionCode.PARAMETER_MISSING.exception("VendorCode");
        }

        // Update Data
        dbData.setUniqueId(syncData.getUniqueId());
        dbData.setMerchantCode(syncData.getMerchantCode());
        dbData.setMerchantReference(syncData.getMerchantReference());
        dbData.setCountryCode(CountryCodeEnum.parse(syncData.getCountry()));
        dbData.setTransactionTypeCode(TransactionTypeCodeEnum.parse(syncData.getTransactionType()));
        dbData.setProductCode(ProductCodeEnum.parse(syncData.getProductCode()));
        dbData.setAmount(syncData.getAmount());
        dbData.setCurrency(CurrencyEnum.parse(syncData.getCurrency()));
        dbData.setDocumentId(StringUtils.defaultIfBlank(syncData.getDocumentId(), ""));
        dbData.setDescription(StringUtils.defaultIfBlank(syncData.getDescription(), ""));
        dbData.setTradeTransactionType(syncData.getTradeTransactionType());
        dbData.setTradeTransferStatus(syncData.getTradeTransferStatus());
        dbData.setLifecycleStatus(TransactionStatusEnum.parse(syncData.getTransactionStatus()));
        dbData.setLifecycleTimestamp(syncData.getFinalStatusTime());
        dbData.setSubmitTimestamp(Optional.ofNullable(syncData.getSubmitTime())
                .filter(v -> v.compareTo(0L) > 0).orElse(syncData.getEventTime()));
        dbData.setEventTimestamp(syncData.getEventTime());
        dbData.setVendor(vendorCode);
        dbData.setSubMerchantId(syncData.getSubMerchantId());
        dbData.setTransactionDataSource(Optional.ofNullable(syncData.getTransactionDataSource())
                .map(TransactionDataSourceEnum::parse)
                .orElse(TransactionDataSourceEnum.TRADE));

        if (TransactionStatusEnum.SETTLED != dbData.getTransactionStatus()) {
            final long transactionTimestamp = Optional.ofNullable(syncData.getFinalStatusTime())
                    .filter(v -> v.compareTo(0L) > 0).orElse(syncData.getEventTime());
            dbData.setTransactionTime(LocalDateTimeUtil.instantToUtc(transactionTimestamp));
            dbData.setTransactionTimestamp(transactionTimestamp);
        }

        if (Objects.isNull(dbData.getCreatedTime())) {
            if (Objects.nonNull(syncData.getCreateTime())) {
                dbData.setCreatedTime(LocalDateTimeUtil.instantToUtc(syncData.getCreateTime()));
            } else {
                dbData.setCreatedTime(LocalDateTimeUtil.nowUtc());
            }
        }

        // other info
        dbData.setAccountName(extractText(others, "targetName")
                .map(accountName -> accountName.length() > 200 ? accountName.substring(0, 200) :
                        accountName).orElse(""));
        dbData.setSubProductCode(extractText(others, "targetAccount").orElse(""));
        others.put("uniqueId", dbData.getUniqueId());
        others.put("bankName", Optional.of(others).filter(v -> v.hasNonNull("bankCode"))
                .map(v -> String.valueOf(v.get("bankCode").asInt()))
                .map(v -> baseFeign.queryDictValue(DictionaryTypeEnum.BANK_CODE, v,
                        dbData.getCountryCode().getCode())).orElse(""));
        Optional.of(others).filter(v -> v.hasNonNull("refundAdditionalInfo"))
                .map(v -> v.get("refundAdditionalInfo"))
                .ifPresent(refundAdditionalInfo -> {
                    final String bankName = Optional.of(refundAdditionalInfo)
                            .filter(info -> info.hasNonNull("bankCode"))
                            .map(info -> String.valueOf(info.get("bankCode").asInt()))
                            .map(code -> baseFeign.queryDictValue(
                                    DictionaryTypeEnum.BANK_CODE, code,
                                    dbData.getCountryCode().getCode())).orElse("");

                    others.put("bankName", bankName);
                    if (refundAdditionalInfo instanceof ObjectNode) {
                        ((ObjectNode) refundAdditionalInfo).put("bankName", bankName);
                    }

                    if (refundAdditionalInfo.has("manualRefund")) {
                        others.put("manualRefund",
                                refundAdditionalInfo.get("manualRefund").asBoolean());
                    }
                });

        DirectionTypeEnum
                syncType = DirectionTypeEnum.parse(syncData.getDirectionCode());
        if (DirectionTypeEnum.REFUND == syncType) {
            dbData.setMerchantReference(Optional.of(others)
                    .filter(v -> v.hasNonNull("referenceId"))
                    .map(v -> v.get("referenceId").asText()).orElse(""));
        }

        if (syncType.getRank().compareTo(2) == 0) {
            others.put("referenceId", dbData.getUniqueId());
        }

        dbData.setOthers(others);

        // Update final state timestamp, use for transaction report
        if (Optional.ofNullable(feeCalculationProperties.getUpdateFinalStatusTimestampStatus())
                .orElse(List.of()).contains(dbData.getLifecycleStatus())) {
            dbData.setFinalStatusTimestamp(dbData.getLifecycleTimestamp());
        } else {
            dbData.setFinalStatusTimestamp(dbData.getSubmitTimestamp());
        }

    }

    private boolean isUpdateShownInfo(
            final TaskFeeCalculation dbData,
            final DwSyncTransactionDto syncData
    ) {

        // Step 1: Data initialization requires update
        if (Objects.isNull(dbData.getEventTimestamp())) {
            return true;
        }

        // Step 2: Abnormal lifecycle state, no update
        int lifecycleTimeCompare =
                dbData.getLifecycleTimestamp().compareTo(syncData.getFinalStatusTime());
        if (lifecycleTimeCompare > 0) {
            return false;
        }

        // Step 3: Abnormal transaction status, no update
        final TransactionStatusEnum dbStatus = dbData.getLifecycleStatus();
        final TransactionStatusEnum syncStatus =
                TransactionStatusEnum.parse(syncData.getTransactionStatus());
        final int statusRankCompare = dbStatus.getRank().compareTo(syncStatus.getRank());
        if (statusRankCompare > 0) {
            if (lifecycleTimeCompare != 0) {
                larkRobotMonitor.warn("Data Warehouse Sync Warn",
                        larkRobotMonitor.buildLarkAlarmContent(
                                syncData.getMerchantCode(),
                                syncData.getCountry(),
                                syncData.getTransactionType(),
                                syncData.getUniqueId(),
                                String.format("The status changed: %s -> %s",
                                        dbStatus, syncStatus)), "");
            }
            return false;
        }

        // Step 4: Abnormal DirectionType, no update
        int directionRankCompare = dbData.getDirectionType().getRank()
                .compareTo(DirectionTypeEnum.parse(syncData.getDirectionCode()).getRank());
        if (directionRankCompare > 0) {
            return false;
        }

        return statusRankCompare != 0 || lifecycleTimeCompare != 0 || directionRankCompare != 0;
    }

    private void updateCalculateInfo(
            final List<String> waitSettleData,
            final TaskFeeCalculation dbData,
            final DwSyncTransactionDto syncData
    ) {

        final TransactionStatusEnum syncStatus =
                TransactionStatusEnum.parse(syncData.getTransactionStatus());
        final DirectionTypeEnum syncType = DirectionTypeEnum.parse(syncData.getDirectionCode());

        // check whether the data needs to be updated
        if (!isUpdateCalculateInfo(dbData, syncData, syncStatus, syncType)) {
            return;
        }

        // update data
        dbData.setTransactionStatus(syncStatus);
        dbData.setDirectionType(syncType);
        dbData.setTransactionTime(LocalDateTimeUtil.instantToUtc(syncData.getFinalStatusTime()));
        dbData.setTransactionTimestamp(syncData.getFinalStatusTime());

        List<TaskFeeCalculation.CalculationNode> calculationNode =
                Optional.ofNullable(dbData.getCalculationNode()).orElse(Lists.newArrayList());
        calculationNode.add(new TaskFeeCalculation.CalculationNode(syncType,
                dbData.getTransactionTimestamp()));
        dbData.setCalculationNode(calculationNode);

        if (syncType.getRank().compareTo(2) >= 0) {
            dbData.setTaskStatus(CalculationTaskStateEnum.WAITING);
        }

        // add the result to waitSettleData
        waitSettleData.add(dbData.getUniqueId());
    }

    private boolean isUpdateCalculateInfo(
            final TaskFeeCalculation dbData,
            final DwSyncTransactionDto syncData,
            final TransactionStatusEnum syncStatus,
            final DirectionTypeEnum syncType
    ) {
        if (TransactionStatusEnum.SETTLED != syncStatus) {
            return false;
        }

        if (TransactionStatusEnum.SETTLED == dbData.getTransactionStatus()
                && dbData.getDirectionType().getRank().compareTo(syncType.getRank()) >= 0) {
            return false;
        }

        if (List.of(DirectionTypeEnum.CHARGE_BACK, DirectionTypeEnum.REJECTED).contains(syncType)
                && (DirectionTypeEnum.SETTLED != dbData.getDirectionType()
                || CalculationTaskStateEnum.SUCCESS != dbData.getTaskStatus())) {
            throw WorkerExceptionCode.FEE_CALCULATION_DATA_NOT_SETTLE.exception(
                    syncData.getUniqueId());
        }

        if (DirectionTypeEnum.CHARGE_BACK_REJECTED == syncType
                && (DirectionTypeEnum.CHARGE_BACK != dbData.getDirectionType()
                || CalculationTaskStateEnum.SUCCESS != dbData.getTaskStatus())) {
            throw WorkerExceptionCode.FEE_CALCULATION_DATA_NOT_SETTLE.exception(
                    syncData.getUniqueId());
        }

        if (syncData.getFinalStatusTime().compareTo(0L) == 0) {
            throw WorkerExceptionCode.FEE_CALCULATION_SYNC_ERROR.exception();
        }

        return true;
    }

    private Boolean checkMerchantAccount(
            final MerchantAccountBo merchantAccountInfo,
            final DwSyncTransactionDto dto
    ) {

        // Non-existent account not calculate
        if (Objects.isNull(merchantAccountInfo)) {
            log.warn("fee calculation sync from data warehouse, "
                    + "doesn't have this merchant or account, data: {}", dto);

            final String larkMonitorAccountKey =
                    String.format(Constant.CACHE.LARK_MONITOR_DONT_HAVE_ACCOUNT,
                            dto.getMerchantCode(), dto.getCountry(), dto.getTransactionType());

            final DwSyncTransactionDto dataDontHaveAccount =
                    redisCacheUtil.getCacheObject(larkMonitorAccountKey);

            if (Objects.nonNull(dataDontHaveAccount)) {
                return false;
            }

            final String content =
                    larkRobotMonitor.buildLarkAlarmContent(dto.getMerchantCode(),
                            dto.getCountry(), dto.getTransactionType(), dto.getUniqueId(),
                            "Doesn't have the merchant or account");

            larkRobotMonitor.warn("Data Warehouse Sync Warn", content, "");

            redisCacheUtil.setCacheObject(larkMonitorAccountKey, dto,
                    Optional.ofNullable(monitorProperties.getWarnAlarmInterval()).orElse(3),
                    TimeUnit.HOURS);
            return false;
        }
        return true;
    }

    private Boolean checkProduct(
            final MerchantAccountBo merchantAccountInfo,
            final DwSyncTransactionDto dto
    ) {

        AccountProductDto product = merchantAccountInfo.getProducts()
                .get(dto.getProductCode());

        // Non-existent product or not business open mode is not calculate
        if (Objects.isNull(product)) {
            log.warn("fee calculation sync from data warehouse, "
                    + "doesn't have this product or this product is not "
                    + "business open mode, data: {}", dto);

            final String larkMonitorProductKey =
                    String.format(Constant.CACHE.LARK_MONITOR_DONT_HAVE_PRODUCT,
                            dto.getMerchantCode(), dto.getCountry(),
                            dto.getTransactionType(), dto.getProductCode());

            final DwSyncTransactionDto dataDontHaveProduct =
                    redisCacheUtil.getCacheObject(larkMonitorProductKey);

            if (Objects.nonNull(dataDontHaveProduct)) {
                return false;
            }
            final String content =
                    larkRobotMonitor.buildLarkAlarmContent(dto.getMerchantCode(),
                            dto.getCountry(), dto.getTransactionType(), dto.getUniqueId(),
                            "Doesn't have the product: " + dto.getProductCode());

            larkRobotMonitor.warn("Data Warehouse Sync Warn", content, "");

            redisCacheUtil.setCacheObject(larkMonitorProductKey, dto,
                    Optional.ofNullable(monitorProperties.getWarnAlarmInterval()).orElse(3),
                    TimeUnit.HOURS);
            return false;
        }
        return true;
    }

    private Boolean checkVendor(final VendorCodeEnum vendorCode, final DwSyncTransactionDto bo) {

        if (StringUtils.isNotBlank(bo.getVendor()) && Objects.isNull(vendorCode)) {

            final String larkMonitorVendorKey = String.format(
                    Constant.CACHE.LARK_MONITOR_DONT_HAVE_VENDOR_TYPE, bo.getVendor());

            final DwSyncTransactionDto dataDontHaveVendor =
                    redisCacheUtil.getCacheObject(larkMonitorVendorKey);

            if (Objects.nonNull(dataDontHaveVendor)) {
                return false;
            }

            final String content = larkRobotMonitor.buildLarkAlarmContent(bo.getMerchantCode(),
                    bo.getCountry(), bo.getTransactionType(), bo.getUniqueId(),
                    "Doesn't have the vendor type: " + bo.getVendor());

            larkRobotMonitor.error("Data Warehouse Sync Warn", content, "");

            redisCacheUtil.setCacheObject(larkMonitorVendorKey, bo,
                    Optional.ofNullable(monitorProperties.getWarnAlarmInterval()).orElse(3),
                    TimeUnit.HOURS);

            return false;
        }

        if (StringUtils.isBlank(bo.getVendor()) && TransactionStatusEnum.SETTLED
                == TransactionStatusEnum.parse(bo.getTransactionStatus())) {
            final String larkMonitorVendorKey = String.format(
                    Constant.CACHE.LARK_MONITOR_DONT_HAVE_VENDOR, bo.getUniqueId());

            final DwSyncTransactionDto dataDontHaveVendor =
                    redisCacheUtil.getCacheObject(larkMonitorVendorKey);

            if (Objects.nonNull(dataDontHaveVendor)) {
                return false;
            }
            final String content = larkRobotMonitor.buildLarkAlarmContent(bo.getMerchantCode(),
                    bo.getCountry(), bo.getTransactionType(), bo.getUniqueId(),
                    "Transaction Settled, doesn't have the vendor. Unique Id:"
                            + bo.getUniqueId());

            larkRobotMonitor.error("Data Warehouse Sync Warn", content, "");

            redisCacheUtil.setCacheObject(larkMonitorVendorKey, bo,
                    Optional.ofNullable(monitorProperties.getWarnAlarmInterval()).orElse(3),
                    TimeUnit.HOURS);

            return false;
        }

        return true;
    }

    private boolean checkUniqueId(
            final TaskFeeCalculation dbData,
            final DwSyncTransactionDto syncData
    ) {
        if (StringUtils.isNoneBlank(dbData.getMerchantCode(), syncData.getMerchantCode())
                && !dbData.getMerchantCode().equals(syncData.getMerchantCode())) {

            final String larkMonitorKey = String.format(
                    Constant.CACHE.LARK_MONITOR_SAME_UNIQUE_ID, dbData.getUniqueId());

            final DwSyncTransactionDto dto = redisCacheUtil.getCacheObject(larkMonitorKey);

            if (Objects.nonNull(dto)) {
                return false;
            }

            final String content =
                    larkRobotMonitor.buildLarkAlarmContent(syncData.getMerchantCode(),
                            syncData.getCountry(), syncData.getTransactionType(),
                            syncData.getUniqueId(),
                            String.format("Different merchant have same unique id. \n"
                                            + "Unique Id: %s, Merchant Code: (db)%s and (sync)%s."
                                            + "Same Unique Id changed to : %s_%s",
                                    dbData.getUniqueId(),
                                    syncData.getMerchantCode(), syncData.getMerchantCode(),
                                    syncData.getUniqueId(), syncData.getMerchantCode()));

            larkRobotMonitor.error("Data Warehouse Sync Warn", content, "");

            redisCacheUtil.setCacheObject(larkMonitorKey, syncData,
                    Optional.ofNullable(monitorProperties.getWarnAlarmInterval()).orElse(3),
                    TimeUnit.HOURS);

            return false;
        }
        return true;
    }

    private void publishRefund(
            final TaskFeeCalculation dbData,
            final DwSyncTransactionDto syncData
    ) {
        if (DirectionTypeEnum.REFUND == DirectionTypeEnum.parse(syncData.getDirectionCode())) {
            publisher.publishEvent(new TaskFeeCalculationEvent.RefundPublishSqsEvent(dbData));
        }
    }

    private List<TransactionInProgressVo> entityToVo(
            final List<PreCalculateFeeDto> preCalculateFeeDtoList
    ) {
        log.info("publishInProgress entityToVo dbDataList size={}", preCalculateFeeDtoList);
        return preCalculateFeeDtoList.stream()
                .map(dbData -> {
                    final BigDecimal fee = dbData.getTransactionFeeList().stream()
                            .filter(f -> FeeGroupEnum.TRANSACTION_FEE == f.getFeeGroup())
                            .map(TransactionFeeDto::getSettlementAmount)
                            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

                    final BigDecimal tax = dbData.getTransactionFeeList().stream()
                            .filter(f -> FeeGroupEnum.TAX == f.getFeeGroup())
                            .map(TransactionFeeDto::getSettlementAmount)
                            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

                    return TransactionInProgressVo.builder()
                            .transactionId(dbData.getTransactionId())
                            .uniqueId(dbData.getUniqueId())
                            .merchantCode(dbData.getMerchantCode())
                            .subMerchantId(dbData.getSubMerchantId())
                            .countryCode(dbData.getCountryCode())
                            .transactionTypeCode(dbData.getTransactionTypeCode())
                            .amount(dbData.getAmount())
                            .fee(fee)
                            .tax(tax)
                            .netAmount(dbData.getSettleAmount().add(fee).add(tax))
                            .currency(dbData.getCurrency())
                            .transactionStatus(dbData.getTransactionStatus())
                            .directionType(dbData.getDirectionType())
                            .lifecycleStatus(dbData.getLifecycleStatus())
                            .lifecycleTimestamp(dbData.getLifecycleTimestamp())
                            .vendor(dbData.getVendor())
                            .build();
                })
                .distinct()
                .collect(Collectors.toList());
    }

    private void publishSettle(final List<TaskFeeCalculation> settleData) {
        final long syncId = SnowflakeIdUtil.generate();
        settleData.stream().collect(Collectors.groupingBy(x -> TaskFeeCalculationSettleBo.builder()
                        .merchantCode(x.getMerchantCode())
                        .countryCode(x.getCountryCode())
                        .transactionTypeCode(x.getTransactionTypeCode())
                        .isSettleFlg(TransactionStatusEnum.SETTLED == x.getTransactionStatus()
                                && DirectionTypeEnum.SETTLED == x.getDirectionType())
                        .date(LocalDateTimeUtil.utcToLocal(
                                        x.getTransactionTime(),
                                        merchantService.getMerchantAccountInfo(
                                                        buildAccountIdentifier(
                                                                x.getMerchantCode(),
                                                                x.getCountryCode().getCode(),
                                                                x.getTransactionTypeCode().getCode()))
                                                .getTimezone())
                                .format(LocalDateUtil.FORMAT_YYYYMMDD))
                        .build()))
                .forEach((key, value) -> {
                    key.setSyncId(syncId);
                    key.setRetryId(SnowflakeIdUtil.generate());
                    key.setTaskIdList(value.stream().map(TaskFeeCalculation::getId)
                            .collect(Collectors.toList()));
                    publisher.publishEvent(new TaskFeeCalculationEvent.FeePublishSqsEvent(key));
                });
    }

    private String buildAccountIdentifier(
            final String merchantCode,
            final String countryCode,
            final String transactionTypeCode
    ) {
        return String.format("%s_%s_%s", merchantCode, countryCode, transactionTypeCode);
    }

    public Optional<String> extractText(final JsonNode node, final String fieldName) {
        return Optional.ofNullable(node)
                .filter(v -> v.hasNonNull(fieldName))
                .map(v -> v.get(fieldName).asText());
    }

    private void allowRejectedDebit(
            final TaskFeeCalculation dbData,
            final DwSyncTransactionDto syncData
    ) {
        final boolean timeLimitCondition =
                LocalDateTimeUtil.utcToInstant(LocalDateTimeUtil.nowUtc())
                        >= coRejectedOrderProperties.getEnableTimestamp();
        final boolean specifiedMerchantCondition =
                coRejectedOrderProperties.getSpecifiedMerchants()
                        .contains(syncData.getMerchantCode());

        if (!(timeLimitCondition || specifiedMerchantCondition)) {
            return;
        }

        // just allow co payout account
        if (!CountryCodeEnum.CO.getCode().equals(syncData.getCountry())
                || !TransactionTypeCodeEnum.PAY_OUT.getCode()
                .equals(syncData.getTransactionType())) {
            return;
        }

        // dbData: transactionStatus: INITIAL_STATUS; directType: SETTLED
        // syncData: transactionStatus: REJECTED; directionCode: SETTLED
        // change to transactionStatus: SETTLED; directionCode: REJECTED_DEBIT
        if (!(TransactionStatusEnum.INITIAL_STATUS.equals(dbData.getTransactionStatus())
                && DirectionTypeEnum.SETTLED.equals(dbData.getDirectionType())
                && TransactionStatusEnum.REJECTED.getCode().equals(syncData.getTransactionStatus())
                && DirectionTypeEnum.SETTLED.getCode().equals(syncData.getDirectionCode()))) {

            // CO PAY_OUT try to change status REJECTED to SETTLED, alarm
            if (DirectionTypeEnum.REJECTED_DEBIT.equals(dbData.getDirectionType())
                    && TransactionStatusEnum.SETTLED.getCode()
                    .equals(syncData.getTransactionStatus())) {
                log.error("rejected debit error, {} try to SETTLED, syncData: {}, dbData: {}",
                        syncData.getUniqueId(), syncData, dbData);
                final String content = larkRobotMonitor.buildLarkAlarmContent(
                        syncData.getMerchantCode(),
                        syncData.getCountry(),
                        syncData.getTransactionType(),
                        syncData.getUniqueId(),
                        "The status changed: The status changed: REJECTED -> SETTLED");
                larkRobotMonitor.error("Data Warehouse Sync Error", content,
                        "this settled will not be credited to the balance.");
            }

            return;
        }

        final String errorCode = syncData.getOthers().get("transferStatusCode").asText();
        if (StringUtils.isBlank(errorCode)
                || coRejectedOrderProperties.getDebitExcludeErrorCode().contains(errorCode)) {
            return;
        }

        log.info("Rejected_debit data will be billed: {}", syncData);

        syncData.setTransactionStatus(TransactionStatusEnum.SETTLED.getCode());
        syncData.setDirectionCode(DirectionTypeEnum.REJECTED_DEBIT.getCode());
    }


}
