package com.liquido.worker.pojo.dto.tokenization;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwSyncDisplayedCardInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bin;

    private String brand;

    private String last4;

    private String cardHolderName;

    private BigDecimal expirationYear;

    private BigDecimal expirationMonth;
}
