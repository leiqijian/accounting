package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.FeeCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryMonthFeeConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * eg. yyyyMM
     */
    private Integer activeMonth;

    private Long accountId;

    private List<Long> accountIds;

    private Long accountProductId;

    private List<Long> accountProductIds;

    private Integer monthlyFeeVersion;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

    @Convert(converter = FeeCodeEnum.Convert.class)
    private FeeCodeEnum feeCode;

}
