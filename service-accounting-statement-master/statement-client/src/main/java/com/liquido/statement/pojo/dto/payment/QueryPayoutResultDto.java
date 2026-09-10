package com.liquido.statement.pojo.dto.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Country: Mexico
 * SpeiPayment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class QueryPayoutResultDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The idempotencyKey
     */
    private String orderId;

    /**
     * The transaction id
     */
    private String transactionId;

    /**
     * 200 Success,
     * 401 Access denied,
     * 422 Required fields missing or invalid data,
     * 500 internal error
     */
    private Integer statusCode;

    /**
     * error message if failed
     */
    private String errorMsg;

    /**
     * Transfer status code, 200 transaction SETTLED or IN_PROGRESS, other FAILED or REJECTED
     */
    private Integer transferStatusCode;

    /**
     * Transfer  Transfer status, enum value, SETTLED, IN_PROGRESS, FAILED or REJECTED
     */
    private String transferStatus;

    /**
     * Transfer error message if failed
     */
    private String transferErrorMsg;

    /**
     * country code, enum value as BR
     */
    private CountryCodeEnum country;

    /**
     * currency code, enum value as MXN
     */
    private CurrencyEnum currency;

    /**
     * The transfer amount. note
     * unit: cent
     */
    private BigDecimal amount;

    /**
     * payment record create time
     * timezone: UTC 0
     */
    private LocalDateTime createTime;

    /**
     * Transfer final status update time, final status include SETTLED, FAILED and REJECTED
     * timezone: UTC 0
     */
    private LocalDateTime finalStatusTime;

    /**
     * Target account holder full name
     */
    private String targetName;

    /**
     * Target Bank AccountId.
     */
    private String targetBankAccountId;
    /**
     * payment target Info
     */
    private Map<String, Object> targetInfo;
}
