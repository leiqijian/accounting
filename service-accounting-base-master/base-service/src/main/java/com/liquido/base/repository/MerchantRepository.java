package com.liquido.base.repository;


import com.liquido.base.pojo.entity.Merchant;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface MerchantRepository extends
        EntityGraphJpaRepository<Merchant, Long>,
        EntityGraphJpaSpecificationExecutor<Merchant>,
        EntityGraphQuerydslPredicateExecutor<Merchant> {
    boolean existsByCode(String code);

}
