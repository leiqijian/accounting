package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryFeeConfigInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long accountId;

    private List<Long> accountIds;

    private Long accountProductId;

    private Integer accountFeeVersion;

    private List<Long> accountProductIds;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private ProductCodeEnum productCode;

}
