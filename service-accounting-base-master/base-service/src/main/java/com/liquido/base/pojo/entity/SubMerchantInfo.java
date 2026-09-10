package com.liquido.base.pojo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.OwnerEnum;
import com.liquido.base.pojo.vo.ServiceApplyingItemVo;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;

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

@SuppressWarnings("PMD.TooManyFields")
@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sub_merchant_info")
@Where(clause = "del_flag = false")
public class SubMerchantInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     **/
    @Id
    @GenericGenerator(name = "snowFlakeIdGenerator",
            strategy = "com.liquido.core.common.snowflake.SnowFlakeIdGenerator")
    @GeneratedValue(generator = "snowFlakeIdGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    /**
     * merchant info: code
     **/
    @Column(name = "merchant_code", nullable = false)
    private String merchantCode;

    /**
     * merchant info: name
     **/
    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Convert(converter = OwnerEnum.Convert.class)
    @Column(name = "owner", nullable = false)
    private OwnerEnum owner;

    /**
     * Legal Name
     **/
    @SensitiveField(SensitiveType.REAL_NAME)
    @Column(name = "legal_name", nullable = false)
    private String legalName;

    /**
     * Commercial Name
     **/
    @Column(name = "commercial_name", nullable = false)
    private String commercialName;

    /**
     * Tax ID
     **/
    @Column(name = "tax_id", nullable = false)
    private String taxId;

    /**
     * Country of Incorporation
     **/
    @Column(name = "incorporation_country", nullable = false)
    private String incorporationCountry;

    /**
     * Website/App Download Link
     **/
    @Column(name = "website", nullable = false)
    @Type(type = "json")
    private List<String> website;

    @Column(name = "service_applying", nullable = false)
    @Type(type = "json")
    private List<ServiceApplyingItemVo> serviceApplying;

    @Column(name = "industry", nullable = false)
    private String industry;

    @Column(name = "cards_appicable", nullable = false)
    @Type(type = "json")
    private List<CountryCodeEnum> cardsAppicable;

    @Column(name = "sub_merchant_id", nullable = false)
    private String subMerchantId;

    /**
     * merchant remark
     **/
    @Column(name = "merchant_remark", nullable = false)
    private String merchantRemark;

    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @LastModifiedDate
    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @Column(name = "del_flag", nullable = false)
    private Boolean delFlag;

}
