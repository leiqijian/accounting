package com.liquido.statement.service.settlement.handler;

import com.liquido.base.enums.BusinessStrategyEnum;

import org.springframework.stereotype.Service;

/**
 * formula: amount = amount + (tradeAmount * fx) - fee
 */
@Service
public class PayInSettledHandler extends AbstractAccountSettlementHandler {

    @Override
    public BusinessStrategyEnum getStrategy() {
        return BusinessStrategyEnum.PAY_IN_SETTLED;
    }

}
