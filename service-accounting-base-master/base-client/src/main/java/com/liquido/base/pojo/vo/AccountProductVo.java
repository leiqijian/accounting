package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.MonthlyVolumeTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountProductVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * fk
     */
    @NotNull
    private Long accountId;

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

    @NotNull
    @Convert(converter = TradingModelEnum.Convert.class)
    private TradingModelEnum tradingModel;

    /**
     * MonthlyVolumeTypeEnum: 0-amount/1-counts
     */
    @NotNull
    @Convert(converter = MonthlyVolumeTypeEnum.Convert.class)
    private MonthlyVolumeTypeEnum monthlyVolumeType;

    private String monthlyVolumeGroup;

    /**
     * redundant account time zone
     */
    @NotBlank
    private String timezone;

    private LocalDateTime openTime;

    private String remark;

}
