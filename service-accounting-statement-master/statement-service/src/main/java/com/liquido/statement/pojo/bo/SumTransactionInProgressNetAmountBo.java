package com.liquido.statement.pojo.bo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SumTransactionInProgressNetAmountBo {
    private String subMerchantId;

    private Long accountId;

    private BigDecimal netAmount;
}
