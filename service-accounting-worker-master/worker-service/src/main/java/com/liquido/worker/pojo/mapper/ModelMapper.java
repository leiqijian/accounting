package com.liquido.worker.pojo.mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.liquido.base.BaseApis;
import com.liquido.base.enums.DataSyncRefundStatusEnum;
import com.liquido.base.enums.DataSyncStatusEnum;
import com.liquido.base.enums.DictionaryTypeEnum;
import com.liquido.base.enums.ReceiptProofEnum;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.pojo.vo.AccountingScheduleVo;
import com.liquido.statement.pojo.vo.DailyExchangeRateVo;
import com.liquido.statement.pojo.vo.RealTimeExchangeRateVo;
import com.liquido.statement.pojo.vo.TransactionCostVo;
import com.liquido.statement.pojo.vo.TransactionExtraFeeVo;
import com.liquido.statement.pojo.vo.TransactionFeeVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;
import com.liquido.worker.pojo.bo.AccountingScheduleBo;
import com.liquido.worker.pojo.bo.PreCalculateFeeBo;
import com.liquido.worker.pojo.bo.TransactionCostBo;
import com.liquido.worker.pojo.bo.TransactionExtraIncomeBo;
import com.liquido.worker.pojo.bo.TransactionFeeBo;
import com.liquido.worker.pojo.bo.TransactionMoneyBo;
import com.liquido.worker.pojo.dto.DefenseOrderDto;
import com.liquido.worker.pojo.dto.PagePaymentLinkDto;
import com.liquido.worker.pojo.dto.PreCalculateFeeDto;
import com.liquido.worker.pojo.dto.QueryPaymentLinkDto;
import com.liquido.worker.pojo.dto.QueryShopifyDto;
import com.liquido.worker.pojo.dto.QueryShoplazzaDto;
import com.liquido.worker.pojo.dto.QueryStatusByDetailDto;
import com.liquido.worker.pojo.dto.QueryTransactionRefundDto;
import com.liquido.worker.pojo.dto.TaskFeeCalculationDto;
import com.liquido.worker.pojo.entity.DefenseOrder;
import com.liquido.worker.pojo.entity.PaymentLink;
import com.liquido.worker.pojo.entity.Shopify;
import com.liquido.worker.pojo.entity.Shoplazza;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;
import com.liquido.worker.pojo.vo.QueryStatusByDetailVo;
import com.liquido.worker.pojo.vo.TaskFeeCalculationVo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModelMapper {

    ModelMapper INSTANT = Mappers.getMapper(ModelMapper.class);

    @Mappings({
            @Mapping(target = "requestId", ignore = true),
            @Mapping(target = "extendData", expression =
                    "java(com.liquido.core.common.utils.JsonUtil.toMap(model.getExtendData()))")})
    TransactionMoneyVo convert(final TransactionMoneyBo model);

    TransactionFeeVo convert(final TransactionFeeBo bo);

    List<AccountingScheduleVo> convertScheduleList(final List<AccountingScheduleBo> boList);

    List<TransactionFeeVo> convertFee(final List<TransactionFeeBo> boList);

    List<TransactionCostVo> convertCost(final List<TransactionCostBo> boList);

    List<TransactionExtraFeeVo> convertExtraFee(final List<TransactionExtraIncomeBo> boList);

    DailyExchangeRateDto convert(final DailyExchangeRateVo model);

    List<DailyExchangeRateDto> convert(final List<DailyExchangeRateVo> modelList);

    TaskFeeCalculationDto convert(final TaskFeeCalculation entity);

    TaskFeeCalculation convert(final TaskFeeCalculationDto dto);

    List<TaskFeeCalculation> convert(final Collection<TaskFeeCalculationVo> taskList);

    QueryTransactionRefundDto convertToRefundDto(final TaskFeeCalculation entity);

    List<QueryTransactionRefundDto> convertToRefundDto(final List<TaskFeeCalculation> entities);

    QueryStatusByDetailDto convert(final QueryStatusByDetailVo.QueryStatusVo vo);

    DefenseOrderDto convertDefenseOrderDto(
            final DefenseOrder order
    );

    PagePaymentLinkDto convert(final PaymentLink entity);

    QueryPaymentLinkDto convert(
            final PaymentLink entity, final JsonNode refundInfo,
            final BaseApis.BaseFeign baseFeign
    );

    @AfterMapping
    default void afterMapping(
            @MappingTarget final QueryPaymentLinkDto.QueryPaymentLinkDtoBuilder dto,
            final PaymentLink entity, JsonNode info,
            final BaseApis.BaseFeign baseFeign
    ) {

        final ReceiptProofEnum proofEnum = ReceiptProofEnum.parse(0, entity.getMerchantCode());
        dto.isSupportProofDownload(Objects.nonNull(proofEnum)
                && DataSyncStatusEnum.SETTLED == entity.getPaymentStatus()
                && DataSyncRefundStatusEnum.REFUNDED != entity.getRefundStatus());
        dto.receiptProof(proofEnum);

        dto.metadata(Optional.ofNullable(entity.getOthers())
                .map(x -> x.get("metadata"))
                .orElse(null));

        if (Objects.isNull(info) || info.isNull() || info.isEmpty()) {
            return;
        }
        if ((info = info.get("refundAdditionalInfo")).isNull() || info.isEmpty()) {
            return;
        }
        if ((info = info.get("bankTransferAccountInfo")).isNull() || info.isEmpty()) {
            return;
        }

        final ObjectNode refundAccountInfo =
                new ObjectNode(JsonNodeFactory.withExactBigDecimals(true));
        if (info.hasNonNull("bankCode")) {
            final String bankName = baseFeign.queryDictValue(DictionaryTypeEnum.BANK_CODE,
                    String.valueOf(info.get("bankCode").asInt()),
                    entity.getCountryCode().getCode());
            refundAccountInfo.put("bankCode", info.get("bankCode").asText());
            refundAccountInfo.put("bankName", bankName);
        }
        if (info.hasNonNull("bankAccountNumber")) {
            refundAccountInfo.put("bankAccountNumber", info.get("bankAccountNumber").asText());
        }
        if (info.hasNonNull("bankAccountType")) {
            refundAccountInfo.put("bankAccountType", info.get("bankAccountType").asText());
        }
        if (info.hasNonNull("bankBranchId")) {
            refundAccountInfo.put("bankBranchId", info.get("bankBranchId").asText());
        }
        if (info.hasNonNull("beneficiaryName")) {
            refundAccountInfo.put("beneficiaryName", info.get("beneficiaryName").asText());
        }

        if (!(info = info.get("document")).isNull() && !info.isEmpty()) {
            if (info.hasNonNull("type")) {
                refundAccountInfo.put("documentType", info.get("type").asText());
            }
            if (info.hasNonNull("documentId")) {
                refundAccountInfo.put("documentId", info.get("documentId").asText());
            }
        }

        dto.detail(new ObjectNode(JsonNodeFactory.withExactBigDecimals(true))
                .putPOJO("refundAccountInfo", refundAccountInfo));

    }

    QueryShopifyDto convert(final Shopify shopify);

    @AfterMapping
    default void afterMapping(
            @MappingTarget final QueryShopifyDto.QueryShopifyDtoBuilder dto,
            final Shopify entity
    ) {
        final ReceiptProofEnum ProofEnum = ReceiptProofEnum.parse(1, entity.getMerchantCode());
        dto.isSupportProofDownload(Objects.nonNull(ProofEnum));
        dto.receiptProof(ProofEnum);
    }

    QueryShoplazzaDto convert(final Shoplazza shopify);

    @AfterMapping
    default void afterMapping(
            @MappingTarget final QueryShoplazzaDto.QueryShoplazzaDtoBuilder dto,
            final Shoplazza entity
    ) {
        final ReceiptProofEnum ProofEnum = ReceiptProofEnum.parse(2, entity.getMerchantCode());
        dto.isSupportProofDownload(Objects.nonNull(ProofEnum));
        dto.receiptProof(ProofEnum);
    }

    List<PreCalculateFeeDto> convertListPreCalculateFeeBoToDto(final List<PreCalculateFeeBo> list);

    List<RealTimeExchangeRateVo> convertRealTimeExchangeRate(
            final List<HourlyExchangeRateDto> dtoList
    );

    @Mappings({
            @Mapping(target = "transactionId", source = "entity.id"),
            @Mapping(target = "settleAmount", source = "settlementAmount"),
            @Mapping(target = "merchantRate", source = "merchantRate"),
            @Mapping(target = "transactionFeeList", source = "transactionFeeBos")})
    PreCalculateFeeBo convertPreCalculateFeeBo(
            final TaskFeeCalculation entity,
            final BigDecimal settlementAmount,
            final BigDecimal merchantRate,
            final List<TransactionFeeBo> transactionFeeBos
    );
}
