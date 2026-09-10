package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Country: Mexico
 * SpeiPayment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryPayoutResultVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Required=true
     * Global Unique orderId
     */
    @NotNull
    @Min(1)
    private Long uniqueId;
}
