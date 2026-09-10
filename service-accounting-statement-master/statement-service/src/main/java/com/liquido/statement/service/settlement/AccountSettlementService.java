package com.liquido.statement.service.settlement;

import com.liquido.statement.pojo.bo.BatchAccountSettlementBo;
import com.liquido.statement.pojo.vo.BatchTransactionMoneyVo;

public interface AccountSettlementService {

    /**
     * prepare process settlement
     *
     * @param batchOrder
     */
    void beforeExecutionSettlement(final BatchTransactionMoneyVo batchOrder);

    /**
     * batch save with Transaction Order
     *
     * @param batchOrder TransactionMoneyVo
     */
    BatchAccountSettlementBo batchSaveTransactionOrder(final BatchTransactionMoneyVo batchOrder);

    /**
     * batch batchExecutionSettlement
     *
     * @param batchOrder TransactionMoneyVo
     */
    void batchExecutionSettlement(final BatchAccountSettlementBo batchOrder);

    /**
     * post execution settlement
     * <p>
     * save realTime transaction summary
     *
     * @param batchOrder
     */
    void afterExecutionSettlement(final BatchAccountSettlementBo batchOrder);

}
