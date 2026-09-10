package com.liquido.statement.configuration;

import java.util.TimeZone;

import com.liquido.statement.common.Constant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

@Slf4j
public class AppStartingListener implements ApplicationListener<ApplicationStartingEvent>, Ordered {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void onApplicationEvent(final ApplicationStartingEvent event) {
        TimeZone.setDefault(TimeZone.getTimeZone(Constant.COMMON.ZONE_UTC));
    }
}
