package com.liquido.statement.controller;

import javax.validation.Valid;

import com.liquido.base.enums.OperateModeEnum;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.TransactionBizApi;
import com.liquido.statement.manage.BatchWithdrawalManager;
import com.liquido.statement.manage.BizAdjustmentManager;
import com.liquido.statement.manage.BizTopupManager;
import com.liquido.statement.manage.BizTransferOutManager;
import com.liquido.statement.pojo.vo.AdjustmentDeductionVo;
import com.liquido.statement.pojo.vo.AdjustmentReimburseVo;
import com.liquido.statement.pojo.vo.BatchWithdrawalApprovedHandleVo;
import com.liquido.statement.pojo.vo.TransactionBizVo;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TransactionBizController implements TransactionBizApi {
    private final BizTopupManager bizTopupManager;
    private final BizAdjustmentManager bizAdjustmentManager;
    private final BizTransferOutManager bizTransferOutManager;
    private final BatchWithdrawalManager batchWithdrawalManager;

    @Override
    @PostMapping("/statement/account/topup")
    public ResponseDto<Void> topup(@RequestBody @Valid final TransactionBizVo order) {
        order.setOperateMode(OperateModeEnum.MANUAL);
        bizTopupManager.accountTopUp(order);
        return ResponseDto.success();
    }

    /**
     * Api for merchant dashboard withdraw approved pass(Approval Transfer Out)
     * /statement/account/approval/apply
     *
     * @param order vo
     */
    @Override
    @PostMapping("/statement/account/transfer-out/approved/pass")
    public ResponseDto<Void> transferOutApprovedPass(
            @RequestBody @Valid final TransactionBizVo order) {
        bizTransferOutManager.approvedPass(order);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/batch-withdrawal/approved/handle")
    public ResponseDto<Void> batchWithdrawalApprovedHandle(
            @RequestBody @Valid final BatchWithdrawalApprovedHandleVo vo) {
        batchWithdrawalManager.approvedHandler(vo);
        return ResponseDto.success();
    }

    /**
     * Api for Console admin
     *
     * @param order vo
     */
    @Override
    @PostMapping("/statement/account/console/transfer-out")
    public ResponseDto<Void> transferOut(@RequestBody @Valid final TransactionBizVo order) {
        bizTransferOutManager.accountTransferOut(order);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/adjustment/reimburse")
    public ResponseDto<Void> adjustmentReimburse(
            @RequestBody @Valid final AdjustmentReimburseVo order) {
        bizAdjustmentManager.adjustmentReimburse(order);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/adjustment/deduction")
    public ResponseDto<Void> adjustmentDeduction(
            @RequestBody @Valid final AdjustmentDeductionVo order) {
        bizAdjustmentManager.adjustmentDeduction(order);
        return ResponseDto.success();
    }

}
