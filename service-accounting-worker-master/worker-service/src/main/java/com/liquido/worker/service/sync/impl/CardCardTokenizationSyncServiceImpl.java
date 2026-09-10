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

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.worker.aws.sqs.msg.ServiceCardTokenizationSyncMsg;
import com.liquido.worker.aws.sqs.publish.ServiceTokenizationSyncPublisher;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.properties.LarkProperties;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.enums.VersionEnum;
import com.liquido.worker.feign.BaseService;
import com.liquido.worker.feign.DataWarehouseFeign;
import com.liquido.worker.pojo.bo.DwPage;
import com.liquido.worker.pojo.bo.DwResponse;
import com.liquido.worker.pojo.dto.DwSyncTokenizationDto;
import com.liquido.worker.pojo.entity.CardTokenization;
import com.liquido.worker.pojo.entity.QCardTokenization;
import com.liquido.worker.pojo.vo.SyncDataVo;
import com.liquido.worker.repository.TokenizationRepository;
import com.liquido.worker.service.sync.CardTokenizationSyncService;

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
public class CardCardTokenizationSyncServiceImpl implements CardTokenizationSyncService {

    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final DataWarehouseFeign dataWarehouseFeign;
    private final WorkerProperties.DataWarehouseProperties dwProperties;
    private final LarkRobotMonitor larkRobotMonitor;
    private final BaseService baseService;
    private final LarkProperties.MonitorProperties monitorProperties;
    private final WorkerProperties.TokenizationProperties tokenizationProperties;
    private final ServiceTokenizationSyncPublisher serviceTokenizationSyncPublisher;
    private final TokenizationRepository tokenizationRepository;

    @Override
    public void sync(final TransactionTypeCodeEnum typeCodeEnum) {
        final Long extension = Optional.ofNullable(dwProperties.getRequestWindowExtension())
                .orElse(0L);
        final QCardTokenization qTokenization = QCardTokenization.cardTokenization;
        final CardTokenization finalData = jpaQueryFactory.select(qTokenization)
                .from(qTokenization)
                .orderBy(qTokenization.eventTimestamp.desc())
                .fetchFirst();

        final Long timeSpace = Optional.ofNullable(dwProperties.getTimeSpace()).orElse(1800L);
        final Long to = Instant.now().getEpochSecond();
        final Long from = Optional.ofNullable(finalData).map(CardTokenization::getEventTimestamp)
                .filter(v -> v.compareTo(to) <= 0 && v.compareTo(to - timeSpace) >= 0)
                .orElseGet(() -> to - timeSpace);

        log.info("tokenization sync, init from: {}, init to: {}, extension: {}", from, to,
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
        log.info("tokenization sync start , from: {}, to: {}, maxGetValueInterval: {}, "
                + "cycles: {}", vo.getFrom(), vo.getTo(), maxGetValueInterval, cycles);

        for (int i = 0; i <= cycles; i++) {
            long realFrom = vo.getFrom() + (i * maxGetValueInterval);
            long realTo = i == cycles ? vo.getTo() : realFrom + maxGetValueInterval;
            final DwResponse<DwPage<DwSyncTokenizationDto>> res =
                    dataWarehouseFeign.getTokenDataByObjectIdTime(
                            vo.getTypeCodeEnum(), false, realFrom - vo.getExtension(), realTo);
            if (!res.isSuccess()) {
                log.error("Tokenization sync, data warehouse visit failed, repeat later, "
                                + "from: {}, to: {} , cycle times: {} res: {}",
                        realFrom - vo.getExtension(), realTo, i, res);

                larkRobotMonitor.error("Token Sync Error", String.format(
                                "Tokenization sync use param from '%s' and to '%s', sync error.",
                                realFrom - vo.getExtension(), realTo),
                        String.format("Response: %s", res));
                return;
            }

            log.info("Tokenization sync from data warehouse , "
                            + "from: {}, to: {}, cycle times: {}, count: {}",
                    realFrom - vo.getExtension(), realTo, i, res.getData().getResults().size());

            final List<DwSyncTokenizationDto> dtoList = res.getData().getResults().stream()
                    .filter(x -> {
                        try {
                            if (!checkMerchantName(x)) {
                                return false;
                            }

                            final String key = String.format("%s_%s",
                                    x.getMerchantName(), x.getTokenInfo().getCountry());

                            if (Objects.nonNull(vo.getFilters()) && !vo.getFilters().isEmpty()) {
                                return vo.getFilters().stream().anyMatch(f -> f.equals(key));
                            }

                            return tokenizationProperties.getExcludeAccount().stream()
                                    .noneMatch(f -> f.equals(key));
                        } catch (Exception e) {
                            log.error("cardToken has something error that could not be handled: {}",
                                    JsonUtil.toJson(x), e);
                            return false;
                        }
                    }).collect(Collectors.toList());

            if (ObjectUtils.isNotEmpty(dtoList)) {
                Lists.partition(dtoList, tokenizationProperties.getBatchQuantity())
                        .forEach(v -> serviceTokenizationSyncPublisher.publish(
                                new ServiceCardTokenizationSyncMsg(v))
                        );
            }

            Thread.sleep(tokenizationProperties.getSyncDelayMs());
        }
    }

    private boolean checkMerchantName(final DwSyncTokenizationDto dto) {
        final String key = dto.getMerchantName();

        final MerchantDto merchants = baseService.getMerchantByCode(key);

        log.info("tokenization sync merchantName={},merchantInfo={}", key, merchants);

        if (StringUtils.isBlank(key) || Objects.isNull(merchants)) {

            final String larkMonitorKey = String.format(
                    Constant.CACHE.LARK_MONITOR_TOKENIZATION_NOT_HAVE_MERCHANT_NAME, key);

            final DwSyncTokenizationDto monitorDto = redisCacheUtil.getCacheObject(larkMonitorKey);

            if (Objects.nonNull(monitorDto)) {
                return false;
            }

            final String content =
                    larkRobotMonitor.buildLarkAlarmContent(dto.getMerchantName(),
                            TransactionTypeCodeEnum.PAY_IN.getCode(), dto.getToken(),
                            "Doesn't have the merchant: " + dto.getMerchantName());

            larkRobotMonitor.error("Tokenization Sync Warn", content, "");

            redisCacheUtil.setCacheObject(larkMonitorKey, dto,
                    Optional.ofNullable(monitorProperties.getWarnAlarmInterval()).orElse(3),
                    TimeUnit.HOURS);

            return false;
        }
        return true;
    }

    @Override
    public void syncHandle(final List<DwSyncTokenizationDto> dtoList) {
        if (ObjectUtils.isEmpty(dtoList)) {
            return;
        }

        // Get the old data
        final QCardTokenization entity = QCardTokenization.cardTokenization;
        final Map<String, CardTokenization> dataMap = jpaQueryFactory.select(entity)
                .from(entity)
                .where(entity.tokenMd5.in(dtoList.stream()
                        .map(DwSyncTokenizationDto::getTokenMd5)
                        .distinct()
                        .collect(Collectors.toList())))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch()
                .stream()
                .collect(Collectors.toMap(CardTokenization::getTokenMd5, Function.identity()));

        // Compare and select updated/insert data
        for (final DwSyncTokenizationDto bo : dtoList) {
            final CardTokenization data = Optional.ofNullable(dataMap.get(bo.getTokenMd5()))
                    .orElse(CardTokenization.builder()
                            .version(VersionEnum.NORMAL)
                            .delFlag(false)
                            .createdTime(LocalDateTimeUtil.nowUtc())
                            .updatedTime(LocalDateTimeUtil.nowUtc())
                            .build()
                    );

            if (!checkTokenization(data, bo)) {
                continue;
            }

            dataMap.put(bo.getTokenMd5(), convertToTokenization(data, bo));
        }

        // save or update
        if (ObjectUtils.isNotEmpty(dataMap)) {
            tokenizationRepository.saveAll(dataMap.values());
        }
    }

    private boolean checkTokenization(final CardTokenization old, final DwSyncTokenizationDto now) {
        if (Objects.isNull(old.getEventTimestamp())) {
            return true;
        }

        return old.getEventTimestamp().compareTo(now.getEventTime()) <= 0;
    }

    private CardTokenization convertToTokenization(final CardTokenization token,
                                                   final DwSyncTokenizationDto bo) {
        token.setTokenId(bo.getToken());
        token.setTokenMd5(bo.getTokenMd5());
        token.setMerchantCode(bo.getMerchantName());
        token.setTokenizationCreatedTime(LocalDateTimeUtil.instantToUtc(bo.getCreateTime()));
        token.setTokenizationCreatedTimestamp(bo.getCreateTime());
        token.setCountryCode(bo.getTokenInfo().getCountry());
        token.setBin(bo.getTokenInfo().getDisplayedCardInfo().getBin());
        token.setBrand(bo.getTokenInfo().getDisplayedCardInfo().getBrand());
        token.setLast4Digit(bo.getTokenInfo().getDisplayedCardInfo().getLast4());
        token.setCardHolderName(bo.getTokenInfo().getDisplayedCardInfo().getCardHolderName());
        token.setExpirationYear(bo.getTokenInfo().getDisplayedCardInfo().getExpirationYear());
        token.setExpirationMonth(bo.getTokenInfo().getDisplayedCardInfo().getExpirationMonth());
        token.setVendor(bo.getVendor());
        token.setTokenInfo(JsonUtil.getObjectMapper().valueToTree(bo.getTokenInfo()));
        token.setEventTimestamp(bo.getEventTime());
        token.setCreatedTime(LocalDateTimeUtil.nowUtc());
        token.setUpdatedTime(LocalDateTimeUtil.nowUtc());
        return token;
    }
}
