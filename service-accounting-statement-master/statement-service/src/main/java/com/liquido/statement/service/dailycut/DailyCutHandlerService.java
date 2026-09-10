package com.liquido.statement.service.dailycut;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.statement.pojo.bo.DailyCutSuccessBo;

public interface DailyCutHandlerService {

    /**
     * handler strategy
     *
     * @return
     */
    TransactionTypeCodeEnum getStrategy();

    /**
     * Async execute account daily cut
     *
     * @param accountId
     * @param billDate
     *
     * @return DailyCutSuccessBo dailyCutSuccessBo
     */
    CompletableFuture<DailyCutSuccessBo> asyncHandleDailyCut(
            final Long accountId,
            final LocalDate billDate);

    /**
     * Sync execute account daily cut
     *
     * @param accountId
     * @param billDate
     *
     * @return DailyCutSuccessBo dailyCutSuccessBo
     */
    DailyCutSuccessBo syncHandleDailyCut(
            final Long accountId,
            final LocalDate billDate);

}

