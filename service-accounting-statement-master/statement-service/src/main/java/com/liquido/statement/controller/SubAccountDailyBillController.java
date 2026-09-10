package com.liquido.statement.controller;

import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.SubAccountDailyBillApi;
import com.liquido.statement.pojo.dto.SummarySubAccountDailyTransactionDto;
import com.liquido.statement.pojo.vo.HandleSubAccountDailyCutVo;
import com.liquido.statement.pojo.vo.SummaryDailyTransactionVo;
import com.liquido.statement.service.SubAccountDailyBillService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class SubAccountDailyBillController implements SubAccountDailyBillApi {

    private final SubAccountDailyBillService subAccountDailyBillService;

    @PostMapping("/statement/sub-account/daily-bill/summary")
    public ResponseDto<SummarySubAccountDailyTransactionDto> summarySubAccountDailyTransaction(
            @RequestBody final SummaryDailyTransactionVo vo) {
        return ResponseDto.success(
                subAccountDailyBillService.summarySubAccountDailyTransaction(vo));
    }

    @PostMapping("/statement/sub-account/daily-bill/cut")
    public ResponseDto<Void> handleSubAccountDailyCut(
            @RequestBody @Valid final HandleSubAccountDailyCutVo vo) {
        subAccountDailyBillService.handleSubAccountDailyCut(vo);
        return ResponseDto.success();
    }


}
