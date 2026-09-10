package com.liquido.statement.pojo.bo;

import java.io.Serializable;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * account_daily_bill
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyCutAccountBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long accountId;

    private String timezone;

    private CountryCodeEnum countryCode;

    private TransactionTypeCodeEnum transactionTypeCode;

}
