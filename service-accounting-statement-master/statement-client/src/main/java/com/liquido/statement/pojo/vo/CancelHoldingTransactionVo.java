package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.util.Set;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelHoldingTransactionVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(1)
    private Long accountId;

    /**
     * reference: transaction_money.transaction_id
     */
    @NotNull
    private Set<Long> transactionIds;

}
