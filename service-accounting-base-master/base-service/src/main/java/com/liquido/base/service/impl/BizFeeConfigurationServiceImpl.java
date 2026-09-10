package com.liquido.base.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.liquido.base.pojo.dto.BizFeeConfigurationDto;
import com.liquido.base.pojo.entity.BizFeeConfiguration;
import com.liquido.base.pojo.entity.QBizFeeConfiguration;
import com.liquido.base.pojo.mapper.ModelMapper;
import com.liquido.base.pojo.vo.QueryBizFeeConfigurationVo;
import com.liquido.base.service.BizFeeConfigurationService;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BizFeeConfigurationServiceImpl implements BizFeeConfigurationService {
    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BizFeeConfigurationDto> queryBizTransactionFeeConfig(
            final QueryBizFeeConfigurationVo vo) {

        final QBizFeeConfiguration entity = QBizFeeConfiguration.bizFeeConfiguration;
        BooleanExpression condition = entity.accountId.eq(vo.getAccountId())
                .and(entity.businessType.eq(vo.getBusinessType()))
                .and(entity.operationMethod.eq(vo.getOperationMethod()));

        if (Objects.nonNull(vo.getOperationMethod())) {
            condition = condition.and(entity.operationMethod.eq(vo.getOperationMethod()));
        }

        // Get max version
        final Integer maxVersion = jpaQueryFactory.select(entity.version)
                .from(entity)
                .where(condition)
                .orderBy(entity.version.desc())
                .fetchOne();

        // Query the configuration of the highest version
        condition = condition.and(entity.version.eq(maxVersion));
        final List<BizFeeConfiguration> configList = jpaQueryFactory.select(entity)
                .from(entity)
                .where(condition)
                .fetch();

        if (CollectionUtils.isEmpty(configList)) {
            return Collections.emptyList();
        }

        return modelMapper.convert(configList);
    }

}
