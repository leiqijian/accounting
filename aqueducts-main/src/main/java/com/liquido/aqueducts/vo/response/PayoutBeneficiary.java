package com.liquido.aqueducts.vo.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutBeneficiary {

    // target_name
    private String accountName;

    // pix_key_type
    private String pixKeyType;

    // pix_key
    private String pixKey;

    // bank_name
    private String bankName;

    // bank_code
    private String bankCode;

    // branch_id
    private String branch;

    // target_document_id
    private String document;

    // target_group_id
    private String targetAccountId;

    // target_email
    private String email;

    // target_phone
    private String phone;

}
