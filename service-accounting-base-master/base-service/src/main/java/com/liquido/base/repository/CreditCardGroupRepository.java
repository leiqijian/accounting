package com.liquido.base.repository;

import java.util.List;

import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.pojo.entity.CreditCardGroup;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface CreditCardGroupRepository extends
        EntityGraphJpaRepository<CreditCardGroup, Long>,
        EntityGraphJpaSpecificationExecutor<CreditCardGroup>,
        EntityGraphQuerydslPredicateExecutor<CreditCardGroup> {

    List<CreditCardGroup> findAllByGroupCode(final CreditCardGroupCodeEnum groupCode);

}
