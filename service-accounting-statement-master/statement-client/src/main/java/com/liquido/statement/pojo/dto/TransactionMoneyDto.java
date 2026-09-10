package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.statement.convert.ListAdditionalChargeConvert;
import com.liquido.statement.pojo.bo.AdditionalCharge;
import com.liquido.statement.pojo.vo.ExtendData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class TransactionMoneyDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uniqueId;

    private Long transactionId;

    private Long merchantId;

    private Long accountId;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    private BigDecimal amountPon;

    private BigDecimal amount;

    private CurrencyEnum currency;

    private BigDecimal fxRate;

    private BigDecimal settlementAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    @Convert(converter = SettleStatusEnum.Convert.class)
    private SettleStatusEnum settleStatus;

    private LocalDate beCreditedDate;

    private BigDecimal beCreditedAmount;

    private String subMerchantId;

    @Convert(converter = ListAdditionalChargeConvert.class)
    private List<AdditionalCharge> additionalCharge;

    @Convert(converter = HoldStatusEnum.Convert.class)
    private HoldStatusEnum holdStatus;

    private LocalDateTime submitTime;

    private LocalDateTime transactionTime;

    private LocalDateTime settleTime;

    private Long billId;

    @Convert(converter = ExtendData.Convert.class)
    private ExtendData extendData;
}
