package com.liquido.base.pojo.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CardTypeEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.VendorCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class CostCardConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer activeVersion;

    private Long id;

    private Long accountId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendorCode;

    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @Convert(converter = CardTypeEnum.Convert.class)
    private CardTypeEnum cardType;

    @Convert(converter = CreditCardGroupCodeEnum.Convert.class)
    private CreditCardGroupCodeEnum cardGroup;

    private Integer installmentBegin;

    private Integer installmentEnd;

    private String feeName;

    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeType;

    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    @Convert(converter = FeeOnEnum.Convert.class)
    private FeeOnEnum feeOn;

    @Convert(converter = FeeValueModelEnum.Convert.class)
    private FeeValueModelEnum feeModel;

    private BigDecimal volume;

    private BigDecimal minVolume;

    private BigDecimal maxVolume;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * TRUE is default config; FALSE is customization config
     */
    private Boolean def;


}
