package com.liquido.statement.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.TransactionCostExtraApi;
import com.liquido.statement.pojo.vo.TransactionCostExtraVo;
import com.liquido.statement.service.TransactionCostExtraService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TransactionCostExtraController implements TransactionCostExtraApi {

    private final TransactionCostExtraService transactionCostExtraService;

    @Override
    @PostMapping("/statement/transaction-cost/extra/add")
    public ResponseDto<Void> addExtraCost(
            @RequestBody @Valid final TransactionCostExtraVo costExtraVo) {

        transactionCostExtraService.addTransactionCostExtra(costExtraVo);

        return ResponseDto.success();
    }
}
