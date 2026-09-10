package com.liquido.transaction.events.handle;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.transaction.Transactional;

import com.liquido.statement.StatementApis;
import com.liquido.transaction.common.Constant;
import com.liquido.transaction.enums.ApprovalBizBatchWithdrawalStatusEnum;
import com.liquido.transaction.events.handle.batchwithdrawal.BaseApprovalBizBatchWithdrawalHandle;
import com.liquido.transaction.pojo.bo.ApprovalBizHandleBo;
import com.liquido.transaction.pojo.bo.event.ApprovalBizBatchWithdrawalFinishEvent;
import com.liquido.transaction.pojo.bo.event.ApprovalBizBatchWithdrawalFinishEventBo;
import com.liquido.transaction.pojo.bo.event.ApprovalCompleteBizBatchWithdrawalEvent;
import com.liquido.transaction.pojo.bo.event.ApprovalCompleteBizBatchWithdrawalEventBo;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizBatchWithdrawal;
import com.liquido.transaction.repository.ApprovalBizBatchWithdrawalRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class ApprovalBizBatchWithdrawalHandleEventListener {

    private final ApplicationEventPublisher publisher;
    private final StatementApis.StatementFeign statementFeign;
    private final List<BaseApprovalBizBatchWithdrawalHandle> batchWithdrawalHandles;
    private final ApprovalBizBatchWithdrawalRepository approvalBizBatchWithdrawalRepository;

    @Async
    @TransactionalEventListener
    public void approvalBatchWithdrawalHandle(final ApprovalCompleteBizBatchWithdrawalEvent event) {
        log.info("approval biz batch withdrawal pre handle event->{}", event);

        final ApprovalCompleteBizBatchWithdrawalEventBo bo = event.getEvent();
        final Approval approval = bo.getApproval();
        final ApprovalBizBatchWithdrawal batchWithdrawal = bo.getBatchWithdrawalBiz();

        final BaseApprovalBizBatchWithdrawalHandle batchWithdrawalHandle =
                batchWithdrawalHandles.stream()
                        .filter(handle -> handle.isSupport(approval, batchWithdrawal))
                        .findFirst().orElse(null);

        if (Objects.isNull(batchWithdrawalHandle)) {
            return;
        }

        final ApprovalBizHandleBo<ApprovalBizBatchWithdrawalStatusEnum> handleResult =
                batchWithdrawalHandle.handle(approval, batchWithdrawal);
        if (Objects.isNull(handleResult)) {
            return;
        }

        if (handleResult.getHandleFlag()) {
            batchWithdrawal.setStatus(handleResult.getSuccessStatus());
            if (ApprovalBizBatchWithdrawalStatusEnum.COMPLETED == batchWithdrawal.getStatus()) {
                batchWithdrawal.setFailCount(Constant.BIZ_APPROVAL.SUCCESS_COUNT);
            }
        } else {
            log.error("batch withdrawal approval handle result fail,message={}",
                    handleResult.getMessage());
            batchWithdrawal.setFailCount(Optional.ofNullable(batchWithdrawal.getFailCount())
                    .map(count -> ++count).orElse(Constant.BIZ_APPROVAL.MIN_FAIL_COUNT));

            if (batchWithdrawal.getFailCount() >= Constant.BIZ_APPROVAL.MAX_FAIL_COUNT
                    || !handleResult.getFailStatus().getNeedRetry()) {
                batchWithdrawal.setStatus(handleResult.getFailStatus());
                batchWithdrawal.setFailCount(Constant.BIZ_APPROVAL.MAX_FAIL_COUNT);
            }
        }
        approvalBizBatchWithdrawalRepository.saveAndFlush(batchWithdrawal);

        if (Boolean.TRUE.equals(batchWithdrawal.getStatus().getFinalHandle())) {
            log.info("batch withdrawal approval post handle result->{}", event);
            publisher.publishEvent(new ApprovalBizBatchWithdrawalFinishEvent(
                    ApprovalBizBatchWithdrawalFinishEventBo.builder()
                            .batchWithdrawalBiz(batchWithdrawal)
                            .approvalStatus(approval.getStatus())
                            .executeResult(handleResult.getHandleFlag())
                            .message(handleResult.getMessage())
                            .build()));
        }

    }
}
