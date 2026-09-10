package com.liquido.worker.service.sync.impl;

import java.math.BigDecimal;
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
import com.liquido.base.enums.DataSyncRefundStatusEnum;
import com.liquido.base.enums.DataSyncStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.worker.aws.sqs.msg.ServicePaymentLinkSyncMsg;
import com.liquido.worker.aws.sqs.publish.ServicePaymentLinkSyncPublisher;
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
import com.liquido.worker.pojo.dto.DwSyncPaymentLinkDto;
import com.liquido.worker.pojo.entity.PaymentLink;
import com.liquido.worker.pojo.entity.QPaymentLink;
import com.liquido.worker.pojo.vo.SyncDataVo;
import com.liquido.worker.repository.PaymentLinkRepository;
import com.liquido.worker.repository.TaskFeeCalculationRepository;
import com.liquido.worker.service.sync.PaymentLinkSyncService;

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
public class PaymentLinkSyncServiceImpl implements PaymentLinkSyncService {

    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final DataWarehouseFeign dataWarehouseFeign;
    private final WorkerProperties.DataWarehouseProperties dwProperties;
    private final LarkProperties.MonitorProperties monitorProperties;
    private final PaymentLinkRepository paymentLinkRepository;
    private final WorkerProperties.PaymentLinkProperties paymentLinkProperties;
    private final ServicePaymentLinkSyncPublisher servicePaymentLinkSyncPublisher;
    private final LarkRobotMonitor larkRobotMonitor;
    private final BaseService baseService;
    private final TaskFeeCalculationRepository taskFeeCalculationRepository;

    @Override
    public void sync(final TransactionTypeCodeEnum typeCodeEnum) {
        final Long extension = Optional.ofNullable(dwProperties.getRequestWindowExtension())
                .orElse(0L);

        final QPaymentLink qPaymentLink = QPaymentLink.paymentLink;
        final PaymentLink finalData = jpaQueryFactory.select(qPaymentLink)
                .from(qPaymentLink)
                .orderBy(qPaymentLink.eventTimestamp.desc())
                .fetchFirst();

        final Long timeSpace = Optional.ofNullable(dwProperties.getTimeSpace()).orElse(1800L);
        final Long to = Instant.now().getEpochSecond();
        final Long from = Optional.ofNullable(finalData).map(PaymentLink::getEventTimestamp)
                .filter(v -> v.compareTo(to) <= 0 && v.compareTo(to - timeSpace) >= 0)
                .orElseGet(() -> to - timeSpace);

        log.info("payment link sync, init from: {}, init to: {}, extension: {}", from, to,
                extension);

        sync(new SyncDataVo(typeCodeEnum, from, to, List.of(), extension));
    }

    @SneakyThrows
    @Override
    public void sync(final SyncDataVo vo) {
        if (Objects.isNull(vo.getExtension())) {
            vo.setExtension(dwProperties.getRequestWindowExtension());
        }

        final Long maxGetValueInterval = dwProperties.getMaxGetValueInterval();
        int cycles = Long.valueOf((vo.getTo() - vo.getFrom()) / maxGetValueInterval).intValue();
        log.info("payment link sync start , from: {}, to: {}, maxGetValueInterval: {}, "
                + "cycles: {}", vo.getFrom(), vo.getTo(), maxGetValueInterval, cycles);

        for (int i = 0; i <= cycles; i++) {
            long realFrom = vo.getFrom() + (i * maxGetValueInterval);
            long realTo = i == cycles ? vo.getTo() : realFrom + maxGetValueInterval;
            final DwResponse<DwPage<DwSyncPaymentLinkDto>> res =
                    dataWarehouseFeign.getPaymentLinkDataByObjectIdTime(
                            vo.getTypeCodeEnum(), false, realFrom - vo.getExtension(), realTo);
            if (!res.isSuccess()) {
                log.error("payment link sync, data warehouse visit failed, repeat later, "
                                + "from: {}, to: {} , cycle times: {} res: {}",
                        realFrom - vo.getExtension(), realTo, i, res);

                larkRobotMonitor.error("Payment Link Sync Error", String.format(
                                "Payment Link sync use param from '%s' and to '%s', sync error.",
                                realFrom - vo.getExtension(), realTo),
                        String.format("Response: %s", res));
                return;
            }

            log.info("payment link sync from data warehouse , "
                            + "from: {}, to: {}, cycle times: {}, count: {}",
                    realFrom - vo.getExtension(), realTo, i, res.getData().getResults().size());

            final List<DwSyncPaymentLinkDto> dtoList = res.getData().getResults().stream()
                    .filter(x -> {

                        if (!checkMerchantName(x)) {
                            return false;
                        }

                        final String key = String.format("%s_%s",
                                x.getMerchantName(), x.getCountry());

                        if (Objects.nonNull(vo.getFilters()) && !vo.getFilters().isEmpty()) {
                            return vo.getFilters().stream().anyMatch(f -> f.equals(key));
                        }

                        return paymentLinkProperties.getExcludeAccount().stream()
                                .noneMatch(f -> f.equals(key));
                    }).peek(x -> {

                        if (DataSyncStatusEnum.SETTLED.getCode().equals(x.getRefundStatus())) {
                            x.setRefundStatus(DataSyncRefundStatusEnum.REFUNDED.getCode());
                        }

                        if (Objects.isNull(x.getRefundStatus())
                                || !List.of(DataSyncRefundStatusEnum.REFUNDED.getCode(),
                                        DataSyncRefundStatusEnum.IN_PROGRESS.getCode())
                                .contains(x.getRefundStatus())) {
                            x.setRefundAmount(BigDecimal.ZERO);
                            x.setRefundStatus(null);
                            x.setRefundTimestamp(0L);
                        }

                    }).collect(Collectors.toList());

            if (ObjectUtils.isNotEmpty(dtoList)) {
                Lists.partition(dtoList, paymentLinkProperties.getBatchQuantity())
                        .forEach(v -> servicePaymentLinkSyncPublisher.publish(
                                new ServicePaymentLinkSyncMsg(v))
                        );
            }

            Thread.sleep(paymentLinkProperties.getSyncDelayMs());
        }
    }

    @Override
    public void syncHandle(final List<DwSyncPaymentLinkDto> dtoList) {

        if (ObjectUtils.isEmpty(dtoList)) {
            return;
        }

        // Get the old data
        final QPaymentLink entity = QPaymentLink.paymentLink;
        final Map<String, PaymentLink> dataMap = jpaQueryFactory.select(entity)
                .from(entity)
                .where(entity.linkId.in(dtoList.stream()
                        .map(DwSyncPaymentLinkDto::getLinkId)
                        .distinct()
                        .collect(Collectors.toList())))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch()
                .stream()
                .collect(Collectors.toMap(PaymentLink::getLinkId, Function.identity()));

        // Compare and select updated/insert data
        for (final DwSyncPaymentLinkDto bo : dtoList) {
            final PaymentLink data = Optional.ofNullable(dataMap.get(bo.getLinkId()))
                    .orElse(PaymentLink.builder()
                            .transactionTypeCode(TransactionTypeCodeEnum.PAY_IN)
                            .version(VersionEnum.NORMAL)
                            .delFlag(false)
                            .createdTime(LocalDateTimeUtil.nowUtc())
                            .updatedTime(LocalDateTimeUtil.nowUtc())
                            .build()
                    );

            if (!checkPaymentLink(data, bo)) {
                if (checkPaymentLinkAppendix(data, bo)) {
                    dataMap.put(bo.getLinkId(), updatePaymentLinkAppendix(data, bo));
                }
            } else {
                dataMap.put(bo.getLinkId(), convertToPaymentLink(data, bo));
            }
        }

        // save or update
        if (ObjectUtils.isNotEmpty(dataMap)) {
            paymentLinkRepository.saveAll(dataMap.values());
        }
    }

    private boolean checkPaymentLink(final PaymentLink old, final DwSyncPaymentLinkDto now) {
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
                .compareTo(DataSyncStatusEnum.parse(now.getPaymentStatus()).getRank()) < 0) {
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

        return old.getRefundStatus().getRank()
                .compareTo(DataSyncRefundStatusEnum.parse(now.getRefundStatus()).getRank()) <= 0;
    }

    private boolean checkPaymentLinkAppendix(
            final PaymentLink old, final DwSyncPaymentLinkDto now) {
        if (Objects.nonNull(now.getAppendix())) {
            if (Objects.isNull(old.getAppendix())) {
                return true;
            } else {
                return !now.getAppendix().equals(old.getAppendix());
            }
        } else {
            return false;
        }
    }

    private PaymentLink updatePaymentLinkAppendix(final PaymentLink link,
                                                  final DwSyncPaymentLinkDto dto) {
        link.setAppendix(dto.getAppendix());
        link.setEventTimestamp(dto.getEventTime());
        return link;
    }

    private PaymentLink convertToPaymentLink(final PaymentLink link,
                                             final DwSyncPaymentLinkDto dto) {
        final DataSyncStatusEnum paymentStatus = DataSyncStatusEnum.parse(dto.getPaymentStatus());
        link.setLinkId(dto.getLinkId());
        link.setMerchantReference(dto.getMerchantReference());
        link.setMerchantCode(dto.getMerchantName());
        link.setCountryCode(CountryCodeEnum.parse(dto.getCountry()));
        link.setTransactionTypeCode(TransactionTypeCodeEnum.PAY_IN);
        link.setProductCode(ProductCodeEnum.parse(dto.getProductCode()));
        link.setAmount(dto.getAmount());
        link.setCurrency(CurrencyEnum.parse(dto.getCurrency()));
        link.setPaymentStatus(paymentStatus);
        link.setSettledUniqueId(dto.getSettledVirgoId());
        if (dto.getCreateTimestamp().compareTo(0L) == 0) {
            link.setSubmitTimestamp(dto.getEventTime());
            link.setSubmitTime(LocalDateTimeUtil.instantToUtc(dto.getEventTime()));
        } else {
            link.setSubmitTimestamp(dto.getCreateTimestamp());
            link.setSubmitTime(LocalDateTimeUtil.instantToUtc(dto.getCreateTimestamp()));
        }
        if (Objects.nonNull(dto.getFinalStatusTimestamp())
                && dto.getFinalStatusTimestamp().compareTo(0L) > 0) {
            link.setTransactionTimestamp(dto.getFinalStatusTimestamp());
            link.setTransactionTime(LocalDateTimeUtil.instantToUtc(dto.getFinalStatusTimestamp()));
        }
        link.setRefundStatus(DataSyncRefundStatusEnum.parse(dto.getRefundStatus()));
        link.setRefundAmount(dto.getRefundAmount());
        link.setRefundedUniqueId(dto.getRefundedVirgoId());
        if (Objects.nonNull(dto.getRefundTimestamp())
                && dto.getRefundTimestamp().compareTo(0L) > 0) {
            link.setRefundTimestamp(dto.getRefundTimestamp());
            link.setRefundTime(LocalDateTimeUtil.instantToUtc(dto.getRefundTimestamp()));
        }
        link.setEventTimestamp(dto.getEventTime());
        link.setUserEmail(dto.getEmail());
        link.setUserPhone(dto.getPhone());
        link.setAppendix(dto.getAppendix());
        link.setDescription(StringUtils.defaultIfBlank(dto.getDescription(), ""));
        link.setSubMerchantId(StringUtils.defaultIfBlank(dto.getSubMerchantId(), ""));
        if (DataSyncStatusEnum.SETTLED == paymentStatus
                && StringUtils.isNotBlank(dto.getSettledVirgoId())) {
            link.setOthers(Optional.ofNullable(
                            taskFeeCalculationRepository.findByUniqueId(dto.getSettledVirgoId()))
                    .map(fee -> new ObjectNode(JsonNodeFactory.withExactBigDecimals(true))
                            .put("documentId", fee.getDocumentId()))
                    .orElseGet(() -> settleDataNotFound(dto)));
        }
        if (!Objects.isNull(dto.getMetadata()) && !dto.getMetadata().isEmpty()) {
            Optional.ofNullable(link.getOthers())
                    .ifPresentOrElse(
                            others -> others.set("metadata", dto.getMetadata()),
                            () -> link.setOthers(
                                    new ObjectNode(JsonNodeFactory.withExactBigDecimals(true))
                                            .set("metadata", dto.getMetadata())
                            )
                    );
        }

        return link;
    }

    @SneakyThrows
    private ObjectNode settleDataNotFound(final DwSyncPaymentLinkDto dto) {
        final String larkMonitorKey = String.format(
                Constant.CACHE.LARK_MONITOR_PAYMENT_LINK_NOT_FOUND_SEATTLE, dto.getLinkId());

        final Integer monitorCount = Optional.ofNullable(
                redisCacheUtil.<Integer>getCacheObject(larkMonitorKey)).orElse(1);

        if (monitorCount.compareTo(5) > 0) {

            final String content =
                    larkRobotMonitor.buildLarkAlarmContent(dto.getMerchantName(), dto.getCountry(),
                            TransactionTypeCodeEnum.PAY_IN.getCode(), dto.getLinkId(),
                            "The Payment Link settled, but settle transaction "
                                    + "data not found. Settled virgo id: "
                                    + dto.getSettledVirgoId());

            larkRobotMonitor.warn("Payment Link Sync Warn", content, "");
            return null;
        } else {
            redisCacheUtil.setCacheObject(larkMonitorKey, monitorCount + 1,
                    Optional.ofNullable(monitorProperties.getWarnAlarmInterval()).orElse(3),
                    TimeUnit.HOURS);
            Thread.sleep(monitorCount * 30000);
            throw WorkerExceptionCode.DATA_SYNC_NOT_FOUND_SETTLED_FEE.exception();
        }
    }

    private Boolean checkMerchantName(final DwSyncPaymentLinkDto dto) {
        final String key = dto.getMerchantName();

        final MerchantDto merchants = baseService.getMerchantByCode(key);

        log.info("paymentLink sync merchantName={},merchantInfo={}", key, merchants);

        if (StringUtils.isBlank(key) || Objects.isNull(merchants)) {

            final String larkMonitorKey = String.format(
                    Constant.CACHE.LARK_MONITOR_PAYMENT_LINK_NOT_HAVE_MERCHANT_NAME, key);

            final DwSyncPaymentLinkDto monitorDto = redisCacheUtil.getCacheObject(larkMonitorKey);

            if (Objects.nonNull(monitorDto)) {
                return false;
            }

            final String content =
                    larkRobotMonitor.buildLarkAlarmContent(dto.getMerchantName(), dto.getCountry(),
                            TransactionTypeCodeEnum.PAY_IN.getCode(), dto.getLinkId(),
                            "Doesn't have the merchant: " + dto.getMerchantName());

            larkRobotMonitor.error("Payment Link Sync Warn", content, "");

            redisCacheUtil.setCacheObject(larkMonitorKey, dto,
                    Optional.ofNullable(monitorProperties.getWarnAlarmInterval()).orElse(3),
                    TimeUnit.HOURS);

            return false;
        }
        return true;
    }

}
