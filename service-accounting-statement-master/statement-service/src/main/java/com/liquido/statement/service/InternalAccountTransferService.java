package com.liquido.statement.service;

import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountTransferConfig;
import com.liquido.statement.pojo.vo.AccountTransferVo;

public interface InternalAccountTransferService {

    /**
     * Within the same merchant Payin account extractable balance
     * auto transferred to Payout account balance
     *
     * @param payinAccount
     * @param payoutAccount
     * @param param
     * @param config
     */
    void executeInternalTransfer(final Account payinAccount,
                                 final Account payoutAccount,
                                 final AccountTransferVo param,
                                 final AccountTransferConfig config);
}
