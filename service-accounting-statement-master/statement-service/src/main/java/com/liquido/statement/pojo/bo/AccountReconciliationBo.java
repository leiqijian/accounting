package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class AccountReconciliationBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long accountId;
    private Long merchantId;
    private String merchantName;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    private String timezone;
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    private BigDecimal latestDailyBalance;
    private BigDecimal subTotalAmount;

    // totalBalance= latestDailyBalance + subTotalAmount
    private BigDecimal totalBalance;

    private BigDecimal extractableBalance;
    private BigDecimal frozenAmount;
    private BigDecimal exchangeAmount;

    private BigDecimal pendingAmount;
    private BigDecimal holdingAmount;

    // amountDiff = totalBalance - (extractableBalance + pendingAmount + holdingAmount)
    private BigDecimal amountDiff;

    private LocalDate lastBillDate;
    private LocalDate nextBillDate;

}
