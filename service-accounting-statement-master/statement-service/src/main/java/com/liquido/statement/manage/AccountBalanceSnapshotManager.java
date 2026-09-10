package com.liquido.statement.manage;

import java.time.LocalDateTime;

import com.liquido.statement.service.AccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class AccountBalanceSnapshotManager {
    private final AccountService accountService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void autoSaveBalanceSnapshot() {
        final long begin = System.currentTimeMillis();
        try {
            log.info("execute balance snapshot job begin, at:{}",
                    LocalDateTime.now());

            accountService.saveBalanceSnapshot();
        } catch (Exception e) {
            log.error("execute balance snapshot job error", e);
        } finally {
            log.info("execute balance snapshot job end, ts:{}ms",
                    System.currentTimeMillis() - begin);
        }
    }

}
