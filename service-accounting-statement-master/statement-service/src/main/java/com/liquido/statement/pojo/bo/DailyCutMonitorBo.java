package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.time.LocalDate;
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
 * account_daily_bill
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyCutMonitorBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long billId;
    private Long accountId;
    private Long merchantId;
    private String merchantName;
    private String timezone;

    private LocalDate billDate;
    private LocalDateTime executeTime;
    private LocalDateTime executeTimeRelativeToUtc8;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

}
