package com.liquido.worker.repository;

import com.liquido.worker.pojo.entity.DefenseOrder;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DefenseOrderRepository extends
        EntityGraphJpaRepository<DefenseOrder, Long>,
        EntityGraphJpaSpecificationExecutor<DefenseOrder>,
        EntityGraphQuerydslPredicateExecutor<DefenseOrder> {

    DefenseOrder findByUniqueId(final String uniqueId);

}
