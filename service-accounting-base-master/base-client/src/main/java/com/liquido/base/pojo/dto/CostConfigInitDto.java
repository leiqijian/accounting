package com.liquido.base.pojo.dto;


import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.FeeOnEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostConfigInitDto {

    @Convert(converter = VendorCodeEnum.Convert.class)
    private List<VendorCodeEnum> vendorCodeList;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private List<ProductCodeEnum> productCodeList;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private List<FeeTypeCodeEnum> feeTypeCodeList;

    @Convert(converter = FeeOnEnum.Convert.class)
    private List<FeeOnEnum> feeOnList;

}
