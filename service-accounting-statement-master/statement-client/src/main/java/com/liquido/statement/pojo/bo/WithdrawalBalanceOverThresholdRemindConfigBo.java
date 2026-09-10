package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalBalanceOverThresholdRemindConfigBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean flag;
    /**
     * Unit: Cent
     */
    private BigDecimal thresholdAmount;

    private List<String> larkUserIds;

    private Integer alarmCycleHours;

}
