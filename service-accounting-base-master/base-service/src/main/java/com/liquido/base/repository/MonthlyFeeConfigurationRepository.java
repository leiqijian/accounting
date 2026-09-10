package com.liquido.base.repository;

import java.util.List;

import com.liquido.base.pojo.entity.MonthlyFeeConfiguration;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;

public interface MonthlyFeeConfigurationRepository extends
        EntityGraphJpaRepository<MonthlyFeeConfiguration, Long>,
        EntityGraphJpaSpecificationExecutor<MonthlyFeeConfiguration>,
        EntityGraphQuerydslPredicateExecutor<MonthlyFeeConfiguration> {

    /**
     * //===================> 以下为临时代码 <===================
     */
    @Query(value = "SELECT * FROM monthly_fee_configuration", nativeQuery = true)
    List<MonthlyFeeConfiguration> findAllByDelFlag();

}
