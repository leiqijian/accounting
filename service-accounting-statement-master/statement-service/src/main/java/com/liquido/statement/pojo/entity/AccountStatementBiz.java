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

import com.liquido.base.enums.BizFinanceTypeEnum;
import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;

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
 * account statement biz
 */
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_statement_biz")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class AccountStatementBiz implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * request_id
     */
    @Column(name = "request_id")
    private String requestId;

    /**
     * transaction_id
     */
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "sub_merchant_id")
    private String subMerchantId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "bill_id")
    private Long billId;

    @Column(name = "business_type")
    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    @Column(name = "finance_type")
    @Convert(converter = BizFinanceTypeEnum.Convert.class)
    private BizFinanceTypeEnum financeType;

    /**
     * transaction time now(UTC+0)
     */
    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    /**
     * unit:cent
     */
    @Column(name = "extractable_amount")
    private BigDecimal extractableAmount;

    /**
     * unit:cent
     */
    @Column(name = "frozen_amount")
    private BigDecimal frozenAmount;

    /**
     * unit:cent
     */
    @Column(name = "exchange_amount")
    private BigDecimal exchangeAmount;

    @Column(name = "currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "version")
    private Integer version;

    @Column(name = "del_flag")
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;
}
