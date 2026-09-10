package com.liquido.statement.manage;

import com.liquido.statement.pojo.vo.FixAccountDailyBillVo;
import com.liquido.statement.service.AccountDailyBillService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixAccountDailyBill {
    private final AccountDailyBillService accountDailyBillService;

    @Async("reRunTaskExecutor")
    public void fixHistoryAccountDailyBill(final FixAccountDailyBillVo vo) {
        accountDailyBillService.fixHistoryAccountDailyBill(vo);
    }
}
