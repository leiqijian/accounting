package com.liquido.worker.service.calculate.handler;

import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Payout TransactionHandler
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutTransactionHandler extends AbstractPayoutTransactionHandler {

    @Override
    public TransactionTypeCodeEnum getStrategy() {
        return TransactionTypeCodeEnum.PAY_OUT;
    }
}
