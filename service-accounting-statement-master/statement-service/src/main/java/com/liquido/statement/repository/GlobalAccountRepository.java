package com.liquido.statement.repository;

import com.liquido.statement.pojo.entity.GlobalAccount;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalAccountRepository extends
        EntityGraphJpaRepository<GlobalAccount, Long>,
        EntityGraphJpaSpecificationExecutor<GlobalAccount>,
        EntityGraphQuerydslPredicateExecutor<GlobalAccount> {

    GlobalAccount findByMerchantId(final Long merchantId);

}
