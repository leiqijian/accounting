package com.liquido.base.pojo.vo;


import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.MonthlyVolumeTypeEnum;
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
public class QueryAccountProductVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * fk
     */
    private Long accountId;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * ProductCodeEnum: SPEI/TED/PIX/CREDIT_CARD/ELO_CREDIT_CARD/BOLETO/OXXO/GIFTCARD/TOPUP/UTILITY
     */
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    /**
     * MonthlyVolumeTypeEnum: 0-amount/1-counts
     */
    @Convert(converter = MonthlyVolumeTypeEnum.Convert.class)
    private MonthlyVolumeTypeEnum monthlyVolumeType;

    /**
     * redundant account time zone
     */
    private String timezone;

    private LocalDateTime openTime;

}
