package com.liquido.statement.repository;

import java.util.ArrayList;
import java.util.List;

import com.liquido.statement.pojo.entity.SubAccountStatement;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BatchOperatorRepository {

    private final JdbcTemplate jdbcTemplate;

    // jpa batch insert need pre query db exist data, so use original sql save subAccount statement data
    public void batchInsertSubAccountStatement(
            final List<SubAccountStatement> subAccountStatementList) {

        if (CollectionUtils.isEmpty(subAccountStatementList)) {
            return;
        }

        Lists.partition(subAccountStatementList, 200).forEach(subAccountStatements -> {
            final StringBuilder sql = new StringBuilder(
                    "INSERT INTO sub_account_statement (id, merchant_id,sub_merchant_id,"
                            + "account_id,sub_account_id,transaction_id,country_code,"
                            + "transaction_type_code,product_code,business_type,"
                            + "direction_type,amount_pon,amount,currency,start_balance,"
                            + "end_balance,transaction_time,transaction_timestamp,"
                            + "settle_time,created_time,updated_time) VALUES ");
            // subAccount statement exist 25 flied
            final ArrayList<Object> params = new ArrayList<>(subAccountStatements.size() * 25);

            for (int i = 0; i < subAccountStatements.size(); i++) {
                sql.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                if (i < subAccountStatements.size() - 1) {
                    sql.append(", ");
                }

                final SubAccountStatement subAccountStatement = subAccountStatements.get(i);
                params.add(subAccountStatement.getId());
                params.add(subAccountStatement.getMerchantId());
                params.add(subAccountStatement.getSubMerchantId());
                params.add(subAccountStatement.getAccountId());
                params.add(subAccountStatement.getSubAccountId());
                params.add(subAccountStatement.getTransactionId());
                params.add(subAccountStatement.getCountryCode().getCode());
                params.add(subAccountStatement.getTransactionTypeCode().getCode());
                params.add(subAccountStatement.getProductCode().getCode());
                params.add(subAccountStatement.getBusinessType().getCode());
                params.add(subAccountStatement.getDirectionType().getCode());
                params.add(subAccountStatement.getAmountPon().getCode());
                params.add(subAccountStatement.getAmount());
                params.add(subAccountStatement.getCurrency().getCode());
                params.add(subAccountStatement.getStartBalance());
                params.add(subAccountStatement.getEndBalance());
                params.add(subAccountStatement.getTransactionTime());
                params.add(subAccountStatement.getTransactionTimestamp());
                params.add(subAccountStatement.getSettleTime());
                params.add(subAccountStatement.getCreatedTime());
                params.add(subAccountStatement.getUpdatedTime());
            }

            jdbcTemplate.update(sql.toString(), params.toArray());
        });
    }
}
