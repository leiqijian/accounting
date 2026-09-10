package com.liquido.worker.aws.sqs.msg;

import com.liquido.worker.pojo.bo.TaskFeeCalculationSettleBo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFeeCalculationMsg {

    /**
     * TaskFeeCalculation data
     */
    private TaskFeeCalculationSettleBo bo;

}
