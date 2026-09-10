package com.liquido.statement.repository;

import java.time.LocalDate;
import java.util.List;

import com.liquido.statement.pojo.entity.AccountDailyBill;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDailyBillRepository extends
        EntityGraphJpaRepository<AccountDailyBill, Long>,
        EntityGraphJpaSpecificationExecutor<AccountDailyBill>,
        EntityGraphQuerydslPredicateExecutor<AccountDailyBill> {

    AccountDailyBill findAccountDailyBillByAccountIdAndBillDate(final Long accountId,
                                                                final LocalDate billDate);

    List<AccountDailyBill> findAccountDailyBillByAccountIdInAndBillDateBetween(final List<Long> accountId,
                                                                             final LocalDate startDate,
                                                                             final LocalDate endDate);

    List<AccountDailyBill> findAccountDailyBillByAccountIdAndBillMonth(final Long accountId,
                                                                       final Integer billMonth);
}
