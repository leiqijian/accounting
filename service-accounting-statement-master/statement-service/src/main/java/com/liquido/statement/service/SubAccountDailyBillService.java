package com.liquido.statement.service;

import java.util.Collection;
import java.util.List;

import com.liquido.statement.pojo.dto.SubAccountDailyBillDto;
import com.liquido.statement.pojo.dto.SummarySubAccountDailyTransactionDto;
import com.liquido.statement.pojo.vo.HandleSubAccountDailyCutVo;
import com.liquido.statement.pojo.vo.SummaryDailyTransactionVo;

public interface SubAccountDailyBillService {

    SummarySubAccountDailyTransactionDto summarySubAccountDailyTransaction(
            final SummaryDailyTransactionVo vo);

    void handleSubAccountDailyCut(HandleSubAccountDailyCutVo vo);

    List<SubAccountDailyBillDto> getLatestSubAccountDailyBillBySubAccountIds(
            Collection<Long> needReloadSubAccountIds);

}
