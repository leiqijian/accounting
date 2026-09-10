package com.liquido.core.configuration;

import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(value = {JPAQueryFactory.class, EntityManager.class})
public class QueryDslJpaFactoryConfiguration {

    @Bean
    public JPAQueryFactory jpaQueryFactory(
            @Autowired(required = false) final EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

}
