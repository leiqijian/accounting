package com.liquido.transaction.api;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.transaction.pojo.dto.ApprovalBizBatchWithdrawalDto;
import com.liquido.transaction.pojo.vo.QueryApprovalBizBatchWithdrawalVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ApprovalBizBatchWithdrawalApi {

    @PostMapping("/transaction/approval/batch/withdrawal/query")
    ResponseDto<ApprovalBizBatchWithdrawalDto> queryApprovalBizBatchWithdrawal(
            @RequestBody final QueryApprovalBizBatchWithdrawalVo vo);

}
