package com.liquido.worker.service.calculate;

import java.util.List;

import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.worker.pojo.bo.PreCalculateConfigBo;
import com.liquido.worker.pojo.bo.TransactionCostBo;
import com.liquido.worker.pojo.bo.TransactionMoneyBo;

public interface TransactionCostService {

    /**
     * Calculate Transaction Cost
     *
     * @param moneyBo
     * @param preConfig
     * @param usdRateInfo
     * @return
     */
    List<TransactionCostBo> calculateTransactionCost(
            final TransactionMoneyBo moneyBo,
            final PreCalculateConfigBo preConfig,
            final DailyExchangeRateDto usdRateInfo);

}
