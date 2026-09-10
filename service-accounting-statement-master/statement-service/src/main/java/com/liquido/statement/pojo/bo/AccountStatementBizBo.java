package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.BizFinanceTypeEnum;
import com.liquido.base.enums.BusinessTypeEnum;
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
public class AccountStatementBizBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;

    private Long transactionId;

    private Long merchantId;

    private String subMerchantId;

    private Long accountId;

    private Long billId;

    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum businessType;

    private BizFinanceTypeEnum financeType;

    private LocalDateTime transactionTime;

    private BigDecimal extractableAmount;

    private BigDecimal frozenAmount;

    private BigDecimal exchangeAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum currency;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private Long createdBy;

    private Long updatedBy;

    private Integer version;

    private Boolean delFlag;

    private String remark;
}
