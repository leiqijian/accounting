package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.statement.common.properties.StatementProperties;
import com.liquido.statement.enums.TransactionChargeBackStatusEnum;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.ChargeBackSummaryBo;
import com.liquido.statement.pojo.dto.ChargeBackSummaryOrderDto;
import com.liquido.statement.pojo.dto.TransactionChargeBackOrderDto;
import com.liquido.statement.pojo.entity.QTransactionChargeBackOrder;
import com.liquido.statement.pojo.entity.TransactionChargeBackOrder;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.AcceptTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.ChargeBackSummaryOrderVo;
import com.liquido.statement.pojo.vo.DefenseTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.PageTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.QueryTransactionChargeBackOrderVo;
import com.liquido.statement.pojo.vo.SolveTransactionChargeBackOrderVo;
import com.liquido.statement.repository.TransactionChargeBackOrderRepository;
import com.liquido.statement.service.TransactionChargeBackOrderService;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionChargeBackOrderServiceImpl implements TransactionChargeBackOrderService {

    private final JPAQueryFactory jpaQueryFactory;
    private final TransactionChargeBackOrderRepository transactionChargeBackOrderRepository;
    private final ModelMapper modelMapper;
    private final StatementProperties.ChargeBackOrder chargeBackOrderProperties;

    @Override
    public ChargeBackSummaryOrderDto chargeBackSummary(final ChargeBackSummaryOrderVo vo) {

        final QTransactionChargeBackOrder entity =
                QTransactionChargeBackOrder.transactionChargeBackOrder;

        final QBean<ChargeBackSummaryBo> bean = Projections.bean(ChargeBackSummaryBo.class,
                entity.id.count().coalesce(0L).as("count"),
                entity.disputeAmount.sum().coalesce(BigDecimal.ZERO).as("amount"),
                entity.currency,
                entity.status);

        final List<ChargeBackSummaryBo> summaryList = jpaQueryFactory.select(bean)
                .from(entity)
                .where(entity.accountId.eq(vo.getAccountId()),
                        entity.disputeTime.goe(vo.getStartDateTime()),
                        entity.disputeTime.lt(vo.getEndDateTime()))
                .groupBy(entity.status, entity.currency)
                .fetch();

        final ChargeBackSummaryOrderDto result = ChargeBackSummaryOrderDto.builder()
                .totalAmount(BigDecimal.ZERO)
                .totalCount(0L)
                .underDefenseAmount(BigDecimal.ZERO)
                .underDefenseCount(0L)
                .defenseWonAmount(BigDecimal.ZERO)
                .defenseWonCount(0L)
                .build();

        summaryList.forEach(bo -> {
            result.setTotalAmount(result.getTotalAmount().add(bo.getAmount()));
            result.setTotalCount(result.getTotalCount() + bo.getCount());
            Optional.ofNullable(result.getCurrency())
                    .ifPresentOrElse(t -> {
                    }, () -> result.setCurrency(bo.getCurrency()));

            if (TransactionChargeBackStatusEnum.DEFENSE_WON == bo.getStatus()) {
                result.setDefenseWonAmount(bo.getAmount());
                result.setDefenseWonCount(bo.getCount());
            } else if (TransactionChargeBackStatusEnum.UNDER_DEFENSE == bo.getStatus()) {
                result.setUnderDefenseAmount(bo.getAmount());
                result.setUnderDefenseCount(bo.getCount());
            }

        });
        return result;
    }

    @Override
    public PageVo<TransactionChargeBackOrderDto> chargeBackPage(
            final PageTransactionChargeBackOrderVo vo) {

        final PredicateBuilder<TransactionChargeBackOrder> spec = Specifications.and();
        spec.eq("accountId", vo.getAccountId());

        spec.ge(Objects.nonNull(vo.getDisputeTimeStartDateTime()), "disputeTime",
                vo.getDisputeTimeStartDateTime());
        spec.le(Objects.nonNull(vo.getDisputeTimeEndDateTime()), "disputeTime",
                vo.getDisputeTimeEndDateTime());

        spec.like(StringUtils.isNotBlank(vo.getUniqueId()), "uniqueId",
                vo.getUniqueId() + "%");


        Collection intersection =
                CollectionUtils.intersection(chargeBackOrderProperties.getSupportProducts(),
                        CollectionUtils.isNotEmpty(vo.getProductCodes()) ? vo.getProductCodes() :
                                chargeBackOrderProperties.getSupportProducts());
        if (CollectionUtils.isEmpty(intersection)) {
            intersection = chargeBackOrderProperties.getSupportProducts();
        }
        spec.in("productCode", intersection);

        spec.eq(Objects.nonNull(vo.getStatus()), "status", vo.getStatus());

        spec.ge(Objects.nonNull(vo.getStartDisputeAmount()), "disputeAmount",
                vo.getStartDisputeAmount());
        spec.le(Objects.nonNull(vo.getEndDisputeAmount()), "disputeAmount",
                vo.getEndDisputeAmount());

        spec.ge(Objects.nonNull(vo.getDefenseDeadlineStartDateTime()), "defenseDeadline",
                vo.getDefenseDeadlineStartDateTime());
        spec.le(Objects.nonNull(vo.getDefenseDeadlineEndDateTime()), "defenseDeadline",
                vo.getDefenseDeadlineEndDateTime());

        spec.ge(Objects.nonNull(vo.getPaymentTimeStartDateTime()), "paymentTime",
                vo.getPaymentTimeStartDateTime());
        spec.le(Objects.nonNull(vo.getPaymentTimeEndDateTime()), "paymentTime",
                vo.getPaymentTimeEndDateTime());

        final Page<TransactionChargeBackOrder> pageResult =
                transactionChargeBackOrderRepository.findAll(spec.build(),
                        PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(),
                                Sort.by(Sort.Order.desc("disputeTime"))));

        if (pageResult.getTotalElements() <= 0) {
            return PageVo.buildEmptyPage(vo.getPageSize());
        }

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(), pageResult.getTotalElements(),
                BeanCopierUtil.copyPropertyList(pageResult.getContent(),
                        TransactionChargeBackOrderDto.class));
    }

    @Override
    public TransactionChargeBackOrderDto chargeBackQuery(
            final QueryTransactionChargeBackOrderVo vo) {

        return this.toDto(getByIdAndMerchantId(vo.getId(), vo.getMerchantId()));
    }

    @Override
    public TransactionChargeBackOrder getById(final Long id) {
        return transactionChargeBackOrderRepository.findById(id)
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
    }

    @Override
    public TransactionChargeBackOrder getByIdAndMerchantId(final Long id, final Long merchantId) {
        final Specification<TransactionChargeBackOrder> spec =
                Specifications.<TransactionChargeBackOrder>and().eq("id", id)
                        .eq(Objects.nonNull(merchantId), "merchantId", merchantId)
                        .build();
        return transactionChargeBackOrderRepository.findOne(spec)
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void save(final List<TransactionChargeBackOrder> list) {
        transactionChargeBackOrderRepository.saveAll(list);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public TransactionChargeBackOrderDto chargeBackDefense(
            final DefenseTransactionChargeBackOrderVo vo) {

        final TransactionChargeBackOrder entity = getById(vo.getId());

        if (TransactionChargeBackStatusEnum.CHARGE_BACK != entity.getStatus()) {
            throw StatementExceptionCode.CHARGE_BACK_ORDER_REPETITION_DEFENSE.exception();
        }

        entity.setDefenseDescription(vo.getDefenseDescription());
        entity.setDefenseAppendixIds(vo.getDefenseAppendixIds());
        entity.setStatus(TransactionChargeBackStatusEnum.UNDER_DEFENSE);
        entity.setDefenseTime(LocalDateTimeUtil.nowUtc());

        return this.toDto(transactionChargeBackOrderRepository.save(entity));
    }

    private TransactionChargeBackOrderDto toDto(final TransactionChargeBackOrder order) {
        return modelMapper.convertTransactionChargeBackOrderDto(order);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void chargeBackDefenseSolve(final SolveTransactionChargeBackOrderVo vo) {

        final TransactionChargeBackOrder entity =
                transactionChargeBackOrderRepository.findByUniqueId(vo.getUniqueId());

        if (Objects.isNull(entity)) {
            return;
        }

        entity.setStatus(vo.getStatus());
        entity.setRefundResults(vo.getResult());

        transactionChargeBackOrderRepository.save(entity);
    }

    @Override
    public void chargeBackAccept(final AcceptTransactionChargeBackOrderVo vo) {

        final TransactionChargeBackOrder entity = getByIdAndMerchantId(vo.getId(),
                vo.getMerchantId());

        if (TransactionChargeBackStatusEnum.CHARGE_BACK != entity.getStatus()) {
            throw StatementExceptionCode.CHARGE_BACK_ORDER_REPETITION_ACCEPT.exception();
        }

        entity.setStatus(TransactionChargeBackStatusEnum.DEFENSE_LOST);
        entity.setDefenseTime(LocalDateTimeUtil.nowUtc());
        entity.setDefenseDescription("accept");

        transactionChargeBackOrderRepository.save(entity);
    }


}
