package com.liquido.worker.service;

import javax.annotation.Resource;

import com.liquido.worker.common.AbstractTest;
import com.liquido.worker.service.calculate.ExchangeRateManager;

import org.testng.annotations.Test;

public class ExchangeRateManagerTest extends AbstractTest {

    @Resource
    private ExchangeRateManager exchangeRateManager;

    @Test(description = "testExchangeRateManagerInit")
    public void testExchangeRateManagerInit() throws Exception {

        exchangeRateManager.initExchangeRate();
    }
}
