package com.liquido.transaction.events.handle.batchwithdrawal;

import com.liquido.transaction.enums.ApprovalBizBatchWithdrawalStatusEnum;
import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.pojo.bo.ApprovalBizHandleBo;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizBatchWithdrawal;

import org.springframework.stereotype.Component;

@Component
public class ApprovalBizBatchWithdrawalProcessingHandle
        implements BaseApprovalBizBatchWithdrawalHandle {
    @Override
    public boolean isSupport(final Approval approval,
                             final ApprovalBizBatchWithdrawal batchWithdrawal) {
        return ApprovalStatusEnum.PROCESSING == approval.getStatus();
    }

    @Override
    public ApprovalBizHandleBo<ApprovalBizBatchWithdrawalStatusEnum> handle(final Approval approval,
                                                                            final ApprovalBizBatchWithdrawal batchWithdrawal) {
        return ApprovalBizHandleBo.success(ApprovalBizBatchWithdrawalStatusEnum.PROCESSING);
    }
}
