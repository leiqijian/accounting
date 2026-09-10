package com.liquido.aqueducts.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"merchantCode", "countryCode", "transactionType"})
public class TransactionMetricItem {
    private String transactionType;
    private String countryCode;
    private String currency;
    private String merchantCode;
    private long countTransaction;
    private long sumAmount;
}
