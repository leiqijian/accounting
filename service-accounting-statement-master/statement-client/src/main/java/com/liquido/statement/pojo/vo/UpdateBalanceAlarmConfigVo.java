package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.liquido.statement.pojo.bo.BalanceAlarmConfigBo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalanceAlarmConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(0)
    private Long merchantId;

    @NotNull
    @Min(0)
    private Long accountId;

    @NotNull
    @Valid
    private BalanceAlarmConfigBo balanceAlarmConfig;

}
