package com.liquido.aqueducts.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundAccountInfo {
    private Boolean manualRefund;
    private String bankCode;
    private String documentType;
    private String documentId;
    private String bankBranchId;
    private String bankAccountType;
    private String beneficiaryName;
    private String bankAccountNumber;
}
