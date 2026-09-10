package com.liquido.base.pojo.vo;

import java.math.BigDecimal;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * this pojo's fields is same as the pojo {@code CreatePayoutPaymentConfigItemVo},
 * but they are two different biz, so we use two independent pojo to bear them.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentLinkPaymentConfigVo implements DefaultPaymentConfigVo {

    /**
     * accounting payin account id.
     */
    @NotNull
    private Long payinAccountId;

    /**
     * account's country code.
     */
    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * transaction system service api key of account's merchant.
     */
    @NotBlank
    @SensitiveField(SensitiveType.SHIELD)
    private String transactionApiKey;

    private BigDecimal maxAmount;

    /**
     * payment config extension config, null by default.
     */
    @SensitiveField(SensitiveType.SHIELD)
    private ObjectNode jsonParam;

    /**
     * remark of this payment config, null by default.
     */
    private String remark;

    @Override
    @JsonIgnore
    public Long getAccountId() {
        return this.payinAccountId;
    }

}
