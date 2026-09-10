package com.liquido.statement.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.vo.TransactionCostExtraVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionCostExtraApi {

    @PostMapping("/statement/transaction-cost/extra/add")
    ResponseDto<Void> addExtraCost(
            @RequestBody @Valid final TransactionCostExtraVo extraCostVo);

}
