package com.liquido.base.pojo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.OwnerEnum;
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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@SuppressWarnings("PMD.TooManyFields")
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "merchant")
@Where(clause = "del_flag = false")
@EntityListeners(AuditingEntityListener.class)
public class Merchant implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "industry")
    private String industry;

    @Column(name = "logo_icon")
    private String logoIcon;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "report_weight")
    private Integer reportWeight;

    @Column(name = "inner_flag")
    private Boolean innerFlag;

    @Convert(converter = OwnerEnum.Convert.class)
    @Column(name = "owner", nullable = false)
    private OwnerEnum owner;

    @Column(name = "merger_account")
    private Boolean mergerAccount;

    @CreatedDate
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    /* 0-normal，1-delete */
    @Column(name = "del_flag")
    private Boolean delFlag;

}
