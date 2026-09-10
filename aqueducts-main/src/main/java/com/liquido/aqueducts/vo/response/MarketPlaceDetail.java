package com.liquido.aqueducts.vo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarketPlaceDetail {
    private String uniqueId;
    private String merchantReference;
    private String status;
    private String transactionType;
    private String settleVendor;
    private String transferErrorMsg;
    private long createTime;
    private long amount;
    private String currency;
    private List<TransactionLifeCycle> lifeCycle;
    private Object skuDetail;

}
