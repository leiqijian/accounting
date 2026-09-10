package com.liquido.statement.repository;

import java.util.List;

import com.liquido.statement.pojo.entity.TransactionFee;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionFeeRepository extends
        EntityGraphJpaRepository<TransactionFee, Long>,
        EntityGraphJpaSpecificationExecutor<TransactionFee>,
        EntityGraphQuerydslPredicateExecutor<TransactionFee> {


    List<TransactionFee> findAllByTransactionId(final Long transactionId);
}
