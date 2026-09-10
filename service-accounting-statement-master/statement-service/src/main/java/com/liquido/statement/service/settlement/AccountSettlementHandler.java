package com.liquido.statement.service.settlement;

import java.util.List;

import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.statement.pojo.bo.BatchAccountSettlementBo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;

public interface AccountSettlementHandler {

    /**
     * get business handler strategy
     *
     * @return
     */
    BusinessStrategyEnum getStrategy();

    /**
     * before execute settlement
     *
     * @param batchVo
     */
    default void beforeExecuteSettlement(final List<TransactionMoneyVo> batchVo) {
    }

    /**
     * prepare execute account settlement
     *
     * @param batchBo
     */
    default void prepareExecuteSettlement(final BatchAccountSettlementBo batchBo) {
    }

    /**
     * execute account settlement
     *
     * @param batchBo
     * @return
     */
    void executeAccountSettlement(final BatchAccountSettlementBo batchBo);


    /**
     * post execute settlement
     */
    default void postExecuteSettlement(final BatchAccountSettlementBo batchBo) {
    }

}

