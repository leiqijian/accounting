package com.liquido.transaction.pojo.entity;


import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
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
@Table(name = "approval_biz_kyc")
@Where(clause = "del_flag = false")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@EntityListeners({AuditingEntityListener.class})
public class ApprovalBizKyc {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "snowFlakeIdGenerator",
            strategy = "com.liquido.core.common.snowflake.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowFlakeIdGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "link_id", nullable = false)
    private Long linkId;

    /**
     * approval process
     */
    @OneToOne(targetEntity = Approval.class)
    @JoinColumn(name = "approval_id")
    private Approval approval;

    @Column(name = "full_access_link", nullable = false)
    private String fullAccessLink;

    @Column(name = "form_submission_time", nullable = false)
    private LocalDateTime formSubmissionTime;

    @Column(name = "merchant_trading_name", nullable = false)
    private String merchantTradingName;

    @Column(name = "merchant_incorporation_country", nullable = false)
    private String merchantIncorporationCountry;

    @Column(name = "processing_countries")
    @Type(type = "json")
    private JsonNode processingCountries;

    @Column(name = "owner_teams")
    @Type(type = "json")
    private JsonNode ownerTeams;

    @CreatedDate
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "del_flag")
    private Boolean delFlag;

}
