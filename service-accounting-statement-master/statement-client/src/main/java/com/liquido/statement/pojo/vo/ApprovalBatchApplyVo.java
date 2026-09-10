package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.BusinessTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalBatchApplyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String requestId;

    @NotNull
    @Convert(converter = BusinessTypeEnum.Convert.class)
    private BusinessTypeEnum bizTypeCode;

    @NotNull
    @Min(1)
    private Long merchantId;

    @NotNull
    @Min(1)
    private Long accountId;

    @NotNull
    @Min(1)
    private Long batchId;

    @NotNull
    private BigDecimal totalAmount;

    @NotEmpty
    private List<ApprovalBatchApplyItemVo> dataList;
}
