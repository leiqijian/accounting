package com.liquido.worker.service.calculate;

import java.util.List;

import com.liquido.worker.pojo.bo.TaskFeeCalculationSettleBo;
import com.liquido.worker.pojo.dto.PreCalculateFeeDto;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.vo.SyncHandleTaskFeeCalculationVo;

public interface TaskTransactionService {


    /**
     * batch load waiting state task transaction
     */
    List<PreCalculateFeeDto> preCalculateFee(final SyncHandleTaskFeeCalculationVo vo);

    List<PreCalculateFeeDto> feeTrialCalculate(final List<TaskFeeCalculation> taskList);

    /**
     * batch load waiting state task transaction
     */
    void batchProcessTransactionTask(final TaskFeeCalculationSettleBo params);

}
