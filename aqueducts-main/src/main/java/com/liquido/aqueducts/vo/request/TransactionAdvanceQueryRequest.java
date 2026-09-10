package com.liquido.aqueducts.vo.request;

import com.liquido.aqueducts.commons.enums.CountryCode;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.commons.enums.TransactionType;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransactionAdvanceQueryRequest {
    private CountryCode countryCode;
    private ProductCode productCode;
    private String merchantCode;
    private TransactionType transactionType;
    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int pageSize = 20;
    private long from;
    private long to;
    private Map<String, String> other;
}
