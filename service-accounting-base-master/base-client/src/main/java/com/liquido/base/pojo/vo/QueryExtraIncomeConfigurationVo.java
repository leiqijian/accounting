package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.validation.constraints.Min;
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
public class QueryExtraIncomeConfigurationVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(0)
    private Long accountId;

    @NotNull
    private CountryCodeEnum countryCode;

    @NotNull
    private TransactionTypeCodeEnum transactionTypeCode;
}
