package com.liquido.statement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.statement.pojo.bo.AccountCardScheduleBo;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.DailyExtractableAmountInfo;
import com.liquido.statement.pojo.bo.DailyTransactionMoneyBo;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.TransactionMoneyDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.vo.BatchWithdrawalApplyVo;
import com.liquido.statement.pojo.vo.ExtendData;
import com.liquido.statement.pojo.vo.FillFieldTransactionMoneyVo;
import com.liquido.statement.pojo.vo.ListTransactionMoneyVo;
import com.liquido.statement.pojo.vo.QueryAccountingCalendarVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;

public interface TransactionMoneyService {


    TransactionMoney save(final TransactionMoney entity);

    List<TransactionMoney> batchSave(final List<TransactionMoney> entityList);

    List<TransactionMoney> batchSave(final List<TransactionMoneyVo> dataList,
            final AccountDailyInitBo dailyInitBo);

    boolean updateState(final Long id,
            final SettleStatusEnum fromState,
            final SettleStatusEnum toState,
            final Integer fromVersion);

    boolean batchUpdateState(final List<Long> idList,
            final SettleStatusEnum fromState,
            final SettleStatusEnum toState,
            final Integer fromVersion);

    /**
     * Query Original Order by UniqueId
     *
     * @param uniqueId
     * @param directionType
     * @return
     */
    TransactionMoney queryOriginalOrderInfo(
            final Long accountId,
            final String uniqueId,
            final DirectionTypeEnum directionType);

    /**
     * Query Original Order by TransactionId
     *
     * @param transactionId
     * @param directionType
     * @return
     */
    TransactionMoney queryOriginalOrderInfo(final Long transactionId,
            final DirectionTypeEnum directionType);

    ExtendData queryExtendInfo(final Long transactionId,
            final DirectionTypeEnum directionType);

    Map<String, String> queryCalculationRule(final Long transactionId,
            final DirectionTypeEnum directionType);

    List<TransactionMoneyDto> listTransactionMoneyInfo(final ListTransactionMoneyVo vo);

    boolean checkUnSettleTransactionCount(final Account account,
            final AccountDailyInitBo dailyInit);

    DailyTransactionMoneyBo statisticsDailyBill(final Account account,
            final AccountDailyInitBo dailyInitBo);

    DailyExtractableAmountInfo statisticsDailyExtractableAmount(
            final Account account,
            final AccountDailyInitBo latestDailyInitBo,
            final AccountDailyInitBo currentDailyInitBo,
            final BigDecimal latestAccountStatementBizAmount,
            final BigDecimal latestDailyTransactionOccurredAmount);

    BigDecimal getPendingBalance(final Account account);


    List<TransactionMoney> queryTransactionMoneyList(final BatchWithdrawalApplyVo vo);

    BigDecimal queryPendingBalance(final Long accountId,
            final LocalDate date);

    BigDecimal getHoldingBalance(final Long accountId);

    List<TransactionMoney> queryHoldingOrderByDocumentIds(final Long accountId,
            final Set<String> documentIds);

    List<TransactionMoney> queryHoldingOrderByTransactionIds(final Long accountId,
            final Set<Long> transactionIds);

    void cancelHoldingTransaction(final Long accountId, final Set<Long> idList);

    List<TransactionMoney> queryTransactionMoneyList(final Long startId,
            final Long accountId,
            final Long billId,
            final Integer limitSize);

    List<TransactionMoneyDto> queryTransactionMoneyByAccountIdSubMerchantIdAndBillId(
            final Collection<Long> accountIds,
            final Set<String> subMerchantIds,
            final Long billId);

    void updateTransactionMoneyFxUsdRate(final AccountDto account,
            final Long dailyBillId,
            final DailyExchangeRateDto fxRate);

    void fillTransactionMoneyField(final List<FillFieldTransactionMoneyVo> list);

    CompletableFuture<List<AccountCardScheduleBo>> runQueryPendAmountTask(
            final QueryAccountingCalendarVo vo,
            final LocalDate date);
}
