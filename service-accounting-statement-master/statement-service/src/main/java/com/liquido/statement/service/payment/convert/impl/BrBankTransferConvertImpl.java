package com.liquido.statement.service.payment.convert.impl;

import java.util.Map;

import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.statement.pojo.dto.payment.BrBankTransferPayoutResult;
import com.liquido.statement.pojo.dto.payment.QueryPayoutResultDto;
import com.liquido.statement.service.payment.convert.PayoutResultConvert;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

/**
 * Country: Brazil
 * TED(BANK-TRANSFER) Payment Payout Response Data Converter
 */
@Component
public class BrBankTransferConvertImpl implements PayoutResultConvert<BrBankTransferPayoutResult> {

    @Override
    public ProductCodeEnum getProductCode() {
        return ProductCodeEnum.TED;
    }

    @Override
    public QueryPayoutResultDto convertResult(final BrBankTransferPayoutResult dto) {
        final Map<String, Object> targetInfo = Maps.newHashMap();
        targetInfo.put("targetName", dto.getTargetName());
        targetInfo.put("targetDocument", dto.getTargetDocument());
        targetInfo.put("targetBankAccountId", dto.getTargetPixKey());
        targetInfo.put("targetBankAccountType", dto.getTargetPixKeyType());
        targetInfo.put("targetPixKey", dto.getTargetPixKey());
        targetInfo.put("targetPixKeyType", dto.getTargetPixKeyType());
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
