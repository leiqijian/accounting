package com.liquido.statement.service;

import java.time.LocalDate;
import javax.annotation.Resource;

import com.liquido.statement.common.AbstractTest;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.entity.Account;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class TransactionMoneyServiceTest extends AbstractTest {

    @Resource
    private AccountService accountService;
    @Resource
    private TransactionMoneyService transactionMoneyService;

    @Test(description = "statisticsDailyBill")
    public void statisticsDailyBill() throws Exception {
        Account account = accountService.getById(83864700725755913L);

        ;
        transactionMoneyService.statisticsDailyBill(account,
                AccountDailyInitBo.builder().accountId(account.getId())
                        .billId(1L)
                        .transactionDate(LocalDate.now())
                        .build());
        log.info(">>>>>>>statisticsDailyBill completed.");
    }

}
