package com.liquido.worker.pojo.dto;

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
public class DwSyncShoplazzaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * link id
     */
    private String linkId;

    /**
     * order id
     */
    private String orderId;

    /**
     * link
     */
    private String link;

    /**
     * merchant name: examples cheng_fan
     */
    private String merchantName;

    /**
     * shop domain
     */
    private String shopDomain;

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

    private Boolean refunded;

    private Long refundTimestamp;

    private String email;

    private String phone;

    private String settledVirgoId;

    private String refundedVirgoId;

    /**
     * submit timestamp
     */
    private Long createTime;

    /**
     * event happen timestamp
     */
    private Long eventTime;

}
