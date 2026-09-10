package com.liquido.base;

import java.time.ZoneId;
import java.util.TimeZone;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SuppressWarnings("PMD.UseUtilityClass")
@EnableFeignClients(basePackageClasses = {
        BaseApplication.class
})
@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
@EnableJpaRepositories(repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class)
public class BaseApplication {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
    }

    public static void main(final String[] args) {
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(BaseApplication.class, args);
    }

}
