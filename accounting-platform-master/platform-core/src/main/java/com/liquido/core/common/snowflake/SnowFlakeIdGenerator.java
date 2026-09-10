package com.liquido.core.common.snowflake;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;
import javax.persistence.Id;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

@Component
public class SnowFlakeIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(final SharedSessionContractImplementor session,
                                 final Object entity) throws HibernateException {

        // check super class has @Id
        final Field[] superFields = entity.getClass().getSuperclass().getDeclaredFields();
        Long primaryId = getPrimaryId(entity, superFields);
        if (primaryId > 0L) {
            return primaryId;
        }

        // check current class has @Id
        final Field[] fields = entity.getClass().getDeclaredFields();
        primaryId = getPrimaryId(entity, fields);
        if (primaryId > 0L) {
            return primaryId;
        }

        return SnowflakeIdUtil.generate();
    }

    private static Long getPrimaryId(final Object entity, final Field[] fields) {
        if (Objects.nonNull(entity) && Objects.nonNull(fields) && fields.length > 0) {
            for (final Field field : fields) {
                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);
                        final Object primaryId = field.get(entity);
                        if (Objects.nonNull(primaryId) && ((Long) primaryId) > 0L) {
                            return (Long) primaryId;
                        }
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        }
        return -1L;
    }

}
