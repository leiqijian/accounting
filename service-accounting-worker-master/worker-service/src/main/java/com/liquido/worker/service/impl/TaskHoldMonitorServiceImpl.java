package com.liquido.worker.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.worker.common.Constant;
import com.liquido.worker.pojo.bo.PreCalculateConfigBo;
import com.liquido.worker.pojo.entity.QTaskHoldMonitor;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.entity.TaskHoldMonitor;
import com.liquido.worker.repository.TaskHoldMonitorRepository;
import com.liquido.worker.service.TaskHoldMonitorService;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskHoldMonitorServiceImpl implements TaskHoldMonitorService {
    private final EntityManager entityManager;
    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final TaskHoldMonitorRepository holdMonitorRepository;

    @Override
    public boolean checkOverLimit(final TaskFeeCalculation taskOrder,
                                  final PreCalculateConfigBo preConfig,
                                  final BigDecimal transactionAmount) {

        final BigDecimal limitAmount = preConfig.getAccountInfo().getHoldingLimit();
        log.info("CheckOverLimit taskId={}, uniqueId={}, accountId={}"
                        + "documentId={}, holdLimit={}, orderAmount={}", taskOrder.getId(),
                taskOrder.getUniqueId(), preConfig.getAccountInfo().getId(),
                taskOrder.getDocumentId(), limitAmount, taskOrder.getAmount());

        // condition preCheck
        // 1. just Brazil need check;
        // 2. just payIn and settled orders need check;
        // 3. holding limit must be greater than 0;
        // 4. documentId required;
        if (CountryCodeEnum.BR != taskOrder.getCountryCode()
                || TransactionTypeCodeEnum.PAY_IN != taskOrder.getTransactionTypeCode()
                || DirectionTypeEnum.SETTLED != taskOrder.getDirectionType()
                || Objects.isNull(limitAmount) || limitAmount.compareTo(BigDecimal.ZERO) <= 0
                || StringUtils.isBlank(taskOrder.getDocumentId())) {

            //skip check.
            log.info("not overLimit, skip check");
            return false;
        }

        final String cacheKey = buildCacheKey(preConfig);
        Boolean isOverLimit = redisCacheUtil.getCacheMapValue(cacheKey, taskOrder.getDocumentId());
        if (Objects.nonNull(isOverLimit) && isOverLimit) {
            return true;
        }

        final String monthly = preConfig.getTransactionDate().format(LocalDateUtil.FORMAT_YYYYMM);
        try {
            // Try to load from database
            final TaskHoldMonitor existRecord =
                    holdMonitorRepository.findByMonthlyAndAccountIdAndDocumentId(
                            Integer.parseInt(monthly),
                            preConfig.getAccountInfo().getId(),
                            taskOrder.getDocumentId());

            /* if record not exist create a new record*/
            if (Objects.isNull(existRecord)) {
                final TaskHoldMonitor task = TaskHoldMonitor.builder()
                        .id(SnowflakeIdUtil.generate())
                        .monthly(Integer.parseInt(monthly))
                        .accountId(preConfig.getAccountInfo().getId())
                        .documentId(taskOrder.getDocumentId())
                        .totalAmount(transactionAmount).version(1)
                        .createdTime(LocalDateTimeUtil.nowUtc()).build();
                holdMonitorRepository.saveAndFlush(task);

                isOverLimit = transactionAmount.compareTo(limitAmount) >= 0;
                this.refreshCache(preConfig, taskOrder, isOverLimit);
                return isOverLimit;
            }

            isOverLimit = (existRecord.getTotalAmount().add(transactionAmount))
                    .compareTo(limitAmount) >= 0;

            this.cumulativeAmount(existRecord, transactionAmount);
            this.refreshCache(preConfig, taskOrder, isOverLimit);
            return isOverLimit;
        } finally {
            entityManager.flush();
            entityManager.clear();
        }
    }

    private void cumulativeAmount(final TaskHoldMonitor task, final BigDecimal transactionAmount) {
        final QTaskHoldMonitor entity = QTaskHoldMonitor.taskHoldMonitor;
        jpaQueryFactory.update(entity)
                .set(entity.totalAmount, entity.totalAmount.add(transactionAmount))
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .set(entity.version, entity.version.add(1))
                .where(entity.id.eq(task.getId()).and(entity.version.eq(task.getVersion())))
                .execute();
    }

    private void refreshCache(final PreCalculateConfigBo preConfig,
                              final TaskFeeCalculation taskOrder,
                              final Boolean overLimit) {

        final String cacheKey = buildCacheKey(preConfig);
        redisCacheUtil.setCacheMapValue(cacheKey, taskOrder.getDocumentId(), overLimit);

        // Set to expire on the first date of the next month
        final LocalDate expireAt =
                preConfig.getTransactionDate().with(TemporalAdjusters.lastDayOfMonth()).plusDays(1);
        if (expireAt.isAfter(preConfig.getTransactionDate())) {
            redisCacheUtil.expireAt(cacheKey, expireAt);
        } else {
            // Renew one day
            redisCacheUtil.expire(cacheKey, 2, TimeUnit.DAYS);
        }
    }

    private static String buildCacheKey(final PreCalculateConfigBo preConfig) {
        return String.format(Constant.CACHE.TASK_HOLDING_MONITOR,
                preConfig.getTransactionDate().format(LocalDateUtil.FORMAT_YYYYMM),
                preConfig.getAccountInfo().getId().toString());
    }
}
