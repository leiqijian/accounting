package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.statement.pojo.bo.BalanceAlarmConfigBo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceAlarmConfigDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long merchantId;

    private Long accountId;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    @Convert(converter = TransactionTypeCodeEnum.Convert.class)
    private TransactionTypeCodeEnum transactionTypeCode;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private BalanceAlarmConfigBo balanceAlarmConfig;
}
