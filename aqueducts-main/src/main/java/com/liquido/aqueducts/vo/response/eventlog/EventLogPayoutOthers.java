package com.liquido.aqueducts.vo.response.eventlog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventLogPayoutOthers {
    private String targetName;
    private String paymentId;
    private long submitUnixTime;
    private String transferErrorMsg;
    private String requestId;
    private String accountId;
    private String branchId;
    private String bankId;
    private String bankName;
    private String bankCode;
    private String targetBankAccountType;
    private String transferStatusCode;
    private String payerComment;
}
