package com.liquido.worker.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwSyncPaymentLinkDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * link id
     */
    private String linkId;

    /**
     * merchant reference
     */
    private String merchantReference;

    /**
     * merchant name: examples cheng_fan
     */
    private String merchantName;

    /**
     * client id
     */
    private String clientId;

    /**
     * product code: SPEI/TED/PIX/CREDIT_CARD/BOLETO/OXXO
     */
    private String productCode;

    private BigDecimal amount;

    private String currency;

    private String country;

    /**
     * payment link status， enum value as: INITIAL_STATUS, IN_PROGRESS, SETTLED.
     */
    private String paymentStatus;

    /**
     * final timestamp
     */
    private Long finalStatusTimestamp;

    /**
     * refund status， enum value as: INITIAL_STATUS, IN_PROGRESS, SETTLED, FAILED.
     */
    private String refundStatus;

    private BigDecimal refundAmount;

    private Long refundTimestamp;

    private String email;

    private String phone;

    private String settledVirgoId;

    private String refundedVirgoId;

    /**
     * subMerchantId
     */
    private String subMerchantId;

    /**
     * submit timestamp
     */
    private Long createTimestamp;

    /**
     * event happen timestamp
     */
    private Long eventTime;

    private String description;

    private JsonNode metadata;

    private JsonNode appendix;

}
