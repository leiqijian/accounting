package com.liquido.base.repository;


import com.liquido.base.pojo.entity.MerchantMessage;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface MerchantMessageRepository extends
        EntityGraphJpaRepository<MerchantMessage, Long>,
        EntityGraphJpaSpecificationExecutor<MerchantMessage>,
        EntityGraphQuerydslPredicateExecutor<MerchantMessage> {
}
