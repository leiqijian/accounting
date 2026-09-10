package com.liquido.statement.repository;

import java.util.Collection;
import java.util.List;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.statement.pojo.entity.AccountInProgress;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountInProgressRepository extends
        EntityGraphJpaRepository<AccountInProgress, Long>,
        EntityGraphJpaSpecificationExecutor<AccountInProgress>,
        EntityGraphQuerydslPredicateExecutor<AccountInProgress> {

    AccountInProgress findByAccountIdAndInProgressCurrency(final Long accountId,
                                                           final CurrencyEnum inProgressCurrency);

    List<AccountInProgress> findAllByAccountId(final Long accountId);

    List<AccountInProgress> findAllByAccountIdIn(final Collection<Long> accountIds);
}
