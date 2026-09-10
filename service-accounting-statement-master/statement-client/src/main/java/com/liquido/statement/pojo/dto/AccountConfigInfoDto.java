package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.statement.pojo.bo.AccountConfigData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountConfigInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long merchantId;

    private Long accountId;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Type(type = "json")
    private AccountConfigData configData;

}
