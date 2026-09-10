package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.liquido.base.enums.CardTypeEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.ExtraFeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
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
@SuppressWarnings("PMD.TooManyFields")
public class ExtraIncomeConfigurationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * format: yyyyMM
     */
    private Integer activeVersion;

    private Long accountId;

    private CountryCodeEnum countryCode;

    private TransactionTypeCodeEnum transactionTypeCode;

    private ProductCodeEnum productCode;

    private CardTypeEnum cardType;

    private CreditCardGroupCodeEnum cardGroup;

    private Integer installmentBegin;

    private Integer installmentEnd;

    private String feeName;

    private FeeTypeCodeEnum feeType;

    private ExtraFeeGroupEnum feeGroup;

    private FeeOnEnum feeOn;

    private FeeValueModelEnum feeModel;

    private BigDecimal volume;

    private CurrencyEnum currency;

}
