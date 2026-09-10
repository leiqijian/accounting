package com.liquido.worker.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.worker.feign.DataWarehouseFeign;
import com.liquido.worker.pojo.dto.DwQueryTransactionDto;
import com.liquido.worker.pojo.dto.QueryBrProofDto;
import com.liquido.worker.pojo.dto.QueryCoProofDto;
import com.liquido.worker.pojo.dto.QueryMxSpeiProofDto;
import com.liquido.worker.pojo.dto.TaskFeeCalculationDto;
import com.liquido.worker.pojo.vo.DwQueryTransactionVo;
import com.liquido.worker.pojo.vo.QueryProofVo;
import com.liquido.worker.service.TaskFeeCalculationService;
import com.liquido.worker.service.TransactionProofService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
public class TransactionProofServiceImpl implements TransactionProofService {

    private final TaskFeeCalculationService taskFeeCalculationService;
    private final DataWarehouseFeign dataWarehouseFeign;

    @Override
    public QueryMxSpeiProofDto queryMxSpeiProof(final QueryProofVo vo) {

        final TaskFeeCalculationDto taskFeeCalculation =
                taskFeeCalculationService.findByUniqueId(vo.getUniqueId());

        if (Objects.isNull(taskFeeCalculation)) {
            return null;
        }

        final DwQueryTransactionDto dto =
                dataWarehouseFeign.queryTransaction(
                        taskFeeCalculation.getTransactionTypeCode(), true,
                        DwQueryTransactionVo.builder()
                                .countryCode(taskFeeCalculation.getCountryCode())
                                .productCode(taskFeeCalculation.getProductCode())
                                .transactionType(taskFeeCalculation.getTransactionTypeCode())
                                .merchant(taskFeeCalculation.getMerchantCode())
                                .uniqueId(taskFeeCalculation.getUniqueId())
                                .build()).getData();

        if (ObjectUtils.anyNull(dto, dto.getCepCredentials())) {
            return null;
        }

        final ObjectNode cepCredentials = dto.getCepCredentials();
        return QueryMxSpeiProofDto.builder()
                .taskFeeCalculation(taskFeeCalculation)
                .amount(cepCredentials.get("amount").asText())
                .targetAccountId(cepCredentials.get("targetAccountId").asText())
                .bankId(cepCredentials.get("bankId").asText())
                .vendorName(cepCredentials.get("vendorName").asText())
                .referenceNumber(cepCredentials.get("referenceNumber").asText())
                .finalStatusTime(LocalDateTimeUtil.formatToLocalDateTime(
                        cepCredentials.get("finalStatusTime").asText()))
                .build();
    }

    @Override
    public QueryBrProofDto queryBrProof(final QueryProofVo vo) {

        final TaskFeeCalculationDto taskFeeCalculation =
                taskFeeCalculationService.findByUniqueId(vo.getUniqueId());

        if (Objects.isNull(taskFeeCalculation)) {
            return null;
        }

        final DwQueryTransactionDto dto =
                dataWarehouseFeign.queryTransaction(
                        taskFeeCalculation.getTransactionTypeCode(), true,
                        DwQueryTransactionVo.builder()
                                .countryCode(taskFeeCalculation.getCountryCode())
                                .productCode(taskFeeCalculation.getProductCode())
                                .transactionType(taskFeeCalculation.getTransactionTypeCode())
                                .merchant(taskFeeCalculation.getMerchantCode())
                                .uniqueId(taskFeeCalculation.getUniqueId())
                                .build()).getData();

        if (ObjectUtils.anyNull(dto, dto.getPixCredentials())) {
            return null;
        }

        // pixCredentials data reedit: Since the daily cut is at 22 o'clock UTC-3,
        // it is equivalent to converting the time according to UTC-1.
        final ObjectNode pixCredentials = dto.getPixCredentials();
        final String[] cpf = Optional.ofNullable(pixCredentials.get("documentId"))
                .filter(v -> !v.isNull())
                .map(JsonNode::asText)
                .filter(com.alibaba.nacos.common.utils.StringUtils::isNotBlank)
                .map(v -> new String[] {v.substring(0, 3), v.substring(3, 6),
                        v.substring(6, 9), v.substring(9, 11)})
                .orElse(new String[] {"", "", "", ""});
        final LocalDateTime dateTime = Optional.ofNullable(pixCredentials.get("date"))
                .filter(v -> !v.isNull())
                .map(JsonNode::asText)
                .filter(com.alibaba.nacos.common.utils.StringUtils::isNotBlank)
                .map(v -> LocalDateTime.parse(v, DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss")))
                .map(v -> LocalDateTimeUtil.utcToLocal(v, "UTC-3"))
                .orElse(null);
        return QueryBrProofDto.builder()
                .pixId(pixCredentials.get("pixId").asText())
                .documentId(String.format("***.%s.%s-**", cpf[1], cpf[2]))
                .description(pixCredentials.get("description").asText())
                .amount(pixCredentials.get("amount").asText())
                .name(pixCredentials.get("name").asText())
                .date(Objects.isNull(dateTime) ? null : String.format("%02d/%02d/%d",
                        dateTime.getDayOfMonth(), dateTime.getMonthValue(), dateTime.getYear()))
                .time(Objects.isNull(dateTime) ? null : String.format("%02d:%02d:%02d",
                        dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond()))
                .build();
    }

    @Override
    public QueryCoProofDto queryCoProof(final QueryProofVo vo) {
        final TaskFeeCalculationDto taskFeeCalculation =
                taskFeeCalculationService.findByUniqueId(vo.getUniqueId());

        if (Objects.isNull(taskFeeCalculation)) {
            return null;
        }

        final QueryCoProofDto dto = QueryCoProofDto.builder()
                .number(taskFeeCalculation.getId().toString().substring(0, 5))
                .documentId(taskFeeCalculation.getDocumentId())
                .targetName(taskFeeCalculation.getAccountName())
                .amount(taskFeeCalculation.getAmount()).build();

        final ObjectNode others = taskFeeCalculation.getOthers();

        if (Objects.isNull(others)) {
            return dto;
        }

        dto.setBankName(others.get("bankName").asText("").toUpperCase());
        dto.setTargetAccountId(others.get("accountId").asText(""));
        dto.setTargetAccountType(others.get("targetBankAccountType").asText("").toUpperCase());

        return dto;
    }

}
