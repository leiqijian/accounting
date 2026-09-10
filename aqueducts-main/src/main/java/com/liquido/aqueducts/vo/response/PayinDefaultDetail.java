package com.liquido.aqueducts.vo.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayinDefaultDetail {
    private String uniqueId;
    private String merchantReference;
    private String status;
    private String tradeTransferStatus;
    private String tradeTransactionType;
    private String settleVendor;
    private String transferErrorMsg;
    private long createTime;
    private long amount;
    private String currency;
    private List<TransactionLifeCycle> lifeCycle;
}
