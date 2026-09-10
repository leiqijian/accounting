package com.liquido.statement.controller;

import java.util.List;
import java.util.Set;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.TransactionSummaryApi;
import com.liquido.statement.pojo.dto.ListTransactionSummaryDto;
import com.liquido.statement.pojo.dto.TransactionSummaryDailyStatisticsDto;
import com.liquido.statement.pojo.dto.TransactionSummaryDto;
import com.liquido.statement.pojo.dto.TransactionSummaryStatisticsDto;
import com.liquido.statement.pojo.vo.ListHasTransactionAccountVo;
import com.liquido.statement.pojo.vo.ListTransactionSummaryVo;
import com.liquido.statement.pojo.vo.QueryGlobalTransactionVo;
import com.liquido.statement.pojo.vo.TransactionSummaryDailyStatisticsVo;
import com.liquido.statement.pojo.vo.TransactionSummaryStatisticsVo;
import com.liquido.statement.service.TransactionSummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionSummaryController implements TransactionSummaryApi {

    private final TransactionSummaryService transactionSummaryService;

    @PostMapping("/statement/global/transaction/summary")
    @Override
    public ResponseDto<TransactionSummaryDto> globalTransactionSummary(
            @RequestBody @Valid final QueryGlobalTransactionVo vo) {
        return ResponseDto.success(transactionSummaryService.globalTransactionSummary(vo));
    }

    @Override
    @PostMapping("/statement/transaction/summary/statistics")
    public ResponseDto<TransactionSummaryStatisticsDto> statisticsTransactionSummary(
            @RequestBody @Valid final TransactionSummaryStatisticsVo vo) {
        return ResponseDto.success(transactionSummaryService.statisticsTransactionSummary(vo));
    }

    @Override
    @PostMapping("/statement/transaction/summary/daily/statistics")
    public ResponseDto<List<TransactionSummaryDailyStatisticsDto>> statisticsTransactionSummaryDaily(
            @RequestBody @Valid final TransactionSummaryDailyStatisticsVo vo) {
        return ResponseDto.success(transactionSummaryService.statisticsTransactionSummaryDaily(vo));
    }

    @Override
    @PostMapping("/statement/transaction/summary/has/transaction/account/id/list")
    public ResponseDto<Set<Long>> listHasTransactionAccountId(
            @RequestBody @Valid final ListHasTransactionAccountVo vo) {
        return ResponseDto.success(transactionSummaryService.listHasTransactionAccountId(vo));
    }

    @Override
    @PostMapping("/statement/transaction/summary/list")
    public ResponseDto<List<ListTransactionSummaryDto>> listTransactionSummary(
            @RequestBody @Valid final ListTransactionSummaryVo vo) {
        return ResponseDto.success(transactionSummaryService.listTransactionSummary(vo));
    }
}
