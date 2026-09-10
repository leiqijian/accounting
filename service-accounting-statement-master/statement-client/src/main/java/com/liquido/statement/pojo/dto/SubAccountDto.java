package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccountDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long merchantId;

    private List<Long> accountIds;

    private String subMerchantId;

    private String subMerchantName;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    private BigDecimal balance;

    private BigDecimal extractableBalance;

    private BigDecimal availableBalance;

    private BigDecimal unavailableBalance;

    private BigDecimal pendingBalance;

    private BigDecimal holdBalance;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private String timezone;

    private String timezoneName;
}
