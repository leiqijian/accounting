package com.liquido.statement.repository;

import java.util.Collection;
import java.util.List;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.statement.pojo.entity.SubAccount;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubAccountRepository extends
        EntityGraphJpaRepository<SubAccount, Long>,
        EntityGraphJpaSpecificationExecutor<SubAccount>,
        EntityGraphQuerydslPredicateExecutor<SubAccount> {

    List<SubAccount> findByMerchantIdAndCountryCodeIn(
            final Long merchantId,
            final Collection<CountryCodeEnum> value);
}
