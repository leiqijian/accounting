package com.liquido.base.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.OperationMethodEnum;
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
@Table(name = "biz_fee_configuration")
@Where(clause = "del_flag = false")
public class BizFeeConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = IdGeneratorStrategy.SNOWFLAKE_GENERATOR)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "business_type")
    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    @Column(name = "operation_method")
    @Convert(converter = OperationMethodEnum.Convert.class)
    private OperationMethodEnum operationMethod;

    @Column(name = "fee_name")
    private String feeName;

    @Column(name = "fee_type")
    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeType;

    @Column(name = "fee_model")
    @Convert(converter = FeeValueModelEnum.Convert.class)
    private FeeValueModelEnum feeModel;

    @Column(name = "fee_on")
    @Convert(converter = FeeOnEnum.Convert.class)
    private FeeOnEnum feeOn;

    @Column(name = "fee_value")
    private BigDecimal feeValue;

    @Column(name = "min_amount")
    private BigDecimal minAmount;

    @Column(name = "max_amount")
    private BigDecimal maxAmount;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "version")
    private Integer version;

    @Column(name = "del_flag")
    private Boolean delFlag;

    @Column(name = "remark")
    private String remark;

}
