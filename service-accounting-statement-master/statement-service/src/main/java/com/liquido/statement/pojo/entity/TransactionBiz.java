package com.liquido.statement.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.AmountPonEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.OperateModeEnum;
import com.liquido.base.enums.OperateSourceEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;
import com.liquido.statement.pojo.vo.TransactionBizExtendInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
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
@Table(name = "transaction_biz")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class TransactionBiz implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "bill_id", nullable = false)
    private Long billId;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "sub_merchant_id")
    private String subMerchantId;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "payment_config_id")
    private Long paymentConfigId;

    @Column(name = "transaction_type", nullable = false)
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionType;

    @Column(name = "business_type", nullable = false)
    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    @Column(name = "payment_channel", nullable = false)
    @Convert(converter = PaymentChannelEnum.Convert.class)
    private PaymentChannelEnum paymentChannel;

    @Column(name = "operate_source", nullable = false)
    @Convert(converter = OperateSourceEnum.Convert.class)
    private OperateSourceEnum operateSource;

    @Column(name = "operate_mode", nullable = false)
    @Convert(converter = OperateModeEnum.Convert.class)
    private OperateModeEnum operateMode;

    /**
     * AmountPonEnum, positive or negative;
     * positive: +1 ; negative: -1
     */
    @Column(name = "amount_pon")
    @Convert(converter = AmountPonEnum.Convert.class)
    private AmountPonEnum amountPon;

    /**
     * unit:cent
     */
    @Column(name = "transaction_amount", nullable = false)
    private BigDecimal transactionAmount;

    /**
     * unit:cent
     */
    @Column(name = "transaction_amount_usd", nullable = false)
    private BigDecimal transactionAmountUsd;


    @Column(name = "transaction_currency", nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum transactionCurrency;

    /**
     * Order currency exchange to account currency fxRate
     * e.g:
     * when orderCurrency=MXN, accountCurrency=MXN, then exchangeRate=1.0;
     * when orderCurrency=MXN, accountCurrency=USD, then exchangeRate ≈ 18.120650;
     */
    @Column(name = "exchange_rate", nullable = false)
    private BigDecimal exchangeRate;

    /**
     * Order currency exchange to account currency fxLose
     */
    @Column(name = "exchange_lose", nullable = false)
    private BigDecimal exchangeLose;

    /**
     * Order currency exchange to USD fxRate
     * e.g: The orderCurrency(MXN) to USD, then exchangeRateUsd ≈ 18.120650;
     */
    @Column(name = "exchange_rate_usd", nullable = false)
    private BigDecimal exchangeRateUsd;

    /**
     * unit:cent
     */
    @Column(name = "settlement_amount", nullable = false)
    private BigDecimal settlementAmount;

    /**
     * unit:cent
     */
    @Column(name = "settlement_amount_usd", nullable = false)
    private BigDecimal settlementAmountUsd;

    @Column(name = "settlement_currency", nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    /**
     * unit:cent
     */
    @Column(name = "fee_amount")
    private BigDecimal feeAmount;

    /**
     * unit:cent
     */
    @Column(name = "fee_amount_usd")
    private BigDecimal feeAmountUsd;

    /**
     * unit:cent
     */
    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    /**
     * unit:cent
     */
    @Column(name = "tax_amount_usd")
    private BigDecimal taxAmountUsd;

    @Column(name = "settlement_status", nullable = false)
    @Convert(converter = SettleStatusEnum.Convert.class)
    private SettleStatusEnum settlementStatus;

    /**
     * UTC+0
     */
    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    /**
     * UTC+0
     */
    @Column(name = "settlement_time")
    private LocalDateTime settlementTime;

    @Column(name = "comments")
    private String comments;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "extend_info")
    @Convert(converter = TransactionBizExtendInfo.Convert.class)
    private TransactionBizExtendInfo extendInfo;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "del_flag")
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;
}
