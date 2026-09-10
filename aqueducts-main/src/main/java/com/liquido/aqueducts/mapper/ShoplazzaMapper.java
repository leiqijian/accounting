package com.liquido.aqueducts.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.liquido.aqueducts.commons.enums.PaymentLinkPaymentMethod;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.config.BaseMapperConfig;
import com.liquido.aqueducts.vo.document.eventlog.ShoplazzaEventLog;
import com.liquido.aqueducts.vo.response.eventlog.ShoplazzaOrderItem;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.StringUtils;

@Mapper(config = BaseMapperConfig.class)
public abstract class ShoplazzaMapper {

    @Mapping(target = "link", source = "after.link")
    @Mapping(target = "linkId", source = "after.id")
    @Mapping(target = "orderId", source = "after.order_id")
    @Mapping(target = "currency", source = "after.currency")
    @Mapping(target = "shopDomain", source = "after.shop_domain")
    @Mapping(target = "merchantName", source = "after.merchant_code")
    @Mapping(target = "paymentStatus", source = "after.status")
    @Mapping(target = "email", source = "after.email")
    @Mapping(target = "phone", source = "after.phone_number")
    @Mapping(target = "refunded", source = "after._refunded")
    @Mapping(target = "settledVirgoId", source = "after.final_status_virgo_id")
    @Mapping(target = "refundedVirgoId", source = "after.refund_id")
    @Mapping(target = "finalStatusTimestamp", source = "after.final_status_timestamp")
    @Mapping(target = "refundTimestamp", source = "after.refund_time")
    public abstract ShoplazzaOrderItem toEventLog(final ShoplazzaEventLog vo);

    @AfterMapping
    void afterMapping(@MappingTarget final ShoplazzaOrderItem target, final ShoplazzaEventLog source) {
        // product code
        if (PaymentLinkPaymentMethod.BANK_TRANSFER_BR.name()
                .equals(source.getAfter().getPayment_method())) {
            target.setProductCode(ProductCode.TED.name());
        } else if (PaymentLinkPaymentMethod.BANK_TRANSFER_MX.name()
                .equals(source.getAfter().getPayment_method())) {
            target.setProductCode(ProductCode.SPEI_BANK_TRANSFER.name());
        } else if (PaymentLinkPaymentMethod.CREDIT_CARD.name()
                .equals(source.getAfter().getPayment_method())) {
            target.setProductCode(ProductCode.CARD.name());
        } else if (PaymentLinkPaymentMethod.BANK_TRANSFER_PE.name()
                .equals(source.getAfter().getPayment_method())) {
            target.setProductCode(ProductCode.BANK_TRANSFER.name());
        } else if (StringUtils.hasText(source.getAfter().getPayment_method())) {
            target.setProductCode(ProductCode.valueOf(source.getAfter().getPayment_method()).name());
        }

        switch (source.getAfter().getCurrency()) {
            case "BRL":
                target.setCountry("BR");
                break;
            case "MXN":
                target.setCountry("MX");
                break;
            case "COP":
                target.setCountry("CO");
                break;
            case "CLP":
                target.setCountry("CL");
                break;
            case "PEN":
                target.setCountry("PE");
                break;
            case "ZAR":
                target.setCountry("ZA");
            default:
                break;
        }

        target.setAmount(new BigDecimal(source.getAfter().getAmount()).multiply(new BigDecimal(100)).longValue());
        target.setCreateTime(source.getAfter().getCreate_time() / 1000);
        target.setEventTime(source.getSource().getTs_ms() / 1000);
        if (source.getAfter().getRefund_time() != null) {
            target.setRefundTimestamp(source.getAfter().getRefund_time() / 1000);
        }

    }

    public abstract List<ShoplazzaOrderItem> toEventLogList(final List<ShoplazzaEventLog> voList);
}
