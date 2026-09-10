package com.liquido.aqueducts.vo.request;

import com.liquido.aqueducts.commons.enums.CountryCode;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.commons.enums.TransactionType;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDetailRequest {
    private String uniqueId;
    private CountryCode countryCode;
    private ProductCode productCode;
    private String merchant;
    private TransactionType transactionType;
}
