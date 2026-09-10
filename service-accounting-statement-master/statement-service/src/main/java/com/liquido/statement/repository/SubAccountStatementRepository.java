package com.liquido.statement.repository;

import com.liquido.statement.pojo.entity.SubAccountStatement;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubAccountStatementRepository extends
        EntityGraphJpaRepository<SubAccountStatement, Long>,
        EntityGraphJpaSpecificationExecutor<SubAccountStatement>,
        EntityGraphQuerydslPredicateExecutor<SubAccountStatement> {
}

