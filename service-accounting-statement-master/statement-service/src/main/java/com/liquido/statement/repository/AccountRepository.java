package com.liquido.statement.repository;

import java.util.List;
import javax.persistence.Tuple;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.statement.pojo.entity.Account;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends
        EntityGraphJpaRepository<Account, Long>,
        EntityGraphJpaSpecificationExecutor<Account>,
        EntityGraphQuerydslPredicateExecutor<Account> {

    List<Account> findByMerchantId(final Long merchantId);

    List<Account> findAllByMerchantIdIn(final List<Long> merchantIds);

    List<Account> findByMerchantIdAndCountryCode(final Long merchantId,
                                                 final CountryCodeEnum countryCodeEnum);

    Account findByIdAndAndDelFlagIn(final Long accountId,
                                    final List<Boolean> delFlag);

    @Query(value = "SELECT merchant_id AS merchantId, "
            + "GROUP_CONCAT(DISTINCT (country_code)) AS countryCode,"
            + "GROUP_CONCAT(DISTINCT (transaction_type_code)) AS transactionTypes "
            + "FROM service_accounting_statement.account "
            + "GROUP BY merchant_id,country_code ",
            nativeQuery = true)
    List<Tuple> queryAccountGroup();
}
