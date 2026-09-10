package com.liquido.worker.events.listener;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.worker.aws.sqs.msg.ServiceCreateApprovalBizRefundMsg;
import com.liquido.worker.aws.sqs.msg.ServiceFeeCalculationMsg;
import com.liquido.worker.aws.sqs.publish.ServiceCreateApprovalBizRefundPublisher;
import com.liquido.worker.aws.sqs.publish.ServiceFeeCalculationPublisher;
import com.liquido.worker.common.Constant;
import com.liquido.worker.enums.CalculationTaskStateEnum;
import com.liquido.worker.events.TaskFeeCalculationEvent;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.pojo.bo.MerchantAccountBo;
import com.liquido.worker.pojo.bo.TaskFeeCalculationSettleBo;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.repository.TaskFeeCalculationRepository;
import com.liquido.worker.service.MerchantService;

import com.alibaba.nacos.common.utils.Objects;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.wenhao.jpa.Specifications;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskFeeCalculationEventListener {

    private final ServiceFeeCalculationPublisher serviceFeeCalculationPublisher;
    private final ServiceCreateApprovalBizRefundPublisher serviceCreateApprovalBizRefundPublisher;
    private final RedisCacheUtil redisCacheUtil;
    private final TaskFeeCalculationRepository taskFeeCalculationRepository;
    private final ApplicationEventPublisher publisher;
    private final MerchantService merchantService;

    @Async("feePublishSqsEventExecutor")
    @TransactionalEventListener(fallbackExecution = true)
    public void feePublishSqsEvent(final TaskFeeCalculationEvent.FeePublishSqsEvent event) {
        serviceFeeCalculationPublisher.publish(new ServiceFeeCalculationMsg(event.getBo()));
    }

    @SuppressWarnings("BusyWait")
    @Async
    @SneakyThrows
    @TransactionalEventListener(fallbackExecution = true)
    public void feeRerunSecondCalculateEvent(
            final TaskFeeCalculationEvent.FeeRerunSecondCalculateEvent event) {

        final List<Long> ids = event.getSecondCalculateIds();

        final MerchantAccountBo merchantAccountBo = merchantService.getMerchantAccountInfo(
                String.format("%s_%s_%s", event.getMerchantCode(), event.getCountryCode().getCode(),
                        event.getTransactionTypeCode().getCode()));

        while (!ids.isEmpty()) {

            Thread.sleep(30000);

            final List<TaskFeeCalculation> tasks = taskFeeCalculationRepository.findAll(
                    Specifications.<TaskFeeCalculation>and().in("id", ids.toArray())
                            .eq("taskStatus", CalculationTaskStateEnum.SUCCESS)
                            .eq("directionType", DirectionTypeEnum.SETTLED).build());

            if (tasks.isEmpty()) {
                continue;
            }

            for (final TaskFeeCalculation task : tasks) {
                final TaskFeeCalculation.CalculationNode node =
                        task.getCalculationNode().stream()
                                .filter(c -> DirectionTypeEnum.SETTLED != c.getDirectionType())
                                .findFirst()
                                .orElseThrow(WorkerExceptionCode
                                        .DATA_RE_RUN_NOT_FOUND_CALCULATE_NODE::exception);
                task.setTaskStatus(CalculationTaskStateEnum.WAITING);
                task.setDirectionType(node.getDirectionType());
                task.setTransactionTimestamp(node.getTimeStamp());
                task.setTransactionTime(LocalDateTimeUtil.instantToUtc(node.getTimeStamp()));
                task.setVersion(task.getVersion() + 1);
            }

            taskFeeCalculationRepository.saveAllAndFlush(tasks);

            final List<Long> settleIds = tasks.stream().map(TaskFeeCalculation::getId).collect(
                    Collectors.toList());

            tasks.stream().collect(Collectors.groupingBy(v -> TaskFeeCalculationSettleBo.builder()
                            .merchantCode(v.getMerchantCode())
                            .countryCode(v.getCountryCode())
                            .transactionTypeCode(v.getTransactionTypeCode())
                            .isSettleFlg(
                                    TransactionStatusEnum.SETTLED == v.getTransactionStatus()
                                            && DirectionTypeEnum.SETTLED == v.getDirectionType())
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

            ids.removeAll(settleIds);
        }
    }

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void refundPublishSqsEvent(final TaskFeeCalculationEvent.RefundPublishSqsEvent event) {
        final TaskFeeCalculation order = event.getData();

        final JsonNode refundInfo = Optional.ofNullable(order.getOthers())
                .filter(v -> v.hasNonNull("refundAdditionalInfo"))
                .map(v -> v.get("refundAdditionalInfo"))
                .orElse(new ObjectNode(JsonNodeFactory.withExactBigDecimals(true)));

        final boolean manualRefund = Optional.ofNullable(refundInfo.get("manualRefund"))
                .map(JsonNode::asBoolean).orElse(false);

        if (!manualRefund) {
            return;
        }

        final String key =
                String.format(Constant.CACHE.TRANSACTION_ALREADY_REFUND, order.getUniqueId());
        if (Objects.nonNull(redisCacheUtil.getCacheObject(key))) {
            return;
        }

        final ServiceCreateApprovalBizRefundMsg msg = ServiceCreateApprovalBizRefundMsg.builder()
                .merchantCode(order.getMerchantCode())
                .countryCode(order.getCountryCode())
                .transactionTypeCode(order.getTransactionTypeCode())
                .productCode(order.getProductCode())
                .merchantReference(order.getUniqueId())
                .currency(order.getCurrency())
                .refundAmount(order.getAmount())
                .refundTime(LocalDateTimeUtil.instantToUtc(order.getLifecycleTimestamp()))
                .refundAdditionalInfo(refundInfo.deepCopy())
                .manualRefund(Optional.ofNullable(refundInfo.get("manualRefund"))
                        .map(JsonNode::asBoolean).orElse(false))
                .build();
        serviceCreateApprovalBizRefundPublisher.publish(msg);

        redisCacheUtil.setCacheObject(key, order.getUniqueId(), 3, TimeUnit.HOURS);
    }

}
