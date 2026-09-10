package com.liquido.statement.service.settlement.handler;

import com.liquido.base.enums.BusinessStrategyEnum;

import org.springframework.stereotype.Service;

/**
 * formula: amount = amount + (tradeAmount * fx) + fee + tax
 */
@Service
public class MarketPlaceRefundHandler extends AbstractAccountSettlementHandler {

    @Override
    public BusinessStrategyEnum getStrategy() {
        return BusinessStrategyEnum.MARKET_PLACE_REFUND;
    }

}
