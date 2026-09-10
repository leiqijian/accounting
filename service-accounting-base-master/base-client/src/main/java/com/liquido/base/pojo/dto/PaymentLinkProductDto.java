package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkProductDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

}
