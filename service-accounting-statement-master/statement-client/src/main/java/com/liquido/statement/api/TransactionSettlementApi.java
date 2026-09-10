package com.liquido.statement.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.vo.BatchTransactionMoneyVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionSettlementApi {

    @PostMapping("/statement/transaction/order/settlement/batch")
    ResponseDto<Void> batchTransactionSettlement(
            @RequestBody @Valid final BatchTransactionMoneyVo vo);
}
