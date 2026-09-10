package com.liquido.statement.pojo.dto.payment;

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
public class PaymentLinkInstallmentsDto implements Serializable {
    private static final long serialVersionUID = 1L;


    private String modifierType;

    private BigDecimal rate;

    private Integer installments;
}
