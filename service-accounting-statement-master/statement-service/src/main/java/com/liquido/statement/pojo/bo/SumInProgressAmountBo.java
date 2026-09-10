package com.liquido.statement.pojo.bo;


import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SumInProgressAmountBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal amount;

    // currency=account currency
    private BigDecimal fee;

    // currency=account currency
    private BigDecimal tax;

    // currency=account currency
    private BigDecimal netAmount;

}
