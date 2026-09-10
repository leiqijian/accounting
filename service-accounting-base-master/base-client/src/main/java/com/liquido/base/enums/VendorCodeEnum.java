package com.liquido.base.enums;

import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.base.constant.dynamic.DynamicConstant;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * VendorCodeEnum
 */
@Getter
@Slf4j
public class VendorCodeEnum extends DynamicConstant<String> {

    private static final long serialVersionUID = 1L;

    public static final VendorCodeEnum UNKNOWN = new VendorCodeEnum("UNKNOWN", "Unknown");

    /* ==================== COMMON =========================== */
    public static final VendorCodeEnum PAYCASH = new VendorCodeEnum("PAYCASH", "Paycash");

    /* ==================== BR =========================== */
    public static final VendorCodeEnum BEXS = new VendorCodeEnum("BEXS", "Bexs");
    public static final VendorCodeEnum BS2 = new VendorCodeEnum("BS2", "BS2");
    public static final VendorCodeEnum TUNA = new VendorCodeEnum("TUNA", "Tuna");
    public static final VendorCodeEnum BANKLY = new VendorCodeEnum("BANKLY", "Bankly");
    public static final VendorCodeEnum CELCOIN = new VendorCodeEnum("CELCOIN", "Celcoin");
    public static final VendorCodeEnum PIC_PAY = new VendorCodeEnum("PIC_PAY", "PicPay");
    public static final VendorCodeEnum AME = new VendorCodeEnum("AME", "Ame");
    public static final VendorCodeEnum PAYPAL = new VendorCodeEnum("PAYPAL", "PayPal");
    public static final VendorCodeEnum ADIQ = new VendorCodeEnum("ADIQ", "ADIQ");
    public static final VendorCodeEnum MERCADO_PAGO
            = new VendorCodeEnum("MERCADO_PAGO", "Mercado Pago");
    public static final VendorCodeEnum RENDIMENTO = new VendorCodeEnum("RENDIMENTO", "Rendimento");
    public static final VendorCodeEnum DOCK = new VendorCodeEnum("DOCK", "dock");
    public static final VendorCodeEnum CIELO = new VendorCodeEnum("CIELO", "Cielo");
    public static final VendorCodeEnum GETNET = new VendorCodeEnum("GETNET", "GETNET");
    public static final VendorCodeEnum EWALLETS = new VendorCodeEnum("EWALLETS", "Ewallets");
    public static final VendorCodeEnum AARIN = new VendorCodeEnum("AARIN", "Aarin");
    public static final VendorCodeEnum TRACE_FINANCE =
            new VendorCodeEnum("TRACE_FINANCE", "Trace finance");


    /* ==================== MX =========================== */
    public static final VendorCodeEnum UNIPAGOS = new VendorCodeEnum("UNIPAGOS", "Unipagos");
    public static final VendorCodeEnum ARCUS = new VendorCodeEnum("ARCUS", "Arcus");
    public static final VendorCodeEnum MIT = new VendorCodeEnum("MIT", "MIT");
    public static final VendorCodeEnum GESTOPAGOS = new VendorCodeEnum("GESTOPAGOS", "Gestopagos");
    public static final VendorCodeEnum STP = new VendorCodeEnum("STP", "Stp");
    public static final VendorCodeEnum BANORTE = new VendorCodeEnum("BANORTE", "Banorte");
    public static final VendorCodeEnum OPENPAY = new VendorCodeEnum("OPENPAY", "OpenPay");
    public static final VendorCodeEnum DLOCAL = new VendorCodeEnum("DLOCAL", "Dlocal");
    public static final VendorCodeEnum FISERV = new VendorCodeEnum("FISERV", "Fiserv");
    public static final VendorCodeEnum KUSHKI = new VendorCodeEnum("KUSHKI", "Kushki");

    /* ==================== CO =========================== */
    public static final VendorCodeEnum BANCOLOMBIA
            = new VendorCodeEnum("BANCOLOMBIA", "Bancolombia");
    public static final VendorCodeEnum WOMPI = new VendorCodeEnum("WOMPI", "WomPi");
    public static final VendorCodeEnum CREDIBANCO = new VendorCodeEnum("CREDIBANCO", "Credibanco");
    public static final VendorCodeEnum COBRE = new VendorCodeEnum("COBRE", "Cobre");
    public static final VendorCodeEnum MONO = new VendorCodeEnum("MONO", "Mono");
    public static final VendorCodeEnum PSE = new VendorCodeEnum("PSE", "Pse");
    public static final VendorCodeEnum TUMIPAY = new VendorCodeEnum("TUMIPAY", "Tumipay");


    /* ==================== CL =========================== */
    public static final VendorCodeEnum PAYKU = new VendorCodeEnum("PAYKU", "Payku");
    public static final VendorCodeEnum SHINKANSEN = new VendorCodeEnum("SHINKANSEN", "Shinkansen");


    /* ==================== ZA =========================== */
    public static final VendorCodeEnum KLASHA = new VendorCodeEnum("KLASHA", "Klasha");

    /* ==================== PE =========================== */
    public static final VendorCodeEnum TUPAY = new VendorCodeEnum("TUPAY", "Tupay");


    private final String remark;

    private VendorCodeEnum(final String code, final String remark) {
        super(code);
        this.remark = remark;
    }

    public static VendorCodeEnum parse(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return parse(VendorCodeEnum.class, code.trim());
    }

    @Converter
    public static class Convert implements AttributeConverter<VendorCodeEnum, String> {
        @Override
        public String convertToDatabaseColumn(final VendorCodeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                return "";
            }
            return enumValue.getCode();
        }

        @Override
        public VendorCodeEnum convertToEntityAttribute(final String dbValue) {
            return VendorCodeEnum.parse(dbValue);
        }
    }
}
