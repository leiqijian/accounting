package com.liquido.worker.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.worker.enums.CalculationTaskStateEnum;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.pojo.bo.TaskFeeCalculationSettleBo;
import com.liquido.worker.pojo.dto.TaskFeeCalculationDto;
import com.liquido.worker.pojo.entity.QTaskFeeCalculation;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.mapper.ModelMapper;
import com.liquido.worker.pojo.vo.FillFieldTaskFeeCalculationVo;
import com.liquido.worker.pojo.vo.UnHoldingDocumentVo;
import com.liquido.worker.pojo.vo.UnHoldingTransactionVo;
import com.liquido.worker.repository.TaskFeeCalculationRepository;
import com.liquido.worker.service.TaskFeeCalculationService;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
public class TaskFeeCalculationServiceImpl implements TaskFeeCalculationService {
    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final TaskFeeCalculationRepository taskFeeCalculationRepository;

    @Override
    public TaskFeeCalculationDto findById(final Long id) {
        if (Objects.isNull(id) || id <= 0) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL.exception(id);
        }

        final TaskFeeCalculation taskFeeCalculation = taskFeeCalculationRepository.findById(id)
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);

        return modelMapper.convert(taskFeeCalculation);
    }

    @Override
    public TaskFeeCalculationDto findByUniqueId(final String uniqueId) {
        if (StringUtils.isBlank(uniqueId)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
        }

        return Optional.ofNullable(taskFeeCalculationRepository.findByUniqueId(uniqueId))
                .map(modelMapper::convert).orElse(null);
    }

    @Override
    public List<TaskFeeCalculation> batchLoadWaitingTaskAndLock(
            final TaskFeeCalculationSettleBo batchOrder) {
        if (Objects.isNull(batchOrder) || CollectionUtils.isEmpty(batchOrder.getTaskIdList())) {
            return Lists.newArrayList();
        }

        final QTaskFeeCalculation entity = QTaskFeeCalculation.taskFeeCalculation;
        final BooleanExpression condition = entity.id.in(batchOrder.getTaskIdList())
                .and(entity.merchantCode.eq(batchOrder.getMerchantCode()))
                .and(entity.countryCode.eq(batchOrder.getCountryCode()))
                .and(entity.transactionTypeCode.eq(batchOrder.getTransactionTypeCode()))
                .and(entity.taskStatus.eq(CalculationTaskStateEnum.WAITING))
                .and(entity.transactionStatus.eq(TransactionStatusEnum.SETTLED));

        final List<TaskFeeCalculation> taskList = jpaQueryFactory.select(entity)
                .from(entity).where(condition).fetch();

        if (CollectionUtils.isEmpty(taskList)) {
            log.warn("load task form database is empty ={}", batchOrder);
            throw WorkerExceptionCode.FEE_CALCULATION_ALREADY_DONE.exception();
        }

        final long result = jpaQueryFactory.update(entity)
                .set(entity.taskStatus, CalculationTaskStateEnum.PROCESSING)
                .set(entity.version, entity.version.add(1))
                .where(condition)
                .execute();

        if (result != taskList.size()) {
            log.error("lock task fail batchOrder={}, taskList={}", batchOrder, taskList);
            throw WorkerExceptionCode.TASK_LOCKED_FAIL.exception();
        }

        return taskList;
    }

    @Override
    public void batchHoldingTask(final List<Long> taskIdList) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            return;
        }

        final QTaskFeeCalculation entity = QTaskFeeCalculation.taskFeeCalculation;
        jpaQueryFactory.update(entity)
                .set(entity.holdStatus, HoldStatusEnum.HOLD)
                .where(entity.id.in(taskIdList)
                        .and(entity.holdStatus.eq(HoldStatusEnum.NORMAL)))
                .execute();

    }

    @Override
    public long batchUnLockTask(final List<Long> taskIdList,
                                final CalculationTaskStateEnum resultState) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            return 0L;
        }

        final QTaskFeeCalculation entity = QTaskFeeCalculation.taskFeeCalculation;
        return jpaQueryFactory.update(entity)
                .set(entity.taskStatus, resultState)
                .set(entity.version, entity.version.add(1))
                .where(entity.id.in(taskIdList)
                        .and(entity.taskStatus.eq(CalculationTaskStateEnum.PROCESSING)))
                .execute();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void cancelHoldingByDocumentIds(UnHoldingDocumentVo order) {
        final QTaskFeeCalculation entity = QTaskFeeCalculation.taskFeeCalculation;
        boolean result = jpaQueryFactory.update(entity)
                .set(entity.holdStatus, HoldStatusEnum.UNHOLD)
                .where(entity.countryCode.eq(order.getCountryCode())
                        .and(entity.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_IN))
                        .and(entity.merchantCode.eq(order.getMerchantCode().trim()))
                        .and(entity.holdStatus.eq(HoldStatusEnum.HOLD))
                        .and(entity.documentId.in(order.getDocumentIds())))
                .execute() > 0;

        if (!result) {
            throw WorkerExceptionCode.CANCEL_HOLDING_TRANSACTION_FAIL.exception();
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void cancelHoldingByTransactionIds(final UnHoldingTransactionVo order) {
        final QTaskFeeCalculation entity = QTaskFeeCalculation.taskFeeCalculation;
        boolean result = jpaQueryFactory.update(entity)
                .set(entity.holdStatus, HoldStatusEnum.UNHOLD)
                .where(entity.id.in(order.getTransactionIds())
                        .and(entity.countryCode.eq(order.getCountryCode()))
                        .and(entity.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_IN))
                        .and(entity.merchantCode.eq(order.getMerchantCode().trim()))
                        .and(entity.holdStatus.eq(HoldStatusEnum.HOLD)))
                .execute() > 0;

        if (!result) {
            throw WorkerExceptionCode.CANCEL_HOLDING_TRANSACTION_FAIL.exception();
        }
    }

    @Override
    public void fillTaskFeeCalculationField(final List<FillFieldTaskFeeCalculationVo> list) {

        log.info("fill taskFeeCalculation money params is->{}", list);

        final List<String> uniqueIds = list.stream().map(FillFieldTaskFeeCalculationVo::getUniqueId)
                .collect(Collectors.toList());

        final Map<String, TaskFeeCalculation> map =
                taskFeeCalculationRepository.findByUniqueIdIn(uniqueIds).stream().collect(
                        Collectors.toMap(TaskFeeCalculation::getUniqueId, Function.identity()));

        list.forEach(vo -> {
            final TaskFeeCalculation taskFeeCalculation = map.get(vo.getUniqueId());
            if (Objects.isNull(taskFeeCalculation)) {
                return;
            }
            final ObjectNode others = taskFeeCalculation.getOthers();
            Optional.ofNullable(vo.getPayerCity())
                    .ifPresent(payerCity -> others.put("payerCity", payerCity));
            Optional.ofNullable(vo.getTargetName())
                    .ifPresent(targetName -> others.put("targetName", targetName));
            Optional.ofNullable(vo.getDocumentId()).ifPresent(taskFeeCalculation::setDocumentId);
        });
        taskFeeCalculationRepository.saveAll(map.values());
    }
}
