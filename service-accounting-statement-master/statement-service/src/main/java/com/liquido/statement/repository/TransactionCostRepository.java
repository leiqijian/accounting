package com.liquido.statement.repository;

import java.math.BigDecimal;

import com.liquido.statement.pojo.entity.TransactionCost;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCostRepository extends
        EntityGraphJpaRepository<TransactionCost, Long>,
        EntityGraphJpaSpecificationExecutor<TransactionCost>,
        EntityGraphQuerydslPredicateExecutor<TransactionCost> {

    @Modifying
    @Query(value = "UPDATE transaction_cost tc "
            + " LEFT JOIN transaction_money tm ON "
            + " (tc.account_id = tm.account_id AND tc.transaction_id = tm.transaction_id "
            + " AND tc.direction_type = tm.direction_type) "
            + " SET tc.fx_rate = :fxRate , tc.fx_lose = :fxLose "
            + " WHERE tc.account_id = :accountId "
            + " AND tm.bill_id = :billId ",
            nativeQuery = true)
    void updateTransactionFxRate(@Param("accountId") final Long accountId,
                                 @Param("billId") final Long billId,
                                 @Param("fxRate") final BigDecimal fxRate,
                                 @Param("fxLose") final BigDecimal fxLose);

    @Modifying
    @Query(value = "UPDATE transaction_cost tc "
            + " LEFT JOIN transaction_money tm ON "
            + " (tc.account_id = tm.account_id AND tc.transaction_id = tm.transaction_id "
            + " AND tc.direction_type = tm.direction_type) "
            + " SET tc.bill_id = tm.bill_id "
            + " WHERE tm.account_id = :accountId "
            + " AND tm.bill_id = :billId ",
            nativeQuery = true)
    void batchUpdateCostBillId(@Param("accountId") final Long accountId,
                               @Param("billId") final Long billId);
}
