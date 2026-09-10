package com.liquido.base.enums;

import java.util.Arrays;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * https://www.bindb.com/card-schemes
 * https://docs.dlocal.com/api-documentation/payins-api-reference/payment-methods/brazil
 */
@Getter
@RequiredArgsConstructor
public enum CardGroupTypeEnum {
    VISA("Visa", "VISA", "Visa"),
    ELO("Elo", "ELO", "Elo"),
    AMEX("Amex", "AMEX", "Amex"),
    MASTERCARD("MasterCard", "MASTERCARD", "MasterCard"),
    UNIONPAY("UnionPay", "UNIONPAY", "UnionPay"),
    HIPER("Hiper", "HIPER", "Hiper"),
    CARNET("Carnet", "CARNET", "Carnet"),

    HIPERCARD("HiperCard", "HIPERCARD", "HiperCard"),
    CARTAO_MERCADOLIVRE("CartaoMercadoLivre", "CARTAO_MERCADOLIVRE", "Cartao MercadoLivre"),
    AMERICAN_EXPRESS("AmericanExpress", "AMERICAN_EXPRESS", "American Express"),
    JCB("JCB", "JCB", "JCB"),
    AURA("Aura", "AURA", "Aura"),
    DISCOVER("Discover", "DISCOVER", "Discover"),
    CARTE_BLANCHE("CarteBlanche", "CARTE_BLANCHE", "CarteBlanche"),

    DINERS_CLUB("DinersClub", "DINERS_CLUB", "DinersClub"),
    ENROUTE("Enroute", "ENROUTE", "Enroute"),
    MAESTRO("Maestro", "MAESTRO", "Maestro"),
    MIR("MIR", "MIR", "MIR"),
    UATP("UATP", "UATP", "UATP"),
    ;

    private final String cardName;
    private final String code;
    private final String remark;

    public static CardGroupTypeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        return Arrays.stream(CardGroupTypeEnum.values())
                .filter(tmp -> tmp.getCode().equals(code.trim())).findFirst().orElse(null);
    }

    @Converter
    public static class Convert implements AttributeConverter<CardGroupTypeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final CardGroupTypeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("CardGroupType");
            }
            return enumValue.getCode();
        }

        @Override
        public CardGroupTypeEnum convertToEntityAttribute(final String dbValue) {
            return CardGroupTypeEnum.parse(dbValue);
        }
    }
}
