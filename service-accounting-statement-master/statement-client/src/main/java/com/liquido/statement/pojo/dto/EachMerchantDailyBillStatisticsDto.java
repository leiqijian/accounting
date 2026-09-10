package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.liquido.base.enums.CountryCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EachMerchantDailyBillStatisticsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate billDate;

    private Long merchantId;

    private CountryCodeEnum countryCode;

    private BigDecimal transactionAmountUsd;

    private Long transactionCount;
}
