package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Convert;

import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryCalendarInfoBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate accountDate;

    private BigDecimal settlementAmount;

    private BigDecimal accountAmount;

    private BigDecimal feeAmount;

    private BigDecimal holdAmount;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

}
