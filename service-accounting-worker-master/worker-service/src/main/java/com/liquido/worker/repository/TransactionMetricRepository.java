package com.liquido.worker.repository;


import com.liquido.worker.pojo.entity.TransactionMetric;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface TransactionMetricRepository
        extends EntityGraphJpaRepository<TransactionMetric, Long>,
        EntityGraphJpaSpecificationExecutor<TransactionMetric>,
        EntityGraphQuerydslPredicateExecutor<TransactionMetric> {
}
