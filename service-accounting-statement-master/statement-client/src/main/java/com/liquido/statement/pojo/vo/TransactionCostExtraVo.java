package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class TransactionCostExtraVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(1)
    private Long accountId;

    @Length(max = 32)
    private String businessTag;

    // account timezone bill date
    @NotNull
    private LocalDate transactionDate;

    private BigDecimal fee;

    private BigDecimal tax;

    private BigDecimal fx;

    private BigDecimal extraFee;

    private BigDecimal extraTax;

    private BigDecimal extraFx;

    private BigDecimal exchangeFee;

    private BigDecimal adjustmentFee;

    private BigDecimal cdiProfitIncome;

    private BigDecimal costFee;

    private BigDecimal costTax;

    private BigDecimal costFx;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum pricingCurrency;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    @NotNull
    private BigDecimal exchangeRate;

    @Length(max = 200)
    private String remark;

    private Long createdBy;

    private Long updatedBy;

    private String createName;

}
