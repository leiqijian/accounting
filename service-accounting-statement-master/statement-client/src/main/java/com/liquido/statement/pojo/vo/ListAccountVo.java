package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListAccountVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private List<Long> ids;

    private Long merchantId;

    private List<Long> merchantIds;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    /**
     * TransactionTypeCodeEnum: PAY_IN/PAY_OUT/MARKET_PLACE_ORDERS
     */
    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private String timezone;

    private String timezoneName;

}
