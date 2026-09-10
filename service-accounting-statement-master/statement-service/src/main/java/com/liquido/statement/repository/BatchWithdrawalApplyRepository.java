package com.liquido.statement.repository;

import com.liquido.statement.pojo.entity.BatchWithdrawalApply;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchWithdrawalApplyRepository extends
        EntityGraphJpaRepository<BatchWithdrawalApply, Long>,
        EntityGraphJpaSpecificationExecutor<BatchWithdrawalApply>,
        EntityGraphQuerydslPredicateExecutor<BatchWithdrawalApply> {

}
