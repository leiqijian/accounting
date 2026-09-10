package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.entity.AccountInProgress;
import com.liquido.statement.repository.AccountInProgressRepository;
import com.liquido.statement.service.AccountInProgressService;

import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountInProgressServiceImpl implements AccountInProgressService {

    private final AccountInProgressRepository accountInProgressRepository;
    private final RedisDistLock redisDistLock;

    @Override
    public AccountInProgress findOrInitEntity(
            final AccountDto account,
            final CurrencyEnum currency
    ) {
        return Optional.ofNullable(accountInProgressRepository
                        .findByAccountIdAndInProgressCurrency(account.getId(), currency))
                .orElseGet(() -> accountInProgressRepository.save(
                        AccountInProgress.builder()
                                .merchantId(account.getMerchantId())
                                .accountId(account.getId())
                                .inProgressAmount(BigDecimal.ZERO)
                                .fee(BigDecimal.ZERO)
                                .tax(BigDecimal.ZERO)
                                .netAmount(BigDecimal.ZERO)
                                .inProgressCurrency(currency)
                                .createdTime(LocalDateTime.now())
                                .build()));
    }

    @Override
    public void tryLockAccountInProgress(final Long accountId, final String lockVal) {

        if (Objects.isNull(accountId) || accountId <= 0 || StringUtil.isBlank(lockVal)) {
            log.warn("get account fail accountId or syncId is illegal : accountId={}, "
                    + "lockVal={}", accountId, lockVal);
            throw StatementExceptionCode.GET_ACCOUNT_IN_PROGRESS_LOCKED_FAIL.exception();
        }

        final boolean isLocked = redisDistLock.tryLock(
                CacheConstant.ACCOUNT_IN_PROGRESS_LOCK_KEY
                        + accountId, lockVal, 30, TimeUnit.MINUTES);

        if (!isLocked) {
            log.warn("get and locked account fail: accountId={}, lockVal={}", accountId, lockVal);
            throw StatementExceptionCode.GET_ACCOUNT_IN_PROGRESS_LOCKED_FAIL.exception();
        }
    }

    @Override
    public boolean releaseLockAccountInProgress(final Long accountId, final String lockVal) {
        return redisDistLock.unlock(
                CacheConstant.ACCOUNT_IN_PROGRESS_LOCK_KEY + accountId, lockVal);
    }


    @Override
    public List<AccountInProgress> findAllByAccountId(final Long accountId) {
        return accountInProgressRepository.findAllByAccountId(accountId);
    }


    @Override
    public List<AccountInProgress> findAllByAccountIdIn(Collection<Long> ids) {
        return accountInProgressRepository.findAllByAccountIdIn(ids);
    }

}
