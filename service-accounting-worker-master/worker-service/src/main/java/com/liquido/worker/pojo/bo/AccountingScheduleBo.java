package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.liquido.base.enums.HoldStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountingScheduleBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate accountingDate;

    private BigDecimal settlementAmount;

    private BigDecimal accountingAmount;

    private BigDecimal feeAmount;

    private BigDecimal taxAmount;

    private String cardType;

    private String cardBrand;

    private Integer currentInstallment;

    private Integer totalInstallment;

    private HoldStatusEnum holdStatus;
}
