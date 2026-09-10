package com.liquido.statement.controller;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestingController {

    @Resource
    private RedisDistLock redisDistLock;

    @PostMapping("/statement/testing/lock")
    public ResponseDto<Void> syncTransactionInProgress() {
        final String val = UUID.randomUUID().toString();
        final boolean locked = redisDistLock.tryLock("TESTING_LOCK", val, 120, TimeUnit.DAYS);
        log.info("locked:{}", locked);

        final boolean unlocked = redisDistLock.unlock("TESTING_LOCK", val);
        log.info("unlocked:{}", unlocked);
        return ResponseDto.success();
    }
}
