package com.liquido.statement.pojo.dto;

import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;
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
public class PaymentLinkDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * uniqueId of the payment link in liquido.
     */
    private String linkId;

    /**
     * the payment link which hosted in liquido.
     */
    private String paymentLink;

    private Integer code;

    private String message;

    private JsonNode data;

}
