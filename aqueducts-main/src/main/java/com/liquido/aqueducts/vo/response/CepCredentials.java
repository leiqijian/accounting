package com.liquido.aqueducts.vo.response;

import lombok.Data;

@Data
public class CepCredentials {
    private String referenceNumber;
    private String vendorName;
    private String status;
    private String targetAccountId;
    private String amount;
    private String bankName;
    private String bankCode;
    private String bankId;
    private String finalStatusTime;
}
