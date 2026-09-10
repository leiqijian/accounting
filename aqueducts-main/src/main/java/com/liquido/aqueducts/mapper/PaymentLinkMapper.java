package com.liquido.aqueducts.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liquido.aqueducts.commons.enums.PaymentLinkPaymentMethod;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.config.BaseMapperConfig;
import com.liquido.aqueducts.vo.document.eventlog.PaymentLinkEventLog;
import com.liquido.aqueducts.vo.response.eventlog.PaymentLinkItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.StringUtils;

@Slf4j
@Mapper(config = BaseMapperConfig.class)
public abstract class PaymentLinkMapper {

    @Mapping(target = "linkId", source = "after.link_id_to_string")
    @Mapping(target = "amount", source = "after.amount")
    @Mapping(target = "country", source = "after.country")
    @Mapping(target = "currency", source = "after.currency")
    @Mapping(target = "clientId", source = "after.client_id")
    @Mapping(target = "merchantReference", source = "after.order_id")
    @Mapping(target = "merchantName", source = "after.merchant_name")
    @Mapping(target = "paymentStatus", source = "after.payment_status")
    @Mapping(target = "refundStatus", source = "after.refund_status")
    @Mapping(target = "refundAmount", source = "after.refund_amount")
    @Mapping(target = "email", source = "after.email")
    @Mapping(target = "phone", source = "after.phone")
    @Mapping(target = "settledVirgoId", source = "after.final_status_virgo_id")
    @Mapping(target = "refundedVirgoId", source = "after.refund_id")
    @Mapping(target = "finalStatusTimestamp", source = "after.final_status_timestamp")
    @Mapping(target = "refundTimestamp", source = "after.refund_timestamp")
    @Mapping(target = "subMerchantId", source = "after.sub_merchant_id")
    @Mapping(target = "description", source = "after.description")
    public abstract PaymentLinkItem toEventLog(final PaymentLinkEventLog vo);

    @AfterMapping
    void afterMapping(@MappingTarget final PaymentLinkItem target,
                      final PaymentLinkEventLog source) {
        // product code
        if (PaymentLinkPaymentMethod.BANK_TRANSFER_BR.name()
                .equals(source.getAfter().getFinal_payment_method())) {
            target.setProductCode(ProductCode.TED.name());
        } else if (PaymentLinkPaymentMethod.BANK_TRANSFER_MX.name()
                .equals(source.getAfter().getFinal_payment_method())) {
            target.setProductCode(ProductCode.SPEI_BANK_TRANSFER.name());
        } else if (PaymentLinkPaymentMethod.BANK_TRANSFER_PE.name()
                .equals(source.getAfter().getFinal_payment_method())) {
            target.setProductCode(ProductCode.BANK_TRANSFER.name());
        } else if (PaymentLinkPaymentMethod.CREDIT_CARD.name()
                .equals(source.getAfter().getFinal_payment_method())) {
            target.setProductCode(ProductCode.CARD.name());
        } else if (StringUtils.hasText(source.getAfter().getFinal_payment_method())) {
            target.setProductCode(
                    ProductCode.valueOf(source.getAfter().getFinal_payment_method()).name());
        }

        final var mapper = new ObjectMapper();
        if (StringUtils.hasText(source.getAfter().getMetadata())) {
            try {
                target.setMetadata(mapper.readValue(source.getAfter().getMetadata(),
                        HashMap.class));
            } catch (JsonProcessingException e) {
                log.error("Payment link metadata process error. metadata: {}",
                        source.getAfter().getMetadata());
            }
        }

        if (StringUtils.hasText(source.getAfter().getAppendix())) {
            try {
                target.setAppendix(
                        mapper.readValue(source.getAfter().getAppendix(), Map.class));
            } catch (JsonProcessingException e) {
                log.error("Payment link appendix process error. appendix detail: {}",
                        source.getAfter().getAppendix());
            }
        }

        target.setCreateTimestamp(source.getAfter().getCreate_time() / 1000);
        target.setEventTime(source.getSource().getTs_ms() / 1000);

    }

    public abstract List<PaymentLinkItem> toEventLogList(final List<PaymentLinkEventLog> voList);
}
