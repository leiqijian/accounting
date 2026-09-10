package com.liquido.statement.pojo.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * account_config
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyExtractableInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * current total extractable amount of T+0
     */
    private BigDecimal currentT0ExtractableAmount;

    /**
     * current total extractable amount of T+n
     */
    private BigDecimal currentTnExtractableAmount;

    /**
     * current daily biz transaction occurred amount
     */
    private BigDecimal currentBizOccurredAmount;

    /**
     * latest daily occurred extractable amount
     */
    private BigDecimal currentOccurredExtractableAmount;

    /**
     * latest daily extractable end balance
     */
    private BigDecimal currentExtractableEndBalance;

    /**
     * next daily total extractable amount of T+0(daily-cut between 00:00~00:15)
     */
    private BigDecimal nextT0ExtractableAmount;

    /**
     * next daily total extractable amount of T+n
     */
    private BigDecimal nextTnExtractableAmount;

}
