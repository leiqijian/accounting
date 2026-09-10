package com.liquido.statement.service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import com.liquido.statement.common.AbstractTest;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class AccountDailyInitServiceTest extends AbstractTest {

    @Resource
    private AccountDailyInitService accountDailyInitService;

    @Test(description = "testGetAccountDailyInitInfo")
    public void testGetAccountDailyInitInfo() throws InterruptedException {

        for (int i = 0; i < 1000; i++) {
            AccountDailyInitBo bo = accountDailyInitService.getDailyBillInitInfo(83864700725755914L,
                    LocalDate.parse("2022-08-21"));
            TimeUnit.MILLISECONDS.sleep(100L);
            log.info("AccountDailyInitBo = {}", bo);
        }
    }
}
