package com.liquido.statement.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.AccountingScheduleStateEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounting_schedule")
public class AccountingSchedule implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "account_id")
    private Long accountId;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "card_brand")
    private String cardBrand;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "unique_id")
    private String uniqueId;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "accounting_date")
    private LocalDate accountingDate;

    @Column(name = "settlement_amount")
    private BigDecimal settlementAmount;

    @Column(name = "accounting_amount")
    private BigDecimal accountingAmount;

    @Column(name = "fee_amount")
    private BigDecimal feeAmount;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    @Column(name = "currency")
    private CurrencyEnum currency;

    @Column(name = "current_installment")
    private Integer currentInstallment;

    @Column(name = "total_installment")
    private Integer totalInstallment;

    @Convert(converter = AccountingScheduleStateEnum.Convert.class)
    @Column(name = "state")
    private AccountingScheduleStateEnum state;

    // UTC+0
    @Column(name = "actual_accounting_time")
    private LocalDateTime actualAccountingTime;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * JPA Use javax.persistence.@Version achieve optimistic locking
     */
    //@Version
    @Column(name = "version")
    private Integer version;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;
}
