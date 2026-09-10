package com.liquido.test.listener;

import com.liquido.test.params.TestNgParams;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class TestNgInvokedMethodListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            Object[] params = testResult.getParameters();
            if (params.length == 1 && params[0].getClass().isAssignableFrom(TestNgParams.class)) {
                TestNgParams param = (TestNgParams) params[0];
                //JawsTestngLog.printCase(param.getDescription(), "请求参数：" + param.getParam() + ",
                // 预期响应码：" + param.getExpectCode());
                testResult.setTestName(param.getDescription());
            }
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

    }

}
