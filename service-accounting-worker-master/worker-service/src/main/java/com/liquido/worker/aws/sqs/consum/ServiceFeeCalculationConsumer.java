package com.liquido.worker.aws.sqs.consum;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.exception.ApplicationException;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.worker.aws.sqs.msg.ServiceFeeCalculationMsg;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.properties.AwsSqsProperties;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.pojo.bo.TaskFeeCalculationSettleBo;
import com.liquido.worker.service.calculate.TaskTransactionService;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"worker.aws.sqs.queue.enable"}, havingValue = "true")
public class ServiceFeeCalculationConsumer extends BaseSqsMessageHandler<ServiceFeeCalculationMsg> {

    private final TaskTransactionService taskTransactionService;
    private final AwsSqsProperties.SqsQueueRetryProperties retryProperties;
    private final WorkerProperties.FeeCalculationProperties feeCalculationProperties;
    private final LarkRobotMonitor larkRobotMonitor;
    private final RedisCacheUtil redisCacheUtil;

    @SuppressWarnings("BusyWait")
    @SneakyThrows
    @Override
    public void handle(ServiceFeeCalculationMsg msg) {

        log.info("aws sqs queue[key: {}] get the message: {}",
                Constant.AWS_QUEUE_KEYS.SERVICE_FEE_CALCULATION, msg);

        final TaskFeeCalculationSettleBo bo = msg.getBo();
        final String excludeKey = String.format("%s_%s_%s", bo.getMerchantCode(),
                bo.getCountryCode(), bo.getTransactionTypeCode());

        if (Optional.ofNullable(feeCalculationProperties.getCalculationExcludeAccount())
                .orElse(List.of()).contains(excludeKey)) {
            log.warn("fee calculation handle exclude account, syncId: {}, key: {}",
                    bo.getSyncId(), excludeKey);
            return;
        }

        int min = retryProperties.getMinIntervalDurationMs();
        int max = retryProperties.getMaxIntervalDurationMs();
        long randomMs = Math.round(Math.random() * (max - min) + min);
        int retries = 0;

        do {
            long sleepMs = randomMs * retries;
            if (retries > 0) {
                if (retryProperties.getTotal().compareTo(retries) >= 0) {
                    log.warn(
                            "fee calculation handle retry, syncId: {}, times: {}, "
                                    + "sleep ms: {} msg: {}",
                            bo.getSyncId(), retries, sleepMs, bo);
                    Thread.sleep(sleepMs);
                } else {
                    log.warn(
                            "fee calculation handle retry reach the upper limit, "
                                    + "make it return to the SQS,"
                                    + " syncId: {}, limit time: {}, msg: {}",
                            bo.getSyncId(), retries - 1, bo);
                    throw WorkerExceptionCode.FEE_CALCULATION_RETRY_FAILED.exception();
                }
            }

            try {
                bo.setRequestId(DataUtil.getUuid());
                taskTransactionService.batchProcessTransactionTask(bo);
                return;
            } catch (ApplicationException e) {
                log.warn("fee calculation handle exception:{}, msg:{}",
                        e.getMessage(), bo, e);

                if (e.getMessage().contains("unique key conflict")) {
                    return;
                }

                if (StatementExceptionCode.ACCOUNT_PROCESSING_DAILY_CUT.getCode()
                        .compareTo(e.getCode()) == 0) {
                    Thread.sleep(30000);
                } else if (WorkerExceptionCode.FEE_CALCULATION_ALREADY_DONE.getCode()
                        .compareTo(e.getCode()) == 0) {
                    return;
                } else if (StatementExceptionCode.ACCOUNT_ALREADY_DAILY_CUT.getCode()
                        .compareTo(e.getCode()) == 0) {

                    final String content =
                            larkRobotMonitor.buildLarkAlarmContent(bo.getMerchantCode(),
                                    bo.getCountryCode().getCode(),
                                    bo.getTransactionTypeCode().getCode(),
                                    "Already daily cut");

                    larkRobotMonitor.error("Data Warehouse Sync Warn", content,
                            String.format("unique ids: %s",
                                    JsonUtil.toJson(bo.getTaskIdList())));
                    return;
                } else if (List.of(WorkerExceptionCode.LOAD_FEE_CONFIG_FAILURE.getCode(),
                                WorkerExceptionCode.LOAD_ACCOUNT_PRODUCT_FAILURE.getCode(),
                                WorkerExceptionCode.FEE_CONFIG_INVALID.getCode(),
                                WorkerExceptionCode.GET_EXCHANGE_RATE_FAIL.getCode())
                        .contains(e.getCode()) && LocalDateTimeUtil.nowUtc().getMinute() > 20) {

                    final String content =
                            larkRobotMonitor.buildLarkAlarmContent(bo.getMerchantCode(),
                                    bo.getCountryCode().getCode(),
                                    bo.getTransactionTypeCode().getCode(),
                                    e.getMessage());

                    larkRobotMonitor.error("Data Warehouse Sync Warn", content,
                            String.format("Date: %s", bo.getDate()));
                    Thread.sleep(120000);
                }
            } catch (Exception e) {

                log.warn("fee calculation handle exception", e);

                if (e.getMessage().contains("unique key conflict")) {
                    return;
                }

                final String key = String.format(
                        Constant.CACHE.TOKENIZATION_SYNC_CALCULATION_ERROR_COUNT,
                        bo.getSyncId());
                final int limitCount =
                        Optional.ofNullable(feeCalculationProperties.getSyncErrorWarnLimitCount())
                                .orElse(30);
                final int errorCount =
                        Optional.ofNullable(redisCacheUtil.<Integer>getCacheObject(key))
                                .orElse(0) + 1;
                if (errorCount % limitCount == 0) {
                    larkRobotMonitor.error("Data Warehouse Sync Warn",
                            "The S3 bucket 'service-fee-calculation' msg backlog in.",
                            String.format("SyncId: %s", bo.getSyncId()));
                }
                redisCacheUtil.setCacheObject(key, errorCount, 30, TimeUnit.MINUTES);

            }

            retries++;

        } while (true);
    }

    @Override
    public Class<ServiceFeeCalculationMsg> messageType() {
        return ServiceFeeCalculationMsg.class;
    }

}
