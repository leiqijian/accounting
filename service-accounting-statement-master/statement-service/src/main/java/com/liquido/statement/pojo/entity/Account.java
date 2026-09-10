package com.liquido.statement.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * account
 */
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "accountConfig", attributeNodes = {
                @NamedAttributeNode("accountConfig")
        }),
        @NamedEntityGraph(name = "noJoins", attributeNodes = {

        })
})
@EntityListeners({AuditingEntityListener.class})
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    /**
     * fk
     */
    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "account_config_id")
    private Long accountConfigId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_config_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    private AccountConfig accountConfig;

    /**
     * CountryCodeEnum: BR/MX/US
     */
    @Column(name = "country_code", nullable = false)
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Column(name = "transaction_type_code", nullable = false)
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * yesterday account daily end balance
     * unit:cent
     */
    @Column(name = "latest_daily_balance")
    private BigDecimal latestDailyBalance;

    /**
     * current day account daily total occurred amount(No daily-cut settlement)
     * unit:cent
     */
    @Column(name = "sub_total_amount")
    private BigDecimal subTotalAmount;

    @Column(name = "sub_total_count")
    private Long subTotalCount;

    /**
     * yesterday account daily extractable end balance
     * unit:cent
     */
    @Column(name = "latest_daily_extractable_balance")
    private BigDecimal latestDailyExtractableBalance;

    /**
     * account extractable balance
     * unit:cent
     */
    @Column(name = "extractable_balance")
    private BigDecimal extractableBalance;

    /**
     * Record the currently frozen amount in extractable balance
     * unit:cent
     */
    @Column(name = "frozen_amount")
    private BigDecimal frozenAmount;

    /**
     * Record the currently frozen amount in exchange balance
     * unit:cent
     */
    @Column(name = "exchange_amount")
    private BigDecimal exchangeAmount;

    /**
     * monthly holding limit amount
     * unit :cent
     */
    @Column(name = "holding_limit")
    private BigDecimal holdingLimit;

    @Column(name = "currency", nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    /**
     * account local time zone
     */
    @Column(name = "timezone", nullable = false)
    private String timezone;

    @Column(name = "timezone_name", nullable = false)
    private String timezoneName;

    @Column(name = "created_time")
    private LocalDateTime createdTime;


    @CreatedDate
    @LastModifiedDate
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * JPA Use javax.persistence.@Version achieve optimistic locking
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * 0-normal，1-delete
     */
    @Column(name = "del_flag")
    private Boolean delFlag;

}
