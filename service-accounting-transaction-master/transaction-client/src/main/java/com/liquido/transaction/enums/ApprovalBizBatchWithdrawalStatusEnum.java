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
public enum ApprovalBizBatchWithdrawalStatusEnum {

    PROCESSING("PROCESSING", "processing", false, false, ApprovalStatusEnum.PROCESSING),

    COMPLETED("COMPLETED", "completed", true, false, ApprovalStatusEnum.COMPLETED),

    REJECTED("REJECTED", "rejected", true, false, ApprovalStatusEnum.REJECTED),

    CANCELED("CANCELED", "canceled", false, false, ApprovalStatusEnum.CANCELED),

    FAILED("FAILED", "failed", true, true, null),

    ;

    private final String code;

    private final String remark;

    private final Boolean finalHandle;

    private final Boolean needRetry;

    private final ApprovalStatusEnum correctApprovalStatus;

    public static ApprovalBizBatchWithdrawalStatusEnum parse(final String code) {
        return Arrays.stream(ApprovalBizBatchWithdrawalStatusEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert
            implements AttributeConverter<ApprovalBizBatchWithdrawalStatusEnum, String> {

        @Override
        public String convertToDatabaseColumn(
                final ApprovalBizBatchWithdrawalStatusEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return null;
            }
            return enumValue.getCode();
        }

        @Override
        public ApprovalBizBatchWithdrawalStatusEnum convertToEntityAttribute(final String dbValue) {
            if (StringUtils.isBlank(dbValue)) {
                return null;
            }
            return ApprovalBizBatchWithdrawalStatusEnum.parse(dbValue);
        }
    }
}
