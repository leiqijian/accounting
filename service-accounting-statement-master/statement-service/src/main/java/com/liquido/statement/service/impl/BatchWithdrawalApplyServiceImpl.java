package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.entity.BatchWithdrawalApply;
import com.liquido.statement.pojo.entity.BatchWithdrawalDetail;
import com.liquido.statement.pojo.entity.QBatchWithdrawalApply;
import com.liquido.statement.pojo.vo.ListBatchWithdrawalApplyVo;
import com.liquido.statement.repository.BatchWithdrawalApplyRepository;
import com.liquido.statement.service.BatchWithdrawalApplyService;

import com.github.wenhao.jpa.Specifications;
import com.google.common.collect.Lists;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchWithdrawalApplyServiceImpl implements BatchWithdrawalApplyService {

    private final JPAQueryFactory jpaQueryFactory;
    private final BatchWithdrawalApplyRepository repository;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<BatchWithdrawalApply> batchSave(
            final long batchId,
            final List<BatchWithdrawalDetail> dataList) {

        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        final Map<String, List<BatchWithdrawalDetail>> dataMap = dataList.stream()
                .collect(Collectors.groupingBy(BatchWithdrawalDetail::getSubMerchantId));

        final List<BatchWithdrawalApply> entityList = Lists.newArrayList();
        for (Map.Entry<String, List<BatchWithdrawalDetail>> entry : dataMap.entrySet()) {
            entityList.add(createBatchWithdrawalApply(batchId, entry.getValue()));
        }

        return repository.saveAllAndFlush(entityList);
    }

    @Override
    public List<BatchWithdrawalApply> queryApplyList(final ListBatchWithdrawalApplyVo vo) {
        final Specification<BatchWithdrawalApply> spec = Specifications.<BatchWithdrawalApply>and()
                .eq(Objects.nonNull(vo.getBatchId()), "batchId", vo.getBatchId())
                .build();
        return repository.findAll(spec);
    }

    @Override
    public List<BatchWithdrawalApply> batchSave(final List<BatchWithdrawalApply> dataList) {
        return repository.saveAllAndFlush(dataList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchUpdateStateProcessing(final List<Long> batchWithdrawalApplyIds) {
        final QBatchWithdrawalApply entity = QBatchWithdrawalApply.batchWithdrawalApply;
        final long successNum = jpaQueryFactory.update(entity)
                .set(entity.state, 1)
                .set(entity.version, entity.version.add(1))
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.id.in(batchWithdrawalApplyIds).and(entity.state.eq(0)))
                .execute();

        if (successNum != batchWithdrawalApplyIds.size()) {
            throw StatementExceptionCode.UPDATE_BATCH_WITHDRAWAL_STATE_FAIL.exception();
        }
    }

    @Override
    public void batchUpdateFinishState(final LocalDateTime completeTime,
                                       final LocalDate completeDate,
                                       final Long batchId, final Set<String> subMerchantId,
                                       final int state) {
        if (state < 2) {
            throw StatementExceptionCode.UPDATE_BATCH_WITHDRAWAL_STATE_FAIL.exception();
        }
        final QBatchWithdrawalApply entity = QBatchWithdrawalApply.batchWithdrawalApply;
        final long willUpdateNum = jpaQueryFactory.select(entity.count()).from(entity)
                .where(entity.batchId.eq(batchId)
                        .and(entity.subMerchantId.in(subMerchantId)))
                .fetchFirst();
        final long updateNum = jpaQueryFactory.update(entity)
                .set(entity.state, state)
                .set(entity.completedTime, completeTime)
                .set(entity.completedDate, completeDate)
                .where(entity.batchId.eq(batchId)
                        .and(entity.subMerchantId.in(subMerchantId))
                        .and(entity.state.eq(1))).execute();
        if (updateNum != willUpdateNum) {
            throw StatementExceptionCode.UPDATE_BATCH_WITHDRAWAL_STATE_FAIL.exception();
        }
    }

    private BatchWithdrawalApply createBatchWithdrawalApply(
            final long batchId,
            final List<BatchWithdrawalDetail> details) {

        final BatchWithdrawalDetail item = details.get(0);

        return BatchWithdrawalApply.builder()
                .id(SnowflakeIdUtil.generate())
                .batchId(batchId)
                .merchantId(item.getMerchantId())
                .subMerchantId(item.getSubMerchantId())
                .subMerchantName(item.getSubMerchantName())
                .accountId(item.getAccountId())
                .subAccountId(item.getSubAccountId())

                .transactionCount(details.size())
                .withdrawalAmount(details.stream()
                        .map(BatchWithdrawalDetail::getCreditedAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .settlementCurrency(item.getSettlementCurrency())

                .feeAmount(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)

                .accountType("")
                .accountName("")
                .accountNumber("")
                .applyDate(item.getApplyDate())
                .completedDate(null)
                .completedTime(null)
                .state(0)
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(0L)
                .updatedBy(0L)
                .version(0)
                .delFlag(false)
                .build();
    }


}
