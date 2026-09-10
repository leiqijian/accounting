package com.liquido.transaction.pojo.bo.event;

import java.io.Serializable;

import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizBatchWithdrawal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalBizBatchWithdrawalFinishEventBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private ApprovalBizBatchWithdrawal batchWithdrawalBiz;

    private ApprovalStatusEnum approvalStatus;

    private Boolean executeResult;

    private String message;
}
