package com.liquido.statement.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Convert;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class DealershipWithdrawalInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long batchId;

    private String subMerchantId;

    private String subMerchantName;

    private Long subAccountId;

    private BigDecimal withdrawalAmount;

    private Integer transactionCount;

    private BigDecimal feeAmount;

    private BigDecimal taxAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    private String accountType;

    private String accountName;

    private String accountNumber;

    // ref: transaction_money.be_credited_date
    private LocalDate completedDate;

    // current apply withdrawal date
    private LocalDate applyDate;

    //state: 0: init; 1: processing; 2:TBC; 3: success;
    private Integer state;

    private String remark;
}
