package com.liquido.statement.service.payment.convert.impl;

import java.util.Map;

import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.statement.pojo.dto.payment.ClBankTransferPayoutResult;
import com.liquido.statement.pojo.dto.payment.QueryPayoutResultDto;
import com.liquido.statement.service.payment.convert.PayoutResultConvert;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

/**
 * Country: Chile
 * BANK-TRANSFER Payment Payout Response Data Converter
 */
@Component
public class ClBankTransferConvertImpl implements PayoutResultConvert<ClBankTransferPayoutResult> {

    @Override
    public ProductCodeEnum getProductCode() {
        return ProductCodeEnum.CL_BANK_TRANSFER;
    }

    @Override
    public QueryPayoutResultDto convertResult(final ClBankTransferPayoutResult dto) {
        final Map<String, Object> targetInfo = Maps.newHashMap();
        targetInfo.put("targetName", dto.getTargetName());
        targetInfo.put("targetDocument", dto.getTargetDocument());
        targetInfo.put("targetBankCode", dto.getTargetBankCode());
        targetInfo.put("targetBankAgency", dto.getTargetBankAgency());
        targetInfo.put("targetBankAccountId", dto.getTargetBankAccountId());

        return QueryPayoutResultDto.builder()
                .orderId(dto.getIdempotencyKey())
                .transactionId(dto.getTransactionId())
                .targetName(dto.getTargetName())
                .targetBankAccountId(dto.getTargetBankAccountId())
                .statusCode(dto.getStatusCode())
                .errorMsg(dto.getErrorMsg())
                .transferStatusCode(dto.getTransferStatusCode())
                .transferStatus(dto.getTransferStatus())
                .transferErrorMsg(dto.getTransferErrorMsg())
                .country(dto.getCountry())
                .currency(dto.getCurrency())
                .amount(dto.getAmountInCents())
                .createTime(dto.getCreateTimeUtc())
                .finalStatusTime(dto.getFinalStatusTimeUtc())
                .targetInfo(targetInfo)
                .build();
    }

}
