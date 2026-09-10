package com.liquido.base.pojo.vo;

import java.io.Serializable;
import javax.persistence.Convert;

import com.liquido.base.enums.CardTypeEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.VendorCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryCostCardConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer activeVersion;

    private Long accountId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum country;

    @Convert(converter = CardTypeEnum.Convert.class)
    private CardTypeEnum cardType;

    @Convert(converter = VendorCodeEnum.Convert.class)
    private VendorCodeEnum vendor;

}
