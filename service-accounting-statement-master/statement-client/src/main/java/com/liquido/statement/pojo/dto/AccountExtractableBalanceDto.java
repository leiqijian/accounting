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

/**
 * Only the payIn account can query the extractable balance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class AccountExtractableBalanceDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long merchantId;

    private Long accountId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * TransactionTypeCodeEnum: PAY_IN
     */
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * latestDailyBalance
     * unit:cent
     */
    private BigDecimal latestDailyBalance;

    /**
     * subTotalAmount
     * unit:cent
     */
    private BigDecimal subTotalAmount;

    /**
     * account realtime total balance
     * latestDailyBalance + subTotalAmount
     * unit:cent
     */
    private BigDecimal totalBalance;

    /**
     * account extractable Amount
     * unit:cent
     */
    private BigDecimal extractableBalance;

    /**
     * Record the currently frozen amount in extractable balance
     * unit:cent
     */
    private BigDecimal frozenAmount;


    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;
}
