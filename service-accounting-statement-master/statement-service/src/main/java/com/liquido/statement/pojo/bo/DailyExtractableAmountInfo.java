package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyExtractableAmountInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * yesterday total extractable amount of T+0
     */
    private BigDecimal latestDailyT0ExtractableAmount;

    /**
     * yesterday total extractable amount of T+n
     */
    private BigDecimal latestDailyTnExtractableAmount;

    /**
     * current daily total extractable amount of T+0(daily-cut between 00:00~00:15)
     */
    private BigDecimal currentDailyT0ExtractableAmount;

    /**
     * current daily total extractable amount of T+n
     */
    private BigDecimal currentDailyTnExtractableAmount;

    /**
     * latest daily biz transaction occurred amount
     */
    private BigDecimal latestDailyBizOccurredAmount;

    /**
     * latest daily occurred extractable amount
     */
    private BigDecimal latestDailyOccurredExtractableAmount;

    /**
     * latest daily extractable end balance
     */
    private BigDecimal latestDailyExtractableEndBalance;

}
