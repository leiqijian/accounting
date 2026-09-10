package com.liquido.base.repository;

import com.liquido.base.pojo.entity.CostConfigurationVersion;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface CostConfigurationVersionRepository extends
        EntityGraphJpaRepository<CostConfigurationVersion, Long>,
        EntityGraphJpaSpecificationExecutor<CostConfigurationVersion>,
        EntityGraphQuerydslPredicateExecutor<CostConfigurationVersion> {

}
