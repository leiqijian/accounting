package com.liquido.statement.repository;

import com.liquido.statement.pojo.entity.BatchWithdrawalDetail;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchWithdrawalDetailRepository extends
        EntityGraphJpaRepository<BatchWithdrawalDetail, Long>,
        EntityGraphJpaSpecificationExecutor<BatchWithdrawalDetail>,
        EntityGraphQuerydslPredicateExecutor<BatchWithdrawalDetail> {

}
