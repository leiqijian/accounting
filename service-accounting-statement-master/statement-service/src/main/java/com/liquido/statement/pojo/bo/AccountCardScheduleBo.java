package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Convert;

import com.liquido.base.enums.AccountingScheduleStateEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCardScheduleBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal settlementAmount;

    private BigDecimal accountingAmount;

    private BigDecimal feeAmount;

    private LocalDate accountingDate;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Convert(converter = HoldStatusEnum.Convert.class)
    private HoldStatusEnum holdStatus;

    @Convert(converter = AccountingScheduleStateEnum.Convert.class)
    private AccountingScheduleStateEnum accountingScheduleState;
}
