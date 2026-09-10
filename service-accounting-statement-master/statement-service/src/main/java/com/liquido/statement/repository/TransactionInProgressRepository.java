package com.liquido.statement.repository;


import java.util.List;
import java.util.Set;

import com.liquido.statement.pojo.entity.TransactionInProgress;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionInProgressRepository extends
        EntityGraphJpaRepository<TransactionInProgress, Long>,
        EntityGraphJpaSpecificationExecutor<TransactionInProgress>,
        EntityGraphQuerydslPredicateExecutor<TransactionInProgress> {

    TransactionInProgress findByUniqueId(final String uniqueId);

    List<TransactionInProgress> findAllByIdIn(final Set<Long> ids);

}
