package com.liquido.worker;

import java.util.TimeZone;

import com.liquido.base.BaseApis;
import com.liquido.statement.StatementApis;
import com.liquido.worker.common.Constant;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SuppressWarnings("PMD.UseUtilityClass")
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
@EnableFeignClients(basePackageClasses = {
        WorkerApplication.class,
        BaseApis.BaseFeign.class,
        StatementApis.StatementFeign.class})
@EnableJpaRepositories(repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class)
public class WorkerApplication {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone(Constant.COMMON.ZONE_UTC));
    }

    public static void main(final String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(WorkerApplication.class, args);
    }

}
