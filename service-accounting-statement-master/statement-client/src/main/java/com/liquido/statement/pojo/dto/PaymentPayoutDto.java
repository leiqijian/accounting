package com.liquido.statement.pojo.dto;

import java.io.Serializable;

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
public class PaymentPayoutDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long uniqueId;

    private Long paymentConfigId;

    /**
     * 200 Success,
     * 401 Access denied,
     * 422 Required fields missing or invalid data,
     * 500 internal error
     */
    private Integer statusCode;

    /**
     * error message if failed
     */
    private String errorMsg;

    /**
     * The transaction id
     */
    private String transactionId;

    /**
     * Transfer  Transfer status, enum value, SETTLED, IN_PROGRESS, FAILED or REJECTED
     */
    private String transferStatus;

    /**
     * Transfer status code, 200 transaction SETTLED or IN_PROGRESS, other FAILED or REJECTED
     */
    private Integer transferStatusCode;

    /**
     * Transfer error message if failed
     */
    private String transferErrorMsg;

}
