package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.HoldStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountingScheduleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private LocalDate accountingDate;

    @NotNull
    @Min(0)
    private BigDecimal accountingAmount;

    @NotNull
    @Min(0)
    private BigDecimal settlementAmount;

    @NotNull
    @Min(0)
    private BigDecimal feeAmount;

    @NotNull
    @Min(0)
    private BigDecimal taxAmount;

    @NotNull
    private Integer currentInstallment;

    @NotNull
    private Integer totalInstallment;

    @NotNull
    private HoldStatusEnum holdStatus;

    private String cardType;

    private String cardBrand;

}
