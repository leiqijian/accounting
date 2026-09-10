package com.liquido.statement.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.dto.CardCostConfigurationDto;
import com.liquido.base.pojo.dto.ExtraIncomeConfigurationDto;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.statement.enums.AdjustmentRevenueRegardEnum;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.RecalculateCostBo;
import com.liquido.statement.pojo.bo.RecalculateCostData;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.TransactionBiz;
import com.liquido.statement.pojo.entity.TransactionCost;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.vo.TransactionBizVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;

public interface TransactionCostService {

    /**
     * save transaction cost
     *
     * @param cost
     */
    void saveTransactionCost(final TransactionCost cost);

    /**
     * batch save transaction costs
     *
     * @param orderList
     */
    void batchSaveTransactionCost(final List<TransactionMoneyVo> orderList,
                                  final AccountDailyInitBo dailyInitBo);

    void batchSaveTransactionCost(final List<TransactionCost> costList);

    /**
     * save biz business transaction costs
     *
     * @param vo
     * @param order
     * @param merchantInfo
     * @param account
     */
    void saveBizTransactionCost(final TransactionBizVo vo,
                                final TransactionBiz order,
                                final MerchantDto merchantInfo,
                                final Account account);

    TransactionCost buildBizTransactionCost(final TransactionBizVo vo,
                                            final TransactionBiz order,
                                            final MerchantDto merchantInfo,
                                            final Account account);


    /**
     * save biz adjustment business costs
     *
     * @param merchantInfo
     * @param account
     * @param order
     * @param revenueRegard
     */
    void saveBizAdjustmentCost(final MerchantDto merchantInfo,
                               final Account account,
                               final TransactionBiz order,
                               final AdjustmentRevenueRegardEnum revenueRegard);

    /**
     * batch load transaction costs
     *
     * @param vo
     */
    List<RecalculateCostData> batchLoadTransactionCost(final RecalculateCostBo vo);


    TransactionCost queryTransactionCostInfo(final Long accountId,
                                             final Long transactionId,
                                             final DirectionTypeEnum directionType);


    /**
     * batch update transaction costs
     *
     * @param costList
     */
    void batchUpdateTransactionCost(final List<RecalculateCostData> costList);

    CompletableFuture<Boolean> processTransactionCostData(final List<TransactionMoney> orderList);

    CompletableFuture<Boolean> doRecalculateCost(
            final List<RecalculateCostData> costList,
            final RecalculateCostBo recalculateCostBo,
            final ApmCostConfigurationDto apmCostConfig,
            final CardCostConfigurationDto cardCostConfig,
            final List<ExtraIncomeConfigurationDto> extraFeeConfig);

    void syncCostBillId(final Long accountId, final Long billId);
}
