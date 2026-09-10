package com.liquido.statement.service;

import java.math.BigDecimal;
import java.util.List;

import com.liquido.statement.pojo.bo.BatchAccountSettlementBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountStatement;
import com.liquido.statement.pojo.entity.TransactionBiz;

public interface AccountStatementService {

    void batchGenerateAndSaveSettlementFlow(
            final Account account,
            final BatchAccountSettlementBo batchBo);

    List<AccountStatement> batchGenerateAndSaveSettlementFlow(
            final List<AccountStatement> accountStatementList);

    Long saveTransactionBizAccountFlow(
            final TransactionBiz transactionBiz,
            final BigDecimal settleAmount,
            final BigDecimal startBalance,
            final BigDecimal endBalance);

    AccountStatement buildAccountStatement(
            final TransactionBiz transactionBiz,
            final BigDecimal settleAmount,
            final BigDecimal startBalance,
            final BigDecimal endBalance);

    Long saveAccountFlow(
            final AccountStatement accountStatement);
}
