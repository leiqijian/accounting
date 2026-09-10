package com.liquido.base.repository;

import java.util.List;

import com.liquido.base.pojo.entity.AccountFeeConfiguration;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;

public interface AccountFeeConfigurationRepository extends
        EntityGraphJpaRepository<AccountFeeConfiguration, Long>,
        EntityGraphJpaSpecificationExecutor<AccountFeeConfiguration>,
        EntityGraphQuerydslPredicateExecutor<AccountFeeConfiguration> {

    List<AccountFeeConfiguration> findByAccountProductId(final Long accountProductId);

    /**
     * //===================> 以下为临时代码 <===================
     */
    @Query(value = "SELECT * FROM account_fee_configuration", nativeQuery = true)
    List<AccountFeeConfiguration> findAllByDelFlag();

}
