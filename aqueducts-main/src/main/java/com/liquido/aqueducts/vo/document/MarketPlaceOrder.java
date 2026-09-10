package com.liquido.aqueducts.vo.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MarketPlaceOrder {
    private long id;
    private String user_id;
    private String idempotency_key;
    private String external_id;
    private String reference_number;
    private String transaction_id;
    private String sku;
    private String order_status;
    private String amount;
    private String currency;
    private String description;
    private String country_code;
    private String buyer_id;
    private int order_type;
    private String biz_content;
    private String biz_content_response;
    private String submit_time;
    private long submit_unix_time;
    private String final_status_time;
    private long final_status_unix_time;
    private String vendor_create_time;
    private String vendor_error_code;
    private String vendor_error_message;
    private int order_status_code;
    private String order_error_msg;
    private String ext_transaction_id;
    private String balance;
    private int flags;
    private String response;
    private String query_response;
    private String create_time;
    private String update_time;
    private int version;
    private int need_retry;
    private String vendor_transaction_id;
    private String category;
    private String vendor_name;
    private String payment_type;
    private String payment_info;
    private String callback_url;
    private String biz_info;

}
