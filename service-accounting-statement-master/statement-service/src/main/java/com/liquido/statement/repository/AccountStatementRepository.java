package com.liquido.statement.repository;

import java.util.List;

import com.liquido.statement.pojo.entity.AccountStatement;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountStatementRepository extends
        EntityGraphJpaRepository<AccountStatement, Long>,
        EntityGraphJpaSpecificationExecutor<AccountStatement>,
        EntityGraphQuerydslPredicateExecutor<AccountStatement> {

    List<AccountStatement> findAllByTransactionIdIn(List<Long> transactionIds);

}
