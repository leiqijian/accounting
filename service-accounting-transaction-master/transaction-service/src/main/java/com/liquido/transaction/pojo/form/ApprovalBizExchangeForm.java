package com.liquido.transaction.pojo.form;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.OwnerEnum;
import com.liquido.transaction.enums.ExchangeAccountTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalBizExchangeForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String approvalCode;

    private String merchantCode;

    @Convert(converter = OwnerEnum.Convert.class)
    private OwnerEnum owner;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * exchange currency
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum exchangeCurrency;

    /**
     * merchant exchange amount (unit: cent)
     */
    private BigDecimal exchangeAmount;

    /**
     * the final rate used by the merchant
     */
    private BigDecimal merchantRate;

    /**
     * The rate at which money is transferred to the merchant
     */
    private BigDecimal currencyRate;

    private BigDecimal actualExchangeAmountTargetCurrency;

    private String beneficiaryAccount;

    private List<String> appendixIds;

    private String merchantRemark;

    private String radioMerchantCode;

    @Convert(converter = ExchangeAccountTypeEnum.Convert.class)
    private ExchangeAccountTypeEnum exchangeAccountType;

    private Boolean customExchangeRate;

    private BigDecimal extraCost;
}
