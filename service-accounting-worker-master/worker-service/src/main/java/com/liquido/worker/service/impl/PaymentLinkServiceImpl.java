package com.liquido.worker.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.liquido.base.BaseApis;
import com.liquido.base.enums.DataSyncStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.base.pojo.vo.QueryMerchantVo;
import com.liquido.base.pojo.vo.QuerySubMerchantListVo;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.enums.SortTypeEnum;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.common.Constant;
import com.liquido.worker.feign.PaymentLinkFeign;
import com.liquido.worker.pojo.dto.PagePaymentLinkDto;
import com.liquido.worker.pojo.dto.QueryPaymentLinkDto;
import com.liquido.worker.pojo.entity.PaymentLink;
import com.liquido.worker.pojo.entity.QPaymentLink;
import com.liquido.worker.pojo.mapper.ModelMapper;
import com.liquido.worker.pojo.vo.BatchQueryPaymentLinkVo;
import com.liquido.worker.pojo.vo.PagePaymentLinkVo;
import com.liquido.worker.pojo.vo.QueryPaymentLinkVo;
import com.liquido.worker.repository.PaymentLinkRepository;
import com.liquido.worker.service.PaymentLinkService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.google.common.collect.Lists;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
public class PaymentLinkServiceImpl implements PaymentLinkService {

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final BaseApis.BaseFeign baseFeign;
    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final PaymentLinkFeign paymentLinkFeign;
    private final PaymentLinkRepository paymentLinkRepository;

    @Override
    public PageVo<PagePaymentLinkDto> pagePaymentLink(final PagePaymentLinkVo vo) {
        final PredicateBuilder<PaymentLink> specBuilder =
                Specifications.<PaymentLink>and()
                        .eq("merchantCode", vo.getMerchantCode())
                        .eq("countryCode", vo.getCountryCode())
                        .eq("transactionTypeCode", TransactionTypeCodeEnum.PAY_IN)
                        .ge(Objects.nonNull(vo.getStartDate()), "submitTimestamp",
                                LocalDateTimeUtil.utcToInstant(vo.getStartDate()))
                        .le(Objects.nonNull(vo.getEndDate()), "submitTimestamp",
                                LocalDateTimeUtil.utcToInstant(vo.getEndDate()))
                        .eq(StringUtils.isNotBlank(vo.getLinkId()), "linkId", vo.getLinkId())
                        .eq(StringUtils.isNotBlank(vo.getSubMerchantId()), "subMerchantId",
                                vo.getSubMerchantId())
                        .in(CollectionUtils.isNotEmpty(vo.getMerchantReferences()),
                                "merchantReference", vo.getMerchantReferences())
                        .eq(Objects.nonNull(vo.getPaymentStatus()), "paymentStatus",
                                vo.getPaymentStatus())
                        .like(StringUtils.isNotBlank(vo.getDescription()), "description",
                                String.format("%s%%", vo.getDescription()))
                        .eq(Objects.nonNull(vo.getRefundStatus()), "refundStatus",
                                vo.getRefundStatus());

        Sort.Order order = Sort.Order.desc("submitTimestamp");
        if (ObjectUtils.isNotEmpty(vo.getSortField()) && ObjectUtils.isNotEmpty(vo.getSortType())) {
            order = SortTypeEnum.ASC == vo.getSortType() ? Sort.Order.asc(vo.getSortField())
                    : Sort.Order.desc(vo.getSortField());
        }

        final Page<PaymentLink> page =
                paymentLinkRepository.findAll(specBuilder.build(),
                        PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(), Sort.by(order)));

        List<PagePaymentLinkDto> paymentLinkDtoList =
                page.getContent().stream().map(modelMapper::convert).collect(Collectors.toList());
        this.buildSubMerchantName(paymentLinkDtoList);

        return new PageVo<>(vo.getPageNo(), vo.getPageSize(), page.getTotalElements(),
                paymentLinkDtoList);
    }

    private void buildSubMerchantName(final List<PagePaymentLinkDto> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        for (PagePaymentLinkDto item : dataList) {
            if (StringUtils.isNotBlank(item.getSubMerchantId())) {
                final String subMerchantName = redisCacheUtil.getCacheMapValue(
                        Constant.CACHE.SUB_MERCHANT_INFO, item.getSubMerchantId());

                if (StringUtils.isNotBlank(subMerchantName)) {
                    item.setSubMerchantName(subMerchantName);
                    continue;
                }

                final List<SubMerchantDto> subMerchantList = loadSubMerchantInfo(
                        dataList.get(0).getMerchantCode(),
                        dataList.stream().map(PagePaymentLinkDto::getSubMerchantId)
                                .collect(Collectors.toSet()));

                final Map<String, String> resultMap = subMerchantList.stream()
                        .collect(Collectors.toMap(
                                SubMerchantDto::getSubMerchantId,
                                x -> StringUtils.defaultIfBlank(x.getCommercialName(), "-"),
                                (k1, k2) -> k2));

                item.setSubMerchantName(resultMap.get(item.getSubMerchantId()));
                redisCacheUtil.setCacheMap(Constant.CACHE.SUB_MERCHANT_INFO, resultMap);
            }
        }
    }

    private List<SubMerchantDto> loadSubMerchantInfo(
            final String merchantCode,
            final Set<String> subMerchantIds) {

        final MerchantDto merchantDto = CheckResponseUtil.checkAndReturnResponseData(
                baseFeign.getEffectiveMerchantInfo(QueryMerchantVo.builder()
                        .code(merchantCode).build()));

        final ResponseDto<List<SubMerchantDto>> subMerchantResp =
                baseFeign.queryBySubMerchantId(QuerySubMerchantListVo.builder()
                        .merchantId(merchantDto.getId())
                        .subMerchantId(subMerchantIds)
                        .build());

        if (ResponseDto.isFail(subMerchantResp)
                || CollectionUtils.isEmpty(subMerchantResp.getData())) {
            return Collections.emptyList();
        }

        return subMerchantResp.getData();
    }

    @Override
    public QueryPaymentLinkDto queryPaymentLink(final QueryPaymentLinkVo vo) {

        if (ObjectUtils.allNull(vo, vo.getId(), vo.getLinkId(), vo.getMerchantReference(),
                vo.getMerchantCode())) {
            return null;
        }

        final PredicateBuilder<PaymentLink> spec = Specifications.and();
        spec.eq(Objects.nonNull(vo.getId()), "id", vo.getId());
        spec.eq(StringUtils.isNotBlank(vo.getLinkId()), "linkId", vo.getLinkId());
        spec.eq(StringUtils.isNotBlank(vo.getMerchantReference()), "merchantReference",
                vo.getMerchantReference());
        spec.eq(StringUtils.isNotBlank(vo.getMerchantCode()), "merchantCode",
                vo.getMerchantCode());

        final PaymentLink paymentLink = paymentLinkRepository.findOne(spec.build()).orElse(null);
        if (Objects.isNull(paymentLink)) {
            return null;
        }

        final JsonNode refundInfo = DataSyncStatusEnum.SETTLED == paymentLink.getPaymentStatus()
                ? queryRefundInfo(paymentLink.getLinkId()) : null;

        return modelMapper.convert(paymentLink, refundInfo, baseFeign);
    }

    @Override
    public List<String> queryExistsPaymentLinkOrderIds(final BatchQueryPaymentLinkVo vo) {
        final QPaymentLink paymentLink = QPaymentLink.paymentLink;

        if (Objects.isNull(vo) || CollectionUtils.isEmpty(vo.getMerchantReference())) {
            return Lists.newArrayList();
        }

        final List<String> resultList = Lists.newArrayList();

        //batch verification of order exists
        final List<List<String>> batchList = Lists.partition(vo.getMerchantReference(), 100);
        for (final List<String> orderList : batchList) {
            final List<String> result = jpaQueryFactory.select(paymentLink.merchantReference)
                    .from(paymentLink)
                    .where(paymentLink.merchantReference.in(orderList)
                            .and(paymentLink.merchantCode.eq(vo.getMerchantCode())))
                    .fetch();
            resultList.addAll(result);
        }

        return resultList;
    }

    @SneakyThrows
    private JsonNode queryRefundInfo(final String linkId) {
        final String cacheKey = String.format(Constant.CACHE.PAYMENT_LINK_REFUND_INFO, linkId);
        String info = redisCacheUtil.getCacheObject(cacheKey);
        if (Objects.isNull(info)) {
            info = objectMapper.writeValueAsString(paymentLinkFeign.queryRefund(linkId).getData());
            if (Objects.nonNull(info)) {
                redisCacheUtil.setCacheObject(cacheKey, info, 2, TimeUnit.DAYS);
            } else {
                return null;
            }
        }
        return objectMapper.readTree(info);
    }

}
