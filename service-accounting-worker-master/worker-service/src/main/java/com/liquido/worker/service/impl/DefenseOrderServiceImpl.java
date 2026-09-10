package com.liquido.worker.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.enums.DefenseStatusEnum;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.pojo.bo.DefenseOrderSummaryBo;
import com.liquido.worker.pojo.dto.DefenseOrderDto;
import com.liquido.worker.pojo.dto.SummaryDefenseOrderDto;
import com.liquido.worker.pojo.entity.DefenseOrder;
import com.liquido.worker.pojo.entity.QDefenseOrder;
import com.liquido.worker.pojo.mapper.ModelMapper;
import com.liquido.worker.pojo.vo.AcceptDefenseOrderVo;
import com.liquido.worker.pojo.vo.DefenseOrderDefenseVo;
import com.liquido.worker.pojo.vo.PageDefenseOrderVo;
import com.liquido.worker.pojo.vo.QueryDefenseOrderVo;
import com.liquido.worker.pojo.vo.SolveDefenseOrderVo;
import com.liquido.worker.pojo.vo.SummaryDefenseOrderVo;
import com.liquido.worker.repository.DefenseOrderRepository;
import com.liquido.worker.service.DefenseOrderService;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefenseOrderServiceImpl implements DefenseOrderService {

    private final JPAQueryFactory jpaQueryFactory;
    private final DefenseOrderRepository defenseOrderRepository;
    private final ModelMapper modelMapper;
    private final WorkerProperties.DefenseOrder defenseOrderProperties;

    @Override
    public SummaryDefenseOrderDto defenseOrderSummary(final SummaryDefenseOrderVo vo) {

        final QDefenseOrder entity =
                QDefenseOrder.defenseOrder;

        final QBean<DefenseOrderSummaryBo> bean = Projections.bean(DefenseOrderSummaryBo.class,
                entity.id.count().coalesce(0L).as("count"),
                entity.disputeAmount.sum().coalesce(BigDecimal.ZERO).as("amount"),
                entity.currency,
                entity.defenseStatus);

        final List<DefenseOrderSummaryBo> summaryList = jpaQueryFactory.select(bean)
                .from(entity)
                .where(entity.accountId.eq(vo.getAccountId()),
                        entity.disputeTime.goe(vo.getStartDateTime()),
                        entity.disputeTime.lt(vo.getEndDateTime()))
                .groupBy(entity.defenseStatus, entity.currency)
                .fetch();

        final SummaryDefenseOrderDto result = SummaryDefenseOrderDto.builder()
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

            if (DefenseStatusEnum.DEFENSE_WON == bo.getDefenseStatus()) {
                result.setDefenseWonAmount(bo.getAmount());
                result.setDefenseWonCount(bo.getCount());
            } else if (DefenseStatusEnum.UNDER_DEFENSE == bo.getDefenseStatus()) {
                result.setUnderDefenseAmount(bo.getAmount());
                result.setUnderDefenseCount(bo.getCount());
            }

        });
        return result;
    }

    @Override
    public PageVo<DefenseOrderDto> defenseOrderPage(
            final PageDefenseOrderVo vo) {

        final PredicateBuilder<DefenseOrder> spec = Specifications.and();
        spec.eq("accountId", vo.getAccountId());

        spec.ge(Objects.nonNull(vo.getDisputeTimeStartDateTime()), "disputeTime",
                vo.getDisputeTimeStartDateTime());
        spec.le(Objects.nonNull(vo.getDisputeTimeEndDateTime()), "disputeTime",
                vo.getDisputeTimeEndDateTime());

        spec.like(StringUtils.isNotBlank(vo.getUniqueId()), "uniqueId",
                vo.getUniqueId() + "%");


        Collection intersection =
                CollectionUtils.intersection(defenseOrderProperties.getSupportProducts(),
                        CollectionUtils.isNotEmpty(vo.getProductCodes()) ? vo.getProductCodes() :
                                defenseOrderProperties.getSupportProducts());
        if (CollectionUtils.isEmpty(intersection)) {
            intersection = defenseOrderProperties.getSupportProducts();
        }
        spec.in("productCode", intersection);

        spec.eq(Objects.nonNull(vo.getDefenseStatus()), "defenseStatus", vo.getDefenseStatus());

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

        final Page<DefenseOrder> pageResult =
                defenseOrderRepository.findAll(spec.build(),
                        PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(),
                                Sort.by(Sort.Order.desc("disputeTime"))));

        if (pageResult.getTotalElements() <= 0) {
            return PageVo.buildEmptyPage(vo.getPageSize());
        }

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(), pageResult.getTotalElements(),
                BeanCopierUtil.copyPropertyList(pageResult.getContent(),
                        DefenseOrderDto.class));
    }

    @Override
    public DefenseOrderDto defenseOrderQuery(
            final QueryDefenseOrderVo vo) {

        return this.toDto(getById(vo.getId()));
    }

    @Override
    public DefenseOrderDto defenseOrderQueryByUniqueId(final String uniqueId) {

        final DefenseOrder entity =
                defenseOrderRepository.findByUniqueId(uniqueId);

        return this.toDto(entity);
    }

    @Override
    public DefenseOrder getById(final Long id) {
        return defenseOrderRepository.findById(id)
                .orElseThrow(CommonExceptionCode.DATA_NOT_FOUND::exception);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void save(final List<DefenseOrder> list) {
        defenseOrderRepository.saveAll(list);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public DefenseOrderDto defenseOrderDefense(
            final DefenseOrderDefenseVo vo) {

        final DefenseOrder entity = getById(vo.getId());

        if (DefenseStatusEnum.CHARGE_BACK != entity.getDefenseStatus()) {
            throw WorkerExceptionCode.CHARGE_BACK_ORDER_REPETITION_DEFENSE.exception();
        }

        entity.setDefenseDescription(vo.getDefenseDescription());
        entity.setDefenseAppendixIds(vo.getDefenseAppendixIds());
        entity.setDefenseStatus(DefenseStatusEnum.UNDER_DEFENSE);
        entity.setDefenseTime(LocalDateTimeUtil.nowUtc());

        return this.toDto(defenseOrderRepository.save(entity));
    }

    private DefenseOrderDto toDto(final DefenseOrder order) {
        return modelMapper.convertDefenseOrderDto(order);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void defenseOrderSolve(final SolveDefenseOrderVo vo) {

        final DefenseOrder entity =
                defenseOrderRepository.findByUniqueId(vo.getUniqueId());

        if (Objects.isNull(entity)) {
            return;
        }

        entity.setDefenseStatus(vo.getDefenseStatus());

        defenseOrderRepository.save(entity);
    }

    @Override
    public void defenseOrderAccept(final AcceptDefenseOrderVo vo) {

        final DefenseOrder entity = getById(vo.getId());

        if (DefenseStatusEnum.CHARGE_BACK != entity.getDefenseStatus()) {
            throw WorkerExceptionCode.CHARGE_BACK_ORDER_REPETITION_ACCEPT.exception();
        }

        entity.setDefenseStatus(DefenseStatusEnum.DEFENSE_LOST);
        entity.setDefenseTime(LocalDateTimeUtil.nowUtc());
        entity.setDefenseDescription("accept");

        defenseOrderRepository.save(entity);
    }


}
