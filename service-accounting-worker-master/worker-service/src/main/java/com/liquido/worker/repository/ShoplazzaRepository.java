package com.liquido.worker.repository;

import com.liquido.worker.pojo.entity.Shoplazza;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoplazzaRepository extends EntityGraphJpaRepository<Shoplazza, Long>,
        EntityGraphJpaSpecificationExecutor<Shoplazza>,
        EntityGraphQuerydslPredicateExecutor<Shoplazza> {

}
