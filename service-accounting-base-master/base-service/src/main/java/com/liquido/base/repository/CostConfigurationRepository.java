package com.liquido.base.repository;

import java.util.List;

import com.liquido.base.pojo.entity.CostConfiguration;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CostConfigurationRepository extends
        EntityGraphJpaRepository<CostConfiguration, Long>,
        EntityGraphJpaSpecificationExecutor<CostConfiguration>,
        EntityGraphQuerydslPredicateExecutor<CostConfiguration> {

    List<CostConfiguration> findByActiveVersion(
            final Integer activeVersion);

    @Query(value = "SELECT * FROM cost_configuration "
            + "WHERE account_id =:accountId "
            + "AND active_version =:activeVersion "
            + "AND vendor_code =:vendorCode "
            + "AND product_code =:productCode "
            + "AND fee_type =:feeType ", nativeQuery = true)
    List<CostConfiguration> findCostConfig(
            @Param("accountId") final Long accountId,
            @Param("activeVersion") final Integer activeVersion,
            @Param("vendorCode") final String vendorCode,
            @Param("productCode") final String productCode,
            @Param("feeType") final String feeType);

}
