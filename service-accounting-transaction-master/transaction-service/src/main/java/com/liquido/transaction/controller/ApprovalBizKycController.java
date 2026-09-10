package com.liquido.transaction.controller;


import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.transaction.api.ApprovalBizKycApi;
import com.liquido.transaction.pojo.dto.ApprovalBizKycDto;
import com.liquido.transaction.pojo.vo.CreateApprovalKycVo;
import com.liquido.transaction.service.ApprovalBizKycService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class ApprovalBizKycController implements ApprovalBizKycApi {

    private final ApprovalBizKycService approvalBizKycService;

    @Override
    @PostMapping("/transaction/approval/kyc/create")
    public ResponseDto<ApprovalBizKycDto> createKycApproval(
            @RequestBody @Valid final CreateApprovalKycVo approvalKycVo) {
        return ResponseDto.success(approvalBizKycService.createKycApproval(approvalKycVo));
    }
}
