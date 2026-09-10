package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.FeeTypeCodeEnum;
import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.base.enums.VersionEnum;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.DailyTransactionFeeBo;
import com.liquido.statement.pojo.dto.TransactionFeeDto;
import com.liquido.statement.pojo.dto.TransactionTaxDetailDto;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.QTransactionFee;
import com.liquido.statement.pojo.entity.TransactionFee;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.ListTransactionFeeVo;
import com.liquido.statement.pojo.vo.QueryTransactionTaxDetailVo;
import com.liquido.statement.pojo.vo.TransactionFeeVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;
import com.liquido.statement.repository.TransactionFeeRepository;
import com.liquido.statement.service.TransactionFeeService;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.google.common.collect.Lists;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionFeeServiceImpl implements TransactionFeeService {
    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final TransactionFeeRepository transactionFeeRepository;

    @Override
    public List<TransactionFee> findSettledFeeList(final Long transactionId) {
        final QTransactionFee entity = QTransactionFee.transactionFee;
        final BooleanExpression condition = entity.transactionId.eq(transactionId)
                .and(entity.directionType.eq(DirectionTypeEnum.SETTLED));

        return jpaQueryFactory.select(entity)
                .from(entity)
                .where(condition)
                .fetch();
    }

    @Override
    public List<TransactionFee> findTransactionFeeList(final Long transactionId,
                                                       final DirectionTypeEnum directionType) {

        final QTransactionFee entity = QTransactionFee.transactionFee;
        final BooleanExpression condition = entity.transactionId.eq(transactionId)
                .and(entity.directionType.eq(directionType));

        return jpaQueryFactory.select(entity)
                .from(entity)
                .where(condition)
                .fetch();
    }

    @Override
    public List<TransactionFee> findTransactionFee(final Set<Long> transactionIds) {
        final QTransactionFee entity = QTransactionFee.transactionFee;
        return jpaQueryFactory.select(entity)
                .from(entity)
                .where(entity.transactionId.in(transactionIds)
                        .and(entity.delFlag.eq(Boolean.FALSE)))
                .fetch();
    }

    @Override
    public List<TransactionFeeDto> findTransactionFee(final ListTransactionFeeVo vo) {
        final PredicateBuilder<TransactionFee> condition = Specifications.and();
        condition.eq(Objects.nonNull(vo.getTransactionId()), "transactionId",
                vo.getTransactionId());
        condition.eq(Objects.nonNull(vo.getUniqueId()), "uniqueId", vo.getUniqueId());
        condition.eq(Objects.nonNull(vo.getDirectionType()), "directionType",
                vo.getDirectionType());

        final List<TransactionFee> feeList = transactionFeeRepository.findAll(condition.build(),
                Sort.by(Sort.Order.asc("feeTypeCode")));

        return CollectionUtils.isEmpty(feeList)
                ? Collections.emptyList() : modelMapper.convertFeeList(feeList);

    }

    @Override
    public List<TransactionFee> batchSave(final List<TransactionMoneyVo> orderList,
                                          final AccountDailyInitBo dailyInitBo) {
        if (CollectionUtils.isEmpty(orderList)) {
            return Lists.newArrayList();
        }

        final List<TransactionFee> transactionFeeList = Lists.newArrayList();
        for (final TransactionMoneyVo moneyVo : orderList) {
            for (final TransactionFeeVo feeVo : moneyVo.getTransactionFeeList()) {
                transactionFeeList.add(this.convertEntity(moneyVo, feeVo, dailyInitBo));
            }
        }

        if (CollectionUtils.isEmpty(transactionFeeList)) {
            return Lists.newArrayList();
        }

        return transactionFeeRepository.saveAllAndFlush(transactionFeeList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean updateState(final Long feeId,
                               final SettleStatusEnum fromState,
                               final SettleStatusEnum toState,
                               final Integer fromVersion) {
        final QTransactionFee entity = QTransactionFee.transactionFee;
        final long result = jpaQueryFactory.update(entity)
                .set(entity.settleStatus, toState)
                .set(entity.version, entity.version.add(1))
                .set(entity.settleTime, LocalDateTimeUtil.nowUtc())
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.id.eq(feeId)
                        .and(entity.settleStatus.eq(fromState))
                        .and(entity.version.eq(fromVersion)))
                .execute();

        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean batchUpdateState(final List<Long> feeIdList,
                                    final SettleStatusEnum fromState,
                                    final SettleStatusEnum toState,
                                    final Integer fromVersion) {
        final QTransactionFee entity = QTransactionFee.transactionFee;
        final long result = jpaQueryFactory.update(entity)
                .set(entity.settleStatus, toState)
                .set(entity.version, entity.version.add(1))
                .set(entity.settleTime, LocalDateTimeUtil.nowUtc())
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.id.in(feeIdList)
                        .and(entity.settleStatus.eq(fromState))
                        .and(entity.version.eq(fromVersion)))
                .execute();

        return result > 0;
    }

    @Override
    public List<DailyTransactionFeeBo> statisticsDailyBill(final Account account,
                                                           final AccountDailyInitBo dailyInitBo) {
        final QTransactionFee entity = QTransactionFee.transactionFee;
        final BooleanExpression condition = entity.accountId.eq(account.getId())
                .and(entity.billId.eq(dailyInitBo.getBillId()));

        final QBean<DailyTransactionFeeBo> bean = Projections.fields(DailyTransactionFeeBo.class,
                entity.feeGroup.as("feeGroup"),

                (entity.calculateAmount.multiply(entity.amountPon)).sum()
                        .coalesce(BigDecimal.ZERO).as("totalCalculateAmount"),

                (entity.settlementAmount.multiply(entity.amountPon)).sum()
                        .coalesce(BigDecimal.ZERO).as("totalAmount"),

                entity.settlementCurrency.as("settlementCurrency"),
                entity.instantFlag.as("instantFlag"),
                entity.id.count().coalesce(0L).as("totalCount"));

        return jpaQueryFactory.select(bean)
                .from(entity)
                .where(condition)
                .groupBy(entity.feeGroup, entity.settlementCurrency, entity.instantFlag)
                .fetch();
    }

    @Override
    public List<TransactionTaxDetailDto> findTransactionTaxDetail(
            final QueryTransactionTaxDetailVo vo) {

        final QTransactionFee entity = QTransactionFee.transactionFee;

        BooleanExpression condition = entity.accountId.eq(vo.getAccountId())
                .and(entity.billId.eq(vo.getBillId()))
                .and(entity.feeTypeCode.eq(FeeTypeCodeEnum.TAX))
                .and(entity.instantFlag.eq(Boolean.TRUE));

        if (Objects.nonNull(vo.getMerchantId())) {
            condition = condition.and(entity.merchantId.eq(vo.getMerchantId()));
        }

        final QBean<TransactionTaxDetailDto> bean =
                Projections.fields(TransactionTaxDetailDto.class,
                        entity.feeName.as("feeName"),
                        entity.settlementAmount.sum().coalesce(BigDecimal.ZERO).as("totalAmount"),
                        entity.settlementCurrency.as("currency"));

        return jpaQueryFactory.select(bean)
                .from(entity)
                .where(condition)
                .groupBy(entity.feeName)
                .fetch();

    }

    private TransactionFee convertEntity(final TransactionMoneyVo moneyVo,
                                         final TransactionFeeVo feeVo,
                                         final AccountDailyInitBo dailyInitBo) {
        return TransactionFee.builder()
                .id(SnowflakeIdUtil.generate())
                .uniqueId(moneyVo.getUniqueId())
                .transactionId(moneyVo.getTransactionId())
                .merchantId(moneyVo.getMerchantId())
                .subMerchantId(moneyVo.getSubMerchantId())
                .accountId(moneyVo.getAccountId())
                .feeConfigurationId(feeVo.getFeeConfigurationId())
                .transactionTypeCode(moneyVo.getTransactionTypeCode())
                .productCode(moneyVo.getProductCode())
                .directionType(moneyVo.getDirectionType())
                .feeName(feeVo.getFeeName())
                .feeTypeCode(feeVo.getFeeTypeCode())
                .feeGroup(feeVo.getFeeGroup())
                .settleStatus(feeVo.getInstantFlag() ? SettleStatusEnum.PROCESSING :
                        SettleStatusEnum.WAITING)
                .instantFlag(feeVo.getInstantFlag())
                .transactionTime(moneyVo.getTransactionTime())
                .amountPon(feeVo.getAmountPon().getCode())
                .calculateAmount(Optional.ofNullable(feeVo.getCalculateAmount())
                        .orElse(BigDecimal.ZERO))
                .settlementAmount(Optional.ofNullable(feeVo.getSettlementAmount())
                        .orElse(BigDecimal.ZERO))
                .settlementAmountUsd(Optional.ofNullable(feeVo.getSettlementAmountUsd())
                        .orElse(BigDecimal.ZERO))
                .settlementCurrency(feeVo.getSettlementCurrency())
                .settleTime(LocalDateTimeUtil.nowUtc())
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(0L)
                .updatedBy(0L)
                .billId(dailyInitBo.getBillId())
                .version(VersionEnum.LOCKED.getCode())
                .build();
    }
}
