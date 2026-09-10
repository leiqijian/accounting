package com.liquido.statement.service;

import java.time.LocalDate;
import java.util.List;
import javax.annotation.Resource;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.statement.common.AbstractTest;
import com.liquido.statement.pojo.bo.DailyCutSuccessBo;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.testng.annotations.Test;

@Slf4j
public class AutoPaymentPayoutServiceTest extends AbstractTest {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Test(description = "testAccountAutoPayment")
    public void testAccountAutoPayment() {

        final DailyCutSuccessBo bo = DailyCutSuccessBo.builder()
                .merchantId(93862925170341916L)
                .accountId(93869700725939154L)
                .timezone("UTC+8")
                .countryCode(CountryCodeEnum.BR)
                .transactionTypeCode(TransactionTypeCodeEnum.PAY_IN)
                .billId(166681172112703494L)
                //.latestDailyExtractableEndBalance(new BigDecimal("252618692"))
                .billDate(LocalDate.now().minusDays(1))
                .currency(CurrencyEnum.BRL)
                .build();
        final List<DailyCutSuccessBo> billList = Lists.newArrayList(bo);

//        eventPublisher.publishEvent(new DailyCutSuccessEvent(
//                DailyCutSuccessEventArgs.builder()
//                        .billList(billList)
//                        .autoPayment(Boolean.TRUE)
//                        .build()));

        log.info(">>>>>>>testAccountAutoPayment completed.");
    }
}
