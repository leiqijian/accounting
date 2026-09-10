package com.liquido.transaction.repository;

import java.util.List;

import com.liquido.transaction.pojo.entity.ApprovalBizExchange;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface ApprovalBizExchangeRepository extends
        EntityGraphJpaRepository<ApprovalBizExchange, Long>,
        EntityGraphJpaSpecificationExecutor<ApprovalBizExchange>,
        EntityGraphQuerydslPredicateExecutor<ApprovalBizExchange> {
    List<ApprovalBizExchange> findByFailCountBetween(final Integer minFailCount,
                                                     final Integer maxFailCount);

}
