package com.liquido.statement.service;

import java.math.BigDecimal;
import java.util.List;

import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.AccountStatementBizBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountStatementBiz;

public interface AccountStatementBizService {

    BigDecimal statisticsDailyBill(final Account account,
                                   final AccountDailyInitBo latestDailyInitBo);

    AccountStatementBiz saveAccountStatementBiz(final AccountStatementBizBo bo);

    List<AccountStatementBiz> saveAccountStatementBiz(final List<AccountStatementBizBo> bo);

}
