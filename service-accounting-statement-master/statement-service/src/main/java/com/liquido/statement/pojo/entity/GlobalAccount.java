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

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;
import com.liquido.statement.enums.GlobalAccountStateEnum;

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
@Table(name = "global_account")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class GlobalAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "merchant_id")
    private Long merchantId;

    /**
     * global balance, unit:cent
     */
    @Column(name = "global_balance")
    private BigDecimal globalBalance;

    @Column(name = "currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * state: 0-DISABLE，1-ENABLE
     */
    @Column(name = "state")
    @Convert(converter = GlobalAccountStateEnum.Convert.class)
    private GlobalAccountStateEnum state;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * JPA Use javax.persistence.@Version achieve optimistic locking
     */
    //@Version
    @Column(name = "version")
    private Long version;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;
}
