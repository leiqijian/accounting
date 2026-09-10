package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.core.mvc.vo.PageCondition;
import com.liquido.statement.enums.TransactionChargeBackStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PageTransactionChargeBackOrderVo extends PageCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(0)
    private Long accountId;

    private String uniqueId;

    @Convert(converter = ProductCodeEnum.Convert.class)
    private Set<ProductCodeEnum> productCodes;

    @Convert(converter = TransactionChargeBackStatusEnum.Convert.class)
    private TransactionChargeBackStatusEnum status;

    private LocalDateTime disputeTimeStartDateTime;

    private LocalDateTime disputeTimeEndDateTime;

    private BigDecimal startDisputeAmount;

    private BigDecimal endDisputeAmount;

    private LocalDateTime defenseDeadlineStartDateTime;

    private LocalDateTime defenseDeadlineEndDateTime;

    private LocalDateTime paymentTimeStartDateTime;

    private LocalDateTime paymentTimeEndDateTime;

}
