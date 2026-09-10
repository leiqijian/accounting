package com.liquido.statement.service.payment;

import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.statement.pojo.dto.PaymentPayoutDto;
import com.liquido.statement.pojo.dto.payment.BasePayoutResult;
import com.liquido.statement.pojo.entity.TransactionPayout;

public interface PaymentPayoutService {

    default PaymentChannelEnum getPayoutChannel() {
        return null;
    }

    /**
     * payout
     * Just call transaction system to create a PAY_OUT order
     * Tips: No deduction of account balance
     *
     * @param parameter parameter
     * @return
     */
    PaymentPayoutDto payout(final TransactionPayout parameter);

    /**
     * get transaction payout result
     *
     * @param parameter parameter
     * @return
     */
    <T extends BasePayoutResult> T queryPayoutResult(final TransactionPayout parameter);
}
