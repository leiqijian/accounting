package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Country: Colombia
 * Bank Transfer Payment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentBrBankTransferPayoutBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Required=true
     * Unique key to ensure idempotent requests
     */
    @NotBlank
    private String idempotencyKey;

    /**
     * Required=true
     * country code, enum value as BR
     */
    @NotBlank
    private String country;

    /**
     * Required=true
     * Target account holder full name
     */
    @NotBlank
    @SensitiveField(SensitiveType.REAL_NAME)
    private String targetName;

    /**
     * Required=true
     * Target account holder email
     */
    @NotBlank
    @SensitiveField(SensitiveType.EMAIL)
    private String targetEmail;

    /**
     * Required=true
     * Target account holder identity, such as CPF for Brazil
     */
    @NotBlank
    @SensitiveField(SensitiveType.SHIELD)
    private String targetDocument;

    /**
     * Required=true
     * Target bank code.
     */
    @NotBlank
    @SensitiveField(SensitiveType.SHIELD)
    private String targetBankCode;

    /**
     * Required=false
     * Target bank agency code.
     */
    @SensitiveField(SensitiveType.SHIELD)
    private String targetBankAgency;

    /**
     * Required=true
     * Target bank account ID.
     */
    @SensitiveField(SensitiveType.SHIELD)
    @NotBlank
    private String targetBankAccountId;

    /**
     * Required=true
     * Target bank account type.CHECKING, SAVINGS.
     */
    @NotBlank
    private String targetBankAccountType;

    /**
     * Required=true
     * The transfer amount. note: 100 = 1BRL/1USD
     */
    @NotNull
    @Min(1)
    private BigDecimal amountInCents;

    /**
     * Required=true
     * The currency code of the transferred fund. enum value as BRL
     */
    @NotBlank
    private String currency;

    /**
     * Required=true
     * Description of payment.
     */
    @NotBlank
    private String comment;


    /**
     * Required=false
     * Target account holder Last Name
     */
    private String targetLastName;

    /**
     * Required=false
     * Target account holder phone . ps: +55xxxxxxxxx
     */
    @SensitiveField(SensitiveType.MOBILE)
    private String targetPhone;

    /**
     * Required=false
     * Target account holder BirthDate，format: yyyy-MM-dd
     */
    private String targetBirthDate;
}
