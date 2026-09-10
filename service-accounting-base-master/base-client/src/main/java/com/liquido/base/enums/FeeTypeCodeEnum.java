package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum FeeTypeCodeEnum {

    TRANSACTION_FEE("TRANSACTION_FEE", "TransactionFee", "transaction fee"),

    FX("FX", "Fx", "Fx"),

    FX_LOSE("FX_LOSE", "FxLose", "fx lose"),

    TAX("TAX", "Tax", "tax"),

    /**
     * since for credit card installment business
     */
    ANTICIPATION_FEE("ANTICIPATION_FEE", "AnticipationFee", "anticipation fee"),

    /**
     * since for credit card installment business cost
     */
    ANTI_FRAUD_FEE("ANTI_FRAUD_FEE", "Anti-FraudFee", "anti-fraud fee"),

    /**
     * since for credit card installment business cost
     */
    THREE_DS_FEE("THREE_DS_FEE", "3DS", "3DS fee"),

    REFUND_FEE("REFUND_FEE", "RefundFee", "refund fee"),

    CHARGE_BACK_FEE("CHARGE_BACK_FEE", "ChargeBackFee", "charge back fee"),

    WITHDRAW_FEE("WITHDRAW_FEE", "WithdrawFee", "withdraw fee"),

    ;

    private final String code;
    private final String name;
    private final String remark;

    public static FeeTypeCodeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(FeeTypeCodeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<FeeTypeCodeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final FeeTypeCodeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("FeeTypeCode");
            }
            return enumValue.getCode();
        }

        @Override
        public FeeTypeCodeEnum convertToEntityAttribute(final String dbValue) {
            return FeeTypeCodeEnum.parse(dbValue);
        }
    }

}
