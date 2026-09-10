package com.liquido.statement.pojo.entity;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.snowflake.IdGeneratorStrategy;
import com.liquido.statement.convert.ListCurrencyConvert;
import com.liquido.statement.pojo.dto.AccountConfigDto;

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
@Table(name = "account_balance_snapshot")
@Where(clause = "del_flag = false")
@SuppressWarnings("PMD.TooManyFields")
public class AccountBalanceSnapshot {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "merchant_code", length = 32, nullable = false)
    private String merchantCode;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Column(name = "country_code", nullable = false)
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Column(name = "transaction_type_code", nullable = false)
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Column(name = "account_config")
    @Convert(converter = AccountConfigDto.Convert.class)
    private AccountConfigDto accountConfig;

    @Column(name = "latest_daily_balance")
    private BigDecimal latestDailyBalance;

    @Column(name = "sub_total_amount")
    private BigDecimal subTotalAmount;

    @Column(name = "total_balance")
    private BigDecimal totalBalance;

    @Column(name = "extractable_balance")
    private BigDecimal extractableBalance;

    @Column(name = "exchange_rate_currency")
    @Convert(converter = ListCurrencyConvert.class)
    private List<CurrencyEnum> exchangeRateCurrency;

    @Column(name = "holding_limit")
    private BigDecimal holdingLimit;

    @Column(name = "holding_amount")
    private BigDecimal holdingAmount;

    @Column(name = "available_amount")
    private BigDecimal availableAmount;

    @Column(name = "unavailable_amount")
    private BigDecimal unavailableAmount;

    @Column(name = "pending_amount")
    private BigDecimal pendingAmount;

    @Column(name = "risk_reserve_hold_amount")
    private BigDecimal riskReserveHoldAmount;

    @Column(name = "legal_hold_amount")
    private BigDecimal legalHoldAmount;

    @Column(name = "currency", length = 3, nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "timezone_name")
    private String timezoneName;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "del_flag")
    private Boolean delFlag;

    @Column(name = "remark", length = 200)
    private String remark;
}
