package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositConfigBo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Convert(converter = CurrencyEnum.Convert.class)
    @NotNull
    private CurrencyEnum currency;

    private BigDecimal amount;

    private BigDecimal legalHoldAmount;

}
