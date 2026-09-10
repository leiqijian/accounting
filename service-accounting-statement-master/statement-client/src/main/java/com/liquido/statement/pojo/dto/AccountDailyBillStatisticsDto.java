package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDailyBillStatisticsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate billDate;

    private BigDecimal transactionAmountUsd;

    private Long transactionCount;

}
