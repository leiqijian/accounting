package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.PaymentChannelEnum;
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
public class OfflineExchangeVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * global unique requestId
     */
    @NotBlank
    private String requestId;

    /**
     * accountId
     */
    @NotNull
    @Min(1)
    private Long accountId;

    @NotNull
    @Convert(converter = PaymentChannelEnum.Convert.class)
    private PaymentChannelEnum paymentChannel;

    /**
     * unit: cent
     */
    @NotNull
    @Min(1)
    private BigDecimal transactionAmount;

    /**
     * unit: cent
     */
    @NotNull
    private BigDecimal transactionAmountUsd;

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
    @Min(1)
    private BigDecimal settlementAmount;

    /**
     * unit:cent
     */
    @NotNull
    @Min(0)
    private BigDecimal settlementAmountUsd;

    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    /**
     * unit:cent
     */
    @NotNull
    @Min(0)
    private BigDecimal feeAmount;

    /**
     * unit:cent
     */
    @NotNull
    @Min(0)
    private BigDecimal feeAmountUsd;

    /**
     * unit:cent
     */
    @NotNull
    @Min(0)
    private BigDecimal taxAmount;

    /**
     * unit:cent
     */
    @NotNull
    @Min(0)
    private BigDecimal taxAmountUsd;

    /**
     * UTC+0
     */
    private LocalDateTime transactionTime;

    /**
     * UTC+0
     */
    private LocalDateTime settlementTime;

    @Length(max = 200)
    private String comments;

    private OfflineExchangeVo.BizIncomeInfo incomeInfo;

    private OfflineExchangeVo.BizCostInfo costInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BizIncomeInfo {

        @Min(0)
        private BigDecimal incomeAmount;

        /**
         * unit:cent
         */
        @Min(0)
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
