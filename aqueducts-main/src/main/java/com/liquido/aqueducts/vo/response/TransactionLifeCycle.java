package com.liquido.aqueducts.vo.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLifeCycle {
    private String uniqueId;
    private String operation;
    private String referenceId;
    private String transactionStatus;
    private long amount;
    private String currency;
    private long timestamp;
}
