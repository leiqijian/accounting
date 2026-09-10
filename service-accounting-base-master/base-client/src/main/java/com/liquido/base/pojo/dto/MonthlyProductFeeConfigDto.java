package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.util.List;

import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyProductFeeConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private ProductCodeEnum productCode;

    /**
     * product monthly fee configs
     */
    private List<MonthlyFeeConfigurationDto> productFeeConfigList;

}
