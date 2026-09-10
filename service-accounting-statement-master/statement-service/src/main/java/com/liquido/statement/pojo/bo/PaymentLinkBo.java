package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * required
     * global uniqueId
     */
    private String orderId;

    private String email;

    private String name;

    private String phone;

    private String documentId;

    /**
     * required
     */
    @NotNull
    private CountryCodeEnum country;

    /**
     * required
     */
    @NotNull
    private CurrencyEnum currency;

    /**
     * required
     * unit:cent
     */
    @NotNull
    @Min(1)
    private BigDecimal amount;

    /**
     * non-required
     */
    private Collection<String> allowPaymentMethods;

    private String installmentPlanId;
}
