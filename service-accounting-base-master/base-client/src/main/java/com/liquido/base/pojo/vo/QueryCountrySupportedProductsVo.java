package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;

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
public class QueryCountrySupportedProductsVo implements Serializable {

    private static final long serialVersionUID = -1400271735894858972L;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionType;

}
