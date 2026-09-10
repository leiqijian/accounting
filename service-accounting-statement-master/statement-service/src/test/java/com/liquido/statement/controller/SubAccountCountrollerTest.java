package com.liquido.statement.controller;


import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.common.AbstractTest;
import com.liquido.statement.pojo.vo.TransactionBizVo;
import com.liquido.test.params.TestNgParams;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

@Slf4j
public class SubAccountCountrollerTest extends AbstractTest {

    /**
     * test accountTopUp
     */
    @Rollback(false)
    @Test(description = "accountTopUp", dataProvider = "topUpProvider",
            dataProviderClass = DataParameter.class)
    public void accountTopUp(TestNgParams<TransactionBizVo> params) throws Exception {
        final String api = "/statement/account/topup";
        ResponseDto<Void> response =
                super.postJson(api, params.getParam(), Void.class, params.getExpectCode(), false);

        System.out.println("=======================================================");
        System.out.println(response);
    }
}
