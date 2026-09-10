package com.liquido.statement.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.dto.BatchWithdrawalApplyDto;
import com.liquido.statement.pojo.dto.DealershipWithdrawalInfoDto;
import com.liquido.statement.pojo.vo.ApprovalBatchApplyVo;
import com.liquido.statement.pojo.vo.BatchWithdrawalApplyVo;
import com.liquido.statement.pojo.vo.ListBatchWithdrawalApplyVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface BatchWithdrawalApi {

    @PostMapping("/statement/withdrawal/batch/apply")
    ResponseDto<BatchWithdrawalApplyDto> batchWithdrawalApply(
            @RequestBody @Valid final BatchWithdrawalApplyVo vo);

    @PostMapping("/statement/withdrawal/batch/apply/list")
    ResponseDto<List<DealershipWithdrawalInfoDto>> listWithdrawalApply(
            @RequestBody @Valid final ListBatchWithdrawalApplyVo vo);

    @PostMapping("/statement/withdrawal/batch/approval/apply")
    ResponseDto<Void> approvalBatchApply(@RequestBody @Valid final ApprovalBatchApplyVo vo);
}
