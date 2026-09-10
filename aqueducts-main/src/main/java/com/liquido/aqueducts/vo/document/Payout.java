package com.liquido.aqueducts.vo.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Payout {

    private Long id;
    private String idempotency_key;
    private String transaction_id;
    private String reference_number;
    private String status;
    private Long amount;
    private String foreign_amount;
    private String exchange_rate;
    private String transaction_fee;
    private String balance;
    private String currency;
    private String submit_time;
    private String create_time;
    private String update_time;
    private String payment_type;
    private String payer_comment;
    private String payee_comment;
    private String country;
    private String merchant_name;
    private String sub_merchant_id;
    private String payment_info;
    private String payee_info;
//    private String bank_name;
//    private String bank_code;
//    private String bank_id;
//    private String branch_id;
//    private String target_account_id;
//    private String pix_key;
//    private String pix_key_type;
//    private String target_name;
//    private String target_last_name;
//    private String target_email;
//    private String target_document_id;
//    private String target_document_type;
//    private String target_birth_date;
//    private String target_phone;
    private String schedule_date;
    private Long submit_unix_time;
    private String final_status_time;
    private Long final_status_unix_time;
    private String vendor_create_time;
    private String vendor_error_code;
    private String vendor_error_message;
    private String transfer_status_code;
    private String transfer_error_msg;
    private String flags;
    private String response;
    private String reserve;
    private Integer version;
    private Integer need_retry;
    private String raw_client_request;
    private String vendor_name;
//    private String target_bank_account_type;
    private String settle_vendor;
//    private String pix_end_to_end_id;
    private String rejected_by_vendors;
    private Integer retry_count;
//    private String full_name_from_pix_key;
//    private String doc_id_from_pix_key;
    private String external_id;
    private String spei_tid;

}
