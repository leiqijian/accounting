package com.liquido.worker.service.calculate.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionDataSourceEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.worker.enums.CalculationTaskStateEnum;
import com.liquido.worker.enums.CalculationTaskTypeEnum;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.pojo.bo.PreCalculateFeeBo;
import com.liquido.worker.pojo.bo.TaskFeeCalculationSettleBo;
import com.liquido.worker.pojo.dto.PreCalculateFeeDto;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.mapper.ModelMapper;
import com.liquido.worker.pojo.vo.SyncHandleTaskFeeCalculationVo;
import com.liquido.worker.service.TaskFeeCalculationService;
import com.liquido.worker.service.calculate.TaskTransactionService;
import com.liquido.worker.service.calculate.TransactionProviderFactory;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskTransactionServiceImpl implements TaskTransactionService {

    private final ModelMapper mapper;
    private final TransactionProviderFactory factory;
    private final TaskFeeCalculationService taskFeeCalculationService;

    /**
     * batch load waiting-process records from database every time
     */
    @Value("${worker.task.process.single-batch-count:1000}")
    private int processSingleBatchCount;

    @Override
    public List<PreCalculateFeeDto> preCalculateFee(
            final SyncHandleTaskFeeCalculationVo vo
    ) {
        final List<TaskFeeCalculation> taskList = vo.getBos().stream()
                .map(x -> TaskFeeCalculation.builder()
                        .id(SnowflakeIdUtil.generate())
                        .uniqueId(x.getUniqueId())
                        .documentId(x.getDocumentId())
                        .taskType(CalculationTaskTypeEnum.UNREPEATABLE)
                        .taskStatus(CalculationTaskStateEnum.WAITING)
                        .holdStatus(HoldStatusEnum.NORMAL)
                        .merchantCode(x.getMerchantCode())
                        .merchantName("")
                        .subMerchantId(x.getSubMerchantId())
                        .countryCode(CountryCodeEnum.parse(x.getCountry()))
                        .transactionTypeCode(TransactionTypeCodeEnum.parse(x.getTransactionType()))
                        .productCode(ProductCodeEnum.parse(x.getProductCode()))
                        .subProductCode("")
                        .merchantReference(x.getMerchantReference())
                        .accountName("")
                        .amount(x.getAmount())
                        .currency(CurrencyEnum.parse(x.getCurrency()))
                        .transactionTime(LocalDateTimeUtil.instantToUtc(x.getFinalStatusTime()))
                        .transactionTimestamp(x.getFinalStatusTime())
                        .submitTimestamp(x.getSubmitTime())
                        .transactionStatus(TransactionStatusEnum.parse(x.getTransactionStatus()))
                        .directionType(DirectionTypeEnum.parse(x.getDirectionCode()))
                        .lifecycleTimestamp(
                                LocalDateTimeUtil.utcToInstant(LocalDateTimeUtil.nowUtc()))
                        .lifecycleStatus(TransactionStatusEnum.SETTLED)
                        .tradeTransferStatus("")
                        .tradeTransactionType("")
                        .vendor(VendorCodeEnum.UNKNOWN)
                        .calculationNode(Lists.newArrayList())
                        .comments("fee trial")
                        .others(x.getOthers())
                        .eventTimestamp(x.getEventTime())
                        .finalStatusTimestamp(x.getFinalStatusTime())
                        .transactionDataSource(
                                TransactionDataSourceEnum.parse(x.getTransactionDataSource()))
                        .createdTime(LocalDateTimeUtil.nowUtc())
                        .createdBy(0L)
                        .updatedTime(LocalDateTimeUtil.nowUtc())
                        .updatedBy(0L)
                        .version(0)
                        .delFlag(false)
                        .build())
                .collect(Collectors.toList());

        return feeTrialCalculate(taskList);
    }

    public List<PreCalculateFeeDto> feeTrialCalculate(
            final List<TaskFeeCalculation> taskList
    ) {
        // group by account info
        final Map<String, List<TaskFeeCalculation>> taskGroup =
                taskList.stream().collect(Collectors.groupingBy(x -> x.getCountryCode().getCode()
                        .concat(x.getTransactionTypeCode().getCode()
                                .concat(x.getMerchantCode()))));

        final List<PreCalculateFeeBo> resultList = new ArrayList<>();
        for (final Map.Entry<String, List<TaskFeeCalculation>> entryMap : taskGroup.entrySet()) {
            resultList.addAll(factory.getProviderFactory(
                            entryMap.getValue().get(0).getTransactionTypeCode())
                    .batchFeeTrialCalculate(entryMap.getValue()));
        }
        return mapper.convertListPreCalculateFeeBoToDto(resultList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchProcessTransactionTask(final TaskFeeCalculationSettleBo taskOrder) {
        if (Objects.isNull(taskOrder) || CollectionUtils.isEmpty(taskOrder.getTaskIdList())) {
            return;
        }

        final int requestBatchSize = taskOrder.getTaskIdList().size();
        if (taskOrder.getTaskIdList().size() > processSingleBatchCount) {
            log.warn("single batch size limit={}, actual request size={}", processSingleBatchCount,
                    requestBatchSize);
            throw WorkerExceptionCode.SINGLE_BATCH_LIMIT_ILLEGAL.exception(processSingleBatchCount);
        }

        final List<TaskFeeCalculation> taskList =
                taskFeeCalculationService.batchLoadWaitingTaskAndLock(taskOrder);
        if (CollectionUtils.isEmpty(taskList) || taskList.size() != requestBatchSize) {
            log.error("batch locked fail requestId={}, before request batch size={}, "
                            + "after lock success batch size={}",
                    taskOrder.getRequestId(), requestBatchSize, taskList.size());
            throw WorkerExceptionCode.SINGLE_BATCH_LOCK_TASK_FAIL.exception();
        }

        log.info("batch process transaction task, orderSize={}, taskSize={}, taskOrder: {}, "
                + "taskList={}", requestBatchSize, taskList.size(), taskOrder, taskList);

        final long start = System.currentTimeMillis();
        factory.getProviderFactory(taskOrder.getTransactionTypeCode())
                .batchProcessCalculate(taskOrder.getRequestId(), taskList);

        log.info("batch process transaction task requestId={}, ts={}ms",
                taskOrder.getRequestId(), (System.currentTimeMillis() - start));
    }
}
