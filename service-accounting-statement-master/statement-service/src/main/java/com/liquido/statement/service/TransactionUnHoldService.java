package com.liquido.statement.service;

import java.math.BigDecimal;

import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.TransactionUnHold;

public interface TransactionUnHoldService {

    BigDecimal statisticsDailyUnHoldAmount(
            final Account account,
            final AccountDailyInitBo dailyInitBo);

    void save(final TransactionUnHold entity);
}
