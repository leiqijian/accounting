package com.liquido.aqueducts.vo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayCashDetail {
    private String uniqueId;
    private String merchantReference;
    private String status;
    private String tradeTransferStatus;
    private String tradeTransactionType;
    private String settleVendor;
    private String transferErrorMsg;
    private Long createTime;
    private Long amount;
    private String currency;
    private List<TransactionLifeCycle> lifeCycle;
    private PayerInfo payer;
    private Information information;
}
