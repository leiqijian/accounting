package com.liquido.transaction.pojo.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Convert;

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
public class ApprovalBizBatchWithdrawalForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String approvalCode;

    private String merchantCode;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    private BigDecimal withdrawalAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum withdrawalCurrency;

    private Integer withdrawalCount;

    private List<String> withdrawalDetailCode;

    private List<String> withdrawalResultCode;

}
