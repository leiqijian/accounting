package com.liquido.transaction.service;

import com.liquido.transaction.enums.ApprovalTypeEnum;
import com.liquido.transaction.pojo.bo.ApprovalBizKycBo;
import com.liquido.transaction.pojo.bo.event.ApprovalCallBackEventBo;
import com.liquido.transaction.pojo.dto.ApprovalBizKycDto;
import com.liquido.transaction.pojo.vo.CreateApprovalKycVo;

public interface ApprovalBizKycService {

    ApprovalBizKycBo retrieveKycApproval(final Long approvalId);

    ApprovalBizKycDto createKycApproval(final CreateApprovalKycVo approvalKycVo);

    default boolean supportsApprovalType(final ApprovalTypeEnum approvalType) {
        return null != approvalType && approvalType.equals(ApprovalTypeEnum.KYC);
    }

    void onKycApprovalCallback(final ApprovalCallBackEventBo callBackEventBo);

}
