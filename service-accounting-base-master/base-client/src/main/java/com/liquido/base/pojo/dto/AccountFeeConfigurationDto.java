package com.liquido.base.pojo.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.persistence.Convert;

import com.liquido.base.convert.ListToCurrencyConvert;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeCodeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.MonthlyVolumeTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("PMD.TooManyFields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountFeeConfigurationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * snowflake id
     */
    private Long id;

    /**
     * fk
     */
    private Long accountId;

    /**
     * fk
     */
    private Long accountProductId;

    /**
     * ProductCodeEnum: SPEI/TED/PIX/CREDIT_CARD/ELO_CREDIT_CARD/BOLETO/OXXO/GIFTCARD/TOPUP/UTILITY
     */
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    private Map<String, String> calculationRule;

    private String feeName;

    @Convert(converter = FeeCodeEnum.Convert.class)
    private FeeCodeEnum feeCode;

    /**
     * FeeTypeCodeEnum: TRANSACTION_FEE/FX/TAX/REFUND_FEE/CHARGE_BACK_FEE/WITHDRAW_FEE
     */
    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeTypeCode;

    /**
     * Since for Report classification summary statistics
     */
    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    /**
     * MonthlyVolumeTypeEnum: 0-amount/1-counts
     */
    @Convert(converter = MonthlyVolumeTypeEnum.Convert.class)
    private MonthlyVolumeTypeEnum monthlyVolumeType;

    private BigDecimal minMonthlyVolume;

    private BigDecimal maxMonthlyVolume;

    /**
     * Min volume(Limit single transaction amount)
     */
    private BigDecimal minVolume;

    /**
     * Max volume(Limit single transaction amount)
     */
    private BigDecimal maxVolume;

    /**
     * FeeValueModelEnum: fixed -0/percent -1
     */
    @Convert(converter = FeeValueModelEnum.Convert.class)
    private FeeValueModelEnum feeValueModel;

    /**
     * fixed value or percentage ratio
     */
    private BigDecimal feeValue;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum accountCurrency;

    @Convert(converter = ListToCurrencyConvert.class)
    private List<CurrencyEnum> sourceCurrency;

    private BigDecimal minFeeAmount;

    private BigDecimal maxFeeAmount;

    /**
     * FeeOnEnum: AMOUNT/SETTLE_AMOUNT
     */
    @Convert(converter = FeeOnEnum.Convert.class)
    private FeeOnEnum feeOn;

    /**
     * DirectionTypeEnum: SETTLED/REFUND/CHARGE_BACK
     */
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    /**
     * 0-non-instant，1-instant
     */
    private Boolean instantFlag;

    private Integer version;

    private String remark;

}
