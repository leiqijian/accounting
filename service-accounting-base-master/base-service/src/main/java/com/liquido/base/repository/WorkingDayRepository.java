package com.liquido.base.repository;


import com.liquido.base.pojo.entity.WorkingDay;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface WorkingDayRepository extends
        EntityGraphJpaRepository<WorkingDay, Long>,
        EntityGraphJpaSpecificationExecutor<WorkingDay>,
        EntityGraphQuerydslPredicateExecutor<WorkingDay> {

}
