package com.liquido.aqueducts.vo.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutOthers {

    // transaction_id
    private String transactionId;

    // set empty
    private String externalId;

    // idempotency_key
    private String referenceNumber;

}
