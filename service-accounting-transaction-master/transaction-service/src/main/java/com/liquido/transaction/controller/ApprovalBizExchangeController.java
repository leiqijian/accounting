package com.liquido.transaction.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.transaction.api.ApprovalBizExchangeApi;
import com.liquido.transaction.pojo.bo.ApprovalBizExchangeConfigBo;
import com.liquido.transaction.pojo.dto.ApprovalBizExchangeDto;
import com.liquido.transaction.pojo.dto.CalculateExchangeAmountDto;
import com.liquido.transaction.pojo.dto.QueryApprovalBizExchangeDto;
import com.liquido.transaction.pojo.dto.QueryApprovalConfigDto;
import com.liquido.transaction.pojo.vo.CalculateExchangeAmountVo;
import com.liquido.transaction.pojo.vo.CreateApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.CreateApprovalConfigVo;
import com.liquido.transaction.pojo.vo.PageApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.QueryApprovalBizExchangeVo;
import com.liquido.transaction.pojo.vo.QueryApprovalConfigVo;
import com.liquido.transaction.pojo.vo.SubmitOfflineProofBizExchangeVo;
import com.liquido.transaction.service.ApprovalBizExchangeService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class ApprovalBizExchangeController implements ApprovalBizExchangeApi {

    private final ApprovalBizExchangeService approvalBizExchangeService;

    @Override
    @PostMapping("/transaction/approval/exchange/config/query")
    public ResponseDto<QueryApprovalConfigDto<ApprovalBizExchangeConfigBo>>
    queryApprovalExchangeConfig(@RequestBody @Valid final QueryApprovalConfigVo vo) {
        return ResponseDto.success(
                approvalBizExchangeService.queryApprovalConfig(vo.getAccountId()));
    }

    @Override
    @PostMapping("/transaction/approval/exchange/create")
    public ResponseDto<ApprovalBizExchangeDto> createApprovalBizExchange(
            @RequestBody @Valid final CreateApprovalBizExchangeVo vo) {
        return ResponseDto.success(approvalBizExchangeService.createApprovalBizExchange(vo));
    }

    @Override
    @PostMapping("/transaction/approval/exchange/offline/proof/submit")
    public ResponseDto<Void> submitOfflineProofBizExchange(
            @RequestBody @Valid final SubmitOfflineProofBizExchangeVo vo) {
        approvalBizExchangeService.submitOfflineProofBizExchange(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/transaction/approval/exchange/page")
    public ResponseDto<PageVo<ApprovalBizExchangeDto>> pageApprovalBizExchange(
            @RequestBody @Valid final PageApprovalBizExchangeVo vo) {
        return ResponseDto.success(approvalBizExchangeService.pageApprovalBizExchange(vo));
    }

    @Override
    @PostMapping("/transaction/approval/exchange/query")
    public ResponseDto<QueryApprovalBizExchangeDto> queryApprovalBizExchange(
            @RequestBody @Valid final QueryApprovalBizExchangeVo vo) {
        return ResponseDto.success(approvalBizExchangeService.queryApprovalBizExchange(vo));
    }

    @Override
    @PostMapping("/transaction/approval/exchange/cancel")
    public ResponseDto<Void> cancelApprovalBizExchange(
            @RequestBody @Valid final QueryApprovalBizExchangeVo vo) {
        approvalBizExchangeService.cancelApprovalBizExchange(vo, true);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/transaction/approval/exchange/contract/confirm/{exchangeApprovalId}")
    public ResponseDto<Void> contractConfirm(
            @PathVariable(value = "exchangeApprovalId") final Long exchangeApprovalId) {
        approvalBizExchangeService.contractConfirm(exchangeApprovalId);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/transaction/approval/exchange/calculate")
    public ResponseDto<CalculateExchangeAmountDto> calculateExchangeAmount(
            @RequestBody @Valid final CalculateExchangeAmountVo vo) {
        return ResponseDto.success(approvalBizExchangeService.calculateExchangeAmount(vo));
    }

    @Override
    @PostMapping("/transaction/approval/exchange/config/create/batch")
    public ResponseDto<List<QueryApprovalConfigDto<ApprovalBizExchangeConfigBo>>> createExchangeConfigBatch(
            @RequestBody @Valid
            final List<CreateApprovalConfigVo<ApprovalBizExchangeConfigBo>> list) {

        return ResponseDto.success(approvalBizExchangeService.createApprovalConfigBatch(list));
    }

}
