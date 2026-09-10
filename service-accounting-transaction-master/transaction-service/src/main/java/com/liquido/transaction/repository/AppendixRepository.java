package com.liquido.transaction.repository;

import com.liquido.transaction.pojo.entity.Appendix;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AppendixRepository extends
        EntityGraphJpaRepository<Appendix, Long>,
        EntityGraphJpaSpecificationExecutor<Appendix>,
        EntityGraphQuerydslPredicateExecutor<Appendix> {

    @Modifying
    @Query("update Appendix set merchantId = :merchantId where id = :id")
    void updateMerchantIdById(final Long merchantId, final Long id);

}
