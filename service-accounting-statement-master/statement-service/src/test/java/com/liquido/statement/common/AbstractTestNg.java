package com.liquido.statement.common;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// extends AbstractTransactionalTestNGSpringContextTests
public abstract class AbstractTestNg extends AbstractTestNGSpringContextTests {

    @BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextBeforeTestClass")
    protected void springTestContextPrepareTestInstance() throws Exception {
        System.setProperty("org.uncommons.reportng.escape-output", "false");
        super.springTestContextPrepareTestInstance();
    }


    @BeforeMethod(alwaysRun = true)
    protected void springTestContextBeforeTestMethod(Method testMethod) throws Exception {
        Test testAnno = AnnotationUtils.findAnnotation(testMethod, Test.class);
        super.springTestContextBeforeTestMethod(testMethod);
    }

    @AfterMethod
    protected void springTestContextAfterTestMethod() {
        Reporter.log("<br/>");
    }

}
