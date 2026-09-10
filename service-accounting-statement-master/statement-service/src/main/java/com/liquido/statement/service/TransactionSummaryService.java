package com.liquido.statement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.liquido.statement.pojo.bo.TransactionSummaryBo;
import com.liquido.statement.pojo.dto.ListTransactionSummaryDto;
import com.liquido.statement.pojo.dto.TransactionSummaryDailyStatisticsDto;
import com.liquido.statement.pojo.dto.TransactionSummaryDto;
import com.liquido.statement.pojo.dto.TransactionSummaryStatisticsDto;
import com.liquido.statement.pojo.entity.TransactionSummary;
import com.liquido.statement.pojo.vo.ListHasTransactionAccountVo;
import com.liquido.statement.pojo.vo.ListTransactionSummaryVo;
import com.liquido.statement.pojo.vo.QueryGlobalTransactionVo;
import com.liquido.statement.pojo.vo.TransactionSummaryDailyStatisticsVo;
import com.liquido.statement.pojo.vo.TransactionSummaryStatisticsVo;

public interface TransactionSummaryService {

    TransactionSummary getOrInitTransactionSummary(
            final Long merchantId,
            final Long accountId,
            final LocalDate transactionDate);

    void saveOrUpdate(final TransactionSummaryBo summary);

    /**
     * query today and yesterday transaction summary
     *
     * @param accountId
     * @return
     */
    List<TransactionSummaryBo> queryLast2DayTransactionSummary(final Long accountId);

    TransactionSummaryDto globalTransactionSummary(
            final QueryGlobalTransactionVo vo);

    TransactionSummaryStatisticsDto statisticsTransactionSummary(
            final TransactionSummaryStatisticsVo vo);

    List<TransactionSummaryDailyStatisticsDto> statisticsTransactionSummaryDaily(
            final TransactionSummaryDailyStatisticsVo vo);

    Set<Long> listHasTransactionAccountId(final ListHasTransactionAccountVo vo);

    List<ListTransactionSummaryDto> listTransactionSummary(final ListTransactionSummaryVo vo);
}
