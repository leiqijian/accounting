package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.liquido.base.enums.BusinessTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyBillSummaryBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private AccountBo account;

    private AccountDailyInitBo dailyInitInfo;

    private DailyTransactionMoneyBo transactionMoney;

    private List<DailyTransactionFeeBo> transactionFeeList;

    private Map<BusinessTypeEnum, DailyTransactionBizBo> transactionBizMap;

    /**
     * Yesterday's total trading volume
     * Only statistics the total transaction volume of merchants yesterday
     */
    private BigDecimal latestDailyTransactionVolume;

    /**
     * Total amount of transactions occurred yesterday;
     */
    private BigDecimal latestDailyOccurredAmount;

    /**
     * Total count of transactions occurred yesterday;
     */
    private Long latestDailyOccurredCount;

    /**
     * Total extractable info;
     */
    private DailyExtractableAmountInfo dailyExtractableAmountInfo;

}
