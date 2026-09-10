package com.liquido.worker.service;

import javax.annotation.Resource;

import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.worker.common.AbstractTest;
import com.liquido.worker.feign.BaseService;

import org.testng.annotations.Test;

public class BaseServiceTest extends AbstractTest {

    @Resource
    private BaseService baseService;

    @Test(description = "testGetMonthFeeConfig")
    public void testGetMonthFeeConfig() throws Exception {

        AccountDto account = new AccountDto();
        account.setId(83864700725755913L);

        for (int i = 0; i < 5; i++) {
//            final List<MonthlyFeeConfigurationDto> list = baseService.getMonthlyProductFeeConfig(
//                    account, ProductCodeEnum.PIX, LocalDate.now(), null);
//            System.out.println(JsonUtil.toJson(list));
        }

    }
}
