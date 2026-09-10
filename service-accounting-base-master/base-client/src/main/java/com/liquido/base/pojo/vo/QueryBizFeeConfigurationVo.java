package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.OperationMethodEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryBizFeeConfigurationVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long accountId;

    @NotNull
    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    @Convert(converter = OperationMethodEnum.Convert.class)
    private OperationMethodEnum operationMethod;

}
