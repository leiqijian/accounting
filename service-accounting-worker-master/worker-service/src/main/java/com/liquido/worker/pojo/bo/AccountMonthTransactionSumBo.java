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
public class AccountMonthTransactionSumBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long accountId;

    /**
     * Total count of monthly transactions.
     */
    private BigDecimal transactionCountSum;

    /**
     * Total amount of monthly transactions (currency type consistent with the account).
     */
    private BigDecimal transactionAmountSum;

}
