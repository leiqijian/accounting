package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.liquido.statement.pojo.entity.SubAccount;
import com.liquido.statement.pojo.entity.SubAccountDailyBill;
import com.liquido.statement.pojo.entity.SubAccountStatement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccountDailyBillFactor implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate billDate;

    private SubAccount subAccount;

    private SubAccountDailyBill lastSubAccountDailyBill;

    private List<SubAccountStatement> subAccountStatementList;

    private List<SubDailyTransactionBizBo> transactionBizList;

    private List<SubDailyTransactionMoneyBo> transactionMoneyList;

    private List<SubDailyTransactionFeeBo> transactionFeeList;

    private BigDecimal latestDailyExtractableAmount;
}
