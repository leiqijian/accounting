package com.liquido.aqueducts.vo.payout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankTransferPaymentInfo {
    
    private String targetBankName;

    private String targetBankCode;

    private String targetBankId;

    private String TargetBankAgency;

    private String targetBankAccountId;

    private String targetBankAccountType;

    private String referenceNumber;

    private String trackingCode;

    private String vendorExternalId;

    private String payerBankAccountId;

}
