package com.liquido.transaction.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.transaction.pojo.dto.ApprovalBizKycDto;
import com.liquido.transaction.pojo.vo.CreateApprovalKycVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ApprovalBizKycApi {

    @PostMapping("/transaction/approval/kyc/create")
    ResponseDto<ApprovalBizKycDto> createKycApproval(
            @RequestBody @Valid final CreateApprovalKycVo approvalKycVo);

}
