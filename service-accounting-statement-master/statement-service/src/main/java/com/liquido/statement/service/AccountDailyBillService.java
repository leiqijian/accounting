package com.liquido.statement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.AccountDailyBillDto;
import com.liquido.statement.pojo.dto.AccountDailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.CountryDailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.DailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.DailyTransactionChartDto;
import com.liquido.statement.pojo.dto.EachMerchantDailyBillStatisticsDto;
import com.liquido.statement.pojo.dto.HistoryDailyBillStatisticsDto;
import com.liquido.statement.pojo.entity.AccountDailyBill;
import com.liquido.statement.pojo.vo.BatchQueryHisAccountDailyBillVo;
import com.liquido.statement.pojo.vo.CreateDailyBillVo;
import com.liquido.statement.pojo.vo.DailyBillStatisticsVo;
import com.liquido.statement.pojo.vo.FixAccountDailyBillVo;
import com.liquido.statement.pojo.vo.ListHasTransactionAccountVo;
import com.liquido.statement.pojo.vo.QueryAccountIdsDateVo;
import com.liquido.statement.pojo.vo.QueryCountryDailyBillStatisticsVo;
import com.liquido.statement.pojo.vo.QueryDailyBillVo;
import com.liquido.statement.pojo.vo.QueryDailyTransactionChartVo;
import com.liquido.statement.pojo.vo.QueryGlobalTransactionVo;
import com.liquido.statement.pojo.vo.QueryHisAccountDailyBillVo;
import com.liquido.statement.pojo.vo.QueryMonthlyBillVo;
import com.liquido.statement.pojo.vo.QueryPageAccountDailyVo;

public interface AccountDailyBillService {

    void generateAccountDailyBill(final CreateDailyBillVo vo);

    AccountDailyBill saveAccountDailyBill(final AccountDailyBill accountDailyBill);

    AccountDailyBillDto queryHisAccountDailyBill(final QueryHisAccountDailyBillVo vo);

    List<AccountDailyBillDto> batchQueryHisAccountDailyBill(BatchQueryHisAccountDailyBillVo vo);

    AccountDailyBillDto queryLatestAccountDailyBill(final Long accountId);

    AccountDailyBill findById(final Long id);

    List<AccountDailyBill> findAllById(final List<Long> ids);

    List<AccountDailyBillDto> findAccountMonthlyBill(final QueryMonthlyBillVo vo);

    List<AccountDailyBillDto> findAccountDailyBill(final QueryDailyBillVo vo);

    AccountDailyBill findAccountBillByBillDay(final Long accountId, final LocalDate billDate);

    List<DailyTransactionChartDto> findDailyTransactionChart(
            final QueryDailyTransactionChartVo vo);

    PageVo<AccountDailyBillDto> pageAccountDaily(final QueryPageAccountDailyVo vo);

    List<AccountDailyBillStatisticsDto> dailyStatisticsByDate(final LocalDate startDate,
                                                              final LocalDate endDate);

    List<EachMerchantDailyBillStatisticsDto> eachMerchantDailyStatisticsByDate(
            final LocalDate startDate,
            final LocalDate endDate);

    HistoryDailyBillStatisticsDto statisticsHistoryDailyBill();

    List<CountryDailyBillStatisticsDto> countryDailyBillStatistics(
            final QueryCountryDailyBillStatisticsVo vo);

    List<AccountDailyBillDto> listAccountDailyBillByIdsDate(final QueryAccountIdsDateVo vo);

    DailyBillStatisticsDto statisticsDailyBill(final DailyBillStatisticsVo vo);

    List<DailyBillStatisticsDto> globalDailyBillList(final QueryGlobalTransactionVo vo);

    Set<Long> listHasTransactionAccount(final ListHasTransactionAccountVo vo);

    void fixHistoryAccountDailyBill(final FixAccountDailyBillVo vo);

}
