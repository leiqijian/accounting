package com.liquido.statement.repository;

import java.time.LocalDate;

import com.liquido.statement.pojo.entity.AccountDailyInit;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDailyInitRepository
        extends EntityGraphJpaRepository<AccountDailyInit, Long>,
        EntityGraphJpaSpecificationExecutor<AccountDailyInit>,
        EntityGraphQuerydslPredicateExecutor<AccountDailyInit> {

    AccountDailyInit findByAccountIdAndTransactionDate(final Long accountId,
                                                       final LocalDate transactionDate);

}
