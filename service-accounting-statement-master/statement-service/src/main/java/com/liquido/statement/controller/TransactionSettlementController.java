package com.liquido.statement.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.TransactionSettlementApi;
import com.liquido.statement.pojo.vo.BatchTransactionMoneyVo;
import com.liquido.statement.service.settlement.TransactionSettlementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class TransactionSettlementController implements TransactionSettlementApi {
    private final TransactionSettlementService transactionSettlementService;

    @Override
    @PostMapping("/statement/transaction/order/settlement/batch")
    public ResponseDto<Void> batchTransactionSettlement(
            @RequestBody @Valid final BatchTransactionMoneyVo vo) {
        transactionSettlementService.batchProcessTransactionSettlement(vo);
        return ResponseDto.success();
    }
}
