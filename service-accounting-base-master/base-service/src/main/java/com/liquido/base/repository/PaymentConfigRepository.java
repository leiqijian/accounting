package com.liquido.base.repository;


import java.util.Collection;

import com.liquido.base.pojo.entity.PaymentConfig;
import com.liquido.base.pojo.entity.QPaymentConfig;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentConfigRepository extends
        EntityGraphJpaRepository<PaymentConfig, Long>,
        EntityGraphJpaSpecificationExecutor<PaymentConfig>,
        EntityGraphQuerydslPredicateExecutor<PaymentConfig> {


    boolean existsByAccountId(final Long accountId);

    default boolean existsByAccountIds(final Collection<Long> accountIds) {
        BooleanExpression condition = QPaymentConfig.paymentConfig.accountId.in(accountIds);
        return exists(condition);
    }

}
