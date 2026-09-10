package com.liquido.transaction.pojo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.statement.pojo.dto.DealershipWithdrawalInfoDto;
import com.liquido.transaction.enums.ApprovalBizBatchWithdrawalStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class ApprovalBizBatchWithdrawalDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long approvalId;

    private Long merchantId;

    private String merchantCode;

    @Convert(converter = CountryCodeEnum.Convert.class)
    private CountryCodeEnum countryCode;

    private Long accountId;

    private BigDecimal withdrawalAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    private Integer withdrawalCount;

    private LocalDate applyDate;

    private LocalDate completedDate;

    private LocalDateTime completedTime;

    @Convert(converter = ApprovalBizBatchWithdrawalStatusEnum.Convert.class)
    private ApprovalBizBatchWithdrawalStatusEnum status;

    private Integer failCount;

    List<DealershipWithdrawalInfoDto> dataList;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}
