package com.liquido.statement.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.TradingModelEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;
import com.liquido.statement.convert.ListAdditionalChargeConvert;
import com.liquido.statement.pojo.bo.AdditionalCharge;
import com.liquido.statement.pojo.vo.ExtendData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

/**
 * transaction_fee
 */
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_money")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class TransactionMoney implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * from global transaction system uniqueId
     * reference task_fee_calculation.unique_id
     */
    @Column(name = "unique_id", nullable = false)
    private String uniqueId;

    /**
     * reference task_fee_calculation.id
     */
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "merchant_id")
    private Long merchantId;

    /**
     * subMerchantId
     */
    @Column(name = "sub_merchant_id")
    private String subMerchantId;

    @Column(name = "account_id")
    private Long accountId;

    @SensitiveField(SensitiveType.SHIELD)
    @Column(name = "document_id")
    private String documentId;

    /**
     * When exchange money required
     */
    @Column(name = "fx_rate_id")
    private Long fxRateId;

    @Column(name = "vendor")
    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;
    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Column(name = "transaction_type_code")
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * ProductEnum
     */
    @Column(name = "product_code")
    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    /**
     * DirectionTypeEnum
     */
    @Column(name = "direction_type")
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @Column(name = "amount_pon")
    private BigDecimal amountPon;

    /**
     * original transaction amount, unit: cent
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * original transaction currency
     */
    @Column(name = "currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * Exchange rate from order to accountCurrency
     */
    @Column(name = "fx_rate")
    private BigDecimal fxRate;

    /**
     * Exchange rate from order to USD
     */
    @Column(name = "fx_rate_usd")
    private BigDecimal fxRateUsd;

    /**
     * Exchange lose from order to USD
     */
    @Column(name = "fx_lose_usd")
    private BigDecimal fxLoseUsd;

    /**
     * The settlement amount is only transferred (fees and taxes non-deduct)
     * unit: cent
     */
    @Column(name = "settlement_amount")
    private BigDecimal settlementAmount;

    /**
     * Amount converted into USD, unit: cent (fees and taxes non-deduct)
     * unit: cent
     */
    @Column(name = "settlement_amount_usd")
    private BigDecimal settlementAmountUsd;

    /**
     * target transaction currency
     */
    @Column(name = "settlement_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    /**
     * StatusEnum: WAITING/PROCESSING/SUCCESS/FAILED
     */
    @Column(name = "settle_status")
    @Convert(converter = SettleStatusEnum.Convert.class)
    private SettleStatusEnum settleStatus;

    /**
     * TradingModelEnum
     */
    @Column(name = "trading_model")
    @Convert(converter = TradingModelEnum.Convert.class)
    private TradingModelEnum tradingModel;

    /**
     * to be credited date(settled at date of merchant timezone)
     */
    @Column(name = "be_credited_date")
    private LocalDate beCreditedDate;

    /**
     * to be credited amount(deducted fees and tax)
     * unit:cent
     */
    @Column(name = "be_credited_amount")
    private BigDecimal beCreditedAmount;

    /**
     * additional charge
     * such as: "PENALTY_INTEREST" ...
     * amount unit:cent
     */
    @Column(name = "additional_charge")
    @Convert(converter = ListAdditionalChargeConvert.class)
    private List<AdditionalCharge> additionalCharge;

    /**
     * holdStatus: 0:NORMAL; 1:HOLD/LOCKED; 2:UNHOLD/UNLOCKED
     */
    @Column(name = "hold_status", nullable = false)
    @Convert(converter = HoldStatusEnum.Convert.class)
    private HoldStatusEnum holdStatus;

    @Column(name = "installment_flag")
    private Boolean installmentFlag;

    /**
     * The original task_fee_calculation.transaction_time UTC+0
     */
    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    /**
     * The original task_fee_calculation.transaction_time UTC+0
     */
    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "settle_time")
    private LocalDateTime settleTime;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "bill_id")
    private Long billId;

    /**
     * transaction calculationRule(cardType,cardBand,cardRegion,cardUse3ds,cardInstallments)
     */
    @Type(type = "json")
    @Column(name = "calculation_rule")
    private Map<String, String> calculationRule;

    @Column(name = "extend_data")
    @Convert(converter = ExtendData.Convert.class)
    private ExtendData extendData;

    /**
     * JPA Use javax.persistence.@Version achieve optimistic locking
     */
    @Version
    @Column(name = "version")
    private Integer version;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;

}
