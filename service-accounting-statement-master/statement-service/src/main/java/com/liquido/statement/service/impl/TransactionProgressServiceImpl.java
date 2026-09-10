package com.liquido.statement.service.impl;

import static com.liquido.statement.pojo.entity.QTransactionInProgress.transactionInProgress;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.liquido.base.enums.TransactionInProgressStatusEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.statement.pojo.bo.SumTransactionInProgressNetAmountBo;
import com.liquido.statement.pojo.dto.TransactionInProgressDto;
import com.liquido.statement.pojo.entity.TransactionInProgress;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.BatchAddTransactionProgressVo;
import com.liquido.statement.repository.TransactionInProgressRepository;
import com.liquido.statement.service.TransactionProgressService;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionProgressServiceImpl implements TransactionProgressService {

    private final ModelMapper modelMapper;
    private final TransactionInProgressRepository transactionInProgressRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TransactionInProgressDto> batchAddTransactionProgress(
            final BatchAddTransactionProgressVo vo) {

        if (CollectionUtils.isEmpty(vo.getListVo())) {
            return Lists.newArrayList();
        }

        return toDto(transactionInProgressRepository.saveAll(
                vo.getListVo().stream().map(modelMapper::convert)
                        .collect(Collectors.toList())));
    }

    @Override
    public TransactionInProgressDto queryByUniqueId(final String uniqueId) {
        return Optional.ofNullable(transactionInProgressRepository.findByUniqueId(uniqueId))
                .map(this::toDto).orElse(null);
    }


    @Override
    public List<SumTransactionInProgressNetAmountBo> sumSubAccountPendAndSettlementAmount(
            final Collection<Long> accountIds,
            final Collection<String> subMerchantIds,
            final TransactionInProgress inProgress) {

        return jpaQueryFactory.select(Projections.fields(SumTransactionInProgressNetAmountBo.class,
                        transactionInProgress.netAmount.sum()
                                .coalesce(BigDecimal.ZERO).as("netAmount"),
                        transactionInProgress.subMerchantId,
                        transactionInProgress.accountId))
                .from(transactionInProgress)
                .where(transactionInProgress.accountId.in(accountIds)
                        .and(transactionInProgress.subMerchantId.in(subMerchantIds))
                        .and(transactionInProgress.id.goe(inProgress.getId()))
                        .and(transactionInProgress.status.eq(
                                TransactionInProgressStatusEnum.IN_PROGRESS))
                        .and(transactionInProgress.transactionStatus.eq(
                                TransactionStatusEnum.INITIAL_STATUS))
                        .and(transactionInProgress.createdTime.goe(inProgress.getCreatedTime())))
                .groupBy(transactionInProgress.accountId,
                        transactionInProgress.subMerchantId)
                .fetch();
    }

    @Override
    public TransactionInProgress findFirstInProgressDataByAccountAndSubMerchant(
            final Set<Long> accountIds,
            final Set<String> subMerchantIds) {
        return jpaQueryFactory.selectFrom(transactionInProgress)
                .where(transactionInProgress.accountId.in(accountIds)
                        .and(transactionInProgress.subMerchantId.in(subMerchantIds))
                        .and(transactionInProgress.status.eq(
                                TransactionInProgressStatusEnum.IN_PROGRESS))
                        .and(transactionInProgress.transactionStatus.eq(
                                TransactionStatusEnum.INITIAL_STATUS)))
                .orderBy(transactionInProgress.id.asc()).fetchFirst();
    }

    private TransactionInProgressDto toDto(final TransactionInProgress original) {
        return modelMapper.convert(original);
    }

    private List<TransactionInProgressDto> toDto(final List<TransactionInProgress> originalList) {
        return originalList.stream().map(this::toDto).collect(Collectors.toList());
    }
}
