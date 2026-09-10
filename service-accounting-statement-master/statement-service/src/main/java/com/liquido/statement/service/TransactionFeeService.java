package com.liquido.statement.service;

import java.util.List;
import java.util.Set;

import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.DailyTransactionFeeBo;
import com.liquido.statement.pojo.dto.TransactionFeeDto;
import com.liquido.statement.pojo.dto.TransactionTaxDetailDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.TransactionFee;
import com.liquido.statement.pojo.vo.ListTransactionFeeVo;
import com.liquido.statement.pojo.vo.QueryTransactionTaxDetailVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;

public interface TransactionFeeService {

    List<TransactionFee> findSettledFeeList(final Long transactionId);

    List<TransactionFee> findTransactionFeeList(final Long transactionId,
                                                final DirectionTypeEnum directionType);

    List<TransactionFee> findTransactionFee(final Set<Long> transactionIds);

    List<TransactionFeeDto> findTransactionFee(final ListTransactionFeeVo vo);

    List<TransactionFee> batchSave(final List<TransactionMoneyVo> orderList,
                                   final AccountDailyInitBo dailyInitBo);

    boolean updateState(final Long id,
                        final SettleStatusEnum fromState,
                        final SettleStatusEnum toState,
                        final Integer fromVersion);

    boolean batchUpdateState(final List<Long> idList,
                             final SettleStatusEnum fromState,
                             final SettleStatusEnum toState,
                             final Integer fromVersion);

    List<DailyTransactionFeeBo> statisticsDailyBill(final Account account,
                                                    final AccountDailyInitBo bo);

    List<TransactionTaxDetailDto> findTransactionTaxDetail(final QueryTransactionTaxDetailVo vo);
}
