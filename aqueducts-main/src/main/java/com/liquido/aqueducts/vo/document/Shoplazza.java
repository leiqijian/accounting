package com.liquido.aqueducts.vo.document;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shoplazza {
    private String id;
    private String app_id;
    private String account_id;
    private String merchant_code;
    private String order_id;
    private String amount;
    private String currency;
    private String status;
    private String link;
    private String payment_method;
    private String final_status_virgo_id;
    private Long final_status_timestamp;
    private Long final_status_time;
    private boolean is_refunded;
    private String refund_id;
    private Long refund_time;
    private String redirect_url;
    private boolean test;
    private String cancel_url;
    private String complete_url;
    private String callback_url;
    private String proposed_at;
    private String email;
    private String phone_number;
    private String type;
    private String billing_address;
    private String shipping_address;
    private String shop_domain;
    private String request_id;
    private String api_version;
    private String timestamp;
    private Long create_time;
    private Long update_time;

}
