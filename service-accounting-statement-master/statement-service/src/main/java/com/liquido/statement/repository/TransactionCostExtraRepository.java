package com.liquido.statement.repository;

import com.liquido.statement.pojo.entity.TransactionCostExtra;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCostExtraRepository extends
        EntityGraphJpaRepository<TransactionCostExtra, Long>,
        EntityGraphJpaSpecificationExecutor<TransactionCostExtra>,
        EntityGraphQuerydslPredicateExecutor<TransactionCostExtra> {
}
