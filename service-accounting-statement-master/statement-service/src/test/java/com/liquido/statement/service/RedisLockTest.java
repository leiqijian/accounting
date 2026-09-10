package com.liquido.statement.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.statement.common.AbstractTest;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class RedisLockTest extends AbstractTest {

    @Resource
    private RedisDistLock redisDistLock;

    @Test(description = "testDistLock")
    public void testDistLock() throws InterruptedException {

        final String val = UUID.randomUUID().toString();
        final boolean locked = redisDistLock.tryLock("TESTING_LOCK", val, 120, TimeUnit.SECONDS);
        log.info("locked:{}", locked);

        final boolean unlocked = redisDistLock.unlock("TESTING_LOCK", val);
        log.info("unlocked:{}", unlocked);

    }
}
