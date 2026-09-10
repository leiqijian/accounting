package com.liquido.statement.controller;

import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.api.AccountDailyBillApi;
import com.liquido.statement.manage.FixAccountDailyBill;
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
import com.liquido.statement.pojo.vo.FixAccountDailyBillVo;
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
import com.liquido.statement.service.AccountDailyBillService;
import com.liquido.statement.service.dailycut.DailyCutService;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class AccountDailyBillController implements AccountDailyBillApi {

    private final DailyCutService dailyCutService;
    private final FixAccountDailyBill fixAccountDailyBill;
    private final AccountDailyBillService accountDailyBillService;

    @Override
    @PostMapping("/statement/account/monthly-bill/list")
    public ResponseDto<List<AccountDailyBillDto>> listAccountMonthlyBill(
            @RequestBody @Valid final QueryMonthlyBillVo vo) {
        return ResponseDto.success(accountDailyBillService.findAccountMonthlyBill(vo));
    }

    @Override
    @PostMapping("/statement/account/daily-bill/query")
    public ResponseDto<AccountDailyBillDto> queryAccountDailyBill(
            @RequestBody @Valid final QueryHisAccountDailyBillVo vo) {
        return ResponseDto.success(accountDailyBillService.queryHisAccountDailyBill(vo));
    }

    @Override
    @PostMapping("/statement/account/daily-bill/batch-query")
    public ResponseDto<List<AccountDailyBillDto>> batchQueryAccountDailyBill(
            final BatchQueryHisAccountDailyBillVo vo) {
        return ResponseDto.success(accountDailyBillService.batchQueryHisAccountDailyBill(vo));
    }

    @Override
    @PostMapping("/statement/account/daily-bill/list")
    public ResponseDto<List<AccountDailyBillDto>> listAccountDailyBill(
            @RequestBody @Valid final QueryDailyBillVo vo) {
        return ResponseDto.success(accountDailyBillService.findAccountDailyBill(vo));
    }

    @Override
    @PostMapping("/statement/account/daily-transaction/chart")
    public ResponseDto<List<DailyTransactionChartDto>> queryDailyTransactionChart(
            @RequestBody @Valid final QueryDailyTransactionChartVo vo) {
        return ResponseDto.success(accountDailyBillService.findDailyTransactionChart(vo));
    }

    @Override
    @PostMapping("/statement/account/daily-cut/run")
    public ResponseDto<Void> runAccountDailyCut() {
        dailyCutService.runAccountDailyCut();
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/daily-cut/rerun")
    public ResponseDto<Void> reRunAccountDailyCut(
            @RequestBody @Valid final ReRunAccountDailyCutVo vo) {
        dailyCutService.reRunAccountDailyCut(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/daily-bill/batch-create")
    public ResponseDto<Void> generateAccountDailyBill(
            @RequestBody @Valid final CreateDailyBillVo vo) {
        accountDailyBillService.generateAccountDailyBill(vo);
        return ResponseDto.success();
    }

    @Override
    @GetMapping("/statement/account/daily-bill/create/{billId}")
    public ResponseDto<Void> generateAccountDailyBill(
            @PathVariable("billId") @Valid @NotNull final Long billId) {

        accountDailyBillService.generateAccountDailyBill(CreateDailyBillVo.builder()
                .billIdList(Lists.newArrayList(billId)).build());

        return ResponseDto.success();
    }

    @Override
    @PostMapping("/statement/account/daily-bill/page")
    public ResponseDto<PageVo<AccountDailyBillDto>> pageAccountDaily(
            @RequestBody @Valid final QueryPageAccountDailyVo vo) {
        return ResponseDto.success(accountDailyBillService.pageAccountDaily(vo));
    }

    /**
     * statistics all accounts history transactionAmountUsd and  transactionCount
     */
    @Override
    @PostMapping("/statement/history/daily-bill/statistics")
    public ResponseDto<HistoryDailyBillStatisticsDto> statisticsHistoryDailyBill() {
        return ResponseDto.success(accountDailyBillService.statisticsHistoryDailyBill());
    }

    @Override
    @PostMapping("/statement/account/daily-bill/statistics")
    public ResponseDto<DailyBillStatisticsDto> statisticsDailyBill(
            @RequestBody @Valid final DailyBillStatisticsVo vo) {
        return ResponseDto.success(accountDailyBillService.statisticsDailyBill(vo));
    }

    @Override
    @PostMapping("/statement/account/daily-bill/specified/ids-date/list")
    public ResponseDto<List<AccountDailyBillDto>> queryAccountDailyBillByIdsDate(
            @RequestBody @Valid final QueryAccountIdsDateVo vo) {
        return ResponseDto.success(accountDailyBillService.listAccountDailyBillByIdsDate(vo));
    }

    @Override
    @PostMapping("/statement/account/daily-bill-statistics/list")
    public ResponseDto<List<AccountDailyBillStatisticsDto>> dailyStatisticsByDate(
            @RequestBody @Valid final QueryAccountDailyBillStatisticsVo vo) {
        return ResponseDto.success(
                accountDailyBillService.dailyStatisticsByDate(vo.getStartDate(), vo.getEndDate()));
    }

    @Override
    @PostMapping("/statement/account/each-merchant-daily-bill-statistics/list")
    public ResponseDto<List<EachMerchantDailyBillStatisticsDto>> eachMerchantDailyStatisticsByDate(
            @RequestBody @Valid final QueryAccountDailyBillStatisticsVo vo) {
        return ResponseDto.success(accountDailyBillService.eachMerchantDailyStatisticsByDate(
                vo.getStartDate(), vo.getEndDate()));
    }

    @Override
    @PostMapping("/statement/account/daily-bill/country-statistics/list")
    public ResponseDto<List<CountryDailyBillStatisticsDto>> countryDailyBillStatistics(
            @RequestBody @Valid final QueryCountryDailyBillStatisticsVo vo) {
        return ResponseDto.success(accountDailyBillService.countryDailyBillStatistics(vo));
    }

    @Override
    @PostMapping("/statement/global/daily-bill/list")
    public ResponseDto<List<DailyBillStatisticsDto>> globalDailyBillList(
            @RequestBody @Valid final QueryGlobalTransactionVo vo) {
        return ResponseDto.success(accountDailyBillService.globalDailyBillList(vo));
    }

    @Override
    @PostMapping("/statement/account/daily-bill/has/transaction/account/list")
    public ResponseDto<Set<Long>> listHasTransactionAccount(
            @RequestBody @Valid final ListHasTransactionAccountVo vo) {
        return ResponseDto.success(accountDailyBillService.listHasTransactionAccount(vo));
    }

    @PostMapping("/statement/account/daily-bill/history/fix")
    public ResponseDto<Void> fixHistoryAccountDailyBill(
            @RequestBody @Valid final FixAccountDailyBillVo vo) {
        fixAccountDailyBill.fixHistoryAccountDailyBill(vo);
        return ResponseDto.success();
    }

}
