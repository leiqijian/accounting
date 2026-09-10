package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.CountryCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Only the payIn account can query the extractable balance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryAccountExtractableBalanceVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(1)
    private Long merchantId;

    /**
     * switch country
     */
    @NotNull
    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

}
