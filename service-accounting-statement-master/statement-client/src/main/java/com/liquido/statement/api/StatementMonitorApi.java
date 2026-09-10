package com.liquido.statement.api;

import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;

public interface StatementMonitorApi {

    @PostMapping("/statement/daily/cut/monitor")
    ResponseDto<Void> dailyCutMonitor();

    @PostMapping("/statement/account/balance/diff/monitor")
    ResponseDto<Void> accountBalanceDiffMonitor();

    @PostMapping("/statement/account/balance/monitor")
    ResponseDto<Void> accountBalanceMonitor();
}
