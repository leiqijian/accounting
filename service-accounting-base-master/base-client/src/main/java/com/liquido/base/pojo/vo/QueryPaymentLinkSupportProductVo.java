package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryPaymentLinkSupportProductVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private Long accountId;

    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;
}
