package com.liquido.worker.service.sync.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.LockModeType;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DataSyncStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.worker.aws.sqs.msg.ServiceShoplazzaSyncMsg;
import com.liquido.worker.aws.sqs.publish.ServiceShoplazzaSyncPublisher;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.properties.LarkProperties;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.enums.VersionEnum;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.feign.BaseService;
import com.liquido.worker.feign.DataWarehouseFeign;
import com.liquido.worker.pojo.bo.DwPage;
import com.liquido.worker.pojo.bo.DwResponse;
import com.liquido.worker.pojo.dto.DwSyncShoplazzaDto;
import com.liquido.worker.pojo.entity.QShoplazza;
import com.liquido.worker.pojo.entity.Shoplazza;
import com.liquido.worker.pojo.vo.SyncDataVo;
import com.liquido.worker.repository.ShoplazzaRepository;
import com.liquido.worker.repository.TaskFeeCalculationRepository;
import com.liquido.worker.service.sync.ShoplazzaSyncService;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
public class ShoplazzaSyncServiceImpl implements ShoplazzaSyncService {

    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final DataWarehouseFeign dataWarehouseFeign;
    private final WorkerProperties.DataWarehouseProperties dwProperties;
    private final LarkProperties.MonitorProperties monitorProperties;
    private final WorkerProperties.ShoplazzaProperties shoplazzaProperties;
    private final ServiceShoplazzaSyncPublisher serviceShoplazzaSyncPublisher;
    private final BaseService baseService;
    private final ShoplazzaRepository shoplazzaRepository;
    private final TaskFeeCalculationRepository taskFeeCalculationRepository;

    private final LarkRobotMonitor larkRobotMonitor;

    @Override
    public void sync(final TransactionTypeCodeEnum typeCodeEnum) {
        final Long extension =
                Optional.ofNullable(dwProperties.getRequestWindowExtension()).orElse(0L);

        final QShoplazza qShoplazza = QShoplazza.shoplazza;
        final Shoplazza finalData = jpaQueryFactory.select(qShoplazza).from(qShoplazza)
                .orderBy(qShoplazza.eventTimestamp.desc()).fetchFirst();

        final Long timeSpace = Optional.ofNullable(dwProperties.getTimeSpace()).orElse(1800L);
        final Long to = Instant.now().getEpochSecond();
        final Long from = Optional.ofNullable(finalData).map(Shoplazza::getEventTimestamp)
                .filter(v -> v.compareTo(to) <= 0 && v.compareTo(to - timeSpace) >= 0)
                .orElseGet(() -> to - timeSpace);

        log.info("shoplazza sync, init from: {}, init to: {}, extension: {}", from, to, extension);

        sync(new SyncDataVo(typeCodeEnum, from, to, List.of(), extension));
    }

    @SneakyThrows
    @Override
    public void sync(SyncDataVo vo) {
        if (Objects.isNull(vo.getExtension())) {
            vo.setExtension(dwProperties.getRequestWindowExtension());
        }

        final Long maxGetValueInterval = dwProperties.getMaxGetValueInterval();
        int cycles = Long.valueOf((vo.getTo() - vo.getFrom()) / maxGetValueInterval).intValue();
        log.info("shoplazza sync start , from: {}, to: {}, maxGetValueInterval: {}, "
                + "cycles: {}", vo.getFrom(), vo.getTo(), maxGetValueInterval, cycles);

        for (int i = 0; i <= cycles; i++) {
            long realFrom = vo.getFrom() + (i * maxGetValueInterval);
            long realTo = i == cycles ? vo.getTo() : realFrom + maxGetValueInterval;
            final DwResponse<DwPage<DwSyncShoplazzaDto>> res =
                    dataWarehouseFeign.getShoplazzaDataByObjectIdTime(
                            vo.getTypeCodeEnum(), false, realFrom - vo.getExtension(), realTo);
            if (!res.isSuccess()) {
                log.error("shoplazza sync, data warehouse visit failed, repeat later, "
                                + "from: {}, to: {} , cycle times: {} res: {}",
                        realFrom - vo.getExtension(),
                        realTo, i, res);

                larkRobotMonitor.error("Shoplazza Sync Error",
                        String.format("Shoplazza sync use param from '%s' and to '%s', "
                                + "sync error.", realFrom - vo.getExtension(), realTo),
                        String.format("Response: %s", res));
                return;
            }

            log.info("shoplazza sync from data warehouse , "
                            + "from: {}, to: {}, cycle times: {}, count: {}",
                    realFrom - vo.getExtension(),
                    realTo, i, res.getData().getResults().size());

            final List<DwSyncShoplazzaDto> dtoList = res.getData().getResults().stream()
                    .filter(x -> {

                        if (!checkMerchantName(x)) {
                            return false;
                        }

                        final String key =
                                String.format("%s_%s", x.getMerchantName(), x.getCountry());

                        if (Objects.nonNull(vo.getFilters()) && !vo.getFilters().isEmpty()) {
                            return vo.getFilters().stream().anyMatch(f -> f.equals(key));
                        }

                        return shoplazzaProperties.getExcludeAccount().stream()
                                .noneMatch(f -> f.equals(key));
                    }).collect(Collectors.toList());

            if (ObjectUtils.isNotEmpty(dtoList)) {
                Lists.partition(dtoList, shoplazzaProperties.getBatchQuantity())
                        .forEach(v -> serviceShoplazzaSyncPublisher.publish(
                                new ServiceShoplazzaSyncMsg(v))
                        );
            }

            Thread.sleep(shoplazzaProperties.getSyncDelayMs());

        }
    }

    @Override
    public void syncHandle(final List<DwSyncShoplazzaDto> dtoList) {

        if (ObjectUtils.isEmpty(dtoList)) {
            return;
        }

        // Get the old data
        final QShoplazza entity = QShoplazza.shoplazza;
        final Map<String, Shoplazza> dataMap = jpaQueryFactory.select(entity).from(entity)
                .where(entity.linkId.in(
                        dtoList.stream().map(DwSyncShoplazzaDto::getLinkId).distinct()
                                .collect(Collectors.toList())))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE).fetch().stream()
                .collect(Collectors.toMap(Shoplazza::getLinkId, Function.identity()));

        // Compare and select updated/insert data
        for (final DwSyncShoplazzaDto bo : dtoList) {
            final Shoplazza data = Optional.ofNullable(dataMap.get(bo.getLinkId()))
                    .orElse(Shoplazza.builder()
                            .transactionTypeCode(TransactionTypeCodeEnum.PAY_IN)
                            .version(VersionEnum.NORMAL).delFlag(false)
                            .createdTime(LocalDateTimeUtil.nowUtc())
                            .updatedTime(LocalDateTimeUtil.nowUtc()).build());

            if (!checkShoplazza(data, bo)) {
                continue;
            }

            dataMap.put(bo.getLinkId(), convertToShoplazza(data, bo));
        }

        // save or update
        if (ObjectUtils.isNotEmpty(dataMap)) {
            shoplazzaRepository.saveAll(dataMap.values());
        }
    }

    private boolean checkShoplazza(final Shoplazza old, final DwSyncShoplazzaDto now) {

        if (Objects.isNull(now.getCountry())) {
            return false;
        }

        if (Objects.isNull(old.getEventTimestamp())) {
            return true;
        }

        if (old.getEventTimestamp().compareTo(now.getEventTime()) > 0) {
            return false;
        }

        if (Objects.isNull(now.getFinalStatusTimestamp())
                || now.getFinalStatusTimestamp().compareTo(0L) <= 0) {
            now.setFinalStatusTimestamp(now.getEventTime());
        }

        if ((Objects.isNull(old.getTransactionTimestamp())
                || old.getTransactionTimestamp().compareTo(0L) == 0)
                && (Objects.nonNull(now.getFinalStatusTimestamp())
                && now.getFinalStatusTimestamp().compareTo(0L) > 0)) {
            return true;
        } else if (ObjectUtils.allNotNull(old.getTransactionTimestamp(),
                now.getFinalStatusTimestamp())
                && old.getTransactionTimestamp().compareTo(now.getFinalStatusTimestamp()) > 0) {
            return false;
        }

        if (old.getPaymentStatus().getRank()
                .compareTo(DataSyncStatusEnum.parse(now.getPaymentStatus()).getRank()) > 0) {
            return true;
        }

        if (Objects.isNull(now.getRefundTimestamp())
                || now.getRefundTimestamp().compareTo(0L) == 0) {
            return false;
        } else if ((Objects.isNull(old.getRefundTimestamp())
                || old.getRefundTimestamp().compareTo(0L) == 0)
                && now.getRefundTimestamp().compareTo(0L) > 0) {
            return true;
        } else if (ObjectUtils.allNotNull(old.getRefundTimestamp(), now.getRefundTimestamp())
                && old.getRefundTimestamp().compareTo(now.getRefundTimestamp()) > 0) {
            return false;
        }

        return !old.getRefunded().equals(now.getRefunded());
    }

    private Shoplazza convertToShoplazza(final Shoplazza dbData,
                                         final DwSyncShoplazzaDto dto) {
        final DataSyncStatusEnum paymentStatus = DataSyncStatusEnum.parse(dto.getPaymentStatus());
        dbData.setLinkId(dto.getLinkId());
        dbData.setOrderId(dto.getOrderId());
        dbData.setLink(dto.getLink());
        dbData.setShopDomain(dto.getShopDomain());
        dbData.setMerchantCode(dto.getMerchantName());
        dbData.setCountryCode(CountryCodeEnum.parse(dto.getCountry()));
        dbData.setTransactionTypeCode(TransactionTypeCodeEnum.PAY_IN);
        dbData.setProductCode(ProductCodeEnum.parse(dto.getProductCode()));
        dbData.setAmount(dto.getAmount());
        dbData.setCurrency(CurrencyEnum.parse(dto.getCurrency()));
        dbData.setPaymentStatus(paymentStatus);
        dbData.setSettledUniqueId(dto.getSettledVirgoId());
        if (dto.getCreateTime().compareTo(0L) == 0) {
            dbData.setSubmitTimestamp(dto.getEventTime());
            dbData.setSubmitTime(LocalDateTimeUtil.instantToUtc(dto.getEventTime()));
        } else {
            dbData.setSubmitTimestamp(dto.getCreateTime());
            dbData.setSubmitTime(LocalDateTimeUtil.instantToUtc(dto.getCreateTime()));
        }
        if (Objects.nonNull(dto.getFinalStatusTimestamp())
                && dto.getFinalStatusTimestamp().compareTo(0L) > 0) {
            dbData.setTransactionTimestamp(dto.getFinalStatusTimestamp());
            dbData.setTransactionTime(
                    LocalDateTimeUtil.instantToUtc(dto.getFinalStatusTimestamp()));
        }
        dbData.setRefunded(dto.getRefunded());
        dbData.setRefundedUniqueId(dto.getRefundedVirgoId());
        if (Objects.nonNull(dto.getRefundTimestamp())
                && dto.getRefundTimestamp().compareTo(0L) > 0) {
            dbData.setRefundTimestamp(dto.getRefundTimestamp());
            dbData.setRefundTime(LocalDateTimeUtil.instantToUtc(dto.getRefundTimestamp()));
        }
        dbData.setEventTimestamp(dto.getEventTime());
        dbData.setUserEmail(dto.getEmail());
        dbData.setUserPhone(dto.getPhone());
        if (DataSyncStatusEnum.SETTLED == paymentStatus
                && StringUtils.isNotBlank(dto.getSettledVirgoId())) {
            dbData.setOthers(Optional.ofNullable(
                            taskFeeCalculationRepository.findByUniqueId(dto.getSettledVirgoId()))
                    .map(fee -> new ObjectNode(JsonNodeFactory.withExactBigDecimals(true))
                            .put("documentId", fee.getDocumentId()))
                    .orElseGet(() -> settleDataNotFound(dto)));
        }
        return dbData;
    }

    @SneakyThrows
    private ObjectNode settleDataNotFound(final DwSyncShoplazzaDto dto) {
        final String larkMonitorKey = String.format(
                Constant.CACHE.LARK_MONITOR_SHOPLAZZA_NOT_FOUND_SEATTLE, dto.getLinkId());

        final Integer monitorCount = Optional.ofNullable(
                redisCacheUtil.<Integer>getCacheObject(larkMonitorKey)).orElse(1);

        if (monitorCount.compareTo(5) > 0) {

            final String content =
                    larkRobotMonitor.buildLarkAlarmContent(dto.getMerchantName(), dto.getCountry(),
                            TransactionTypeCodeEnum.PAY_IN.getCode(), dto.getLinkId(),
                            "The Shoplazza settled, but settle transaction "
                                    + "data not found. Settled virgo id: "
                                    + dto.getSettledVirgoId());

            larkRobotMonitor.warn("Shoplazza Sync Warn", content, "");
            return null;
        } else {
            redisCacheUtil.setCacheObject(larkMonitorKey, monitorCount + 1,
                    Optional.ofNullable(monitorProperties.getWarnAlarmInterval()).orElse(3),
                    TimeUnit.HOURS);
            Thread.sleep(monitorCount * 30000);
            throw WorkerExceptionCode.DATA_SYNC_NOT_FOUND_SETTLED_FEE.exception();
        }
    }

    private Boolean checkMerchantName(final DwSyncShoplazzaDto dto) {
        final String key = dto.getMerchantName();

        final MerchantDto merchants = baseService.getMerchantByCode(key);

        log.info("shoplazza sync merchantName={},merchantInfo={}", key, merchants);

        if (StringUtils.isBlank(key) || Objects.isNull(merchants)) {

            final String larkMonitorKey = String.format(
                    Constant.CACHE.LARK_MONITOR_SHOPLAZZA_NOT_HAVE_MERCHANT_NAME, key);

            final DwSyncShoplazzaDto monitorDto = redisCacheUtil.getCacheObject(larkMonitorKey);

            if (Objects.nonNull(monitorDto)) {
                return false;
            }

            final String content =
                    larkRobotMonitor.buildLarkAlarmContent(dto.getMerchantName(), dto.getCountry(),
                            TransactionTypeCodeEnum.PAY_IN.getCode(), dto.getLinkId(),
                            "Doesn't have the merchant: " + dto.getMerchantName());

            larkRobotMonitor.error("Shoplazza Sync Warn", content, "");

            redisCacheUtil.setCacheObject(larkMonitorKey, dto,
                    Optional.ofNullable(monitorProperties.getWarnAlarmInterval()).orElse(3),
                    TimeUnit.HOURS);

            return false;
        }
        return true;
    }

}
