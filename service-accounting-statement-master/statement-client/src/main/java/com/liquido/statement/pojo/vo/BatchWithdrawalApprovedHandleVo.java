package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;

import com.liquido.base.enums.CurrencyEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchWithdrawalApprovedHandleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Length(max = 64)
    private String requestId;

    private Long accountId;

    private Long batchId;

    private BigDecimal unFrozenAmount;

    private BigDecimal approvalApprovedAmount;

    private Integer approvalApprovedCount;

    private BigDecimal approvalRejectedAmount;

    @Convert(converter = CurrencyEnum.Convert.class)
    private CurrencyEnum settlementCurrency;

    // approvalApproved detail
    private List<BatchWithdrawalApprovedHandleDetailVo> approvalApprovedList;

    // approvalRejected detail
    private List<BatchWithdrawalApprovedHandleDetailVo> approvalRejectedList;

}
