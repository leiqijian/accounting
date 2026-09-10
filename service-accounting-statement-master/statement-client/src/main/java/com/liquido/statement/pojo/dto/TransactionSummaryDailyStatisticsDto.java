package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSummaryDailyStatisticsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate transactionDate;

    private Integer transactionCount;

    private BigDecimal settlementVolumeAmountUsd;
}
