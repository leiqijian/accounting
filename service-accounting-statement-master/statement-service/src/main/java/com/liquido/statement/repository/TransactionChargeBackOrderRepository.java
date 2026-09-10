package com.liquido.statement.repository;

import com.liquido.statement.pojo.entity.TransactionChargeBackOrder;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionChargeBackOrderRepository extends
        EntityGraphJpaRepository<TransactionChargeBackOrder, Long>,
        EntityGraphJpaSpecificationExecutor<TransactionChargeBackOrder>,
        EntityGraphQuerydslPredicateExecutor<TransactionChargeBackOrder> {

    TransactionChargeBackOrder findByUniqueId(final String uniqueId);

}
