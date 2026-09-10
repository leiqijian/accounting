package com.liquido.statement.pojo.dto;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("PMD.TooManyFields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFeeDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long transactionId;

    private String uniqueId;

    private Long merchantId;

    private Long accountId;

    private Long feeConfigurationId;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    private String feeName;

    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeTypeCode;

    private BigDecimal amountPon;

    private BigDecimal settlementAmount;

    /**
     * target transaction currency
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    /**
     * StatusEnum: WAITING/PROCESSING/SUCCESS/FAILED
     */
    @Convert(converter = SettleStatusEnum.Convert.class)
    private SettleStatusEnum settleStatus;

    private Boolean instantFlag;

    /**
     * The original task_fee_calculation.transaction_time UTC+0
     */
    private LocalDateTime transactionTime;

    private LocalDateTime settleTime;

    private Long billId;

}
