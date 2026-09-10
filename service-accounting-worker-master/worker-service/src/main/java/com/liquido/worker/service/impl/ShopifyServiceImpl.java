package com.liquido.worker.service.impl;

import java.util.Objects;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.PageUtil;
import com.liquido.core.mvc.enums.SortTypeEnum;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.pojo.dto.PageShopifyDto;
import com.liquido.worker.pojo.dto.QueryShopifyDto;
import com.liquido.worker.pojo.entity.Shopify;
import com.liquido.worker.pojo.mapper.ModelMapper;
import com.liquido.worker.pojo.vo.PageShopifyVo;
import com.liquido.worker.pojo.vo.QueryShopifyVo;
import com.liquido.worker.repository.ShopifyRepository;
import com.liquido.worker.service.ShopifyService;

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
public class ShopifyServiceImpl implements ShopifyService {

    private final ShopifyRepository shopifyRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageVo<PageShopifyDto> pageShopify(PageShopifyVo vo) {
        final PredicateBuilder<Shopify> specBuilder =
                Specifications.<Shopify>and()
                        .eq("merchantCode", vo.getMerchantCode())
                        .eq("countryCode", vo.getCountryCode())
                        .eq("transactionTypeCode", TransactionTypeCodeEnum.PAY_IN)
                        .ge(Objects.nonNull(vo.getStartDate()), "submitTimestamp",
                                LocalDateTimeUtil.utcToInstant(vo.getStartDate()))
                        .le(Objects.nonNull(vo.getEndDate()), "submitTimestamp",
                                LocalDateTimeUtil.utcToInstant(vo.getEndDate()))
                        .eq(StringUtils.isNotBlank(vo.getPaymentId()), "paymentId",
                                vo.getPaymentId())
                        .eq(Objects.nonNull(vo.getPaymentStatus()), "paymentStatus",
                                vo.getPaymentStatus())
                        .eq(Objects.nonNull(vo.getRefundStatus()), "refundStatus",
                                vo.getRefundStatus());

        Sort.Order order = Sort.Order.desc("submitTimestamp");
        if (ObjectUtils.isNotEmpty(vo.getSortField()) && ObjectUtils.isNotEmpty(vo.getSortType())) {
            order = SortTypeEnum.ASC == vo.getSortType() ? Sort.Order.asc(vo.getSortField())
                    : Sort.Order.desc(vo.getSortField());
        }

        final Page<Shopify> page =
                shopifyRepository.findAll(specBuilder.build(),
                        PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(), Sort.by(order)));

        return PageUtil.buildPage(page, vo, PageShopifyDto.class);
    }

    @Override
    public QueryShopifyDto queryShopify(final QueryShopifyVo vo) {
        if (Objects.isNull(vo) || ObjectUtils.allNull(vo.getId(), vo.getPaymentId())) {
            return null;
        }

        final PredicateBuilder<Shopify> spec = Specifications.and();
        spec.eq(Objects.nonNull(vo.getId()), "id", vo.getId());
        spec.eq(StringUtils.isNotBlank(vo.getPaymentId()), "paymentId", vo.getPaymentId());
        spec.eq(StringUtils.isNotBlank(vo.getMerchantCode()), "merchantCode", vo.getMerchantCode());

        final Shopify shopify = shopifyRepository.findOne(spec.build()).orElse(null);

        if (Objects.isNull(shopify)) {
            return null;
        }

        return modelMapper.convert(shopify);
    }

}
