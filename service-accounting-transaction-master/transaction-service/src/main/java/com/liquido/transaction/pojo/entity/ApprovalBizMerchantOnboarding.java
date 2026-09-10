package com.liquido.transaction.pojo.entity;


import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.liquido.transaction.enums.ApprovalBizOnboardingStatusEnum;

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

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "approval_biz_merchant_onboarding")
@Where(clause = "del_flag = false")
@EntityListeners({AuditingEntityListener.class})
public class ApprovalBizMerchantOnboarding {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "snowFlakeIdGenerator",
            strategy = "com.liquido.core.common.snowflake.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowFlakeIdGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "merchant_storage_id", nullable = false)
    private Long merchantStorageId;

    @Column(name = "status", nullable = false)
    @Convert(converter = ApprovalBizOnboardingStatusEnum.Convert.class)
    private ApprovalBizOnboardingStatusEnum status;

    /**
     * approval process
     */
    @OneToOne(targetEntity = Approval.class)
    @JoinColumn(name = "approval_id")
    private Approval approval;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Column(name = "merchant_code", nullable = false)
    private String merchantCode;

    @Column(name = "merchant_uuid", nullable = false)
    private String merchantUuid;

    @CreatedDate
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "del_flag")
    private Boolean delFlag;

}
