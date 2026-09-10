package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class PageSubAccountDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long merchantId;

    private String subMerchantId;

    private String subMerchantName;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

    private LocalDate date;

    private BigDecimal balance;

    private BigDecimal availableBalance;

    private BigDecimal unavailableBalance;

    private BigDecimal pendingBalance;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;


}
