package com.liquido.base.pojo.vo;


import java.io.Serializable;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
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
public class ApmCostConfigVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long accountId;
    @NotNull
    private CountryCodeEnum country;
    @NotNull
    private TransactionTypeCodeEnum transactionType;
    @NotNull
    private VendorCodeEnum vendor;
    @NotNull
    private ProductCodeEnum productCode;

    private Integer activeVersion;
}
