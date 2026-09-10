package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class ProcessPayoutVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Required=true
     * Global Unique orderId
     */
    @NotNull
    @Min(1)
    private Long uniqueId;

    @NotBlank
    private String transactionStatus;

    @NotNull
    private LocalDateTime settleTime;

    private ObjectNode paymentResponse;
}
