package com.liquido.base.repository;

import com.liquido.base.pojo.entity.AccountProductVersion;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;

public interface AccountProductVersionRepository extends
        EntityGraphJpaRepository<AccountProductVersion, Long>,
        EntityGraphJpaSpecificationExecutor<AccountProductVersion>,
        EntityGraphQuerydslPredicateExecutor<AccountProductVersion> {


}
