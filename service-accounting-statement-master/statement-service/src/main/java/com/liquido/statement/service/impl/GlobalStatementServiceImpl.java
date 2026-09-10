package com.liquido.statement.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.pojo.dto.GlobalStatementPageDto;
import com.liquido.statement.pojo.entity.GlobalStatement;
import com.liquido.statement.pojo.entity.QAccount;
import com.liquido.statement.pojo.entity.QGlobalAccount;
import com.liquido.statement.pojo.entity.QGlobalStatement;
import com.liquido.statement.pojo.vo.QueryGlobalStatementPageVo;
import com.liquido.statement.repository.GlobalStatementRepository;
import com.liquido.statement.service.GlobalStatementService;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GlobalStatementServiceImpl implements GlobalStatementService {
    private final JPAQueryFactory jpaQueryFactory;
    private final GlobalStatementRepository repository;

    @Override
    public void saveAccountFlow(final GlobalStatement statement) {
        repository.saveAndFlush(statement);
    }

    @Override
    public PageVo<GlobalStatementPageDto> queryGlobalStatementPage(
            final QueryGlobalStatementPageVo vo) {

        final QAccount account = QAccount.account;
        final QGlobalAccount globalAccount = QGlobalAccount.globalAccount;
        final QGlobalStatement globalStatement = QGlobalStatement.globalStatement;

        final List<BooleanExpression> condition = Lists.newArrayList();
        condition.add(globalAccount.merchantId.eq(vo.getMerchantId()));
        if (Objects.nonNull(vo.getCountryCode())) {
            condition.add(account.countryCode.eq(vo.getCountryCode()));
        }
        if (Objects.nonNull(vo.getBusinessType())) {
            condition.add(globalStatement.businessType.eq(vo.getBusinessType()));
        }
        if (Objects.nonNull(vo.getTargetType())) {
            condition.add(globalStatement.targetType.eq(vo.getTargetType()));
        }
        if (Objects.nonNull(vo.getStarTime())) {
            condition.add(globalStatement.createdTime.goe(vo.getStarTime()));
        }
        if (Objects.nonNull(vo.getEndTime())) {
            condition.add(globalStatement.createdTime.loe(vo.getEndTime()));
        }

        final QBean<GlobalStatementPageDto> bean = Projections.fields(GlobalStatementPageDto.class,
                globalStatement.id,
                globalStatement.businessType,
                globalStatement.targetType,
                globalStatement.amount,
                account.countryCode,
                account.transactionTypeCode,
                globalStatement.startBalance,
                globalStatement.endBalance,
                globalStatement.currency,
                globalStatement.createdTime,
                globalStatement.updatedTime);

        final BooleanExpression[] conditionArray = condition.toArray(new BooleanExpression[] {});

        final long count = jpaQueryFactory.select(globalStatement.id.count())
                .from(globalStatement)
                .leftJoin(globalAccount).on(globalStatement.globalAccountId.eq(globalAccount.id))
                .leftJoin(account).on(globalStatement.subAccountId.eq(account.id))
                .where(conditionArray)
                .fetchOne();
        if (count <= 0) {
            return new PageVo<>(vo.getPageNo(), vo.getPageSize(), count, Collections.emptyList());
        }

        final List<GlobalStatementPageDto> resultList = jpaQueryFactory.select(bean)
                .from(globalStatement)
                .leftJoin(globalAccount).on(globalStatement.globalAccountId.eq(globalAccount.id))
                .leftJoin(account).on(globalStatement.subAccountId.eq(account.id))
                .where(conditionArray)
                .orderBy(globalStatement.id.desc())
                .offset(vo.getOffset()).limit(vo.getPageSize())
                .fetch();

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(), count, resultList);
    }
}
