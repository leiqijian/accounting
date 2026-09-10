package com.liquido.statement.controller;

import java.math.BigDecimal;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.pojo.vo.TransactionBizVo;
import com.liquido.test.params.TestNgParams;

import org.testng.annotations.DataProvider;

public class DataParameter {

    @DataProvider(name = "topUpProvider")
    public Object[][] topUpProvider() {
        final String action1 = "account-topUp";
        TransactionBizVo params = new TransactionBizVo();
        params.setRequestId(DataUtil.getUuid());
        params.setAccountId(83864700725755914L);
        params.setTransactionAmount(new BigDecimal(100));
        params.setTransactionCurrency(CurrencyEnum.BRL);
        params.setExchangeRate(new BigDecimal(19.8));
        params.setFeeAmount(new BigDecimal(20));
        params.setTaxAmount(new BigDecimal(10));
        params.setSettlementAmount(new BigDecimal(9980));
        params.setSettlementCurrency(CurrencyEnum.BRL);
        params.setComments("test");
        params.setCreatedBy(0L);

        TestNgParams<TransactionBizVo> params1 =
                TestNgParams.newInstance(action1, params, ResponseDto.SUCCESS_CODE);

        // test api idempotent
        TestNgParams<TransactionBizVo> params2 =
                TestNgParams.newInstance(action1, params, ResponseDto.SUCCESS_CODE);
        //return new Object[][] {{params1}, {params2}};


        return new Object[][] {{params1}};
    }

    @DataProvider(name = "transferOutProvider")
    public Object[][] transferOutProvider() {
        final String action1 = "account-transferOut";
        TransactionBizVo params = new TransactionBizVo();
        params.setRequestId(DataUtil.getUuid());
        params.setAccountId(83864700725755914L);
        params.setTransactionAmount(new BigDecimal(100));
        params.setTransactionCurrency(CurrencyEnum.MXN);
        params.setExchangeRate(new BigDecimal(1));
        params.setFeeAmount(new BigDecimal(5));
        params.setTaxAmount(new BigDecimal(2));
        params.setSettlementAmount(new BigDecimal(100));
        params.setSettlementCurrency(CurrencyEnum.MXN);
        params.setComments("test");
        params.setCreatedBy(0L);

        TestNgParams<TransactionBizVo> params1 =
                TestNgParams.newInstance(action1, params, ResponseDto.SUCCESS_CODE);

        // test api idempotent
        TestNgParams<TransactionBizVo> params2 =
                TestNgParams.newInstance(action1, params, ResponseDto.SUCCESS_CODE);
        //return new Object[][] {{params1}, {params2}};


        return new Object[][] {{params1}};
    }


    @DataProvider(name = "refundProvider")
    public Object[][] refundProvider() {
        final String action1 = "account-refund";
        TransactionBizVo params = new TransactionBizVo();
        params.setRequestId(DataUtil.getUuid());
        params.setAccountId(83864700725755914L);
        params.setTransactionAmount(new BigDecimal(100));
        params.setTransactionCurrency(CurrencyEnum.MXN);
        params.setExchangeRate(new BigDecimal(1));
        params.setFeeAmount(new BigDecimal(5));
        params.setTaxAmount(new BigDecimal(2));
        params.setSettlementAmount(new BigDecimal(100));
        params.setSettlementCurrency(CurrencyEnum.MXN);
        params.setComments("test");
        params.setCreatedBy(0L);

        TestNgParams<TransactionBizVo> params1 =
                TestNgParams.newInstance(action1, params, ResponseDto.SUCCESS_CODE);

        // test api idempotent
        TestNgParams<TransactionBizVo> params2 =
                TestNgParams.newInstance(action1, params, ResponseDto.SUCCESS_CODE);
        //return new Object[][] {{params1}, {params2}};


        return new Object[][] {{params1}};
    }
}
