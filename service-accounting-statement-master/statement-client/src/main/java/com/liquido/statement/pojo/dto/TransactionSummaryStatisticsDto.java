package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSummaryStatisticsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer transactionCount;

    private BigDecimal settlementVolumeAmountUsd;
}
