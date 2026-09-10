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

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.GlobalTargetTypeEnum;
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

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "global_statement")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class GlobalStatement implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * reference global_account.id
     */
    @Column(name = "global_account_id")
    private Long globalAccountId;

    /**
     * reference account.id, when targetType=SELF then set subAccountId=0
     */
    @Column(name = "sub_account_id")
    private Long subAccountId;

    @Column(name = "business_type")
    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    @Column(name = "target_type")
    @Convert(converter = GlobalTargetTypeEnum.Convert.class)
    private GlobalTargetTypeEnum targetType;

    /**
     * transaction amount, unit:cent
     */
    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "start_balance")
    private BigDecimal startBalance;

    @Column(name = "end_balance")
    private BigDecimal endBalance;

    @Column(name = "currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * JPA Use javax.persistence.@Version achieve optimistic locking
     */
    @Column(name = "version")
    private Integer version;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;
}
