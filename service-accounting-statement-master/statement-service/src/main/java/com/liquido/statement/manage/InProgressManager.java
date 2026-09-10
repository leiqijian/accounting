package com.liquido.statement.manage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.Constant;
import com.liquido.statement.common.properties.AwsSqsProperties;
import com.liquido.statement.common.properties.StatementProperties;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.vo.TransactionInProgressVo;
import com.liquido.statement.service.AccountInProgressService;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.inprogress.InProgressService;
import com.liquido.statement.service.monitor.lark.LarkRobotMonitor;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
@RequiredArgsConstructor
public class InProgressManager {

    private final InProgressService inProgressService;
    private final AccountInProgressService accountInProgressService;

    private final LarkRobotMonitor larkRobotMonitor;
    private final AwsSqsProperties.SqsQueueRetryProperties retryProperties;
    private final StatementProperties.InProgressCalculationProperties
            inProgressCalculationProperties;
    private final RedisCacheUtil redisCacheUtil;
    private final AccountService accountService;

    @SneakyThrows
    public void syncTransactionInProgress(List<TransactionInProgressVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        log.info("sync transaction inProgress data size={}", list.size());
        Map<String, List<TransactionInProgressVo>> map =
                list.stream().collect(Collectors.groupingBy(
                        vo -> buildAccountIdentifier(vo.getMerchantCode(),
                                vo.getCountryCode().getCode(),
                                vo.getTransactionTypeCode().getCode())));

        final int min = retryProperties.getMinIntervalDurationMs();
        final int max = retryProperties.getMaxIntervalDurationMs();
        int retries = 0;
        long randomMs = ThreadLocalRandom.current().nextLong(min, max + 1);

        for (Map.Entry<String, List<TransactionInProgressVo>> entry : map.entrySet()) {

            final String key = entry.getKey();
            final List<TransactionInProgressVo> transactionInProgressList = entry.getValue();
            final TransactionInProgressVo vo = transactionInProgressList.get(0);
            final String uuid = DataUtil.getUuid();
            final StopWatch watch = new StopWatch("start sync inProgress data");
            do {
                watch.start();
                long sleepMs = randomMs * retries;
                if (retries > 0) {
                    log.warn("retry initiated, account info: {}, limit retries size: {}, "
                                    + "sleep time: {} ms, in progress list info: {}",
                            key, retries - 1, sleepMs, transactionInProgressList);
                }
                // PAY_IN filter refund data
                this.filterRefundDataForPayIn(transactionInProgressList);

                if (CollectionUtils.isEmpty(transactionInProgressList)) {
                    break;
                }
                final AccountDto account = accountService.queryAccountByIdentifier(
                        key,
                        vo.getMerchantCode(),
                        vo.getCountryCode(),
                        vo.getTransactionTypeCode());
                try {
                    accountInProgressService.tryLockAccountInProgress(account.getId(), uuid);
                    inProgressService.processing(transactionInProgressList, account);
                    break;
                } catch (Exception e) {
                    log.error(
                            "inProgress sync error,merchant={}, country={}, transactionType={},"
                                    + "nowTime={},errorMessage={}:",
                            vo.getMerchantCode(), vo.getCountryCode(),
                            vo.getTransactionTypeCode(),
                            LocalDateTimeUtil.nowUtcToLocal("UTC+8"), e.getMessage());
                    processSyncErrorWarning(account, uuid);

                } finally {
                    accountInProgressService.releaseLockAccountInProgress(account.getId(), uuid);
                    watch.stop();
                    log.info(
                            "inProgressSync data time end ts={}ms, merchant={}, country={},"
                                    + " transactionType={}, nowTime={},dataSize={}",
                            watch.getTotalTimeMillis(), vo.getMerchantCode(),
                            vo.getCountryCode(),
                            vo.getTransactionTypeCode(),
                            LocalDateTimeUtil.nowUtcToLocal("UTC+8"),
                            transactionInProgressList.size());
                }
                retries++;

            } while (retryProperties.getTotal().compareTo(retries) >= 0);
        }
    }


    private void processSyncErrorWarning(final AccountDto accountDto, final String uuid) {
        log.info("processSyncErrorWarning, accountId: {}", accountDto.getId());
        final String key = String.format(
                Constant.CACHE.INPROGRESS_SYNC_CALCULATION_ERROR_COUNT, uuid);

        final int limitCount = Optional.ofNullable(
                inProgressCalculationProperties.getSyncErrorWarnLimitCount()).orElse(30);

        final int errorCount = Optional.ofNullable(redisCacheUtil.<Integer>getCacheObject(key))
                .orElse(0) + 1;

        if (errorCount % limitCount == 0) {
            log.warn("Transaction InProgress Sync Warn, errorCount: {}, limitCount: {}",
                    errorCount, limitCount);
            larkRobotMonitor.error("Transaction InProgress Sync Warn",
                    "The S3 bucket 'service-in-progress-calculation' msg backlog in.",
                    String.format("accountId: %s", accountDto.getId()));
        }

        redisCacheUtil.setCacheObject(key, errorCount, 30, TimeUnit.MINUTES);
    }

    private void filterRefundDataForPayIn(
            final List<TransactionInProgressVo> transactionInProgressList) {
        if (TransactionTypeCodeEnum.PAY_IN ==
                transactionInProgressList.get(0).getTransactionTypeCode()) {
            transactionInProgressList.removeIf(
                    x -> DirectionTypeEnum.REFUND != x.getDirectionType());
        }
    }

    private String buildAccountIdentifier(final String merchantCode,
                                          final String countryCode,
                                          final String transactionTypeCode) {
        return String.format("%s_%s_%s", merchantCode, countryCode, transactionTypeCode);
    }

}
