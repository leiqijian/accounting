package com.liquido.aqueducts.vo.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Payin {
    private Long id;
    private String idempotency_key;
    private String merchant_name;
    private String reference_id;
    private String internal_transaction_id;
    private String vendor_transaction_id;
    // It was called vendorName in payin table
    private String vendor;
    private String settle_vendor;
    private String transfer_status;
    private String transaction_type;
    private String amount;
    private String final_amount;
    private String refunded_amount;
    // It was called refunded_amount in payin table
    private String final_refunded_amount;
    private String currency;
    private String final_currency;
    private String country;
    private String payment_method;
    private String payment_flow;
    private String payment_info;
    private String payer;
    private String document_id;
    private String payer_comment;
    private String payee_comment;
    private String vendor_status_code;
    private String vendor_status_message;
    private String transfer_status_code;
    private String transfer_error_msg;
    private String response;
    private Integer version;
    private String create_time;
    private String update_time;
    private String submit_time;
    private String scheduled_time;
    private String final_status_time;
    private String settled_time;
    private String client_zone_id;
    private String raw_client_request;
    private Integer flags;
    private String callback_url;
    private String sub_merchant_id;
    private String risk_data;
    private String order_info;
    private String refund_additional_info;
    private String amount_details;
    private String description;

    private String payment_proposal_info;
    private String device_info;
}
