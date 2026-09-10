package com.liquido.core.common.snowflake;

import javax.annotation.Resource;

import com.liquido.core.common.logger.LogWrapper;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.configuration.CommonProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

/**
 * Snowflake Algorithm Initializer
 */
@Slf4j
public class SnowflakeInitializer implements SmartLifecycle {

    private final int phase = -1;
    private boolean isRunning = false;
    @Resource
    private CommonProperties commonProperties;

    @Override
    public void start() {
        log.info(LogWrapper.op("SnowflakeInitializer.start")
                .wrap("snowflake config=", JsonUtil.toJson(commonProperties.getSnowFlake()))
                .toString());
        SnowflakeIdUtil.K8S_ENVIRONMENT = commonProperties.getSnowFlake().isK8s();
        if (commonProperties.getSnowFlake().isK8s()) {
            log.info(LogWrapper.op("SnowflakeInitializer.init K8S env")
                    .wrap("snowflake class", "SnowflakeIdK8S").toString());
            SnowflakeIdK8S.init(commonProperties);
        } else {
            log.info(LogWrapper.op("SnowflakeInitializer.init Default env")
                    .wrap("snowflake class", "SnowflakeId").toString());
            SnowflakeId.init(commonProperties);
        }

        isRunning = true;
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return phase;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        isRunning = false;
    }

}
