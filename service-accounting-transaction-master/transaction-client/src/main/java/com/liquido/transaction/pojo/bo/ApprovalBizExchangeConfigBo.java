package com.liquido.transaction.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.persistence.Convert;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.transaction.enums.FeeModeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalBizExchangeConfigBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean overWithdrawAmountFlag;

    private List<CurrencyEnum> supportCurrency;

    private Map<CurrencyEnum, SupportConfigBo> supportConfig;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupportConfigBo extends ApprovalBizBaseConfigBo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Min(0)
        private BigDecimal ratioLose;

        private Boolean customExchangeRate;

        /**
         * Does the merchant need to confirm the exchange rate and amount in the contract
         */
        private Boolean needConfirmContract;

        @NotNull
        @Valid
        private ExchangeFeeBo exchangeFee;

        @NotEmpty
        private List<ApprovalConfigAccountBo> merchantAccountInfo;

        private List<ApprovalConfigAccountBo> ownerAccountInfo;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExchangeFeeBo implements Serializable {

        private static final long serialVersionUID = 1L;

        @NotNull
        private Boolean openFlag;

        /**
         * 0: fixed value charge 1:rate charge
         */
        @Convert(converter = FeeModeEnum.Convert.class)
        @NotNull
        private FeeModeEnum feeMode;

        @NotNull
        @Min(0)
        private BigDecimal feeValue;

        @Convert(converter = CurrencyEnum.Convert.class)
        @NotNull
        private CurrencyEnum currency;

        /**
         * small fee
         */
        @NotNull
        @Min(0)
        private BigDecimal smallFee;

        /**
         * small fee limit
         */
        @NotNull
        @Min(0)
        private BigDecimal smallFeeLimit;

    }

}
