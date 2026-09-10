package com.liquido.statement.pojo.bo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.statement.convert.ListAdditionalChargeConvert;
import com.liquido.statement.pojo.vo.ExtendData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMoneyBo {

    private Long id;

    private String uniqueId;

    private Long transactionId;

    private Long merchantId;

    private Long accountId;

    private String documentId;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    private BigDecimal amountPon;

    private BigDecimal amount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private BigDecimal fxRate;

    /**
     * Exchange rate from order to USD
     */
    private BigDecimal fxRateUsd;

    /**
     * Exchange lose from order to USD
     */
    private BigDecimal fxLoseUsd;

    private BigDecimal settlementAmount;

    private BigDecimal settlementAmountUsd;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    @Convert(converter = SettleStatusEnum.Convert.class)
    private SettleStatusEnum settleStatus;

    private LocalDate beCreditedDate;

    private BigDecimal beCreditedAmount;

    @Convert(converter = ListAdditionalChargeConvert.class)
    private List<AdditionalCharge> additionalCharge;

    private Boolean holdStatus;

    private LocalDateTime submitTime;

    private LocalDateTime transactionTime;

    private LocalDateTime settleTime;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Long createdBy;

    private Long updatedBy;

    private Long billId;


    @Convert(converter = ExtendData.Convert.class)
    private ExtendData extendData;
}
