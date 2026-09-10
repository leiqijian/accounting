package com.liquido.base.repository;

import com.liquido.base.pojo.entity.ExtraIncomeConfiguration;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface ExtraIncomeConfigurationRepository extends
        EntityGraphJpaRepository<ExtraIncomeConfiguration, Long>,
        EntityGraphJpaSpecificationExecutor<ExtraIncomeConfiguration>,
        EntityGraphQuerydslPredicateExecutor<ExtraIncomeConfiguration> {
}
