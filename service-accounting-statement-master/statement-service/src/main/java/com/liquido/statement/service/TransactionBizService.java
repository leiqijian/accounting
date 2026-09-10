package com.liquido.statement.service;

import java.util.List;

import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.DailyTransactionBizBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.TransactionBiz;
import com.liquido.statement.pojo.vo.TransactionBizVo;

public interface TransactionBizService {

    TransactionBiz findByTransactionId(final Long transactionId);

    TransactionBiz saveTransactionBizOrder(final Account account,
                                           final TransactionBizVo vo);

    TransactionBiz buildTransactionBiz(final Account account,
                                       final TransactionBizVo vo,
                                       final AccountDailyInitBo billInitInfo);

    List<TransactionBiz> saveBatchTransactionBiz(List<TransactionBiz> list);

    TransactionBiz updateTransactionBizOrder(TransactionBiz order);

    void updateTransactionBizStatus(final Long transactionId,
                                    final SettleStatusEnum fromStatus,
                                    final SettleStatusEnum toStatus);

    List<TransactionBiz> queryPaymentInProgressRecord();

    List<DailyTransactionBizBo> statisticsDailyBill(final Account account,
                                                    final AccountDailyInitBo dailyInit);

}
