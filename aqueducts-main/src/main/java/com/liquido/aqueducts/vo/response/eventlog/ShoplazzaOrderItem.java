package com.liquido.aqueducts.vo.response.eventlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoplazzaOrderItem {

    private String orderId;
    private String link;
    private String linkId;
    private String merchantName;
    private String shopDomain;
    private Long amount;
    private String currency;
    private String country;
    private String productCode;
    private String paymentStatus;
    private Long finalStatusTimestamp;
    private boolean isRefunded;
    private Long refundTimestamp;
    private String settledVirgoId;
    private String refundedVirgoId;
    private Long refundTime;
    private String email;
    private String phone;
    private Long createTime;
    private Long eventTime;
}
