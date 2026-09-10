package com.liquido.statement.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.api.TransactionChargeBackOrderApi;
import com.liquido.statement.pojo.dto.ChargeBackSummaryOrderDto;
import com.liquido.statement.pojo.dto.TransactionChargeBackOrderDto;
import com.liquido.statement.pojo.vo.AcceptTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.ChargeBackSummaryOrderVo;
import com.liquido.statement.pojo.vo.DefenseTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.PageTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.QueryTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.SolveTransactionChargeBackOrderVo;
import com.liquido.statement.service.TransactionChargeBackOrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TransactionChargeBackOrderController implements TransactionChargeBackOrderApi {

    private final TransactionChargeBackOrderService transactionChargeBackOrderService;

    @Override
    @PostMapping("/statement/transaction/charge-back-order/summary")
    public ResponseDto<ChargeBackSummaryOrderDto> chargeBackSummary(
            @RequestBody @Valid final ChargeBackSummaryOrderVo vo) {
        return ResponseDto.success(transactionChargeBackOrderService.chargeBackSummary(vo));
    }

    @Override
    @PostMapping("/statement/transaction/charge-back-order/page")
    public ResponseDto<PageVo<TransactionChargeBackOrderDto>> chargeBackPage(
            @RequestBody @Valid final PageTransactionChargeBackOrderVo vo) {
        return ResponseDto.success(transactionChargeBackOrderService.chargeBackPage(vo));
    }

    @Override
    @PostMapping("/statement/transaction/charge-back-order/query")
    public ResponseDto<TransactionChargeBackOrderDto> chargeBackQuery(
            @RequestBody @Valid final QueryTransactionChargeBackOrderVo vo) {
        return ResponseDto.success(transactionChargeBackOrderService.chargeBackQuery(vo));
    }

    @Override
    @PostMapping("/statement/transaction/charge-back-order/defense")
    public ResponseDto<TransactionChargeBackOrderDto> chargeBackDefense(
            @RequestBody @Valid final DefenseTransactionChargeBackOrderVo vo) {
        return ResponseDto.success(transactionChargeBackOrderService.chargeBackDefense(vo));
    }

    @Override
    @PostMapping("/statement/transaction/charge-back-order/defense/solve")
    public ResponseDto<Void> chargeBackDefenseSolve(
            @RequestBody @Valid final SolveTransactionChargeBackOrderVo vo) {
        transactionChargeBackOrderService.chargeBackDefenseSolve(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/transaction/charge-back-order/accept")
    public ResponseDto<Void> chargeBackAccept(
            @RequestBody @Valid final AcceptTransactionChargeBackOrderVo vo) {
        transactionChargeBackOrderService.chargeBackAccept(vo);
        return ResponseDto.success();
    }

}
