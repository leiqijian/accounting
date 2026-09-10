package com.liquido.statement.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

/**
 * transaction unhold
 */
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_unhold")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class TransactionUnHold implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * fk
     */
    @Column(name = "account_id")
    private Long accountId;

    /**
     * ref: account_daily_bill.id
     */
    @Column(name = "bill_id")
    private Long billId;

    /**
     * unhold_date(has been convert to account timezone)
     */
    @Column(name = "unhold_date")
    private LocalDate unholdDate;

    /**
     * unhold_time yyyy-MM-dd HH:mm:ss (UTC+0)
     */
    @Column(name = "unhold_time")
    private LocalDateTime unholdTime;

    /**
     * current batch unhold total amount
     * unit:cent
     */
    @Column(name = "unhold_amount")
    private BigDecimal unholdAmount;

    /**
     * can add to extractable balance amount
     * unit:cent
     */
    @Column(name = "unfreeze_amount")
    private BigDecimal unfreezeAmount;


    @Column(name = "currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * this batch unhold total transactionId list
     */
    @Type(type = "json")
    @Column(name = "unhold_list")
    private Set<Long> unholdList;

    /**
     * this batch can add to extractable balance transactionId list
     */
    @Type(type = "json")
    @Column(name = "unfreeze_list")
    private Set<Long> unfreezeList;

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

    @Column(name = "remark")
    private String remark;

}
