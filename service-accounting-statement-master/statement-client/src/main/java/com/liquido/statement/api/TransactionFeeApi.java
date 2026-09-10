package com.liquido.statement.api;

import java.util.List;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.dto.TransactionFeeDto;
import com.liquido.statement.pojo.dto.TransactionTaxDetailDto;
import com.liquido.statement.pojo.vo.ListTransactionFeeVo;
import com.liquido.statement.pojo.vo.QueryTransactionTaxDetailVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionFeeApi {

    @PostMapping("/statement/transaction-fee/list")
    ResponseDto<List<TransactionFeeDto>> listTransactionFee(
            @RequestBody @Valid final ListTransactionFeeVo vo);


    @PostMapping("/statement/transaction-tax/detail")
    ResponseDto<List<TransactionTaxDetailDto>> queryTransactionTaxDetail(
            @RequestBody @Valid final QueryTransactionTaxDetailVo vo);

}
