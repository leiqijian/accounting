package com.liquido.transaction.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.OwnerEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;
import com.liquido.transaction.enums.ApprovalBizExchangeStatusEnum;
import com.liquido.transaction.enums.ExchangeAccountTypeEnum;
import com.liquido.transaction.enums.FeeModeEnum;
import com.liquido.transaction.pojo.bo.BeneficiaryAccountBo;

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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "approval_biz_exchange")
@Where(clause = "del_flag = false")
@EntityListeners({AuditingEntityListener.class})
public class ApprovalBizExchange implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "snowFlakeIdGenerator",
            strategy = "com.liquido.core.common.snowflake.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowFlakeIdGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * approval process
     */
    @OneToOne(targetEntity = Approval.class)
    @JoinColumn(name = "approval_id")
    private Approval approval;


    @Column(name = "transaction_id")
    private Long transactionId;

    /**
     * merchant info: id
     */
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    /**
     * sub merchant id
     */
    @Column(name = "sub_merchant_id")
    private String subMerchantId;

    /**
     * merchant info: code
     */
    @Column(name = "merchant_code", nullable = false)
    private String merchantCode;

    /**
     * merchant info: name
     */
    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Convert(converter = OwnerEnum.Convert.class)
    @Column(name = "owner", nullable = false)
    private OwnerEnum owner;

    /**
     * country: CountryCodeEnum
     */
    @Column(name = "country_code", nullable = false)
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * transaction type: TransactionTypeCodeEnum
     */
    @Column(name = "transaction_type_code", nullable = false)
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * account info : id
     */
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Convert(converter = ExchangeAccountTypeEnum.Convert.class)
    @Column(name = "exchange_account_type", nullable = false)
    private ExchangeAccountTypeEnum exchangeAccountType;

    /**
     * The source currency, such as BRL, MXN
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    @Column(name = "source_currency", nullable = false)
    private CurrencyEnum sourceCurrency;

    /**
     * The target currency, that merchants want to change, such as USD, EUR
     */
    @Convert(converter = CurrencyEnum.Convert.class)
    @Column(name = "target_currency", nullable = false)
    private CurrencyEnum targetCurrency;

    /**
     * exchange rate
     */
    @Column(name = "exchange_rate", nullable = false)
    private BigDecimal exchangeRate;

    /**
     * true: Manually modified exchange_rate
     * false: Real-time exchange rates are used
     */
    @Column(name = "custom_exchange_rate", nullable = false)
    private Boolean customExchangeRate;

    /**
     * Does the merchant need to confirm the exchange rate and amount in the contract
     */
    @Column(name = "need_confirm_contract", nullable = false)
    private Boolean needConfirmContract;

    /**
     * merchant ratio lose
     */
    @Column(name = "ratio_lose", nullable = false)
    private BigDecimal ratioLose;

    /**
     * the final rate used by the merchant
     */
    @Column(name = "merchant_rate", nullable = false)
    private BigDecimal merchantRate;

    /**
     * The rate at which money is transferred to the merchant
     */
    @Column(name = "currency_rate", nullable = false)
    private BigDecimal currencyRate;

    /**
     * merchant exchange amount (unit: cent)
     */
    @Column(name = "exchange_amount", nullable = false)
    private BigDecimal exchangeAmount;

    /**
     * merchant exchange amount converted into TargetCurrency (unit: cent)
     */
    @Column(name = "exchange_amount_target_currency", nullable = false)
    private BigDecimal exchangeAmountTargetCurrency;

    /**
     * merchant exchange amount converted into TargetCurrency (unit: cent)
     */
    @Column(name = "actual_exchange_amount_target_currency", nullable = false)
    private BigDecimal actualExchangeAmountTargetCurrency;

    /**
     * exchange currency
     */
    @Column(name = "exchange_currency", nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum exchangeCurrency;

    /**
     * The amount requested for exchange
     */
    @Column(name = "requested_amount", nullable = false)
    private BigDecimal requestedAmount;

    /**
     * The amount unit chosen by the merchant to deduct the balance of the account
     */
    @Column(name = "requested_currency", nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum requestedCurrency;

    @Column(name = "fee_flag", nullable = false)
    private Boolean feeFlag;

    @Column(name = "fee_mode", nullable = false)
    @Convert(converter = FeeModeEnum.Convert.class)
    private FeeModeEnum feeMode;

    @Column(name = "fee_value", nullable = false)
    private BigDecimal feeValue;

    @Column(name = "custom_fee_flag", nullable = false)
    private Boolean customFeeFlag;

    /**
     * paymentFee (unit: cent)
     */
    @Column(name = "payment_fee", nullable = false)
    private BigDecimal paymentFee;

    /**
     * paymentFeeCurrency
     */
    @Column(name = "payment_fee_currency", nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum paymentFeeCurrency;

    /**
     * The fee deducted must be in local currency (unit: cent)
     */
    @Column(name = "deduction_fee", nullable = false)
    private BigDecimal deductionFee;

    @Column(name = "deduction_fee_target_currency", nullable = false)
    private BigDecimal deductionFeeTargetCurrency;

    /**
     * smallTransferFee (unit: cent)
     */
    @Column(name = "small_fee_limit", nullable = false)
    private BigDecimal smallFeeLimit;

    /**
     * The samllFee configured in the approval config table (unit: cent)
     */
    @Column(name = "config_small_fee", nullable = false)
    private BigDecimal configSmallFee;

    /**
     * The small fee actually charged (unit: cent)
     */
    @Column(name = "small_transfer_fee", nullable = false)
    private BigDecimal smallTransferFee;

    @Column(name = "small_transfer_fee_currency", nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum smallTransferFeeCurrency;

    @Column(name = "extra_cost", nullable = false)
    private BigDecimal extraCost;

    /**
     * merchant beneficiary bank
     */
    @Type(type = "json")
    @Column(name = "merchant_account")
    private BeneficiaryAccountBo merchantAccount;

    /**
     * owner beneficiary bank
     */
    @Type(type = "json")
    @Column(name = "owner_account")
    private BeneficiaryAccountBo ownerAccount;

    @Column(name = "status", nullable = false)
    @Convert(converter = ApprovalBizExchangeStatusEnum.Convert.class)
    private ApprovalBizExchangeStatusEnum status;

    @Column(name = "fail_count")
    private Integer failCount;

    @Column(name = "contract_confirm_flag")
    private Boolean contractConfirmFlag;

    /**
     * merchant user info: id
     */
    @Column(name = "merchant_user_id")
    private Long merchantUserId;

    /**
     * merchant user info: name
     */
    @Column(name = "merchant_user_name")
    @SensitiveField(SensitiveType.REAL_NAME)
    private String merchantUserName;

    /**
     * merchant user info: email
     */
    @SensitiveField(SensitiveType.EMAIL)
    @Column(name = "merchant_user_email")
    private String merchantUserEmail;

    @Column(name = "merchant_remark")
    private String merchantRemark;

    @CreatedDate
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "del_flag")
    private Boolean delFlag;

}
