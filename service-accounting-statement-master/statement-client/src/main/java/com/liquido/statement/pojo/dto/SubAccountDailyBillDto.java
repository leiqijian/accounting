package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class SubAccountDailyBillDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long merchantId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    private String subMerchantId;

    private Long subAccountId;

    private Integer billMonth;

    private LocalDate billDate;

    private BigDecimal startBalance;

    private BigDecimal happenAmount;

    private BigDecimal endBalance;

    private Long payinTransactionCount;

    private BigDecimal payinSettlementAmount;

    private BigDecimal payinTransactionFee;

    private BigDecimal payinTransactionTax;

    private Long payoutTransactionCount;

    private BigDecimal payoutSettlementAmount;

    private BigDecimal payoutTransactionFee;

    private BigDecimal payoutTransactionTax;

    private Long topupCount;

    private BigDecimal topupAmount;

    private BigDecimal topupFee;

    private BigDecimal topupTax;

    private Long transferCount;

    private BigDecimal transferAmount;

    private BigDecimal transferFee;

    private BigDecimal transferTax;

    private Long exchangeCount;

    private BigDecimal exchangeAmount;

    private BigDecimal exchangeFee;

    private BigDecimal exchangeTax;

    private BigDecimal adjustmentAmount;

    private Long adjustmentCount;

    private BigDecimal endExtractableBalance;

    private CurrencyEnum currency;
}
