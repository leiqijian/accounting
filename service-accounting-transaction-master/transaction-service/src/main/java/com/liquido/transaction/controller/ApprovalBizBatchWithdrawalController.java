package com.liquido.transaction.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.transaction.api.ApprovalBizBatchWithdrawalApi;
import com.liquido.transaction.pojo.dto.ApprovalBizBatchWithdrawalDto;
import com.liquido.transaction.pojo.dto.PageApprovalBizBatchWithdrawalDto;
import com.liquido.transaction.pojo.vo.CreateApprovalBizBatchWithdrawalVo;
import com.liquido.transaction.pojo.vo.CreateBatchWithdrawalApprovalVo;
import com.liquido.transaction.pojo.vo.PageApprovalBizBatchWithdrawalVo;
import com.liquido.transaction.pojo.vo.QueryApprovalBizBatchWithdrawalVo;
import com.liquido.transaction.service.ApprovalBizBatchWithdrawalService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
public class ApprovalBizBatchWithdrawalController implements ApprovalBizBatchWithdrawalApi {

    private final ApprovalBizBatchWithdrawalService approvalBizBatchWithdrawalService;

    @PostMapping("/transaction/approval/batch/withdrawal/create")
    public ResponseDto<ApprovalBizBatchWithdrawalDto> createApprovalBizBatchWithdrawal(
            @RequestBody @Valid final CreateBatchWithdrawalApprovalVo vo) {
        return ResponseDto.success(
                approvalBizBatchWithdrawalService.createApprovalBizBatchWithdrawal(vo));
    }

    @PostMapping("/transaction/approval/batch/withdrawal/create/test")
    public ResponseDto<ApprovalBizBatchWithdrawalDto> createApprovalBizBatchWithdrawalTest(
            @RequestBody @Valid final CreateApprovalBizBatchWithdrawalVo vo) {
        return ResponseDto.success(
                approvalBizBatchWithdrawalService.createApprovalBizBatchWithdrawal(vo));
    }

    @Override
    @PostMapping("/transaction/approval/batch/withdrawal/query")
    public ResponseDto<ApprovalBizBatchWithdrawalDto> queryApprovalBizBatchWithdrawal(
            @RequestBody final QueryApprovalBizBatchWithdrawalVo vo) {
        return ResponseDto.success(
                approvalBizBatchWithdrawalService.queryApprovalBizBatchWithdrawal(vo));
    }

    @PostMapping("/transaction/approval/batch/withdrawal/page")
    public ResponseDto<PageVo<PageApprovalBizBatchWithdrawalDto>> pageApprovalBizBatchWithdrawal(
            @RequestBody final PageApprovalBizBatchWithdrawalVo vo) {
        return ResponseDto.success(
                approvalBizBatchWithdrawalService.pageApprovalBizBatchWithdrawal(vo));
    }

    @PostMapping("/transaction/approval/batch/withdrawal/retry")
    public ResponseDto<Void> retryApprovalBizBatchWithdrawal(
            final @RequestParam("file") MultipartFile file,
            final @RequestParam("batchId") Long batchId) {
        approvalBizBatchWithdrawalService.retryUnfrozenAmount(file, batchId);
        return ResponseDto.success();
    }
}
