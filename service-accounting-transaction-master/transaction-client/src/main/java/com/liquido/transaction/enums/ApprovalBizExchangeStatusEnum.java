package com.liquido.transaction.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum ApprovalBizExchangeStatusEnum {

    WAIT_EXCHANGE_RATE("WAIT_EXCHANGE_RATE", "wait exchange rate", false, false,
            ApprovalStatusEnum.PROCESSING),

    WAIT_CONFIRM_CONTRACT("WAIT_CONFIRM_CONTRACT", "wait confirm contract", false, false,
            ApprovalStatusEnum.WAIT_PROCESSING),

    WAIT_PROOF("WAIT_PROOF", "wait proof", false, false, ApprovalStatusEnum.PROCESSING),

    PROCESSING("PROCESSING", "processing", false, false, ApprovalStatusEnum.PROCESSING),

    COMPLETED("COMPLETED", "completed", true, false, ApprovalStatusEnum.COMPLETED),

    REJECTED("REJECTED", "rejected", true, false, ApprovalStatusEnum.REJECTED),

    CANCELED("CANCELED", "canceled", false, false, ApprovalStatusEnum.CANCELED),

    FAILED("FAILED", "failed", true, true, null);


    private final String code;
    private final String remark;
    private final Boolean finalHandle;
    private final Boolean needRetry;
    private final ApprovalStatusEnum correctApprovalStatus;

    public static ApprovalBizExchangeStatusEnum parse(final String code) {
        return Arrays.stream(ApprovalBizExchangeStatusEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert
            implements AttributeConverter<ApprovalBizExchangeStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(final ApprovalBizExchangeStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return null;
            }
            return enumValue.getCode();
        }

        @Override
        public ApprovalBizExchangeStatusEnum convertToEntityAttribute(final String dbValue) {
            if (StringUtils.isBlank(dbValue)) {
                return null;
            }
            return ApprovalBizExchangeStatusEnum.parse(dbValue);
        }
    }

}
