package com.liquido.worker.pojo.bo;

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
public class ProductTransactionSumBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long accountProductId;

    private BigDecimal transactionCountSum;

    private BigDecimal transactionAmountSum;

}
