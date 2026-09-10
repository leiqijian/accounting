package com.liquido.statement.repository;


import com.liquido.statement.pojo.entity.DailyExchangeRate;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyExchangeRateRepository extends
        EntityGraphJpaRepository<DailyExchangeRate, Long>,
        EntityGraphJpaSpecificationExecutor<DailyExchangeRate>,
        EntityGraphQuerydslPredicateExecutor<DailyExchangeRate> {
}
