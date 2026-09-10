package com.liquido.statement.repository;

import com.liquido.statement.pojo.entity.AccountTransferConfig;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTransferConfigRepository
        extends EntityGraphJpaRepository<AccountTransferConfig, Long>,
        EntityGraphJpaSpecificationExecutor<AccountTransferConfig>,
        EntityGraphQuerydslPredicateExecutor<AccountTransferConfig> {


    AccountTransferConfig findByMerchantIdAndPayinAccountId(final Long merchantId,
                                                            final Long payinAccountId);


    AccountTransferConfig findByMerchantIdAndPayoutAccountId(final Long merchantId,
                                                             final Long payoutAccountId);
}
