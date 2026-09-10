package com.liquido.base.repository;

import java.util.List;

import com.liquido.base.pojo.entity.CardCostConfiguration;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface CardCostConfigurationRepository extends
        EntityGraphJpaRepository<CardCostConfiguration, Long>,
        EntityGraphJpaSpecificationExecutor<CardCostConfiguration>,
        EntityGraphQuerydslPredicateExecutor<CardCostConfiguration> {

    List<CardCostConfiguration> findByActiveVersion(final Integer activeVersion);

}
