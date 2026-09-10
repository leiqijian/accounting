package com.liquido.worker.repository;

import java.util.List;

import com.liquido.worker.pojo.entity.TaskFeeCalculation;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskFeeCalculationRepository
        extends EntityGraphJpaRepository<TaskFeeCalculation, Long>,
        EntityGraphJpaSpecificationExecutor<TaskFeeCalculation>,
        EntityGraphQuerydslPredicateExecutor<TaskFeeCalculation> {

    TaskFeeCalculation findByUniqueId(final String uniqueId);

    List<TaskFeeCalculation> findByUniqueIdIn(final List<String> uniqueId);

}
