package com.liquido.statement.api;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.dto.SummarySubAccountDailyTransactionDto;
import com.liquido.statement.pojo.vo.SummaryDailyTransactionVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SubAccountDailyBillApi {

    @PostMapping("/statement/sub-account/daily-bill/summary")
    ResponseDto<SummarySubAccountDailyTransactionDto> summarySubAccountDailyTransaction(
            @RequestBody @Valid final SummaryDailyTransactionVo vo);

}
