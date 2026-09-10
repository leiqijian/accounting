package com.liquido.aqueducts.vo.response.eventlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopifyOrderItem {

    // payment id in shopify admin
    private String paymentId;
    private String merchantName;
    private String shopDomain;
    private Long amount;
    private String currency;
    private String country;
    private String productCode;
    private String paymentStatus;
    private Long finalStatusTimestamp;
    private String refundStatus;
    private Long refundAmount;
    private Long refundTimestamp;
    private String settledVirgoId;
    private String refundedVirgoId;
    private Long refundTime;
    private String email;
    private String phone;
    private Long createTime;
    private Long eventTime;

}
