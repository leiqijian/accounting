package com.liquido.worker.repository;

import com.liquido.worker.pojo.entity.TaskHoldMonitor;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskHoldMonitorRepository extends EntityGraphJpaRepository<TaskHoldMonitor, Long>,
        EntityGraphJpaSpecificationExecutor<TaskHoldMonitor>,
        EntityGraphQuerydslPredicateExecutor<TaskHoldMonitor> {


    TaskHoldMonitor findByMonthlyAndAccountIdAndDocumentId(final Integer monthly,
                                                           final Long accountId,
                                                           final String documentId);
}
