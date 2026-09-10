package com.liquido.statement.repository;

import com.liquido.statement.pojo.entity.AccountStatementBiz;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountStatementBizRepository extends
        EntityGraphJpaRepository<AccountStatementBiz, Long>,
        EntityGraphJpaSpecificationExecutor<AccountStatementBiz>,
        EntityGraphQuerydslPredicateExecutor<AccountStatementBiz> {

}
