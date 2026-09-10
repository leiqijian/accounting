package com.liquido.statement.repository;

import java.time.LocalDate;

import com.liquido.statement.pojo.entity.TransactionSummary;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionSummaryRepository extends
        EntityGraphJpaRepository<TransactionSummary, Long>,
        EntityGraphJpaSpecificationExecutor<TransactionSummary>,
        EntityGraphQuerydslPredicateExecutor<TransactionSummary> {

    TransactionSummary findByAccountIdAndTransactionDate(
            final Long accountId, final LocalDate transactionDate);
}
