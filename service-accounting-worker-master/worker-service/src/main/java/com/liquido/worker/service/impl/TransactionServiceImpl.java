package com.liquido.worker.service.impl;

import static com.liquido.worker.pojo.entity.QTransactionRatio.transactionRatio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.liquido.base.BaseApis;
import com.liquido.base.constant.dynamic.DynamicConstant;
import com.liquido.base.enums.DictionaryTypeEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.HoldStatusEnum;
import com.liquido.base.enums.PaymentProofEnum;
import com.liquido.base.enums.TransactionDataSourceEnum;
import com.liquido.base.enums.TransactionStatusEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.enums.TransferErrorResultCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.base.pojo.vo.QueryMerchantVo;
import com.liquido.base.pojo.vo.QuerySubMerchantListVo;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.BeanCopierUtil;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.core.mvc.enums.SortTypeEnum;
import com.liquido.core.mvc.vo.PageVo;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.monitor.LarkRobotMonitor;
import com.liquido.worker.common.utils.IpUtils;
import com.liquido.worker.feign.DataWarehouseFeign;
import com.liquido.worker.pojo.bo.DwPage;
import com.liquido.worker.pojo.bo.DwResponse;
import com.liquido.worker.pojo.dto.CountryTransactionRatioDto;
import com.liquido.worker.pojo.dto.DwMetricSuccessRateDto;
import com.liquido.worker.pojo.dto.DwMetricTransactionDto;
import com.liquido.worker.pojo.dto.DwQueryTransactionDto;
import com.liquido.worker.pojo.dto.MetricTransactionDto;
import com.liquido.worker.pojo.dto.PageTransactionDto;
import com.liquido.worker.pojo.dto.QueryStatusByDetailDto;
import com.liquido.worker.pojo.dto.QueryStatusByUniqueIdDto;
import com.liquido.worker.pojo.dto.QueryTransactionDto;
import com.liquido.worker.pojo.dto.TransactionRatioDto;
import com.liquido.worker.pojo.entity.QTaskFeeCalculation;
import com.liquido.worker.pojo.entity.QTransactionMetric;
import com.liquido.worker.pojo.entity.QTransactionRatio;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.entity.TransactionMetric;
import com.liquido.worker.pojo.entity.TransactionRatio;
import com.liquido.worker.pojo.mapper.ModelMapper;
import com.liquido.worker.pojo.vo.AdvancedPageTransactionVo;
import com.liquido.worker.pojo.vo.DwAdvancedQueryTransactionVo;
import com.liquido.worker.pojo.vo.DwQueryTransactionVo;
import com.liquido.worker.pojo.vo.PageTransactionVo;
import com.liquido.worker.pojo.vo.QueryCountryTransactionRatioVo;
import com.liquido.worker.pojo.vo.QueryStatusByDetailVo;
import com.liquido.worker.pojo.vo.QueryStatusByUniqueIdVo;
import com.liquido.worker.pojo.vo.QueryTransactionMetricVo;
import com.liquido.worker.pojo.vo.QueryTransactionRatioVo;
import com.liquido.worker.pojo.vo.QueryTransactionVo;
import com.liquido.worker.pojo.vo.SyncTransactionVo;
import com.liquido.worker.repository.TaskFeeCalculationRepository;
import com.liquido.worker.repository.TransactionMetricRepository;
import com.liquido.worker.repository.TransactionRatioRepository;
import com.liquido.worker.service.TransactionService;

import com.alibaba.nacos.common.utils.MapUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.wenhao.jpa.PredicateBuilder;
import com.github.wenhao.jpa.Specifications;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
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
@Transactional(rollbackFor = Throwable.class)
public class TransactionServiceImpl implements TransactionService {

    private final JPAQueryFactory jpaQueryFactory;
    private final TaskFeeCalculationRepository taskFeeCalculationRepository;
    private final TransactionMetricRepository transactionMetricRepository;
    private final TransactionRatioRepository transactionRatioRepository;
    private final DataWarehouseFeign dataWarehouseFeign;
    private final RedisCacheUtil redisCacheUtil;
    private final ModelMapper modelMapper;
    private final BaseApis.BaseFeign baseFeign;
    private final LarkRobotMonitor larkRobotMonitor;

    /**
     * Transaction detail re-handler
     */
    private static final Map<String, Consumer<QueryTransactionDto>> transactionDetailHandle =
            Map.of(
                    "PAY_IN_CARD", x -> {
                        final ObjectNode card = (ObjectNode) x.getDetail().get("card");
                        if (Objects.nonNull(card)) {
                            if (Optional.of(card).map(e -> e.get("cardId"))
                                    .map(JsonNode::asText)
                                    .filter(StringUtils::isNotEmpty)
                                    .isPresent()) {
                                String cardId = Optional.of(card).map(e -> e.get("cardId"))
                                        .map(JsonNode::asText).get();
                                card.put("tokenId", "card_" + maskString(cardId));
                                card.remove("cardId");
                            }
                        }
                        final ObjectNode payer = (ObjectNode) x.getDetail().get("payer");
                        if (Objects.nonNull(payer) && payer.hasNonNull("documentId")) {
                            payer.put("documentId",
                                    StringUtils.right(payer.get("documentId").asText(), 4));
                        }
                        final ObjectNode information =
                                (ObjectNode) x.getDetail().get("information");
                        if (Objects.nonNull(information)
                                && information.hasNonNull("transferStatusCode")) {
                            JsonNode transferStatusCode = information.get("transferStatusCode");
                            final List<String> code = TransferErrorResultCodeEnum.values(
                                    TransferErrorResultCodeEnum.class).stream().map(
                                    DynamicConstant::getCode).collect(Collectors.toList());
                            if (code.contains(transferStatusCode.asText())) {
                                TransferErrorResultCodeEnum resultCodeEnum =
                                        TransferErrorResultCodeEnum.parseStr(
                                                transferStatusCode.asText());
                                information.remove("transferErrorMsg");
                                information.put("transferErrorMsg",
                                        resultCodeEnum.getDescription());
                                information.put("transferErrorType", resultCodeEnum.getType());
                                information.put("transferErrorCode", resultCodeEnum.getErrorCode());
                            }
                        }
                    },
                    "PAY_IN_PIX", x -> {
                        final JsonNode defaultPix =
                                new ObjectNode(JsonNodeFactory.withExactBigDecimals(true))
                                        .put("pixId", "");
                        MapUtils.putIfValNoNull(x.getDetail(), "information",
                                new ObjectNode(JsonNodeFactory.withExactBigDecimals(true))
                                        .put("creationDate", x.getCreateTime())
                                        .put("amount", x.getAmount())
                                        .put("currency", x.getCurrency().getCode())
                                        .put("e2eid",
                                                x.getDetail().getOrDefault("pixCredentials",
                                                        defaultPix).findValue("pixId").asText(""))
                        );
                        final ObjectNode payer = (ObjectNode) x.getDetail().get("payer");
                        if (payer.hasNonNull("ip") && Stream.of("127.", "192.", "172.", "10.")
                                .noneMatch(v -> payer.get("ip").asText().startsWith(v))) {
                            payer.put("country",
                                    IpUtils.reverseLookupAddress(payer.get("ip").asText()));
                        }
                    },
                    "PAY_IN_PAY_CASH", x -> MapUtils.putIfValNoNull(x.getDetail(), "information",
                            new ObjectNode(JsonNodeFactory.withExactBigDecimals(true))
                                    .put("creationDate", x.getCreateTime())
                                    .put("amount", x.getAmount())
                                    .put("currency", x.getCurrency().getCode())
                                    .put("referenceCode", x.getDetail().get("information")
                                            .findValue("referenceCode").asText(""))
                    )
            );

    @Override
    public PageVo<PageTransactionDto> pageTransaction(final PageTransactionVo vo) {

        final PredicateBuilder<TaskFeeCalculation> specBuilder =
                Specifications.<TaskFeeCalculation>and()
                        .eq("countryCode", vo.getCountry())
                        .eq("merchantCode", vo.getMerchantCode())
                        .eq("transactionTypeCode", vo.getTransactionType())
                        .ge(Objects.nonNull(vo.getStartDate()), "submitTimestamp",
                                LocalDateTimeUtil.utcToInstant(vo.getStartDate()))
                        .le(Objects.nonNull(vo.getEndDate()), "submitTimestamp",
                                LocalDateTimeUtil.utcToInstant(vo.getEndDate()))
                        .ge(Objects.nonNull(vo.getStartTransactionDate()), "transactionTimestamp",
                                LocalDateTimeUtil.utcToInstant(vo.getStartTransactionDate()))
                        .le(Objects.nonNull(vo.getEndTransactionDate()), "transactionTimestamp",
                                LocalDateTimeUtil.utcToInstant(vo.getEndTransactionDate()))
                        .eq(Objects.nonNull(vo.getProduct()), "productCode", vo.getProduct())
                        .eq(StringUtils.isNotBlank(vo.getTradeTransactionType()),
                                "tradeTransactionType",
                                vo.getTradeTransactionType())
                        .eq(StringUtils.isNotBlank(vo.getTradeTransferStatus()),
                                "tradeTransferStatus",
                                vo.getTradeTransferStatus())
                        .eq(Objects.nonNull(vo.getSubMerchantId()), "subMerchantId",
                                vo.getSubMerchantId())
                        .in(Objects.nonNull(vo.getHoldStatus()), "holdStatus",
                                (Objects.nonNull(vo.getHoldStatus()) && vo.getHoldStatus())
                                        ? HoldStatusEnum.HOLD :
                                        List.of(HoldStatusEnum.NORMAL, HoldStatusEnum.UNHOLD))
                        .like(StringUtils.isNotBlank(vo.getAccountName()), "accountName",
                                String.format("%s%%", vo.getAccountName()))
                        .like(StringUtils.isNotBlank(vo.getDescription()), "description",
                                String.format("%s%%", vo.getDescription()));

        if (StringUtils.isNotBlank(vo.getMerchantReference())) {
            specBuilder.in("uniqueId", List.of(vo.getMerchantReference(),
                    vo.getMerchantReference() + "_" + vo.getMerchantCode()));
        }

        Sort.Order order = Sort.Order.desc("submitTimestamp");
        if (ObjectUtils.isNotEmpty(vo.getSortField()) && ObjectUtils.isNotEmpty(vo.getSortType())) {
            order = SortTypeEnum.ASC == vo.getSortType() ? Sort.Order.asc(vo.getSortField())
                    : Sort.Order.desc(vo.getSortField());
        }

        final Page<TaskFeeCalculation> page =
                taskFeeCalculationRepository.findAll(specBuilder.build(),
                        PageRequest.of(vo.getPageNo() - 1, vo.getPageSize(), Sort.by(order)));

        final PageVo<PageTransactionDto> pateData = new PageVo<>(
                vo.getPageNo(), vo.getPageSize(), page.getTotalElements(),
                page.getContent().stream()
                        .map(x -> entityToPageDto(x, vo.getMerchantName()))
                        .collect(Collectors.toList()));


        this.buildSubMerchantName(pateData.getDataList());

        return pateData;
    }

    private void buildSubMerchantName(final List<PageTransactionDto> dataList) {

        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        for (final PageTransactionDto item : dataList) {
            if (StringUtils.isNotBlank(item.getSubMerchantId())) {
                final String subMerchantName = redisCacheUtil.getCacheMapValue(
                        Constant.CACHE.SUB_MERCHANT_INFO, item.getSubMerchantId());

                if (StringUtils.isNotBlank(subMerchantName)) {
                    item.setSubMerchantName(subMerchantName);
                    continue;
                }

                final List<SubMerchantDto> subMerchantList = loadSubMerchantInfo(
                        dataList.get(0).getMerchantCode(),
                        dataList.stream().map(PageTransactionDto::getSubMerchantId)
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
    public PageVo<PageTransactionDto> advancedPageTransaction(AdvancedPageTransactionVo vo) {
        final DwAdvancedQueryTransactionVo dwVo = DwAdvancedQueryTransactionVo.builder()
                .merchantCode(vo.getMerchantCode())
                .countryCode(vo.getCountry())
                .transactionType(vo.getTransactionType())
                .productCode(vo.getProduct())
                .from(LocalDateTimeUtil.utcToInstant(vo.getStartDate()))
                .to(Objects.nonNull(vo.getEndDate())
                        ? LocalDateTimeUtil.utcToInstant(vo.getEndDate())
                        : LocalDateTimeUtil.utcToInstant(LocalDateTimeUtil.nowUtc()))
                .other(vo.getOther())
                .page(vo.getPageNo())
                .pageSize(vo.getPageSize())
                .build();

        final DwResponse<DwPage<String>> result = dataWarehouseFeign.advancedPageTransaction(
                vo.getTransactionType(), true, dwVo);

        return Optional.ofNullable(result).filter(DwResponse::isSuccess).map(r -> {
            final DwPage<String> page = r.getData();

            List<PageTransactionDto> dtoList = taskFeeCalculationRepository.findAll(
                            Specifications.<TaskFeeCalculation>and()
                                    .in("uniqueId", page.getResults())
                                    .build())
                    .stream().map(x -> entityToPageDto(x, null))
                    .collect(Collectors.toList());

            return new PageVo<>(page.getPage().intValue(),
                    page.getPageSize().intValue(), page.getTotalCount(), dtoList);
        }).orElseGet(() -> {
            log.warn("get advanced page of transaction error, request params: {}, error msg: {}",
                    vo, Optional.ofNullable(result).map(DwResponse::getMessage).orElse(""));
            return new PageVo<>(0, 0, 0, List.of());
        });
    }

    @Override
    public QueryTransactionDto queryTransaction(final QueryTransactionVo vo) {

        final PredicateBuilder<TaskFeeCalculation> spec = Specifications.and();
        spec.in("uniqueId",
                List.of(vo.getUniqueId(), vo.getUniqueId() + "_" + vo.getMerchantCode()));
        if (StringUtils.isNotBlank(vo.getMerchantCode())) {
            spec.eq("merchantCode", vo.getMerchantCode());
        }
        final TaskFeeCalculation order = taskFeeCalculationRepository.findOne(spec.build())
                .orElse(null);
        if (Objects.isNull(order)) {
            return null;
        }

        final QueryTransactionDto resultDto = QueryTransactionDto.builder()
                .id(order.getId())
                .uniqueId(order.getUniqueId()
                        .equals(order.getMerchantReference() + "_" + order.getMerchantCode()) ?
                        order.getMerchantReference() : order.getUniqueId())
                .documentId(order.getDocumentId())
                .merchantReference(order.getMerchantReference())
                .merchantCode(order.getMerchantCode())
                .merchantName(order.getMerchantName())
                .country(order.getCountryCode())
                .transactionType(order.getTransactionTypeCode())
                .product(order.getProductCode())
                .subProduct(order.getSubProductCode())
                .subMerchantId(order.getSubMerchantId())
                .accountName(order.getAccountName())
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .status(getStatus(order.getTransactionStatus(), order.getDirectionType()))
                .directionType(order.getDirectionType())
                .transactionStatus(order.getTransactionStatus())
                .tradeTransferStatus(order.getTradeTransferStatus())
                .tradeTransactionType(order.getTradeTransactionType())
                .vendor(order.getVendor())
                .feeOthers(order.getOthers())
                .detail(new HashMap<>())
                .build();

        if (TransactionDataSourceEnum.ACCOUNT == order.getTransactionDataSource()) {
            return buildTransactionDetailDataSourceFromAccount(order, resultDto);
        } else {
            return buildTransactionDetailDataSourceFromTrade(order, resultDto);
        }
    }

    private QueryTransactionDto buildTransactionDetailDataSourceFromAccount(
            final TaskFeeCalculation order, final QueryTransactionDto resultDto) {
        resultDto.setCreateTime(LocalDateTimeUtil.utcToInstant(LocalDateTimeUtil.nowUtc()));
        resultDto.setIsSupportProofDownload(false);
        ObjectNode lifeCycle = new ObjectNode(JsonNodeFactory.withExactBigDecimals(true))
                .put("amount", order.getAmount())
                .put("currency", order.getCurrency().getCode())
                .put("operation", "c")
                .put("transactionStatus", order.getTradeTransferStatus())
                .putNull("referenceId")
                .put("timestamp", order.getTransactionTimestamp())
                .put("uniqueId", order.getUniqueId());
        resultDto.setLifeCycle(new ArrayNode(JsonNodeFactory.withExactBigDecimals(true),
                List.of(lifeCycle)));
        return resultDto;
    }

    private QueryTransactionDto buildTransactionDetailDataSourceFromTrade(
            final TaskFeeCalculation order, final QueryTransactionDto resultDto) {

        final PaymentProofEnum proofEnum = PaymentProofEnum.parse(
                order.getCountryCode(), order.getProductCode(), order.getVendor(),
                Optional.ofNullable(order.getOthers())
                        .filter(v -> v.hasNonNull("bankCode"))
                        .map(v -> v.get("bankCode").intValue())
                        .orElse(0));
        resultDto.setCreateTime(order.getSubmitTimestamp());
        resultDto.setIsSupportProofDownload(Optional.ofNullable(proofEnum).isPresent()
                && TransactionStatusEnum.SETTLED == order.getTransactionStatus()
                && TransactionTypeCodeEnum.PAY_OUT == order.getTransactionTypeCode());
        resultDto.setPaymentProof(proofEnum);

        if (TransactionTypeCodeEnum.PAY_IN == resultDto.getTransactionType()
                && TransactionStatusEnum.SETTLED == resultDto.getTransactionStatus()
                && DirectionTypeEnum.SETTLED == resultDto.getDirectionType()) {

            QTaskFeeCalculation entity = QTaskFeeCalculation.taskFeeCalculation;
            final List<TaskFeeCalculation> refundDataList =
                    jpaQueryFactory.select(entity)
                            .from(entity).where(entity.merchantReference
                                    .eq(resultDto.getMerchantReference())
                                    .and(entity.uniqueId.ne(entity.merchantReference)))
                            .orderBy(entity.submitTimestamp.asc())
                            .fetch();

            if ((Objects.isNull(refundDataList) || refundDataList.isEmpty())) {
                resultDto.setRefundList(null);
                resultDto.setRefundTotalAmount(BigDecimal.ZERO);
            } else {
                resultDto.setRefundList(modelMapper.convertToRefundDto(refundDataList));
                resultDto.setRefundTotalAmount(refundDataList.stream()
                        .filter(r -> TransactionStatusEnum.SETTLED == r.getTransactionStatus()
                                && DirectionTypeEnum.REFUND == r.getDirectionType()
                        ).map(TaskFeeCalculation::getAmount).reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO));
            }
        }

        final DwQueryTransactionDto dto = dataWarehouseFeign.queryTransaction(
                order.getTransactionTypeCode(), true,
                DwQueryTransactionVo.builder()
                        .uniqueId(order.getUniqueId())
                        .merchant(order.getMerchantCode())
                        .productCode(order.getProductCode())
                        .transactionType(order.getTransactionTypeCode())
                        .countryCode(order.getCountryCode())
                        .build()).getData();
        return Optional.ofNullable(dto).map(x -> {

            resultDto.setTransferErrorMsg(Optional.ofNullable(order.getOthers())
                    .filter(v -> v.hasNonNull("transferErrorMsg"))
                    .map(v -> v.get("transferErrorMsg").asText())
                    .orElse(x.getTransferErrorMsg()));
            resultDto.setLifeCycle(x.getLifeCycle());

            final ObjectNode refundAccountInfo = x.getRefundAccountInfo();
            if (Objects.nonNull(refundAccountInfo)) {
                final String bankName = Optional.of(refundAccountInfo)
                        .filter(v -> v.hasNonNull("bankCode"))
                        .map(v -> String.valueOf(v.asInt()))
                        .map(code -> baseFeign.queryDictValue(
                                DictionaryTypeEnum.BANK_CODE, code,
                                order.getCountryCode().getCode()))
                        .orElse("");
                refundAccountInfo.put("bankName", bankName);
            }

            MapUtils.putIfValNoNull(resultDto.getDetail(), "card", x.getCard());
            MapUtils.putIfValNoNull(resultDto.getDetail(), "subAccount", x.getSubAccount());
            MapUtils.putIfValNoNull(resultDto.getDetail(), "account", x.getAccount());
            MapUtils.putIfValNoNull(resultDto.getDetail(), "payer", x.getPayer());
            MapUtils.putIfValNoNull(resultDto.getDetail(), "beneficiary", x.getBeneficiary());
            MapUtils.putIfValNoNull(resultDto.getDetail(), "card", x.getCard());
            MapUtils.putIfValNoNull(resultDto.getDetail(), "skuDetail", x.getSkuDetail());
            MapUtils.putIfValNoNull(resultDto.getDetail(), "information", x.getInformation());
            MapUtils.putIfValNoNull(resultDto.getDetail(), "others", x.getOthers());
            MapUtils.putIfValNoNull(resultDto.getDetail(), "refundAccountInfo",
                    x.getRefundAccountInfo());
            MapUtils.putIfValNoNull(resultDto.getDetail(), "transferDetail",
                    x.getTransferDetail());

            Optional.ofNullable(transactionDetailHandle.get(
                    String.format("%s_%s", order.getTransactionTypeCode().getCode(),
                            order.getProductCode().getCode()))
            ).ifPresentOrElse(d -> d.accept(resultDto), () ->
                    Optional.ofNullable(
                                    transactionDetailHandle.get(order.getProductCode().getCode()))
                            .ifPresent(e -> e.accept(resultDto)));
            return resultDto;
        }).orElse(resultDto);
    }

    @Override
    public List<MetricTransactionDto> queryTransactionMetric(final QueryTransactionMetricVo vo) {
        final Specification<TransactionMetric> specification =
                Specifications.<TransactionMetric>and()
                        .between("date", vo.getStartDate(), vo.getEndDate())
                        .eq(Objects.nonNull(vo.getMerchantCode()), "merchantCode",
                                vo.getMerchantCode())
                        .eq(Objects.nonNull(vo.getCountry()), "country", vo.getCountry())
                        .build();
        return transactionMetricRepository.findAll(specification, Sort.by(Sort.Order.asc("date")))
                .stream().map(x -> {
                    final MetricTransactionDto dto = new MetricTransactionDto();
                    BeanCopierUtil.copyProperties(x, dto);
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public void syncTransactionMetric(final SyncTransactionVo vo) {
        final LocalDateTime to = Optional.ofNullable(vo.getEndDate())
                .orElse(LocalDateTimeUtil.nowUtc()).withSecond(0).withNano(0);
        LocalDateTime from = Optional.ofNullable(vo.getStartDate()).orElseGet(() -> {
            final QTransactionMetric qTransactionMetric = QTransactionMetric.transactionMetric;
            final TransactionMetric finalData = jpaQueryFactory.select(qTransactionMetric)
                    .from(qTransactionMetric)
                    .where(qTransactionMetric.transactionType.eq(vo.getTypeCodeEnum()))
                    .orderBy(qTransactionMetric.date.desc())
                    .fetchFirst();
            final LocalDateTime fromDate = Optional.ofNullable(finalData)
                    .map(TransactionMetric::getDate)
                    .orElse(to.withMinute(0)).withSecond(0).withNano(0);
            final LocalDateTime toMinusHours = to.withMinute(0).minusHours(1);
            return toMinusHours.isAfter(fromDate) ? toMinusHours : fromDate;
        });

        log.info("sync transaction metric utc time from->{},to->{}", from, to);
        while (!to.isBefore(from.plusMinutes(15))) {
            final LocalDateTime toDate = from.plusMinutes(15);
            try {
                final DwPage<DwMetricTransactionDto> data = dataWarehouseFeign.metricTransaction(
                        vo.getTypeCodeEnum(), false,
                        LocalDateTimeUtil.utcToInstant(from),
                        LocalDateTimeUtil.utcToInstant(toDate)).getData();

                if (Objects.isNull(data) || ObjectUtils.isEmpty(data.getResults())) {
                    log.warn("sync transaction metric data is empty, from->{}, to->{}", from, to);
                } else {
                    data.getResults().parallelStream()
                            .filter(v -> v.getTransactionType() == vo.getTypeCodeEnum())
                            .filter(v -> Objects.isNull(vo.getMerchantCode())
                                    || v.getMerchantCode().equals(vo.getMerchantCode()))
                            .forEach(r -> {
                                final TransactionMetric transactionMetric =
                                        TransactionMetric.builder()
                                                .merchantCode(r.getMerchantCode())
                                                .country(r.getCountryCode())
                                                .transactionType(r.getTransactionType())
                                                .countTransaction(r.getCountTransaction())
                                                .sumAmount(r.getSumAmount())
                                                .currency(r.getCurrency())
                                                .date(toDate)
                                                .build();
                                transactionMetricRepository.save(transactionMetric);
                            });
                }
            } catch (Exception e) {
                log.error("sync transaction metric error e", e);
                larkRobotMonitor.error("sync Transaction Metric error",
                        "sync utc time statTime" + from + " sync utc time endTime" + to,
                        e.getMessage());
            }
            from = toDate;
        }
    }

    @Override
    public List<QueryStatusByDetailDto> queryStatusByDetail(final QueryStatusByDetailVo vo) {
        return vo.getQueries().stream().map(v -> {
            final QueryStatusByDetailDto dto = modelMapper.convert(v);
            final String cacheKey =
                    String.format(Constant.CACHE.TRANSACTION_WORK_ORDER_QUERY_STATUS,
                            v.getSubAccountId(), v.getTrackingId());

            // Get Unique Id
            String uniqueId = redisCacheUtil.getCacheObject(cacheKey);
            if (StringUtils.isBlank(uniqueId)) {
                uniqueId = dataWarehouseFeign.queryUniqueId(TransactionTypeCodeEnum.PAY_IN, false,
                        v.getMerchantCode(), v.getSubAccountId(), v.getTrackingId()).getData();
                if (StringUtils.isNotBlank(uniqueId)) {
                    redisCacheUtil.setCacheObject(cacheKey, uniqueId, 2, TimeUnit.DAYS);
                } else {
                    return dto;
                }
            }

            // Query Status
            final TaskFeeCalculation order = taskFeeCalculationRepository.findByUniqueId(uniqueId);
            dto.setUniqueId(order.getUniqueId());
            dto.setTransactionStatus(order.getTransactionStatus());
            dto.setVendor(order.getVendor());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<QueryStatusByUniqueIdDto> queryStatusByUniqueId(final QueryStatusByUniqueIdVo vo) {

        final Set<String> uniqueIds = vo.getQueries().stream()
                .map(QueryStatusByUniqueIdVo.QueryStatusVo::getUniqueId)
                .collect(Collectors.toSet());

        final Map<String, TaskFeeCalculation> orderMap =
                taskFeeCalculationRepository.findAll(Specifications.<TaskFeeCalculation>and()
                                .in("uniqueId", uniqueIds).build()).stream()
                        .collect(Collectors.toMap(TaskFeeCalculation::getUniqueId,
                                Function.identity()));

        return vo.getQueries().stream().filter(v -> {
            final TaskFeeCalculation order = orderMap.get(v.getUniqueId());
            if (Objects.isNull(order)) {
                return false;
            }
            return order.getMerchantCode().equals(v.getMerchantCode());
        }).map(v -> {
            final TaskFeeCalculation order = orderMap.get(v.getUniqueId());
            return QueryStatusByUniqueIdDto.builder()
                    .uniqueId(v.getUniqueId())
                    .workOrderId(v.getWorkOrderId())
                    .merchantCode(v.getMerchantCode())
                    .transactionStatus(order.getTransactionStatus())
                    .vendor(order.getVendor())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public void syncTransactionRatio(final SyncTransactionVo vo) {
        final LocalDateTime to = Optional.ofNullable(vo.getEndDate())
                .orElse(LocalDateTimeUtil.nowUtc()).withSecond(0).withNano(0);
        LocalDateTime from = Optional.ofNullable(vo.getStartDate()).orElseGet(() -> {
            final TransactionRatio finalData = jpaQueryFactory.select(transactionRatio)
                    .from(transactionRatio)
                    .where(transactionRatio.transactionType.eq(vo.getTypeCodeEnum()))
                    .orderBy(transactionRatio.date.desc())
                    .fetchFirst();
            final LocalDateTime fromDate = Optional.ofNullable(finalData)
                    .map(TransactionRatio::getDate)
                    .orElse(to.withMinute(0)).withSecond(0).withNano(0);
            final LocalDateTime toMinusHours = to.withMinute(0).minusHours(1);
            return toMinusHours.isAfter(fromDate) ? toMinusHours : fromDate;
        });

        log.info("sync transaction ratio utc time from->{},to->{}", from, to);
        while (!to.isBefore(from.plusMinutes(15))) {
            final LocalDateTime toDate = from.plusMinutes(15);
            try {
                final DwPage<DwMetricSuccessRateDto> data = dataWarehouseFeign.metricSuccessRate(
                        vo.getTypeCodeEnum(), false,
                        LocalDateTimeUtil.utcToInstant(from),
                        LocalDateTimeUtil.utcToInstant(toDate)).getData();
                if (Objects.isNull(data) || ObjectUtils.isEmpty(data.getResults())) {
                    log.warn("sync transaction ratio is empty, from->{}, to->{}", from, to);
                } else {
                    data.getResults().parallelStream()
                            .filter(v -> v.getTransactionType() == vo.getTypeCodeEnum())
                            .filter(v -> Objects.isNull(vo.getMerchantCode())
                                    || v.getMerchantCode().equals(vo.getMerchantCode()))
                            .forEach(r -> {
                                BigDecimal successRate = BigDecimal.ZERO;
                                if (r.getTotalCount().compareTo(0) == 0
                                        && r.getSuccessCount().compareTo(0) != 0) {
                                    successRate = BigDecimal.ONE;
                                }
                                if (r.getTotalCount().compareTo(0) != 0) {
                                    successRate = AmountUtil.division(
                                            BigDecimal.valueOf(r.getSuccessCount()),
                                            BigDecimal.valueOf(r.getTotalCount()), 2);
                                }
                                final TransactionRatio transactionRatio = TransactionRatio.builder()
                                        .merchantCode(r.getMerchantCode())
                                        .country(r.getCountryCode())
                                        .transactionType(r.getTransactionType())
                                        .productCode(r.getProduct())
                                        .successCount(r.getSuccessCount())
                                        .totalCount(r.getTotalCount())
                                        .successRate(successRate)
                                        .date(toDate)
                                        .createTime(LocalDateTimeUtil.nowUtc())
                                        .delFlag(false)
                                        .build();
                                transactionRatioRepository.save(transactionRatio);
                            });
                }
            } catch (Exception e) {
                log.error("sync transaction ratio error e", e);
                larkRobotMonitor.error("Sync Transaction Ratio Error",
                        "sync utc time statTime " + from + " sync utc time endTime " + to,
                        e.getMessage());
            }
            from = toDate;
        }
    }

    private PageTransactionDto entityToPageDto(final TaskFeeCalculation fee,
                                               final String merchantName) {
        return PageTransactionDto.builder()
                .uniqueId(fee.getUniqueId()
                        .equals(fee.getMerchantReference() + "_" + fee.getMerchantCode()) ?
                        fee.getMerchantReference() : fee.getUniqueId())
                .taskStatus(fee.getTaskStatus())
                .merchantReference(fee.getMerchantReference())
                .merchantCode(fee.getMerchantCode())
                .merchantName(merchantName)
                .accountName(fee.getAccountName())
                .date(LocalDateTimeUtil.instantToUtc(fee.getSubmitTimestamp()))
                .transactionDate(fee.getTransactionTime())
                .amount(fee.getAmount())
                .currency(fee.getCurrency())
                .country(fee.getCountryCode())
                .product(fee.getProductCode())
                .transactionType(fee.getTransactionTypeCode())
                .directionType(fee.getDirectionType())
                .transactionStatus(fee.getTransactionStatus())
                .status(getStatus(fee.getTransactionStatus(), fee.getDirectionType()))
                .tradeTransactionType(fee.getTradeTransactionType())
                .tradeTransferStatus(HoldStatusEnum.HOLD == fee.getHoldStatus()
                        ? "ON_HOLDING" : fee.getTradeTransferStatus())
                .holdStatus(HoldStatusEnum.HOLD == fee.getHoldStatus())
                .subMerchantId(fee.getSubMerchantId())
                .description(fee.getDescription())
                .build();
    }

    private TransactionStatusEnum getStatus(final TransactionStatusEnum transactionStatusEnum,
                                            final DirectionTypeEnum directionTypeEnum) {
        if (DirectionTypeEnum.SETTLED == directionTypeEnum) {
            return transactionStatusEnum;
        }

        if (DirectionTypeEnum.REFUND == directionTypeEnum) {
            return TransactionStatusEnum.REFUNDED;
        }

        if (DirectionTypeEnum.CHARGE_BACK == directionTypeEnum) {
            return TransactionStatusEnum.CHARGED_BACK;
        }

        if (DirectionTypeEnum.CHARGE_BACK_REJECTED == directionTypeEnum) {
            return TransactionStatusEnum.SETTLED;
        }

        if (DirectionTypeEnum.REJECTED == directionTypeEnum) {
            return TransactionStatusEnum.REJECTED;
        }

        return transactionStatusEnum;
    }

    @Override
    public List<CountryTransactionRatioDto> queryCountryTransactionRatio(
            final QueryCountryTransactionRatioVo vo) {

        final QTransactionRatio entity = QTransactionRatio.transactionRatio;
        return jpaQueryFactory.select(Projections.fields(CountryTransactionRatioDto.class,
                        entity.totalCount.sum().as("totalCount"),
                        entity.successCount.sum().as("successCount"),
                        entity.date.as("date"),
                        entity.productCode.as("productCode"),
                        entity.transactionType.as("transactionTypeCode")))
                .from(entity)
                .where(entity.country.eq(vo.getCountryCode()),
                        entity.transactionType.eq(vo.getTransactionTypeCode()),
                        entity.productCode.in(vo.getProductCodeList()),
                        entity.date.between(vo.getStartDate(), vo.getEndDate()))
                .groupBy(entity.productCode, entity.date)
                .orderBy(entity.date.asc())
                .fetch();
    }

    @Override
    public List<TransactionRatioDto> queryMerchantRatioSummaryByProduct(
            final QueryTransactionRatioVo vo) {
        final QTransactionRatio qTransactionRatio = QTransactionRatio.transactionRatio;
        final QBean<TransactionRatioDto> bean = Projections.fields(TransactionRatioDto.class,
                qTransactionRatio.totalCount.sum().as("totalCount"),
                qTransactionRatio.successCount.sum().as("successCount"),
                qTransactionRatio.productCode.as("productCode"),
                qTransactionRatio.date.as("date"),
                qTransactionRatio.merchantCode.as("merchantCode"),
                qTransactionRatio.country.as("countryCode"),
                qTransactionRatio.transactionType.as("transactionTypeCode"));

        BooleanExpression condition = qTransactionRatio.country.eq(vo.getCountryCode())
                .and(qTransactionRatio.transactionType.eq(vo.getTransactionTypeCode()))
                .and(qTransactionRatio.merchantCode.eq(vo.getMerchantCode()))
                .and(qTransactionRatio.date.goe(vo.getStartDate()))
                .and(qTransactionRatio.date.lt(vo.getEndDate()));

        if (CollectionUtils.isNotEmpty(vo.getProductCodeList())) {
            condition = condition.and(qTransactionRatio.productCode.in(vo.getProductCodeList()));
        }

        return jpaQueryFactory.select(bean)
                .from(qTransactionRatio)
                .where(condition)
                .groupBy(qTransactionRatio.productCode, qTransactionRatio.date)
                .fetch();
    }

    private static String maskString(final String str) {
        if (str.length() <= 10) {
            return str;
        }
        int length = str.length();
        String prefix = str.substring(0, 6);
        String suffix = str.substring(length - 4);
        return prefix + "*".repeat(length - 10) + suffix;
    }

}
