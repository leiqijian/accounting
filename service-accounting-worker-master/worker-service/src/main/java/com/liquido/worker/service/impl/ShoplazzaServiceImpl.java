package com.liquido.worker.service.impl;

import java.util.Objects;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.PageUtil;
import com.liquido.core.mvc.enums.SortTypeEnum;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.PageShoplazzaDto;
import com.liquido.worker.pojo.dto.QueryShoplazzaDto;
import com.liquido.worker.pojo.entity.Shoplazza;
import com.liquido.worker.pojo.mapper.ModelMapper;
import com.liquido.worker.pojo.vo.PageShoplazzaVo;
import com.liquido.worker.pojo.vo.QueryShoplazzaVo;
import com.liquido.worker.repository.ShoplazzaRepository;
import com.liquido.worker.service.ShoplazzaService;

import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
public class ShoplazzaServiceImpl implements ShoplazzaService {

    private final ShoplazzaRepository shoplazzaRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageVo<PageShoplazzaDto> pageShoplazza(PageShoplazzaVo vo) {
        final PredicateBuilder<Shoplazza> specBuilder =
                Specifications.<Shoplazza>and()
                        .eq("merchantCode", vo.getMerchantCode())
                        .eq("countryCode", vo.getCountryCode())
                        .eq("transactionTypeCode", TransactionTypeCodeEnum.PAY_IN)
                        .ge(Objects.nonNull(vo.getStartDate()), "submitTimestamp",
                                LocalDateTimeUtil.utcToInstant(vo.getStartDate()))
                        .le(Objects.nonNull(vo.getEndDate()), "submitTimestamp",
                                LocalDateTimeUtil.utcToInstant(vo.getEndDate()))
                        .eq(StringUtils.isNotBlank(vo.getLinkId()), "linkId", vo.getLinkId())
                        .eq(StringUtils.isNotBlank(vo.getOrderId()), "orderId",
                                vo.getOrderId())
                        .eq(Objects.nonNull(vo.getPaymentStatus()), "paymentStatus",
                                vo.getPaymentStatus());

        Sort.Order order = Sort.Order.desc("submitTimestamp");
        if (ObjectUtils.isNotEmpty(vo.getSortField()) && ObjectUtils.isNotEmpty(vo.getSortType())) {
            order = SortTypeEnum.ASC == vo.getSortType() ? Sort.Order.asc(vo.getSortField())
                    : Sort.Order.desc(vo.getSortField());
        }

        final Page<Shoplazza> page =
                shoplazzaRepository.findAll(specBuilder.build(),
                        PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(), Sort.by(order)));

        return PageUtil.buildPage(page, vo, PageShoplazzaDto.class);
    }

    @Override
    public QueryShoplazzaDto queryShoplazza(final QueryShoplazzaVo vo) {
        if (Objects.isNull(vo)
                || ObjectUtils.allNull(vo.getId(), vo.getLinkId(), vo.getOrderId())) {
            return null;
        }

        final PredicateBuilder<Shoplazza> spec = Specifications.and();
        spec.eq(Objects.nonNull(vo.getId()), "id", vo.getId());
        spec.eq(StringUtils.isNotBlank(vo.getLinkId()), "linkId", vo.getLinkId());
        spec.eq(StringUtils.isNotBlank(vo.getOrderId()), "orderId", vo.getOrderId());
        spec.eq(StringUtils.isNotBlank(vo.getMerchantCode()), "merchantCode", vo.getMerchantCode());

        final Shoplazza shoplazza = shoplazzaRepository.findOne(spec.build()).orElse(null);

        if (Objects.isNull(shoplazza)) {
            return null;
        }

        return modelMapper.convert(shoplazza);
    }
}
