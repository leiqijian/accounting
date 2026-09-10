package com.liquido.transaction.pojo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.enums.ApprovalTypeEnum;
import com.liquido.transaction.pojo.bo.ApprovalUserInfoBo;

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
@Table(name = "approval")
@Where(clause = "del_flag = false")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@EntityListeners({AuditingEntityListener.class})
public class Approval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "snowFlakeIdGenerator",
            strategy = "com.liquido.core.common.snowflake.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowFlakeIdGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * approval type: TOPUP/TRANSFER_OUT
     */
    @Column(name = "type", nullable = false)
    @Convert(converter = ApprovalTypeEnum.Convert.class)
    private ApprovalTypeEnum type;

    /**
     * Approval status
     */
    @Column(name = "status", nullable = false)
    @Convert(converter = ApprovalStatusEnum.Convert.class)
    private ApprovalStatusEnum status;

    /**
     * Instances of each approval in lark
     */
    @Column(name = "instance_code", nullable = false)
    private String instanceCode;

    /**
     * appendix
     */
    @Column(name = "appendix_ids")
    @Type(type = "json")
    private List<Long> appendixIds;

    /**
     * remark
     */
    @Type(type = "json")
    private List<ApprovalUserInfoBo> remark;


    @Column(name = "revoke_flag", nullable = false)
    private Boolean revokeFlag;


    @CreatedDate
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "del_flag")
    private Boolean delFlag;

}
