package com.liquido.worker.service.calculate.handler;

import com.liquido.base.enums.TransactionTypeCodeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * MarketPlace TransactionHandler
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketPlaceTransactionHandler extends AbstractMarketPlaceTransactionHandler {

    @Override
    public TransactionTypeCodeEnum getStrategy() {
        return TransactionTypeCodeEnum.MARKET_PLACE_ORDERS;
    }
}
