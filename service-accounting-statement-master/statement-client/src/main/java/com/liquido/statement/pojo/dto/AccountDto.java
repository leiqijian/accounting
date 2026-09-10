package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
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
public class AccountDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * accountId
     */
    private Long id;

    /**
     * merchantId
     */
    private Long merchantId;

    /**
     * accountConfigId
     */
    private Long accountConfigId;

    private AccountConfigDto accountConfig;

    /**
     * CountryCodeEnum: BR/MX/US
     */
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    private BigDecimal latestDailyBalance;

    private BigDecimal latestDailyExtractableBalance;

    private BigDecimal subTotalAmount;

    private BigDecimal extractableBalance;

    private BigDecimal frozenAmount;

    /**
     * Record the currently frozen amount in exchange balance
     * unit:cent
     */
    private BigDecimal exchangeAmount;

    /**
     * MXN/BRL/USD
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * account local time zone
     */
    private String timezone;

    private String timezoneName;

    /**
     * month holding limit
     */
    private BigDecimal holdingLimit;

    private boolean overWithdrawalAmount;

    // only querySubtractInProgressAmountAccountInfo interface this value is not null
    private BigDecimal unavailableAmount;

    // only querySubtractInProgressAmountAccountInfo interface this value is not null
    private BigDecimal pendingAmount;

}
