package com.liquido.aqueducts.vo.document;


import com.liquido.aqueducts.vo.document.eventlog.SubAccountEventLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubAccountPayBack {
    private long id;
    private String user_id;
    private String parent_account_uuid;
    private String parent_account_id;
    private String sub_account_id;
    private String sub_account_uuid;
    private String friendly_name;
    private String legal_name;
    private String external_bank_account;
    private String request_id;
    private String external_id;
    private String transaction_id;
    private String reference_number;
    private String status;
    private String amount;
    private String currency;
    private String description;
    private long submit_unix_time;
    private String submit_time;
    private String final_status_time;
    private long final_status_unix_time;
    private String vendor_error_code;
    private String vendor_error_message;
    private String response;
    private String notify_client_status;
    private String create_time;
    private String update_time;
    private int version;
    private int flags;
    private List<SubAccountEventLog> sub_account_info;

}
