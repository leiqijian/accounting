package com.liquido.statement;

import java.util.TimeZone;

import com.liquido.base.BaseApis;
import com.liquido.report.ReportApis;
import com.liquido.statement.common.Constant;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SuppressWarnings("PMD.UseUtilityClass")
@EnableAsync
@EnableFeignClients(basePackageClasses = {
        StatementApplication.class,
        BaseApis.BaseFeign.class,
        ReportApis.ReportFeign.class
})
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class)
public class StatementApplication {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone(Constant.COMMON.ZONE_UTC));
    }

    public static void main(final String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(StatementApplication.class, args);
    }

}
