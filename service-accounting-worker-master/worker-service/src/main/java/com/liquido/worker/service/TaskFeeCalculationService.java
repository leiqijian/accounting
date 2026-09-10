package com.liquido.worker.service;

import java.util.List;

import com.liquido.worker.enums.CalculationTaskStateEnum;
import com.liquido.worker.pojo.bo.TaskFeeCalculationSettleBo;
import com.liquido.worker.pojo.dto.TaskFeeCalculationDto;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.vo.FillFieldTaskFeeCalculationVo;
import com.liquido.worker.pojo.vo.UnHoldingDocumentVo;
import com.liquido.worker.pojo.vo.UnHoldingTransactionVo;

public interface TaskFeeCalculationService {

    TaskFeeCalculationDto findById(final Long id);

    TaskFeeCalculationDto findByUniqueId(final String id);

    List<TaskFeeCalculation> batchLoadWaitingTaskAndLock(final TaskFeeCalculationSettleBo task);

    void batchHoldingTask(final List<Long> taskIdList);

    long batchUnLockTask(final List<Long> taskIdList, final CalculationTaskStateEnum resultState);

    /**
     * Cancel holding amount by documentIds
     *
     * @param order
     */
    void cancelHoldingByDocumentIds(final UnHoldingDocumentVo order);

    /**
     * Cancel holding amount by transactionIds
     *
     * @param order
     */
    void cancelHoldingByTransactionIds(final UnHoldingTransactionVo order);

    void fillTaskFeeCalculationField(final List<FillFieldTaskFeeCalculationVo> list);
}
