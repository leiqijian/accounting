package com.liquido.statement.repository;

import com.liquido.statement.pojo.entity.SubAccountDailyBill;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubAccountDailyBillRepository extends
        EntityGraphJpaRepository<SubAccountDailyBill, Long>,
        EntityGraphJpaSpecificationExecutor<SubAccountDailyBill>,
        EntityGraphQuerydslPredicateExecutor<SubAccountDailyBill> {

}
