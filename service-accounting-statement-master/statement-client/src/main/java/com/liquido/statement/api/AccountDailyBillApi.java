package com.liquido.statement.api;


import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.AccountDailyBillDto;
import com.liquido.statement.pojo.dto.AccountDailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.CountryDailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.DailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.DailyTransactionChartDto;
import com.liquido.statement.pojo.dto.EachMerchantDailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.HistoryDailyBillStatisticsDto;
import com.liquido.statement.pojo.vo.BatchQueryHisAccountDailyBillVo;
import com.liquido.statement.pojo.vo.CreateDailyBillVo;
import com.liquido.statement.pojo.vo.DailyBillStatisticsVo;
import com.liquido.statement.pojo.vo.ListHasTransactionAccountVo;
import com.liquido.statement.pojo.vo.QueryAccountDailyBillStatisticsVo;
import com.liquido.statement.pojo.vo.QueryAccountIdsDateVo;
import com.liquido.statement.pojo.vo.QueryCountryDailyBillStatisticsVo;
import com.liquido.statement.pojo.vo.QueryDailyBillVo;
import com.liquido.statement.pojo.vo.QueryDailyTransactionChartVo;
import com.liquido.statement.pojo.vo.QueryGlobalTransactionVo;
import com.liquido.statement.pojo.vo.QueryHisAccountDailyBillVo;
import com.liquido.statement.pojo.vo.QueryMonthlyBillVo;
import com.liquido.statement.pojo.vo.QueryPageAccountDailyVo;
import com.liquido.statement.pojo.vo.ReRunAccountDailyCutVo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AccountDailyBillApi {

    @PostMapping("/statement/account/monthly-bill/list")
    ResponseDto<List<AccountDailyBillDto>> listAccountMonthlyBill(
            @RequestBody @Valid final QueryMonthlyBillVo vo);

    @PostMapping("/statement/account/daily-bill/query")
    ResponseDto<AccountDailyBillDto> queryAccountDailyBill(
            @RequestBody @Valid final QueryHisAccountDailyBillVo vo);

    @PostMapping("/statement/account/daily-bill/batch-query")
    ResponseDto<List<AccountDailyBillDto>> batchQueryAccountDailyBill(
            @RequestBody @Valid final BatchQueryHisAccountDailyBillVo vo);

    @PostMapping("/statement/account/daily-bill/list")
    ResponseDto<List<AccountDailyBillDto>> listAccountDailyBill(
            @RequestBody @Valid final QueryDailyBillVo vo);

    @PostMapping("/statement/account/daily-transaction/chart")
    ResponseDto<List<DailyTransactionChartDto>> queryDailyTransactionChart(
            @RequestBody @Valid final QueryDailyTransactionChartVo vo);

    @PostMapping("/statement/account/daily-cut/run")
    ResponseDto<Void> runAccountDailyCut();

    @PostMapping("/statement/account/daily-cut/rerun")
    ResponseDto<Void> reRunAccountDailyCut(@Valid @RequestBody final ReRunAccountDailyCutVo vo);

    @PostMapping("/statement/account/daily-bill/create")
    ResponseDto<Void> generateAccountDailyBill(@RequestBody @Valid final CreateDailyBillVo vo);

    @GetMapping("/statement/account/daily-bill/create/{billId}")
    ResponseDto<Void> generateAccountDailyBill(
            @PathVariable("billId") @Valid @NotNull final Long billId);

    @PostMapping("/statement/account/daily-bill/page")
    ResponseDto<PageVo<AccountDailyBillDto>> pageAccountDaily(
            @RequestBody @Valid final QueryPageAccountDailyVo vo);

    @PostMapping("/statement/account/daily-bill-statistics/list")
    ResponseDto<List<AccountDailyBillStatisticsDto>> dailyStatisticsByDate(
            @RequestBody @Valid final QueryAccountDailyBillStatisticsVo vo);

    @PostMapping("/statement/account/each-merchant-daily-bill-statistics/list")
    ResponseDto<List<EachMerchantDailyBillStatisticsDto>> eachMerchantDailyStatisticsByDate(
            @RequestBody @Valid final QueryAccountDailyBillStatisticsVo vo);

    @PostMapping("/statement/history/daily-bill/statistics")
    ResponseDto<HistoryDailyBillStatisticsDto> statisticsHistoryDailyBill();

    @PostMapping("/statement/account/daily-bill/statistics")
    ResponseDto<DailyBillStatisticsDto> statisticsDailyBill(
            @RequestBody @Valid final DailyBillStatisticsVo vo);

    @PostMapping("/statement/account/daily-bill/specified/ids-date/list")
    ResponseDto<List<AccountDailyBillDto>> queryAccountDailyBillByIdsDate(
            @RequestBody @Valid final QueryAccountIdsDateVo vo);

    @PostMapping("/statement/account/daily-bill/country-statistics/list")
    ResponseDto<List<CountryDailyBillStatisticsDto>> countryDailyBillStatistics(
            @RequestBody @Valid final QueryCountryDailyBillStatisticsVo vo);

    @PostMapping("/statement/global/daily-bill/list")
    ResponseDto<List<DailyBillStatisticsDto>> globalDailyBillList(
            @RequestBody @Valid final QueryGlobalTransactionVo vo);

    @PostMapping("/statement/account/daily-bill/has/transaction/account/list")
    ResponseDto<Set<Long>> listHasTransactionAccount(
            @RequestBody @Valid final ListHasTransactionAccountVo vo);
}
