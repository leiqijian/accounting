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
public class MonthlyFeeConfigurationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long accountId;

    private Long accountProductId;

    private Long accountFeeConfigurationId;

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
     * same fee amount for each transaction-0/percentage fee rate for each transaction-1
     */
    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeTypeCode;

    /**
     * Since for Report classification summary statistics
     */
    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    /**
     * the month this configuration active for, eg. 202204
     */
    private Integer activeMonth;

    /**
     * Min volume(Limit single transaction amount)
     */
    private BigDecimal minVolume;

    /**
     * Max volume(Limit single transaction amount)
     */
    private BigDecimal maxVolume;

    @Convert(converter = FeeValueModelEnum.Convert.class)
    private FeeValueModelEnum feeValueModel;

    private BigDecimal feeValue;

    /**
     * MXN/BRL/USD
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum accountCurrency;

    @Convert(converter = ListToCurrencyConvert.class)
    private List<CurrencyEnum> sourceCurrency;

    private BigDecimal minFeeAmount;

    private BigDecimal maxFeeAmount;

    /**
     * fee calculation base on:FEE-0;SETTLE_AMOUNT-1
     */
    @Convert(converter = FeeOnEnum.Convert.class)
    private FeeOnEnum feeOn;

    /**
     * SETTLED/REFUND/CHARGE_BACK
     */
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    /**
     * instant settlement
     */
    private Boolean instantFlag;

    private Integer version;

    private String remark;

}
