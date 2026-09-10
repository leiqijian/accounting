package com.liquido.statement.pojo.entity;

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
@Table(name = "account_in_progress")
@Where(clause = "del_flag = false")
public class AccountInProgress {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "in_progress_amount")
    private BigDecimal inProgressAmount;

    @Column(name = "in_progress_currency")
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum inProgressCurrency;

    //pre calculate fee currency =account currency
    @Column(name = "fee", nullable = false)
    private BigDecimal fee;

    //pre calculate tax currency =account currency
    @Column(name = "tax", nullable = false)
    private BigDecimal tax;

    //pre-calculate amount netAmount=amount/rate + fee + tax, currency =account currency
    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

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
