package com.liquido.base.repository;

import java.util.List;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.entity.AccountProduct;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;

public interface AccountProductRepository extends
        EntityGraphJpaRepository<AccountProduct, Long>,
        EntityGraphJpaSpecificationExecutor<AccountProduct>,
        EntityGraphQuerydslPredicateExecutor<AccountProduct> {

    List<AccountProduct> findByAccountId(final Long accountId);

    List<AccountProduct> findAllByTransactionTypeCode(final TransactionTypeCodeEnum code);

    /**
     * //===================> 以下为临时代码 <===================
     */
    @Query(value = "SELECT * FROM account_product", nativeQuery = true)
    List<AccountProduct> findAllProducts();

}
