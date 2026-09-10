package com.liquido.worker.service;

import javax.annotation.Resource;

import com.liquido.worker.common.AbstractTest;
import com.liquido.worker.service.calculate.ExchangeRateManager;

import org.testng.annotations.Test;

public class MerchantExchangeRateManagerTest extends AbstractTest {

    @Resource
    private ExchangeRateManager exchangeRateManager;

    @Test(description = "testGetExchangeRate")
    public void testGetExchangeRate() throws Exception {
    }

}
