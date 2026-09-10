package com.liquido.base.service.impl;

import com.liquido.base.enums.CostTypeEnum;
import com.liquido.base.pojo.entity.QCostConfigurationVersion;
import com.liquido.base.service.CostConfigurationVersionService;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CostConfigurationVersionServiceImpl implements CostConfigurationVersionService {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public int queryActiveConfig(final CostTypeEnum costType) {
        final QCostConfigurationVersion entity = QCostConfigurationVersion.costConfigurationVersion;
        return jpaQueryFactory.select(entity.version.max().coalesce(0))
                .from(entity)
                .where(entity.costType.eq(costType))
                .fetchOne();
    }
}
