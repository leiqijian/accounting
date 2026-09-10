package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TaxRuleEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * non-card cost configuration
 */
@SuppressWarnings("PMD.TooManyFields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralCostConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer activeVersion;

    private Long accountId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendorCode;

    @Convert(converter = FeeValueModelEnum.Convert.class)
    private FeeValueModelEnum feeModel;

    @Convert(converter = TaxRuleEnum.Convert.class)
    private TaxRuleEnum taxRule;

    private String feeName;

    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeType;

    @Convert(converter = FeeGroupEnum.Convert.class)
    private FeeGroupEnum feeGroup;

    @Convert(converter = FeeOnEnum.Convert.class)
    private FeeOnEnum feeOn;

    private BigDecimal volume;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;
}
