package com.liquido.aqueducts.vo.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClabeAccountTransferDetail {
    private String beneficiaryAccountNumber;
    private String referenceNumber;
    private String referenceId;
    private String trackingId;
    private String senderName;
    private String senderAccountNumber;
    private String paymentTime;
}
