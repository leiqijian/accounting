package com.liquido.worker.service.calculate;

import java.util.List;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.pojo.bo.PreCalculateFeeBo;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;

public interface TransactionStrategy {

    TransactionTypeCodeEnum getStrategy();

    void batchProcessCalculate(final String requestId, final List<TaskFeeCalculation> taskOrders);

    List<PreCalculateFeeBo> batchFeeTrialCalculate(final List<TaskFeeCalculation> taskOrders);
}

