package com.liquido.statement.pojo.bo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountConfigData implements Serializable {

    private static final long serialVersionUID = 1L;

    private BalanceAlarmConfigBo balanceAlarm;

    private PayoutInsufficientBalanceAlarmConfigBo payOutInsufficientBalanceAlarmConfig;

    private String unHoldCron;

    private CalculateConfigBo calculateConfig;

    private PaymentLinkConfigBo paymentLinkConfig;

    private PaymentVerifyConfigBo paymentVerifyConfig;

    private WithdrawalBalanceOverThresholdRemindConfigBo withdrawalBalanceOverThresholdRemindConfig;

    private DepositConfigBo depositConfig;

}
