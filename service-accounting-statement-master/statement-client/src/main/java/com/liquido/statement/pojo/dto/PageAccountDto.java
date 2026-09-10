package com.liquido.statement.pojo.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
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
public class PageAccountDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * accountId
     */
    private Long id;

    /**
     * merchantId
     */
    private Long merchantId;

    private String merchantCode;

    private String merchantName;

    private Long accountConfigId;

    private AccountConfigDto accountConfigDto;

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

    private BigDecimal subTotalAmount;

    private BigDecimal totalBalance;

    private BigDecimal extractableBalance;

    /**
     * MXN/BRL/USD
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private List<CurrencyEnum> exchangeRateCurrency;

    /**
     * account local time zone
     */
    private String timezone;

    private String timezoneName;

    /**
     * month holding limit
     */
    private BigDecimal holdingLimit;

    private BigDecimal holdingAmount;

    private Long version;

    private BigDecimal availableAmount;

    private BigDecimal unavailableAmount;

    private BigDecimal pendingAmount;
}
