package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryProductVo implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * id
     */
    private Long id;

    /**
     * CountryCodeEnum: BR/MX/US
     */
    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @NotNull
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * ProductCodeEnum: SPEI/TED/PIX/CREDIT_CARD/ELO_CREDIT_CARD/BOLETO/OXXO/GIFTCARD/TOPUP/UTILITY
     */
    @NotNull
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

}
