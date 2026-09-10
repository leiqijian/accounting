package com.liquido.statement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.statement.pojo.bo.AccountCardScheduleBo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SumAccountScheduleDataRepository {

    private final ObjectMapper objectMapper;

    private final EntityManager entityManager;

    public List<AccountCardScheduleBo> sumAccountScheduleData(
            final Long accountId, final LocalDate beCreditedDate,
            final ProductCodeEnum productCode) {

        final StringBuilder sql = new StringBuilder();
//        sql.append(
//                        "SELECT IFNULL(SUM( money.settlement_amount * money.amount_pon ), 0) AS settlementAmount ")
//                .append(", IFNULL(SUM( money.be_credited_amount ), 0) AS accountingAmount ")
//                .append(", IFNULL(SUM( fee.settlement_amount * fee.amount_pon ), 0) AS feeAmount ")
//                .append(", money.be_credited_date AS accountingDate ")
//                .append(", money.product_code AS productCode ")
//                .append(", money.hold_status AS holdStatus ")
//                .append("FROM ( ")
//                .append("SELECT transaction_id, direction_type, settlement_amount, amount_pon, be_credited_amount, ")
//                .append("be_credited_date, product_code, hold_status ")
//                .append("FROM service_accounting_statement.transaction_money ")
//                .append("WHERE account_id = :accountId ")
//                .append("AND be_credited_date = :beCreditedDate ")
////                .append("AND installment_flag = 0")
//        ;
//        if (Objects.nonNull(productCode)) {
//            sql.append("AND product_code = :productCode ");
//        }
//        sql.append(") AS money ")
//                .append("LEFT JOIN transaction_fee fee ")
//                .append("ON (money.transaction_id = fee.transaction_id AND money.direction_type = fee.direction_type) ")
//                .append("GROUP BY money.be_credited_date, money.product_code, money.hold_status ");


        sql.append(
                        "SELECT IFNULL(SUM( money.settlement_amount * money.amount_pon ), 0) AS settlementAmount ")
                .append(", IFNULL(SUM( money.be_credited_amount ), 0) AS accountingAmount ")
                .append(", (IFNULL(SUM( money.be_credited_amount * money.amount_pon ), 0)-IFNULL(SUM( money.settlement_amount), 0)) * money.amount_pon AS feeAmount ")
                .append(", money.be_credited_date AS accountingDate ")
                .append(", money.product_code AS productCode ")
                .append(", money.hold_status AS holdStatus ")
                .append(" FROM service_accounting_statement.transaction_money AS money")
                .append(" WHERE account_id = :accountId ")
                .append(" AND be_credited_date = :beCreditedDate ")
                .append("AND installment_flag = 0 ")
        ;
        if (Objects.nonNull(productCode)) {
            sql.append("AND product_code = :productCode ");
        }
        sql.append("GROUP BY money.be_credited_date, money.product_code, money.hold_status ");

        log.info("query account schedule sql={}", sql);
        final Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("accountId", accountId);
        query.setParameter("beCreditedDate", beCreditedDate);
        if (Objects.nonNull(productCode)) {
            query.setParameter("productCode", productCode.getCode());
        }
        org.hibernate.query.NativeQuery<?> nativeQuery =
                query.unwrap(org.hibernate.query.NativeQuery.class);
        nativeQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        final List<AccountCardScheduleBo> resultList =
                objectMapper.convertValue(nativeQuery.getResultList(), new TypeReference<>() {
                });

        entityManager.clear();

        return resultList;
    }

}
