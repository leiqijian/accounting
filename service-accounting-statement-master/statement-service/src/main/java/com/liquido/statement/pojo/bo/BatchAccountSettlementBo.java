package com.liquido.statement.pojo.bo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.statement.pojo.entity.TransactionFee;
import com.liquido.statement.pojo.entity.TransactionMoney;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAccountSettlementBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long accountId;
    /**
     * non-realtime account snapshot
     */
    private AccountBo accountSnapshot;

    private LocalDate transactionDate;

    private AccountDailyInitBo billInitInfo;

    private BusinessTypeEnum businessType;

    private List<TransactionMoney> transactionMoneyList;

    // payin refund, charge_back, charge_back_rejected;
    private List<TransactionMoney> needCreditOriginalMoneyList;

    // all original transaction money list
    private List<TransactionMoney> allOriginalMoneyList;

    private List<TransactionFee> transactionFeeList;
}
