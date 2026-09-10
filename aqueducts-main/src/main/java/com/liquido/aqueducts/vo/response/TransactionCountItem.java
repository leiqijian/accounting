package com.liquido.aqueducts.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCountItem {
    private String transactionType;
    private String countryCode;
    private String merchantCode;
    private String product;
    private int totalCount;
    private int successCount;
}
