package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
public class PaymentPixPayoutBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Required=true
     * Unique key to ensure idempotent requests
     */
    @NotBlank
    private String idempotencyKey;

    /**
     * Required=true
     * Target account holder full name
     */
    @NotBlank
    private String targetName;

    /**
     * Required=true
     * Target account holder identity, such as CPF for Brazil
     */
    @NotBlank
    private String targetDocument;

    /**
     * Required=true
     * The type of target PIX key, enum value as email, phone, document or random
     */
    @NotBlank
    private String targetPixKeyType;

    /**
     * Required=true
     * Target PIX Key
     */
    @NotBlank
    private String targetPixKey;

    /**
     * Required=true
     * country code, enum value as BR
     */
    @NotBlank
    private String country;

    /**
     * Required=true
     * The transfer amount. note: 100 = 1BRL/1USD
     */
    @NotNull
    private BigDecimal amountInCents;

    /**
     * Required=true
     * The currency code of the transferred fund. enum value as BRL
     */
    @NotBlank
    private String currency;

    /**
     * Required=false
     * Target account holder Last Name
     */
    private String targetLastName;

    /**
     * Required=false
     * Target account holder email
     */
    private String targetEmail;

    /**
     * Required=false
     * Target account holder phone . ps: +55xxxxxxxxx
     */
    private String targetPhone;

    /**
     * Required=false
     * Target account holder BirthDate，format: yyyy-MM-dd
     */
    private String targetBirthDate;
}
