package com.liquido.worker.repository;

import com.liquido.worker.pojo.entity.TransactionRatio;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface TransactionRatioRepository extends
        EntityGraphJpaRepository<TransactionRatio, Long>,
        EntityGraphJpaSpecificationExecutor<TransactionRatio>,
        EntityGraphQuerydslPredicateExecutor<TransactionRatio> {
}
