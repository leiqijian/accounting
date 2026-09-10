package com.liquido.aqueducts.vo.response.eventlog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEventLogItem {
    private String uniqueId;
    private String merchantCode;
    private String merchantReference;
    private String transactionType;
    private String productCode;
    private long amount;
    private String currency;
    private String country;
    private String transactionStatus;
    private String documentId;
    private String directionCode;
    // timestamp seconds
    private long SubmitTime;
    private long finalStatusTime;
    private long eventTime;
    private long createTime;
    private int flags;
    private String tradeTransferStatus;
    private String tradeTransactionType;
    private String vendor;
    private String subMerchantId;
    private String description;
    private Object others;
}
