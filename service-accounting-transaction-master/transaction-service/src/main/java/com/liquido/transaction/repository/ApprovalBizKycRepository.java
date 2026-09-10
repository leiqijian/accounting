package com.liquido.transaction.repository;

import com.liquido.transaction.pojo.entity.ApprovalBizKyc;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface ApprovalBizKycRepository extends
        EntityGraphJpaRepository<ApprovalBizKyc, Long>,
        EntityGraphJpaSpecificationExecutor<ApprovalBizKyc>,
        EntityGraphQuerydslPredicateExecutor<ApprovalBizKyc> {

    boolean existsByLinkId(final Long linkId);

    ApprovalBizKyc findByApprovalId(final Long approvalId);

}
