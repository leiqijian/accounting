package com.liquido.statement.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Tuple;

import com.liquido.statement.pojo.entity.TransactionMoney;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionMoneyRepository extends
        EntityGraphJpaRepository<TransactionMoney, Long>,
        EntityGraphJpaSpecificationExecutor<TransactionMoney>,
        EntityGraphQuerydslPredicateExecutor<TransactionMoney> {

    TransactionMoney findByTransactionId(final Long transactionId);

    List<TransactionMoney> findAllByUniqueId(final String uniqueId);

    List<TransactionMoney> findByUniqueIdIn(final List<String> uniqueId);

    @Query(value = "SELECT IFNULL(SUM( be_credited_amount ), 0) AS pendingAmount "
            + " FROM service_accounting_statement.transaction_money "
            + " FORCE INDEX(idx_account_be_credit_date) "
            + " WHERE account_id = :accountId "
            + " AND be_credited_date > :beCreditedDate "
            + " AND hold_status IN (0,2) "
            + " AND del_flag = 0 ",
            nativeQuery = true)
    BigDecimal pendingAmount(@Param("accountId") final Long accountId,
                             @Param("beCreditedDate") final LocalDate beCreditedDate);

    @Query(value = "SELECT IFNULL(SUM( be_credited_amount ), 0) AS amount, "
            + " be_credited_date as `date` "
            + " FROM service_accounting_statement.transaction_money "
            + " FORCE INDEX(idx_account_be_credit_date) "
            + " WHERE account_id = :accountId "
            + " AND be_credited_date > :beCreditedDate "
            + " AND hold_status IN (0,2) "
            + " AND del_flag = 0 "
            + " GROUP BY be_credited_date ",
            nativeQuery = true)
    List<Tuple> pendingAmountList(
            @Param("accountId") final Long accountId,
            @Param("beCreditedDate") final LocalDate beCreditedDate);


    @Query(value = "SELECT IFNULL(SUM( be_credited_amount ), 0) AS holdingAmount"
            + " FROM service_accounting_statement.transaction_money "
            + " FORCE INDEX(idx_account_hold) "
            + " WHERE account_id = :accountId "
            + " AND hold_status = 1 "
            + " AND del_flag = 0 ",
            nativeQuery = true)
    BigDecimal holdingAmount(@Param("accountId") final Long accountId);

    @Query(value = "SELECT IFNULL(SUM( be_credited_amount ), 0) AS amount,"
            + " be_credited_date as `date` "
            + " FROM service_accounting_statement.transaction_money "
            + " FORCE INDEX(idx_account_hold) "
            + " WHERE account_id = :accountId "
            + " AND hold_status = 1 "
            + " AND del_flag = 0 "
            + " GROUP BY be_credited_date ",
            nativeQuery = true)
    List<Tuple> holdingAmountList(@Param("accountId") final Long accountId);


    @Query(value = "SELECT IFNULL(SUM(tmp.amount), 0) AS total_additional_charge "
            + " FROM service_accounting_statement.transaction_money me "
            + " CROSS JOIN JSON_TABLE(me.additional_charge, "
            + " '$[*]' COLUMNS (amount DECIMAL(26,0) PATH '$.amount')) AS tmp "
            + " WHERE me.account_id = :accountId "
            + " AND me.bill_id = :billId "
            + " AND me.settle_status='SUCCESS'",
            nativeQuery = true)
    BigDecimal sumDailyAdditionalCharge(
            @Param("accountId") final Long accountId,
            @Param("billId") final Long billId);


}
