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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePayoutPaymentConfigVo implements DefaultPaymentConfigVo {

    /**
     * accounting payout account id.
     */
    @NotNull
    private Long payoutAccountId;

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
        return this.payoutAccountId;
    }

}
