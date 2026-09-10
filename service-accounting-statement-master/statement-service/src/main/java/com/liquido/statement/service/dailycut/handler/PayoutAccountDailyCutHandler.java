package com.liquido.statement.service.dailycut.handler;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.DailyBillSummaryBo;
import com.liquido.statement.pojo.entity.Account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * formula: amount = amount - (tradeAmount * fx) - fee
 */
@Service
@RequiredArgsConstructor
public class PayoutAccountDailyCutHandler extends AbstractAccountDailyCutHandler {

    @Override
    public TransactionTypeCodeEnum getStrategy() {
        return TransactionTypeCodeEnum.PAY_OUT;
    }

    /**
     * PAY-IN:
     * dailyOccurredAmount:
     * +biz_topup.settlement_amount)-fee(biz_topup.fee)-tax(biz_topup.tax)
     * + (
     * - SETTLE(transaction_money.settlement_amount - fee - tax;
     * + REFUND(transaction_money.settlement_amount + fee + tax;
     * )
     * - transfer out(biz_transfer_out.settlement_amount - fee - tax
     * + refund(biz_refund.settlement_amount + fee + tax
     *
     * @param account     account
     * @param dailyInitBo dailyInit
     * @return
     */
    @Override
    public DailyBillSummaryBo statisticDailyBill(final Account account,
                                                 final AccountDailyInitBo dailyInitBo) {
        return super.statisticDailyBill(account, dailyInitBo);
    }
}
