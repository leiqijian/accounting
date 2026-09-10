package com.liquido.statement.configuration;

import java.util.TimeZone;

import com.liquido.statement.common.Constant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ApplicationInitStarter implements CommandLineRunner {
    @Override
    public void run(final String... args) {
        TimeZone.setDefault(TimeZone.getTimeZone(Constant.COMMON.ZONE_UTC));
    }
}
