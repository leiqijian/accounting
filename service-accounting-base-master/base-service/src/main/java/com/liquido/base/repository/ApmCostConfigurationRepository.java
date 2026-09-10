package com.liquido.base.repository;

import java.util.List;

import com.liquido.base.pojo.entity.ApmCostConfiguration;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface ApmCostConfigurationRepository extends
        EntityGraphJpaRepository<ApmCostConfiguration, Long>,
        EntityGraphJpaSpecificationExecutor<ApmCostConfiguration>,
        EntityGraphQuerydslPredicateExecutor<ApmCostConfiguration> {

    List<ApmCostConfiguration> findByActiveVersion(final Integer activeVersion);
}
