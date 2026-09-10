package com.liquido.aqueducts.vo.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopifyOrder {

    private String id;
    private String gid;
    private String group_id;
    private String merchant_code;
    private String amount;
    private String currency;
    private String status;
    private String link;
    private String payment_method;
    private String final_status_virgo_id;
    private long final_status_timestamp;
    private long final_status_time;
    private boolean is_refunded;
    private String refund_id;
    private long refund_time;
    private long refund_amount;
    private String refund_status;
    private String redirect_url;
    private boolean test;
    private String merchant_locale;
    private String cancel_url;
    private String proposed_at;
    private String email;
    private String phone_number;
    private String customer_locale;
    private String kind;
    private String billing_address;
    private String shipping_address;
    private String shop_domain;
    private String request_id;
    private String shopify_api_version;
    private long create_time;
    private long update_time;

}
