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
public enum FeeCodeEnum {

    TRANSACTION_FEE("TRANSACTION_FEE", "TransactionFee", "transaction fee"),

    /**
     * 信用卡分期
     */
    INSTALLMENT_FEE("INSTALLMENT_FEE", "InstallmentFee", "installment fee"),

    ANTICIPATION_FEE("ANTICIPATION_FEE", "AnticipationFee", "anticipation fee"),

    REFUND_FEE("REFUND_FEE", "RefundFee", "refund fee"),

    CHARGE_BACK_FEE("CHARGE_BACK_FEE", "Chargeback fee", "charge back fee"),

    REJECTED_FEE("REJECTED_FEE", "Rejected fee", "rejected fee"),

    FX("FX", "FX", "FX"),

    /**
     * BR-Fee * 12.68%
     */
    TAX("TAX", "TAX", "TAX"),

    /**
     * BR-AMOUNT * 0.38%
     */
    IOF("IOF", "IOF", "IOF"),

    /**
     * MX-FEE * 16%
     * CL-FEE * 19%
     */
    VAT("VAT", "VAT", "VAT"),

    /**
     * CO(tax):FEE * 19%
     */
    IVA("IVA", "IVA", "IVA"),

    /**
     * CO(tax):AMOUNT * 0.4%
     */
    GMF("GMF", "GMF", "GMF"),

    /**
     * PE(tax): FEE * 18%
     */
    IGV("IGV", "IGV", "IGV"),

    RETE_IVA("RETE_IVA", "Rete IVA", "Rete IVA"),

    RETE_ICA("RETE_ICA", "Rete ICA", "Rete ICA"),

    RETE_FUENTE("RETE_FUENTE", "Rete fuente", "Rete fuente"),

    ;

    private final String code;
    private final String name;
    private final String remark;

    public static FeeCodeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return Arrays.stream(FeeCodeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<FeeCodeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final FeeCodeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("FeeCode");
            }
            return enumValue.getCode();
        }

        @Override
        public FeeCodeEnum convertToEntityAttribute(final String dbValue) {
            return FeeCodeEnum.parse(dbValue);
        }
    }

}
