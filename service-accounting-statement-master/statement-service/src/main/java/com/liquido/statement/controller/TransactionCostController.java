package com.liquido.statement.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.TransactionCostApi;
import com.liquido.statement.manage.TransactionCostManager;
import com.liquido.statement.pojo.dto.TransactionMoneyDto;
import com.liquido.statement.pojo.vo.BatchRecalculateCostVo;
import com.liquido.statement.pojo.vo.BatchSyncCostBillVo;
import com.liquido.statement.pojo.vo.SyncTransactionCostVo;
import com.liquido.statement.pojo.vo.SyncTransactionFxRateVo;

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
public class TransactionCostController implements TransactionCostApi {
    private final TransactionCostManager transactionCostManager;

    @PostMapping("/statement/transaction-cost/data/sync")
    public ResponseDto<Void> syncTransactionCostData(
            @RequestBody @Valid final SyncTransactionCostVo vo) {
        transactionCostManager.syncTransactionCostData(vo);
        return ResponseDto.success();
    }

    @PostMapping("/statement/transaction-fx/data/sync")
    public ResponseDto<Void> syncTransactionFxRateData(
            @RequestBody @Valid final SyncTransactionFxRateVo vo) {
        transactionCostManager.syncTransactionFxRateData(vo);
        return ResponseDto.success();
    }

    @PostMapping("/statement/transaction-cost/recalculate/batch")
    public ResponseDto<TransactionMoneyDto> batchRecalculateTransactionCost(
            @RequestBody @Valid final BatchRecalculateCostVo vo) {

        transactionCostManager.batchRecalculateTransactionCost(vo);
        return ResponseDto.success();
    }

    @PostMapping("/statement/transaction-cost/bill-id/sync")
    public ResponseDto<TransactionMoneyDto> syncCostBillId(
            @RequestBody @Valid final BatchSyncCostBillVo vo) {
        transactionCostManager.syncCostBillId(vo);
        return ResponseDto.success();
    }

}
