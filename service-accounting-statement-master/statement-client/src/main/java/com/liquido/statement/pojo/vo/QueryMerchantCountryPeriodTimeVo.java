package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryMerchantCountryPeriodTimeVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long merchantId;

    /**
     * switch country
     */
    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    private LocalDate startDate;

    private LocalDate endDate;
}
