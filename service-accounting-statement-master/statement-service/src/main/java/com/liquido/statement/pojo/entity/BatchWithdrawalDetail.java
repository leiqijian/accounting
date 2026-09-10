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

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
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
 * batch withdrawal detail
 */

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "batch_withdrawal_detail")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class BatchWithdrawalDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * FK ref: batch_withdrawal_apply.batch_id
     */
    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "unique_id")
    private String uniqueId;

    /**
     * FK ref: task_fee_calculation.id
     */
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "sub_merchant_id")
    private String subMerchantId;

    @Column(name = "sub_merchant_name")
    private String subMerchantName;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "sub_account_id")
    private Long subAccountId;

    @Column(name = "direction_type")
    @Convert(converter = DirectionTypeEnum.Convert.class)
    private DirectionTypeEnum directionType;

    @Column(name = "credited_date")
    private LocalDate creditedDate;

    @Column(name = "credited_amount")
    private BigDecimal creditedAmount;

    @Column(name = "settlement_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    @Column(name = "apply_date")
    private LocalDate applyDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    //UTC zone
    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    //state: 0: init; 1: processing; 2:success; 3: fail;
    @Column(name = "state")
    private Integer state;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

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
}
