package com.liquido.statement.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.vo.AdjustmentDeductionVo;
import com.liquido.statement.pojo.vo.AdjustmentReimburseVo;
import com.liquido.statement.pojo.vo.BatchWithdrawalApprovedHandleVo;
import com.liquido.statement.pojo.vo.TransactionBizVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionBizApi {

    @PostMapping("/statement/account/topup")
    ResponseDto<Void> topup(@RequestBody @Valid final TransactionBizVo order);

    @PostMapping("/statement/account/console/transfer-out")
    ResponseDto<Void> transferOut(@RequestBody @Valid final TransactionBizVo order);

    @PostMapping("/statement/account/transfer-out/approved/pass")
    ResponseDto<Void> transferOutApprovedPass(@RequestBody @Valid final TransactionBizVo order);

    @PostMapping("/statement/account/batch-withdrawal/approved/handle")
    ResponseDto<Void> batchWithdrawalApprovedHandle(
            @RequestBody @Valid final BatchWithdrawalApprovedHandleVo vo);

    //@PostMapping("/statement/account/payment/transfer-out/result/query")
    //ResponseDto<Void> rotationQueryPaymentStatus();

    //@PostMapping("/statement/account/refund")
    //ResponseDto<Void> refund(@RequestBody @Valid final TransactionBizVo order);

    @PostMapping("/statement/account/adjustment/reimburse")
    ResponseDto<Void> adjustmentReimburse(@RequestBody @Valid final AdjustmentReimburseVo vo);

    @PostMapping("/statement/account/adjustment/deduction")
    ResponseDto<Void> adjustmentDeduction(@RequestBody @Valid final AdjustmentDeductionVo vo);

}
