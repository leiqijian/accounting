package com.liquido.transaction.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.transaction.enums.ApprovalBizTransferOutStatusEnum;
import com.liquido.transaction.enums.ApprovalBizBatchWithdrawalStatusEnum;

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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "approval_biz_batch_withdrawal")
@Where(clause = "del_flag = false")
@EntityListeners({AuditingEntityListener.class})
public class ApprovalBizBatchWithdrawal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "snowFlakeIdGenerator",
            strategy = "com.liquido.core.common.snowflake.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowFlakeIdGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(targetEntity = Approval.class)
    @JoinColumn(name = "approval_id", nullable = false)
    private Approval approval;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "merchant_code", nullable = false)
    private String merchantCode;

    @Column(name = "country_code", nullable = false)
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "withdrawal_amount", nullable = false)
    private BigDecimal withdrawalAmount;

    @Column(name = "settlement_currency", nullable = false)
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    @Column(name = "withdrawal_count", nullable = false)
    private Integer withdrawalCount;

    @Column(name = "apply_date", nullable = false)
    private LocalDate applyDate;

    @Column(name = "completed_date", nullable = false)
    private LocalDate completedDate;

    @Column(name = "completed_time", nullable = false)
    private LocalDateTime completedTime;

    @Column(name = "status", nullable = false)
    @Convert(converter = ApprovalBizBatchWithdrawalStatusEnum.Convert.class)
    private ApprovalBizBatchWithdrawalStatusEnum status;

    @Column(name = "fail_count")
    private Integer failCount;

    @CreatedDate
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "remark", nullable = false)
    private String remark;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "del_flag")
    private Boolean delFlag;

}
