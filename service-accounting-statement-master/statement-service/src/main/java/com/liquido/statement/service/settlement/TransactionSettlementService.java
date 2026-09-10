package com.liquido.statement.service.settlement;

import com.liquido.statement.pojo.vo.BatchTransactionMoneyVo;

public interface TransactionSettlementService {

    /**
     * batch process transaction account settlement
     *
     * @param batchOrder
     * @return
     */
    void batchProcessTransactionSettlement(final BatchTransactionMoneyVo batchOrder);

}
