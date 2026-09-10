package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Convert;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.OperateModeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(1)
    private Long merchantId;

    @NotNull
    @Min(1)
    private Long payinAccountId;
    /**
     * if transactionAmount is null or 0,
     * system will auto transfer the extractableBalance of payinAccount to payoutAccount;
     */
    @Min(value = 1, message = "transfer amount must be greater than 0")
    @NotNull
    private BigDecimal transactionAmount;

    @Convert(converter = OperateModeEnum.Convert.class)
    private OperateModeEnum operateMode;

    /**
     * UTC+0
     */
    private LocalDateTime transactionTime;

    /**
     * UTC+0
     */
    private LocalDateTime settlementTime;
}
