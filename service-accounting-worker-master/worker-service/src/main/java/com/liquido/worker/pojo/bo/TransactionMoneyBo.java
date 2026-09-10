package com.liquido.worker.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.persistence.Convert;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMoneyBo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * reference TaskFeeCalculation.id
     */
    private Long transactionId;

    /**
     * from global transaction system uniqueId
     * reference task_fee_calculation.unique_id
     */
    private String uniqueId;

    /**
     * reference original uniqueId
     * scene: Required when directionType in(REFUND, REJECTED, CHARGE_BACK)
     * reference task_fee_calculation.unique_id
     */
    private String referenceId;

    /**
     * merchantId
     */
    private Long merchantId;

    /**
     * accountId
     */
    private Long accountId;

    private String documentId;

    /**
     * When exchange money required
     */
    private Long fxRateId;

    /**
     * accountTimezone
     */
    private String accountTimeZone;

    /**
     * businessType
     */
    private BusinessStrategyEnum businessStrategy;


    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    private String merchantCode;

    /**
     * PAY_IN, PAY_OUT, MARKET_PLACE_ORDERS
     */
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * PAY_IN, PAY_OUT, MARKET_PLACE_ORDERS
     */
    private ProductCodeEnum productCode;

    private CreditCardGroupCodeEnum cardGroup;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

    private String subMerchantId;

    /**
     * SETTLED("SETTLED", "settled"),
     * REFUND("REFUND", "refund"),
     * CHARGE_BACK("CHARGE_BACK", "charge_back");
     */
    private DirectionTypeEnum directionType;

    /**
     * amountPon: positive or negative; positive :+1,  negative:-1
     */
    private AmountPonEnum amountPon;

    /**
     * original transaction amount, unit: cent
     */
    private BigDecimal amount;

    /**
     * original transaction currency
     */
    private CurrencyEnum currency;

    /**
     * transaction exchange rate
     */
    private BigDecimal fxRate;

    /**
     * Amount converted into account currency, unit: cent
     */
    private BigDecimal settlementAmount;

    /**
     * Amount converted into USD, unit: cent
     */
    private BigDecimal settlementAmountUsd;

    /**
     * Currency exchange to USD rate
     */
    private BigDecimal fxUsdRate;

    /**
     * Currency exchange to USD lose
     */
    private BigDecimal fxUsdLose;

    /**
     * target transaction currency
     */
    private CurrencyEnum settlementCurrency;

    /**
     * tradingModel,
     */
    private TradingModelEnum tradingModel;

    /**
     * to be credited date,
     * beCreditedDate = transactionDate(converted merchant timezone) + tradingModel;
     */
    private LocalDate beCreditedDate;

    /**
     * to be credited amount(deducted fee)
     * unit:cent
     */
    private BigDecimal beCreditedAmount;

    /**
     * payin credit-card transaction accounting schedules
     */
    private List<AccountingScheduleBo> accountingScheduleList;

    /**
     * just pay-in account
     * holdStatus: 0:NORMAL; 1:HOLD/LOCKED; 2:UNHOLD/UNLOCKED
     */
    @Convert(converter = HoldStatusEnum.Convert.class)
    private HoldStatusEnum holdStatus;

    /**
     * calculateTime UTC+0
     */
    private LocalDateTime calculateTime;

    /**
     * The original task_fee_calculation.transaction_time UTC+0
     */
    private LocalDateTime transactionTime;

    /**
     * Has been converted to the merchant-account timezone
     */
    private LocalDate transactionDate;

    /**
     * SubmitTime UTC+0
     */
    private LocalDateTime submitTime;

    /**
     * transaction calculationRule(cardType,cardBand,cardRegion,cardUse3ds,cardInstallments)
     */
    private Map<String, String> calculationRule;

    /**
     * mark this order whether is a rejected_debit
     */
    private Boolean isRejectedDebit;


    private Boolean installmentFlag;

    /**
     * json Object
     * extend data
     */
    private JsonNode extendData;

    /**
     * additionalCharge
     */
    private List<AdditionalChargeBo> additionalCharge;

    /**
     * transaction fee list
     */
    private List<TransactionFeeBo> transactionFeeList;

    /**
     * transaction cost list
     */
    private List<TransactionCostBo> transactionCostList;

    /**
     * transaction extra fee list
     */
    private List<TransactionExtraIncomeBo> transactionExtraFeeList;
}
