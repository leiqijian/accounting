package com.liquido.statement.pojo.dto;


import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple account information(excluding account balance and other information)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBasicInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long merchantId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private String timezone;

    private String timezoneName;

    private LocalDateTime createdTime;

}
