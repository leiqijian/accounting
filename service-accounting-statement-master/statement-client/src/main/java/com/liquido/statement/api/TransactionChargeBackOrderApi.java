package com.liquido.statement.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.ChargeBackSummaryOrderDto;
import com.liquido.statement.pojo.dto.TransactionChargeBackOrderDto;
import com.liquido.statement.pojo.vo.AcceptTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.ChargeBackSummaryOrderVo;
import com.liquido.statement.pojo.vo.DefenseTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.PageTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.QueryTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.SolveTransactionChargeBackOrderVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionChargeBackOrderApi {

    @PostMapping("/statement/transaction/charge-back-order/summary")
    ResponseDto<ChargeBackSummaryOrderDto> chargeBackSummary(
            @RequestBody @Valid final ChargeBackSummaryOrderVo vo);

    @PostMapping("/statement/transaction/charge-back-order/page")
    ResponseDto<PageVo<TransactionChargeBackOrderDto>> chargeBackPage(
            @RequestBody @Valid final PageTransactionChargeBackOrderVo vo);

    @PostMapping("/statement/transaction/charge-back-order/query")
    ResponseDto<TransactionChargeBackOrderDto> chargeBackQuery(
            @RequestBody @Valid final QueryTransactionChargeBackOrderVo vo);

    @PostMapping("/statement/transaction/charge-back-order/defense")
    ResponseDto<TransactionChargeBackOrderDto> chargeBackDefense(
            @RequestBody @Valid final DefenseTransactionChargeBackOrderVo vo);

    @PostMapping("/statement/transaction/charge-back-order/defense/solve")
    ResponseDto<Void> chargeBackDefenseSolve(
            @RequestBody @Valid final SolveTransactionChargeBackOrderVo vo);

    @PostMapping("/statement/transaction/charge-back-order/accept")
    ResponseDto<Void> chargeBackAccept(
            @RequestBody @Valid final AcceptTransactionChargeBackOrderVo vo);
}
