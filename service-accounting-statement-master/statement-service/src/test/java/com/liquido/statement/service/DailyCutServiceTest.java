package com.liquido.statement.service;

import javax.annotation.Resource;

import com.liquido.statement.common.AbstractTest;
import com.liquido.statement.service.dailycut.DailyCutService;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class DailyCutServiceTest extends AbstractTest {

    @Resource
    private DailyCutService dailyCutService;

    @Test(description = "testAccountDailyCut")
    public void testAccountDailyCut() throws Exception {
        dailyCutService.runAccountDailyCut();
        log.info(">>>>>>>testAccountDailyCut completed.");
    }
}
