package com.liquido.worker.repository;

import com.liquido.worker.pojo.entity.CardTokenization;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenizationRepository extends EntityGraphJpaRepository<CardTokenization, Long>,
        EntityGraphJpaSpecificationExecutor<CardTokenization>,
        EntityGraphQuerydslPredicateExecutor<CardTokenization> {

}
