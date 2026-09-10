package com.liquido.statement.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import com.liquido.statement.pojo.bo.AccountDailyInitBo;

public interface AccountDailyInitService {

    AccountDailyInitBo getDailyBillInitInfo(
            final Long accountId,
            final LocalDate transactionDate);

    List<AccountDailyInitBo> queryDailyBillInitList(
            final Collection<Long> accountIds,
            final LocalDate beginDate,
            final LocalDate endDate);

}
