package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.ProductCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentLinkVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * required
     */
    private Long accountId;

    /**
     * required
     */
    @Convert(converter = CountryCodeEnum.Convert.class)
    @NotNull
    private CountryCodeEnum country;

    /**
     * required
     */
    @Convert(converter = CurrencyEnum.Convert.class)
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
    @Convert(converter = ProductCodeEnum.Convert.class)
    private Set<ProductCodeEnum> allowPaymentMethods;

    @NotNull
    @Length(max = 128)
    private String orderId;

    private String email;

    private String name;

    private String phone;

    private String documentId;

    private String installmentPlanId;

}
