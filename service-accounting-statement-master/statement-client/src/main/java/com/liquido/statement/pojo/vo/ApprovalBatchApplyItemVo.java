package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class ApprovalBatchApplyItemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long transactionId;

    private String subMerchantId;

    /**
     * apply frozen amount
     * unit: cent
     */
    @NotNull
    @Min(1)
    private BigDecimal amount;
}
