package com.liquido.base.pojo.vo;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceApplyingItemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @NotNull
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private Set<TransactionTypeCodeEnum> transactionTypeCodeList;

}
