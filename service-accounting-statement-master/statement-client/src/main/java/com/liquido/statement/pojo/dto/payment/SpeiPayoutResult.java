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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("PMD.TooManyFields")
public class SpeiPayoutResult implements BasePayoutResult {
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
     * The transfer amount. note: 1 = 1MXN
     */
    private BigDecimal amount;
    /**
     * The transfer amount. note: 100 = 1MXN
     */
    private BigDecimal amountInCents;
    /**
     * country code, enum value as BR
     */
    private CountryCodeEnum country;
    /**
     * The transfer amount. note: 1= 1BRL
     */
    private CurrencyEnum currency;
    /**
     * The creation time of payout request
     */
    private String createTime;
    /**
     * The creation time of payout request
     * UTC0
     */
    private LocalDateTime createTimeUtc;
    /**
     * The settled or failed time of payout transaction
     */
    private String finalStatusTime;
    /**
     * The settled or failed time of payout transaction
     * UTC0
     */
    private LocalDateTime finalStatusTimeUtc;
    /**
     * Target account holder full name
     */
    private String targetName;
    /**
     * Target account holder identity, such as CPF for Brazil
     */
    private String targetDocument;
    /**
     * Target bank account id
     */
    private String targetBankAccountId;
    /**
     * Target bank name
     */
    private String targetBankName;
    /**
     * Target bank code
     */
    private String targetBankCode;
    /**
     * Target bank branch id
     */
    private String targetBankBranchId;

    @Override
    public ProductCodeEnum getProductCode() {
        return ProductCodeEnum.SPEI;
    }
}
