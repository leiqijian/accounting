package com.liquido.aqueducts.vo.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubAccount {
    private long id;
    private String parent_id;
    private String friendly_name;
    private String legal_name;
    private String external_bank_account;
    private String sub_account_uuid;
    private String extra_data;
    private String create_time;
    private String update_time;
    private int version;
    private String account_id;
    private String user_id;
    private String idempotency_key;
    private String mdn;
    private int is_prefab;
    private String parent_clabe_id;
}
