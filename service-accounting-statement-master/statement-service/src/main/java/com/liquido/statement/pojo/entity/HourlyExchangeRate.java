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
import javax.persistence.Version;

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

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hourly_exchange_rate")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class HourlyExchangeRate implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "source_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum sourceCurrency;

    @Column(name = "target_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum targetCurrency;

    /**
     * exchange datetime(UTC+0),
     * format: yyyy-MM-dd HH:00:00
     */
    @Column(name = "exchange_time")
    private LocalDateTime exchangeTime;

    @Column(name = "exchange_rate", nullable = false)
    private BigDecimal exchangeRate;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    /**
     * JPA Use javax.persistence.@Version achieve optimistic locking
     */
    @Version
    @Column(name = "version")
    private Integer version;

    /**
     * 0-normal,1-delete
     */
    @Column(name = "del_flag", nullable = false)
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;
}
