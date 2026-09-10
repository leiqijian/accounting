package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Convert;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.FeeValueModelEnum;
import com.liquido.base.enums.OperationMethodEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizFeeConfigurationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    @Convert(converter = OperationMethodEnum.Convert.class)
    private OperationMethodEnum operationMethod;

    private Long accountId;

    private String feeName;

    @Convert(converter = FeeTypeCodeEnum.Convert.class)
    private FeeTypeCodeEnum feeType;

    @Convert(converter = FeeValueModelEnum.Convert.class)
    private FeeValueModelEnum feeModel;

    @Convert(converter = FeeOnEnum.Convert.class)
    private FeeOnEnum feeOn;

    private BigDecimal feeValue;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private Integer version;

    private String remark;

}
