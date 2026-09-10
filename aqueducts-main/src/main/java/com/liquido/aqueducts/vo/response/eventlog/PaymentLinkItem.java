package com.liquido.aqueducts.vo.response.eventlog;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkItem {

    private String linkId;
    private String merchantReference;
    private String merchantName;
    private String clientId;
    private String productCode;
    private Long amount;
    private String currency;
    private String country;
    private String paymentStatus;
    private Long finalStatusTimestamp;
    private String refundStatus;
    private Long refundAmount;
    private Long refundTimestamp;
    private String email;
    private String phone;
    private String settledVirgoId;
    private String refundedVirgoId;
    private String subMerchantId;
    private Long createTimestamp;
    private Long eventTime;
    private String description;
    private HashMap<String, String> metadata;
    private Map<String, Object> appendix;
}
