package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

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
public class DailyCutSuccessBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long billId;
    private Long accountId;
    private Long merchantId;
    private String timezone;
    private LocalDate billDate;
    private CountryCodeEnum countryCode;
    private TransactionTypeCodeEnum transactionTypeCode;

    /**
     * extractable amount info;
     */
    private DailyExtractableAmountInfo dailyExtractableAmountInfo;

    /**
     * The real-time balance after the corresponding daily switch is successful
     */
    private BigDecimal dailyEndBalance;

    /**
     * Yesterday's total trading volume
     * Only statistics the total transaction volume of merchants yesterday
     */
    private BigDecimal latestDailyTransactionVolume;

    /**
     * Total amount of transactions occurred yesterday;
     * statistics all the total transaction volume of merchants yesterday
     */
    private BigDecimal latestDailyOccurredAmount;

    /**
     * Total count of transactions occurred yesterday;
     */
    private Long latestDailyOccurredCount;

    private CurrencyEnum currency;

    private Long transactionCount;
}
