package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalBatchRollBackVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String requestId;

    private BigDecimal totalAmount;

    @NotNull
    @Min(1)
    private Long merchantId;

    @NotNull
    @Min(1)
    private Long accountId;

    private List<ApprovalRollBackVo> voList;

}
