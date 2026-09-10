package com.liquido.base.repository;

import com.liquido.base.pojo.entity.CountryProduct;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface CountryProductRepository extends
        EntityGraphJpaRepository<CountryProduct, Long>,
        EntityGraphJpaSpecificationExecutor<CountryProduct>,
        EntityGraphQuerydslPredicateExecutor<CountryProduct> {

}
