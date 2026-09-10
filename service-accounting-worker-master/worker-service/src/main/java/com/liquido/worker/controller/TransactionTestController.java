package com.liquido.worker.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.worker.pojo.bo.TaskFeeCalculationSettleBo;
import com.liquido.worker.pojo.dto.QueryMxSpeiProofDto;
import com.liquido.worker.service.calculate.ExchangeRateManager;
import com.liquido.worker.service.calculate.TaskTransactionService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class TransactionTestController {
    private final TaskTransactionService taskTransactionService;
    private final ExchangeRateManager exchangeRateManager;

    @Profile("dev")
    @PostMapping("/worker/transaction/task/test")
    public ResponseDto<QueryMxSpeiProofDto> queryMxSpeiProof(
            @RequestBody @Valid final TaskFeeCalculationSettleBo vo) {

        taskTransactionService.batchProcessTransactionTask(vo);
        return ResponseDto.success();
    }

    @Profile("dev")
    @PostMapping("/worker/transaction/rate/test")
    public ResponseDto<Void> initExchangeRate() {
        exchangeRateManager.initExchangeRate();
        return ResponseDto.success();
    }

}
