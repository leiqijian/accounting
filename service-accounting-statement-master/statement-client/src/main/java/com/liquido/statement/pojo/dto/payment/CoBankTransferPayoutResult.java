package com.liquido.statement.pojo.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Country: Mexico
 * SpeiPayment
 */
@SuppressWarnings("PMD.TooManyFields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CoBankTransferPayoutResult implements BasePayoutResult {
    private static final long serialVersionUID = 1L;
    /**
     * Unique key to ensure idempotent requests
     */
    private String idempotencyKey;
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
     * Transfer  Transfer status, enum value, SETTLED, IN_PROGRESS, FAILED or REJECTED
     */
    private String transferStatus;
    /**
     * Transfer status code, 200 transaction SETTLED or IN_PROGRESS, other FAILED or REJECTED
     */
    private Integer transferStatusCode;
    /**
     * Transfer error message if failed
     */
    private String transferErrorMsg;
    /**
     * country code, enum value as BR
     */
    private CountryCodeEnum country;

    /**
     * @deprecated The transfer amount. note: 1=1COP
     * use: amountInCents
     */
    @Deprecated
    private BigDecimal amount;

    /**
     * The transfer amount. note: 100=1COP
     */
    private BigDecimal amountInCents;

    /**
     * The currency code of the transferred fund. enum value as COP
     */
    private CurrencyEnum currency;

    /**
     * payment record create time
     * format: 2022-01-14 07:00:31 GMT+08:00
     */
    private String createTime;
    /**
     * payment record create time
     * utc0
     */
    private LocalDateTime createTimeUtc;
    /**
     * Transfer final status update time, final status include SETTLED, FAILED and REJECTED
     * format: 2022-01-14 07:00:45 GMT+08:00
     */
    private String finalStatusTime;
    /**
     * transfer final status update time
     * utc0
     */
    private LocalDateTime finalStatusTimeUtc;
    /**
     * Target account holder full name
     */
    private String targetName;
    /**
     * Target account holder identity.
     */
    private String targetDocument;
    /**
     * Target Bank Code.
     */
    private String targetBankCode;
    /**
     * Target Bank AccountId.
     */
    private String targetBankAccountId;

    /**
     * Target Bank Agency Code.
     */
    private String targetBankAgency;

    @Override
    public ProductCodeEnum getProductCode() {
        return ProductCodeEnum.CO_BANK_TRANSFER;
    }

}
