package com.liquido.statement.service;

import java.util.List;

import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.statement.pojo.dto.PaymentPayoutDto;
import com.liquido.statement.pojo.entity.TransactionPayout;

public interface TransactionPayoutService {

    TransactionPayout save(final TransactionPayout entity);

    List<TransactionPayout> batchSave(final List<TransactionPayout> payoutList);

    TransactionPayout findByUniqueId(final Long uniqueId);

    List<TransactionPayout> findDelayPayoutOrder();

    void postProcessPayoutOrder(final TransactionPayout payout,
                                final PaymentPayoutDto result);

    SettleStatusEnum transferStatus(final String transferStatus);
}
