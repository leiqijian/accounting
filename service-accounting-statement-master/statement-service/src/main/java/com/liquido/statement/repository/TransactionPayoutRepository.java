package com.liquido.statement.repository;

import com.liquido.statement.pojo.entity.TransactionPayout;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionPayoutRepository extends
        EntityGraphJpaRepository<TransactionPayout, Long>,
        EntityGraphJpaSpecificationExecutor<TransactionPayout>,
        EntityGraphQuerydslPredicateExecutor<TransactionPayout> {

    TransactionPayout findByUniqueId(final Long uniqueId);
}
