package com.liquido.base.repository;


import com.liquido.base.pojo.entity.BizFeeConfiguration;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BizFeeConfigurationRepository extends
        EntityGraphJpaRepository<BizFeeConfiguration, Long>,
        EntityGraphJpaSpecificationExecutor<BizFeeConfiguration>,
        EntityGraphQuerydslPredicateExecutor<BizFeeConfiguration> {

}
