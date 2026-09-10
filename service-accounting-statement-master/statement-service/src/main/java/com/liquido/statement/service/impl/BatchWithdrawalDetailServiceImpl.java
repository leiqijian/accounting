package com.liquido.statement.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.entity.BatchWithdrawalDetail;
import com.liquido.statement.pojo.entity.QBatchWithdrawalDetail;
import com.liquido.statement.pojo.vo.ListBatchWithdrawalDetailVo;
import com.liquido.statement.repository.BatchWithdrawalDetailRepository;
import com.liquido.statement.service.BatchWithdrawalDetailService;

import com.github.wenhao.jpa.Specifications;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchWithdrawalDetailServiceImpl implements BatchWithdrawalDetailService {

    private final JPAQueryFactory jpaQueryFactory;

    private final BatchWithdrawalDetailRepository repository;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<BatchWithdrawalDetail> batchSave(
            final List<BatchWithdrawalDetail> entityList) {

        return repository.saveAllAndFlush(entityList);
    }

    @Override
    public boolean idempotentCheck(final Long transactionId,
                                   final DirectionTypeEnum directionType,
                                   final Long accountId) {

        final QBatchWithdrawalDetail entity = QBatchWithdrawalDetail.batchWithdrawalDetail;

        return Optional.ofNullable(jpaQueryFactory.select(entity.id)
                .from(entity)
                .where(entity.transactionId.eq(transactionId)
                        .and(entity.directionType.eq(directionType))
                        .and(entity.accountId.eq(accountId))
                        // state: 0: init; 1: processing; 2:success; 3: fail;
                        .and(entity.state.in(1, 2)))
                .limit(1)
                .fetchOne()).orElse(0L) <= 0;
    }

    @Override
    public List<BatchWithdrawalDetail> list(final ListBatchWithdrawalDetailVo vo) {
        final Specification<BatchWithdrawalDetail> spec =
                Specifications.<BatchWithdrawalDetail>and()
                        .eq(Objects.nonNull(vo.getBatchId()), "batchId", vo.getBatchId())
                        .build();
        return repository.findAll(spec);
    }

    @Override
    public void batchUpdateStateProcessing(final Long batchId) {
        final QBatchWithdrawalDetail entity = QBatchWithdrawalDetail.batchWithdrawalDetail;
        final long batchNum = Optional.ofNullable(
                jpaQueryFactory.select(entity.count()).from(entity)
                        .where(entity.batchId.eq(batchId))
                        .fetchOne()).orElse(0L);

        final long successNum = jpaQueryFactory.update(entity)
                .set(entity.state, 1)
                .set(entity.version, entity.version.add(1))
                .set(entity.updatedTime, LocalDateTimeUtil.nowUtc())
                .where(entity.batchId.eq(batchId).and(entity.state.eq(0)))
                .execute();

        if (successNum != batchNum) {
            throw StatementExceptionCode.UPDATE_BATCH_WITHDRAWAL_STATE_FAIL.exception();
        }
    }

    public void batchUpdateFinishState(final LocalDateTime completeTime,
                                       final LocalDate completeDate,
                                       final Long batchId, final Set<String> subMerchantId,
                                       final int state) {
        if (state < 2) {
            throw StatementExceptionCode.UPDATE_BATCH_WITHDRAWAL_STATE_FAIL.exception();
        }
        final QBatchWithdrawalDetail entity = QBatchWithdrawalDetail.batchWithdrawalDetail;
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

}
