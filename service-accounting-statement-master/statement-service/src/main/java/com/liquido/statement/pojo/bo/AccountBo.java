package com.liquido.statement.pojo.bo;

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

/**
 * account
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class AccountBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long merchantId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    private BigDecimal latestDailyBalance;

    private BigDecimal subTotalAmount;

    private BigDecimal latestDailyExtractableBalance;

    private BigDecimal extractableBalance;

    private BigDecimal holdingLimit;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private String timezone;

    private String timezoneName;

    private Long version;

    private Boolean delFlag;
}
