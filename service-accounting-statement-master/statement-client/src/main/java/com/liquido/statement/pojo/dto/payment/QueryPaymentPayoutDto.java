package com.liquido.statement.pojo.dto.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class QueryPaymentPayoutDto implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * The transaction id
     */
    private String transactionId;

    /**
     * Target account holder full name
     */
    private String targetName;

    /**
     * Target account holder identity, such as CPF for Brazil
     */
    private String targetDocument;

    /**
     * Target PIX Key
     */
    private String targetPixKey;

    /**
     * The type of target PIX key, enum value as email, phone, document or random
     */
    private String targetPixKeyType;

    /**
     * Target Bank Code. three digits.
     */
    private String targetBankCode;

    /**
     * Target Bank Agency Code.
     */
    private String targetBankAgency;

    /**
     * Target Bank Account Id.
     */
    private String targetBankAccountId;

    /**
     * country code, enum value as BR
     */
    private String country;

    /**
     * The transfer amount. note: 1= 1BRL
     */
    private BigDecimal amount;

    /**
     * payment record create time
     * format: 2022-01-14 07:00:31 GMT+08:00
     */
    private String createTime;

    /**
     * Transfer final status update time, final status include SETTLED, FAILED and REJECTED
     * format: 2022-01-14 07:00:45 GMT+08:00
     */
    private String finalStatusTime;

    private LocalDateTime finalStatusTimeUtc;

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

}
