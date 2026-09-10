package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.statement.convert.ListAdditionalChargeConvert;
import com.liquido.statement.pojo.bo.AdditionalCharge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class TransactionMoneyVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String requestId;

    /**
     * from global transaction system uniqueId
     * reference task_fee_calculation.unique_id
     */
    @NotBlank
    private String uniqueId;

    /**
     * reference original uniqueId
     * scene: Required when directionType in(REFUND, REJECTED, CHARGE_BACK)
     * reference task_fee_calculation.unique_id
     */
    private String referenceId;

    @NotNull
    private BusinessStrategyEnum businessStrategy;

    /**
     * reference TaskFeeCalculation.id
     */
    @NotNull
    private Long transactionId;

    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @NotNull
    private String merchantCode;

    /**
     * merchantId
     */
    @NotNull
    private Long merchantId;

    /**
     * subMerchantId
     */
    private String subMerchantId;

    /**
     * accountId
     */
    @NotNull
    private Long accountId;

    private String documentId;

    /**
     * When exchange money required
     */
    private Long fxRateId;

    @NotNull
    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    /**
     * accountTimeZone
     */
    @NotBlank
    private String accountTimeZone;

    /**
     * PAY_IN, PAY_OUT, MARKET_PLACE_ORDERS
     */
    @NotNull
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * PAY_IN, PAY_OUT, MARKET_PLACE_ORDERS
     */
    @NotNull
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @NotNull
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @NotNull
    @Convert(converter = AmountPonEnum.Convert.class)
    private AmountPonEnum amountPon;

    /**
     * original transaction amount, unit: cent
     */
    @NotNull
    private BigDecimal amount;

    /**
     * original transaction currency
     */
    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * transaction exchange rate
     */
    @NotNull
    private BigDecimal fxRate;

    /**
     * Currency exchange to USD rate
     */
    @NotNull
    private BigDecimal fxUsdRate;

    /**
     * Currency exchange to USD lose
     */
    @NotNull
    private BigDecimal fxUsdLose;

    /**
     * Amount converted into account currency, unit: cent
     */
    @NotNull
    private BigDecimal settlementAmount;

    /**
     * Amount converted into USD, unit: cent
     */
    @NotNull
    private BigDecimal settlementAmountUsd;

    /**
     * target transaction currency
     */
    @NotNull
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    /**
     * tradingModel,
     */
    @NotNull
    @Convert(converter = TradingModelEnum.Convert.class)
    private TradingModelEnum tradingModel;

    /**
     * just pay-in account
     * to be credited date,
     * beCreditedDate = transactionDate(converted merchant timezone) + tradingModel;
     */
    @NotNull
    private LocalDate beCreditedDate;

    /**
     * just pay-in account
     * to be credited amount(deducted fee)
     * unit:cent
     */
    @NotNull
    private BigDecimal beCreditedAmount;

    /**
     * additional charge
     * type such as: "PENALTY_INTEREST" ...
     * amount unit:cent
     */
    @Convert(converter = ListAdditionalChargeConvert.class)
    private List<AdditionalCharge> additionalCharge;

    /**
     * just pay-in account
     * holdStatus: 0:NORMAL; 1:HOLD/LOCKED; 2:UNHOLD/UNLOCKED
     */
    @Convert(converter = HoldStatusEnum.Convert.class)
    private HoldStatusEnum holdStatus;

    /**
     * calculate time UTC+0
     */
    @NotNull
    private LocalDateTime calculateTime;

    /**
     * The original task_fee_calculation.submit_time UTC+0
     */
    @NotNull
    private LocalDateTime submitTime;

    /**
     * The original task_fee_calculation.transaction_time UTC+0
     */
    @NotNull
    private LocalDateTime transactionTime;

    /**
     * Has been converted to the merchant-account timezone
     */
    private LocalDate transactionDate;

    /**
     * transaction calculationRule(cardType,cardBand,cardRegion,cardUse3ds,cardInstallments)
     */
    private Map<String, String> calculationRule;

    /**
     * json Object
     * extend data
     */
    private Map<String, Object> extendData;

    /**
     * mark this order whether is a rejected_debit or not
     */
    private Boolean isRejectedDebit;

    private Boolean installmentFlag;

    /**
     * accounting schedule list(use credit-card and installment transactions)
     */
    private List<AccountingScheduleVo> accountingScheduleList;

    /**
     * transaction fee List
     */
    private List<TransactionFeeVo> transactionFeeList;

    /**
     * transaction cost List
     */
    private List<TransactionCostVo> transactionCostList;

    /**
     * transaction extra fee List
     */
    private List<TransactionExtraFeeVo> transactionExtraFeeList;
}
