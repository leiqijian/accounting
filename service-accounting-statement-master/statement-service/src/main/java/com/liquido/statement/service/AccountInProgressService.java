package com.liquido.statement.service;

import java.util.Collection;
import java.util.List;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.entity.AccountInProgress;

public interface AccountInProgressService {

    AccountInProgress findOrInitEntity(final AccountDto account, final CurrencyEnum currency);

    void tryLockAccountInProgress(final Long accountId, final String lockVal);

    boolean releaseLockAccountInProgress(final Long accountId, final String lockVal);


    List<AccountInProgress> findAllByAccountId(final Long id);

    List<AccountInProgress> findAllByAccountIdIn(final Collection<Long> ids);
}
