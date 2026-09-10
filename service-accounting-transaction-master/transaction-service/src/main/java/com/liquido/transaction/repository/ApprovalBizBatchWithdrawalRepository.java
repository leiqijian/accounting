package com.liquido.transaction.repository;

import com.liquido.transaction.pojo.entity.ApprovalBizBatchWithdrawal;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface ApprovalBizBatchWithdrawalRepository extends
        EntityGraphJpaRepository<ApprovalBizBatchWithdrawal, Long>,
        EntityGraphJpaSpecificationExecutor<ApprovalBizBatchWithdrawal>,
        EntityGraphQuerydslPredicateExecutor<ApprovalBizBatchWithdrawal> {
}
