package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;

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
public class PaymentSpeiPayoutBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Required=true
     * Unique key to ensure idempotent requests.
     */
    @NotBlank
    private String idempotencyKey;

    /**
     * Required=true
     * The user's country. ISO 3166-1 alpha-2 code, Length 2 characters, such as BR, MX.
     */
    @NotBlank
    private String country;

    /**
     * Required=true
     * Target account holder full name or account holder first name if targetLastName has been set.
     */
    @NotBlank
    private String targetName;

    /**
     * Required=true
     * Target bank account id. For SPEI, CLABEL number
     */
    @NotBlank
    private String targetBankAccountId;

    /**
     * Required=true
     * The transfer amount. note: 1 = 1MXN
     */
    @NotBlank
    private BigDecimal amountInCents;

    /**
     * Required=true
     * The currency of the transferred fund, Length 3 characters, such as BRL and MXN
     */
    @NotBlank
    private String currency;

    /**
     * Required=false
     * Target account holder last name
     */
    private String targetLastName;

    /**
     * Required=false
     * Target account holder email address
     */
    private String targetEmail;

    /**
     * Required=false
     * for Brazil, Optional for Mexico. Target account holder identity, such as CPF for Brazil
     */
    private String targetDocument;

    /**
     * Required=false
     * for Brazil, Optional for Mexico, Don't need for SPEI. Target bank name
     */
    private String targetBankName;

    /**
     * Required=false
     * for Brazil, Optional for Mexico. Don't need for SPEI. Target bank code
     */
    private String targetBankCode;

    /**
     * Target bank id. Required when targetBankAccountId is a 16-digit bank card;
     */
    private String targetBankId;

    /**
     * Required=false
     * for Brazil, Optional for Mexico, Don't need for SPEI. Target bank branch id
     */
    private String targetBankBranchId;

    /**
     * Required=false
     * The payment description. It is recommended to use "Liquido-" + company name or APP name.
     * The default is "Liquido CompanyName",
     * where "CompanyName" is your default company name in the system.
     */
    private String comment;
}
