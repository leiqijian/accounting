package com.liquido.aqueducts.vo.document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLink {

    private String link_id;
    private String merchant_name;
    private String allow_payment_methods;
    private long amount;
    private String api_key;
    private String billing_address;
    private String callback_url;
    private String client_id;
    private String country;
    private long create_time;
    private String currency;
    private String description;
    private String email;
    private String final_payment_method;
    private String final_status_time;
    private long final_status_timestamp;
    private String final_status_virgo_id;
    private boolean is_notified;
    private boolean is_refunded;
    private String items;
    private String link_id_to_string;
    private String order_id;
    private String payment_status;
    private String phone;
    private String redirect_url;
    private String refund_id;
    private String refund_reason;
    private String refund_time;
    private long refund_timestamp;
    private long update_time;
    private long refund_amount;
    private String refund_status;
    private String metadata;
    private String appendix;
    private String sub_merchant_id;

}
