package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDate;
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
public class QueryCountryDailyBillStatisticsVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * CountryCodeEnum: BR/MX/US
     */
    @Convert(converter = CountryCodeEnum.Convert.class)
    @NotNull
    private CountryCodeEnum countryCode;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT
     */
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    @NotNull
    private TransactionTypeCodeEnum transactionTypeCode;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

}
