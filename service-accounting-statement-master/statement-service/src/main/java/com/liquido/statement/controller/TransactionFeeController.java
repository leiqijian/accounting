package com.liquido.statement.controller;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.TransactionFeeApi;
import com.liquido.statement.pojo.dto.TransactionFeeDto;
import com.liquido.statement.pojo.dto.TransactionTaxDetailDto;
import com.liquido.statement.pojo.vo.ListTransactionFeeVo;
import com.liquido.statement.pojo.vo.QueryTransactionTaxDetailVo;
import com.liquido.statement.service.TransactionFeeService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TransactionFeeController implements TransactionFeeApi {

    private final TransactionFeeService transactionFeeService;

    @Override
    @PostMapping("/statement/transaction-fee/list")
    public ResponseDto<List<TransactionFeeDto>> listTransactionFee(
            @RequestBody @Valid final ListTransactionFeeVo vo) {
        return ResponseDto.success(transactionFeeService.findTransactionFee(vo));
    }

    @Override
    @PostMapping("/statement/transaction-tax/detail")
    public ResponseDto<List<TransactionTaxDetailDto>> queryTransactionTaxDetail(
            @RequestBody @Valid final QueryTransactionTaxDetailVo vo) {
        return ResponseDto.success(transactionFeeService.findTransactionTaxDetail(vo));
    }

}
