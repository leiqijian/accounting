package com.liquido.base.repository;


import java.util.List;

import com.liquido.base.pojo.entity.SubMerchantInfo;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface SubMerchantInfoRepository extends
        EntityGraphJpaRepository<SubMerchantInfo, Long>,
        EntityGraphJpaSpecificationExecutor<SubMerchantInfo>,
        EntityGraphQuerydslPredicateExecutor<SubMerchantInfo> {

    List<SubMerchantInfo> queryAllByMerchantId(
            final Long merchantId);

    List<SubMerchantInfo> queryAllByMerchantIdAndSubMerchantId(
            final Long merchantId,
            final String subMerchantId);
}
