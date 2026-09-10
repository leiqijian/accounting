package com.liquido.worker.pojo.vo;


import java.io.Serializable;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralCostConfigVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long transactionId;

    private String merchantCode;

    private CountryCodeEnum country;

    private TransactionTypeCodeEnum transactionType;

    private ProductCodeEnum productCode;

    private DirectionTypeEnum directionType;

    private Long accountId;

    private VendorCodeEnum vendor;
}
