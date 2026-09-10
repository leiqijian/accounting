package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.OperateModeEnum;
import com.liquido.base.enums.OperateSourceEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.VendorCodeEnum;

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
public class TransactionBizVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    @Length(max = 64)
    private String requestId;

    private Long transactionId;

    private String referenceId;

    private Long merchantId;

    @NotNull
    @Min(1)
    private Long accountId;

    private String subMerchantId;

    private Long billId;

    private Long paymentConfigId;

    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    @Convert(converter = PaymentChannelEnum.Convert.class)
    private PaymentChannelEnum paymentChannel;

    @Convert(converter = OperateSourceEnum.Convert.class)
    private OperateSourceEnum operateSource;

    @Convert(converter = OperateModeEnum.Convert.class)
    private OperateModeEnum operateMode;

    /**
     * unit:cent
     */
    @Min(1)
    @NotNull
    private BigDecimal transactionAmount;

    /**
     * unit:cent
     */
    @Min(0)
    private BigDecimal transactionAmountUsd;

    /**
     * AmountPonEnum, positive or negative;
     * positive: +1 ; negative: -1
     */
    @Convert(converter = AmountPonEnum.Convert.class)
    private AmountPonEnum amountPon;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionCurrency;

    /**
     * Order currency exchange to account currency fxRate
     * e.g:
     * when orderCurrency=MXN, accountCurrency=MXN, then exchangeRate=1.0;
     * when orderCurrency=MXN, accountCurrency=USD, then exchangeRate ≈ 18.120650;
     */
    private BigDecimal exchangeRate;

    /**
     * Order currency exchange to account currency fxLose
     */
    private BigDecimal exchangeLose;

    /**
     * Order currency exchange to USD fxRate
     * e.g: The orderCurrency(MXN) to USD, then exchangeRateUsd ≈ 18.120650;
     */
    private BigDecimal exchangeRateUsd;

    /**
     * unit:cent
     */
    @NotNull
    @Min(0)
    private BigDecimal settlementAmount;

    /**
     * unit:cent
     */
    @Min(0)
    private BigDecimal settlementAmountUsd;


    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    /**
     * unit:cent
     */
    @Min(0)
    private BigDecimal feeAmount;

    /**
     * unit:cent
     */
    @Min(0)
    private BigDecimal feeAmountUsd;

    /**
     * unit:cent
     */
    @Min(0)
    private BigDecimal taxAmount;

    /**
     * unit:cent
     */
    @Min(0)
    private BigDecimal taxAmountUsd;


    @Convert(converter = SettleStatusEnum.Convert.class)
    private SettleStatusEnum settlementStatus;

    @Length(max = 200)
    private String comments;

    /**
     * UTC+0
     */
    private LocalDateTime transactionTime;

    /**
     * UTC+0
     */
    private LocalDateTime settlementTime;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Long createdBy;

    private Long updatedBy;

    @Convert(converter = TransactionBizExtendInfo.Convert.class)
    private TransactionBizExtendInfo extendInfo;

    private Integer version;

    private Boolean delFlag;

    private String remark;

    private BizIncomeInfo incomeInfo;

    private BizCostInfo costInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BizIncomeInfo {

        @Min(0)
        @NotNull
        private BigDecimal incomeAmount;

        /**
         * unit:cent
         */
        @Min(0)
        @NotNull
        private BigDecimal incomeAmountUsd;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BizCostInfo {

        @Convert(converter = VendorCodeEnum.Convert.class)
        private VendorCodeEnum vendor;

        /**
         * Cost Fee, Unit: cent(USD)
         */
        @Min(0)
        private BigDecimal costFee;

        /**
         * Cost Tax, Unit: cen(USD)
         */
        @Min(0)
        private BigDecimal costTax;

        /**
         * Cost FX, Unit: cent(USD)
         */
        @Min(0)
        private BigDecimal costFx;

        /**
         * Other cost fees besides costFee, costTax, costFx
         */
        @Min(0)
        private BigDecimal costOther;
    }
}
