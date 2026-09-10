package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class BatchWithdrawalApplyDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long merchantId;

    private String merchantCode;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    private Long accountId;

    private Long batchId;

    private BigDecimal withdrawalAmount;
    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    private Integer withdrawalCount;

    // ref: transaction_money.be_credited_date
    private LocalDate creditedDate;

    // current apply withdrawal date
    private LocalDate applyDate;

    private List<DealershipWithdrawalInfoDto> dataList;
}
