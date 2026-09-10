package com.liquido.statement.pojo.bo;


import java.io.Serializable;

import com.liquido.base.enums.CardTypeEnum;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CreditCardGroupCodeEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.VendorCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCostConfigBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long accountId;

    private CountryCodeEnum countryCode;

    private VendorCodeEnum vendor;

    private DirectionTypeEnum directionType;

    private CardTypeEnum cardType;

    private CreditCardGroupCodeEnum cardGroup;

    private int installment;

}
