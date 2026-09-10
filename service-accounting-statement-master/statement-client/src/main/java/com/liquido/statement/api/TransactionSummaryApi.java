package com.liquido.statement.api;

import java.util.List;
import java.util.Set;
import javax.validation.Valid;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.dto.ListTransactionSummaryDto;
import com.liquido.statement.pojo.dto.TransactionSummaryDailyStatisticsDto;
import com.liquido.statement.pojo.dto.TransactionSummaryDto;
import com.liquido.statement.pojo.dto.TransactionSummaryStatisticsDto;
import com.liquido.statement.pojo.vo.ListHasTransactionAccountVo;
import com.liquido.statement.pojo.vo.ListTransactionSummaryVo;
import com.liquido.statement.pojo.vo.QueryGlobalTransactionVo;
import com.liquido.statement.pojo.vo.TransactionSummaryDailyStatisticsVo;
import com.liquido.statement.pojo.vo.TransactionSummaryStatisticsVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TransactionSummaryApi {

    @PostMapping("/statement/global/transaction/summary")
    ResponseDto<TransactionSummaryDto> globalTransactionSummary(
            @RequestBody @Valid final QueryGlobalTransactionVo vo);

    @PostMapping("/statement/transaction/summary/statistics")
    ResponseDto<TransactionSummaryStatisticsDto> statisticsTransactionSummary(
            @RequestBody @Valid final TransactionSummaryStatisticsVo vo);

    @PostMapping("/statement/transaction/summary/daily/statistics")
    ResponseDto<List<TransactionSummaryDailyStatisticsDto>> statisticsTransactionSummaryDaily(
            @RequestBody @Valid final TransactionSummaryDailyStatisticsVo vo);

    @PostMapping("/statement/transaction/summary/has/transaction/account/id/list")
    ResponseDto<Set<Long>> listHasTransactionAccountId(
            @RequestBody @Valid final ListHasTransactionAccountVo vo);

    @PostMapping("/statement/transaction/summary/list")
    ResponseDto<List<ListTransactionSummaryDto>> listTransactionSummary(
            @RequestBody @Valid final ListTransactionSummaryVo vo);
}
