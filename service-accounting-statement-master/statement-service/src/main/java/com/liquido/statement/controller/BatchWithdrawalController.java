package com.liquido.statement.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.BatchWithdrawalApi;
import com.liquido.statement.manage.BatchWithdrawalManager;
import com.liquido.statement.pojo.dto.BatchWithdrawalApplyDto;
import com.liquido.statement.pojo.dto.DealershipWithdrawalInfoDto;
import com.liquido.statement.pojo.vo.ApprovalBatchApplyVo;
import com.liquido.statement.pojo.vo.BatchWithdrawalApplyVo;
import com.liquido.statement.pojo.vo.ListBatchWithdrawalApplyVo;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dealership Batch Withdrawal Controller
 */
@Validated
@RestController
@RequiredArgsConstructor
public class BatchWithdrawalController implements BatchWithdrawalApi {

    private final BatchWithdrawalManager batchWithdrawalManager;

    @Override
    @PostMapping("/statement/withdrawal/batch/apply")
    public ResponseDto<BatchWithdrawalApplyDto> batchWithdrawalApply(
            @RequestBody @Valid final BatchWithdrawalApplyVo vo) {

        return ResponseDto.success(batchWithdrawalManager.batchWithdrawalApply(vo));
    }

    @Override
    @PostMapping("/statement/withdrawal/batch/apply/list")
    public ResponseDto<List<DealershipWithdrawalInfoDto>> listWithdrawalApply(
            @RequestBody @Valid final ListBatchWithdrawalApplyVo vo) {

        return ResponseDto.success(batchWithdrawalManager.listBatchWithdrawalApply(vo));
    }

    @Override
    @PostMapping("/statement/withdrawal/batch/approval/apply")
    public ResponseDto<Void> approvalBatchApply(
            @RequestBody @Valid final ApprovalBatchApplyVo vo) {

        batchWithdrawalManager.approvalApply(vo);

        return ResponseDto.success();
    }

}
