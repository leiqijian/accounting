package com.liquido.worker.pojo.bo;

import java.io.Serializable;

import com.liquido.worker.enums.CalculationTaskStateEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTeeCalculationBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private CalculationTaskStateEnum taskStatus;

    private Integer batchCount;

}
