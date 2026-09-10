package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryMxSpeiProofDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private TaskFeeCalculationDto taskFeeCalculation;

    private String referenceNumber;

    private String vendorName;

    private String targetAccountId;

    private String amount;

    private LocalDateTime finalStatusTime;

    private String bankId;

}
