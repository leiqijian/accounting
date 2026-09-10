package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecalculateCostData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String uniqueId;
    private Long transactionId;
    private Long accountId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    private LocalDateTime transactionTime;
    private Long transactionTimestamp;

    private BigDecimal fxRate;
    private BigDecimal fxLose;

    private BigDecimal amount;
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private BigDecimal amountUsd;

    // transaction income
    private BigDecimal feeUsd;
    private BigDecimal taxUsd;
    private BigDecimal fxUsd;

    private BigDecimal extraFeeUsd;
    private BigDecimal extraTaxUsd;
    private BigDecimal extraFxUsd;

    // transaction cost
    private BigDecimal costFee;
    private BigDecimal costTax;
    private BigDecimal costFx;
    private BigDecimal costOther;

    private String remark;
}
