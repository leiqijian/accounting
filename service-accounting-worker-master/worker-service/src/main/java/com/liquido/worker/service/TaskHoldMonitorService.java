package com.liquido.worker.service;

import java.math.BigDecimal;

import com.liquido.worker.pojo.bo.PreCalculateConfigBo;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;

public interface TaskHoldMonitorService {

    /**
     * check condition
     * 1. just Brazil need check;
     * 2. just payIn need check;
     * 3. account holding limit must be greater than 0;
     * 4. transaction Cert required;
     *
     * @param taskOrder
     * @param preConfig
     * @return
     */
    boolean checkOverLimit(final TaskFeeCalculation taskOrder,
                           final PreCalculateConfigBo preConfig,
                           final BigDecimal transactionAmount);

}
