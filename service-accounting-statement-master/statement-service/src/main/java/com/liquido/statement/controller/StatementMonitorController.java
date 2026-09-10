package com.liquido.statement.controller;

import javax.annotation.Resource;

import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.api.StatementMonitorApi;
import com.liquido.statement.service.monitor.StatementMonitor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class StatementMonitorController implements StatementMonitorApi {

    @Resource(name = "dailyCutMonitor")
    private StatementMonitor dailyCutMonitor;

    @Resource(name = "accountReconciliationMonitor")
    private StatementMonitor accountReconciliationMonitor;

    @Resource(name = "accountBalanceMonitor")
    private StatementMonitor accountBalanceMonitor;

    /**
     * dailyCutMonitor
     */
    @Override
    @PostMapping("/statement/daily/cut/monitor")
    public ResponseDto<Void> dailyCutMonitor() {
        dailyCutMonitor.monitor();
        return ResponseDto.success();
    }


    /**
     * account balance Monitor
     */
    @Override
    @PostMapping("/statement/account/balance/diff/monitor")
    public ResponseDto<Void> accountBalanceDiffMonitor() {
        accountReconciliationMonitor.monitor();
        return ResponseDto.success();
    }

    /**
     * accountBalanceMonitor
     */
    @PostMapping("/statement/account/balance/monitor")
    public ResponseDto<Void> accountBalanceMonitor() {
        accountBalanceMonitor.manualPush();
        return ResponseDto.success();
    }

}
