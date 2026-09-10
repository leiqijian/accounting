package com.liquido.statement.repository;

import com.liquido.statement.pojo.entity.TransactionBiz;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionBizRepository extends
        EntityGraphJpaRepository<TransactionBiz, Long>,
        EntityGraphJpaSpecificationExecutor<TransactionBiz>,
        EntityGraphQuerydslPredicateExecutor<TransactionBiz> {

    TransactionBiz findByTransactionId(final Long transactionId);
}
