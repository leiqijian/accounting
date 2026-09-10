package com.liquido.aqueducts.mapper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

import com.liquido.aqueducts.commons.enums.CountryCode;
import com.liquido.aqueducts.commons.enums.DirectionCode;
import com.liquido.aqueducts.commons.enums.PaymentStatusCode;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.commons.enums.TransactionType;
import com.liquido.aqueducts.config.BaseMapperConfig;
import com.liquido.aqueducts.util.DateUtil;
import com.liquido.aqueducts.util.JsonUtil;
import com.liquido.aqueducts.util.PayinEventLogUtil;
import com.liquido.aqueducts.util.PayoutEventLogUtil;
import com.liquido.aqueducts.vo.document.eventlog.MarketPlaceOrderEventLog;
import com.liquido.aqueducts.vo.document.eventlog.PayinEventLog;
import com.liquido.aqueducts.vo.document.eventlog.PayoutBackEventLog;
import com.liquido.aqueducts.vo.document.eventlog.PayoutEventLog;
import com.liquido.aqueducts.vo.document.eventlog.SubAccountEventLog;
import com.liquido.aqueducts.vo.document.eventlog.SubAccountPayBackEventLog;
import com.liquido.aqueducts.vo.payout.BankTransferPaymentInfo;
import com.liquido.aqueducts.vo.payout.PayeeInfo;
import com.liquido.aqueducts.vo.payout.PaymentInfo;
import com.liquido.aqueducts.vo.payout.PixPaymentInfo;
import com.liquido.aqueducts.vo.response.eventlog.EventLogPayoutOthers;
import com.liquido.aqueducts.vo.response.eventlog.TransactionEventLogItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.StringUtils;

@Slf4j
@Mapper(config = BaseMapperConfig.class)
public abstract class EventLogListMapper {

    @Mapping(target = "uniqueId", source = "after.idempotency_key")
    @Mapping(target = "merchantReference", source = "after.idempotency_key")
    @Mapping(target = "transactionStatus", source = "after.status")
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "currency", source = "after.currency")
    @Mapping(target = "country", source = "after.country")
    @Mapping(target = "flags", source = "after.flags")
    @Mapping(target = "submitTime", source = "after.submit_unix_time")
    @Mapping(target = "finalStatusTime", source = "after.final_status_unix_time")
    @Mapping(target = "documentId", source = "after.target_document_id")
    @Mapping(target = "tradeTransferStatus", source = "after.status")
    @Mapping(target = "description", source = "after.payer_comment")
    public abstract TransactionEventLogItem toEventLog(final PayoutBackEventLog vo);

    @AfterMapping
    void afterMapping(@MappingTarget final TransactionEventLogItem target,
                      final PayoutBackEventLog source) {
        if ("REJECTED".equals(source.getAfter().getStatus()) && source.getBefore() != null
                && "SETTLED".equals(source.getBefore().getStatus())) {
            target.setTransactionStatus("SETTLED");
            target.setDirectionCode(DirectionCode.REJECTED.name());
        } else {
            target.setDirectionCode(DirectionCode.SETTLED.name());
        }
        target.setMerchantCode(source.getSource().getTable().replace("payout_", ""));
        final BigDecimal sourceAmount = new BigDecimal(source.getAfter().getAmount());
        target.setAmount(sourceAmount.multiply(new BigDecimal(100)).longValue());
        target.setTransactionType(TransactionType.PAY_OUT.name());
        target.setTradeTransactionType(TransactionType.PAY_OUT.name());

        // sub_merchant_id
        target.setSubMerchantId(JsonUtil.getJsonText(
                JsonUtil.parseJson(source.getAfter().getReserve()), "sub_merchant_id"));

        // createTime
        target.setCreateTime(
                DateUtil.UTCDateToTimestamp(source.getAfter().getCreate_time()) / 1000);

        // product code
        target.setProductCode(PayoutEventLogUtil.paymentTypeToProductCode(
                source.getAfter().getPayment_type(), source.getAfter().getCountry()));

        target.setVendor(StringUtils.hasText(source.getAfter().getSettle_vendor()) ?
                source.getAfter().getSettle_vendor() :
                source.getAfter().getVendor_name());

        target.setOthers(new EventLogPayoutOthers(
                PayoutEventLogUtil.getName(source),
                source.getAfter().getTransaction_id(),
                source.getAfter().getSubmit_unix_time(),
                source.getAfter().getTransfer_error_msg(),
                source.getAfter().getIdempotency_key(),
                source.getAfter().getTarget_account_id(),
                source.getAfter().getBranch_id(),
                source.getAfter().getBank_id(),
                source.getAfter().getBank_name(),
                PayoutEventLogUtil.getBankCode(source),
                source.getAfter().getTarget_bank_account_type(),
                source.getAfter().getTransfer_status_code(),
                source.getAfter().getPayer_comment()
        ));

    }

    @Mapping(target = "uniqueId", source = "after.idempotency_key")
    @Mapping(target = "merchantReference", source = "after.idempotency_key")
    @Mapping(target = "transactionStatus", source = "after.status")
    @Mapping(target = "amount", source = "after.amount")
    @Mapping(target = "merchantCode", source = "after.merchant_name")
    @Mapping(target = "currency", source = "after.currency")
    @Mapping(target = "country", source = "after.country")
    @Mapping(target = "flags", source = "after.flags")
    @Mapping(target = "submitTime", source = "after.submit_unix_time")
    @Mapping(target = "finalStatusTime", source = "after.final_status_unix_time")
    @Mapping(target = "tradeTransferStatus", source = "after.status")
    @Mapping(target = "description", source = "after.payer_comment")
    public abstract TransactionEventLogItem toEventLog(final PayoutEventLog vo);

    @AfterMapping
    void afterMapping(@MappingTarget final TransactionEventLogItem target,
                      final PayoutEventLog source) {
        if ("REJECTED".equals(source.getAfter().getStatus()) && source.getBefore() != null
                && "SETTLED".equals(source.getBefore().getStatus())) {
            target.setTransactionStatus("SETTLED");
            target.setDirectionCode(DirectionCode.REJECTED.name());
        } else {
            target.setDirectionCode(DirectionCode.SETTLED.name());
        }
        target.setTransactionType(TransactionType.PAY_OUT.name());
        target.setTradeTransactionType(TransactionType.PAY_OUT.name());

        // sub_merchant_id
        target.setSubMerchantId(source.getAfter().getSub_merchant_id());

        // createTime
        target.setCreateTime(
                DateUtil.UTCDateToTimestamp(source.getAfter().getCreate_time()) / 1000);

        // product code
        target.setProductCode(PayoutEventLogUtil.paymentTypeToProductCode(
                source.getAfter().getPayment_type(), source.getAfter().getCountry()));

        target.setVendor(StringUtils.hasText(source.getAfter().getSettle_vendor()) ?
                source.getAfter().getSettle_vendor() :
                source.getAfter().getVendor_name());


        PayeeInfo payeeInfo = PayeeInfo.builder().build();
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .pix(PixPaymentInfo.builder().build())
                .bankTransfer(BankTransferPaymentInfo.builder().build())
                .build();

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            payeeInfo = mapper.readValue(
                    source.getAfter().getPayee_info(), PayeeInfo.class);
            paymentInfo = Optional.of(mapper.readValue(
                            source.getAfter().getPayment_info(), PaymentInfo.class))
                    .orElse(paymentInfo);
            if (paymentInfo.getBankTransfer() == null) {
                paymentInfo.setBankTransfer(BankTransferPaymentInfo.builder().build());
            } else if (paymentInfo.getPix() == null) {
                paymentInfo.setPix(PixPaymentInfo.builder().build());
            }
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage());
        }

        target.setDocumentId(payeeInfo.getTargetDocumentId());
        target.setOthers(new EventLogPayoutOthers(
                        PayoutEventLogUtil.getName(paymentInfo, payeeInfo, source.getAfter().getResponse()),
                        source.getAfter().getTransaction_id(),
                        source.getAfter().getSubmit_unix_time(),
                        source.getAfter().getTransfer_error_msg(),
                        source.getAfter().getIdempotency_key(),
                        paymentInfo.getBankTransfer().getTargetBankAccountId(),
                        paymentInfo.getBankTransfer().getTargetBankAgency(),
                        paymentInfo.getBankTransfer().getTargetBankId(),
                        paymentInfo.getBankTransfer().getTargetBankName(),
                        PayoutEventLogUtil.getBankCode(paymentInfo.getBankTransfer().getTargetBankCode()),
                        paymentInfo.getBankTransfer().getTargetBankAccountType(),
                        source.getAfter().getTransfer_status_code(),
                        source.getAfter().getPayer_comment()
                )
        );


    }

    @Mapping(target = "uniqueId", source = "after.idempotency_key")
    @Mapping(target = "merchantReference", source = "after.idempotency_key")
    @Mapping(target = "transactionStatus", source = "after.transfer_status")
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "currency", source = "after.currency")
    @Mapping(target = "flags", source = "after.flags")
    @Mapping(target = "country", source = "after.country")
    @Mapping(target = "tradeTransactionType", source = "after.transaction_type")
    @Mapping(target = "tradeTransferStatus", source = "after.transfer_status")
    @Mapping(target = "subMerchantId", source = "after.sub_merchant_id")
    @Mapping(target = "description", source = "after.payer_comment")
    public abstract TransactionEventLogItem toEventLog(PayinEventLog vo);

    @AfterMapping
    void afterMapping(@MappingTarget final TransactionEventLogItem target,
                      final PayinEventLog source) {
        Map<String, Object> others = new LinkedHashMap<>();
        Map<String, Object> refundAdditionalInfo =
                Optional.ofNullable(PayinEventLogUtil.getRefundAccountInfo(source))
                        .orElse(new HashMap<>());
        // documentId
        String payerJsonStr = source.getAfter().getPayer();
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (StringUtils.hasText(payerJsonStr)) {
                JsonNode payerNode = mapper.readTree(payerJsonStr);
                if (payerNode.hasNonNull("document") &&
                        payerNode.get("document").hasNonNull("documentId")) {
                    target.setDocumentId(payerNode.get("document").get("documentId").asText());
                }
                if (payerNode.hasNonNull("billingAddress") &&
                        payerNode.get("billingAddress").hasNonNull("city")) {
                    others.put("payerCity", payerNode.get("billingAddress").get("city").asText());
                }
            } else {
                target.setDocumentId("");
            }
        } catch (JsonProcessingException ignored) {
            target.setDocumentId("");
        }

        ObjectMapper mapper = new ObjectMapper();
        // direction code
        if (Objects.nonNull(source.getBefore()) &&
                PaymentStatusCode.CHARGED_BACK.name()
                        .equals(source.getBefore().getTransfer_status()) &&
                PaymentStatusCode.SETTLED.name().equals(source.getAfter().getTransfer_status())) {
            target.setTransactionStatus(PaymentStatusCode.SETTLED.name());
            target.setDirectionCode(DirectionCode.CHARGE_BACK_REJECTED.name());
        } else if ("PAY_IN".equals(source.getAfter().getTransaction_type())
                && PaymentStatusCode.SETTLED.name()
                .equals(source.getAfter().getTransfer_status())) {
            target.setDirectionCode(DirectionCode.SETTLED.name());
        } else if (DirectionCode.REFUND.name().equals(source.getAfter().getTransaction_type())) {
            target.setDirectionCode(DirectionCode.REFUND.name());
            others.put("referenceId", source.getAfter().getReference_id());
        } else if (PaymentStatusCode.CHARGED_BACK.name()
                .equals(source.getAfter().getTransfer_status())) {
            target.setTransactionStatus(DirectionCode.SETTLED.name());
            target.setDirectionCode(DirectionCode.CHARGE_BACK.name());
        } else {
            target.setDirectionCode(DirectionCode.SETTLED.name());
        }
        target.setMerchantCode(source.getAfter().getMerchant_name());
        target.setTransactionType(TransactionType.PAY_IN.name());
        // amount
        if (source.getAfter().getAmount() != null) {
            final BigDecimal sourceAmount = new BigDecimal(source.getAfter().getAmount());
            target.setAmount(sourceAmount.longValue());
        }

        target.setProductCode(PayinEventLogUtil.paymentMethodToProductCode(
                source.getAfter().getPayment_method(), source.getAfter().getCountry()));

        if (ProductCode.CARD.name().equals(target.getProductCode())) {
            try {
                others.put("cardType", "CREDIT_CARD");
                if (StringUtils.hasText(source.getAfter().getPayment_info())) {
                    JsonNode paymentInfo = mapper.readTree(source.getAfter().getPayment_info());
                    if (paymentInfo.hasNonNull("cardPaymentInfo")) {
                        final JsonNode cardPaymentInfo = paymentInfo.get("cardPaymentInfo");
                        others.put("cardInstallments", 0);
                        if (cardPaymentInfo.hasNonNull("installments")) {
                            others.put("cardInstallments",
                                    cardPaymentInfo.get("installments").asInt());
                        }

                        if (cardPaymentInfo.hasNonNull("card3dsInfo")) {
                            JsonNode card3dsInfo = cardPaymentInfo.get("card3dsInfo");
                            if (card3dsInfo.hasNonNull("use3ds")) {
                                others.put("cardUse3ds", card3dsInfo.get("use3ds").asBoolean());
                            }
                        }

                        if (cardPaymentInfo.hasNonNull("additionalCardInfo")) {
                            JsonNode additionalCardInfo = cardPaymentInfo.get("additionalCardInfo");
                            if (additionalCardInfo.hasNonNull("country")) {
                                others.put("cardRegion", "INTERNATIONAL");
                                if (target.getCountry().equals(
                                        additionalCardInfo.get("country").asText())) {
                                    others.put("cardRegion", "LOCAL");
                                }
                            }
                            if (additionalCardInfo.hasNonNull("accountFundingSource")
                                    && "DEBIT".equals(
                                    additionalCardInfo.get("accountFundingSource").asText())) {
                                others.put("cardType", "DEBIT_CARD");
                            }
                        }

                        JsonNode displayedCardInfo = cardPaymentInfo.get("displayedCardInfo");
                        others.put("cardNumberFirst6", displayedCardInfo.get("bin").asText());
                        others.put("cardNumberLast4", displayedCardInfo.get("last4").asText());
                        others.put("cardBrand", displayedCardInfo.get("brand").asText());
                        refundAdditionalInfo.put("cardBrand",
                                displayedCardInfo.get("brand").asText());
                        refundAdditionalInfo.put("cardFourLastDigits",
                                displayedCardInfo.get("last4").asText());
                        refundAdditionalInfo.put("authorizationCode",
                                cardPaymentInfo.get("authorizationCode").asText());
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // createTime
        target.setCreateTime(
                DateUtil.UTCDateToTimestamp(source.getAfter().getCreate_time()) / 1000);

        BigDecimal purchaseAmount = new BigDecimal(source.getAfter().getFinal_amount());
        refundAdditionalInfo.put("purchaseAmount", purchaseAmount.longValue());
        refundAdditionalInfo.put("transactionDate", source.getAfter().getCreate_time());

        // refundAdditionalInfo
        if (ProductCode.NEQUI.name().equals(target.getProductCode())) {
            JsonNode refundAdditionalInfoJsonNode = null;
            try {
                refundAdditionalInfoJsonNode =
                        mapper.readTree(
                                Optional.ofNullable(
                                                source.getAfter().getRefund_additional_info())
                                        .orElse("{}"));
            } catch (Exception e) {
            }
            others.put("refundAdditionalInfo", refundAdditionalInfoJsonNode);
        } else {
            others.put("refundAdditionalInfo", refundAdditionalInfo);
        }

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            // in_progress data don't exist final_status_time， use create_time instead.
            if (source.getAfter().getFinal_status_time() != null) {
                target.setFinalStatusTime(
                        sdf.parse(source.getAfter().getFinal_status_time()).getTime() / 1000);
            } else {
                target.setFinalStatusTime(
                        sdf.parse(source.getAfter().getCreate_time()).getTime() / 1000);
            }
            target.setSubmitTime(sdf.parse(source.getAfter().getSubmit_time()).getTime() / 1000);
        } catch (ParseException | NullPointerException e) {
            target.setFinalStatusTime(0L);
            target.setSubmitTime(0L);
        }

        target.setVendor(StringUtils.hasText(source.getAfter().getSettle_vendor()) ?
                source.getAfter().getSettle_vendor() :
                source.getAfter().getVendor());

        others.put("transferErrorMsg", source.getAfter().getTransfer_error_msg());
        try {
            if (StringUtils.hasText(source.getAfter().getPayer())) {
                JsonNode payerNode = mapper.readTree(source.getAfter().getPayer());
                if (payerNode.hasNonNull("name")) {
                    others.put("targetName", payerNode.get("name").asText().length() > 200 ?
                            payerNode.get("name").asText().substring(0, 200) :
                            payerNode.get("name").asText());
                }
                if (payerNode.hasNonNull("email")) {
                    others.put("payerEmail", payerNode.get("email").asText());
                }
                if (payerNode.hasNonNull("phone")) {
                    others.put("payerPhone", payerNode.get("phone").asText());
                }
            }
        } catch (JsonProcessingException ignored) {
        }
        others.put("payerComment", source.getAfter().getPayer_comment());

        String amount_details = source.getAfter().getAmount_details();
        others.put("baseAmount", null);
        if (StringUtils.hasLength(amount_details)) {
            try {
                Map<String, Object> map = mapper.readValue(amount_details, Map.class);
                others.put("baseAmount", map);
            } catch (JsonProcessingException ignore) {
            }
        }
//        others.put("refundAccountInfo", PayinEventLogUtil.getRefundAccountInfo(source));

        // orderId
        if (StringUtils.hasText(source.getAfter().getOrder_info())) {
            try {
                JsonNode orderInfo = mapper.readTree(source.getAfter().getOrder_info());
                if (orderInfo.hasNonNull("orderId")) {
                    others.put("orderId", orderInfo.get("orderId").asText());
                }
            } catch (Exception ignore) {
            }
        }

        //expirationTime
        try {
            if (StringUtils.hasText(source.getAfter().getPayment_info())) {
                final JsonNode paymentInfo = mapper.readTree(source.getAfter().getPayment_info());
                if (paymentInfo.hasNonNull("pixPaymentInfo")) {
                    final JsonNode pixPaymentInfo = paymentInfo.get("pixPaymentInfo");
                    final String expirationTime =
                            Optional.ofNullable(pixPaymentInfo.get("expirationTime"))
                                    .map(JsonNode::asText).orElse(null);
                    if (StringUtils.hasText(expirationTime)) {
                        others.put("expirationTime", sdf.parse(expirationTime).getTime() / 1000);
                    }
                }
                if (paymentInfo.hasNonNull("boletoPaymentInfo")) {
                    final JsonNode boletoPaymentInfo = paymentInfo.get("boletoPaymentInfo");
                    Optional.ofNullable(boletoPaymentInfo)
                            .map(v -> v.path("paymentTerm"))
                            .map(v -> v.path("paymentDeadline"))
                            .map(JsonNode::asText)
                            .filter(StringUtils::hasText)
                            .ifPresent(v -> others.put("expirationTime", v));
                    Optional.ofNullable(boletoPaymentInfo)
                            .map(v -> v.path("paidAmount"))
                            .map(JsonNode::asText)
                            .filter(StringUtils::hasText)
                            .ifPresent(v -> others.put("paidAmount", v));
                }
            }
        } catch (Exception ignore) {
        }

        others.put("transferStatusCode", source.getAfter().getTransfer_status_code());
        target.setOthers(others);
    }

    @Mapping(target = "uniqueId", source = "after.idempotency_key")
    @Mapping(target = "merchantReference", source = "after.idempotency_key")
    @Mapping(target = "transactionStatus", source = "after.order_status")
    @Mapping(target = "currency", source = "after.currency")
    @Mapping(target = "country", source = "after.country_code")
    @Mapping(target = "flags", source = "after.flags")
    @Mapping(target = "submitTime", source = "after.submit_unix_time")
    @Mapping(target = "finalStatusTime", source = "after.final_status_unix_time")
    @Mapping(target = "tradeTransferStatus", source = "after.order_status")
    public abstract TransactionEventLogItem toEventLog(MarketPlaceOrderEventLog vo);

    @AfterMapping
    void afterMapping(@MappingTarget final TransactionEventLogItem target,
                      final MarketPlaceOrderEventLog source) {
        // direction code
        target.setDirectionCode(DirectionCode.SETTLED.name());
        // merchant code
        target.setMerchantCode(source.getSource().getTable().replace("orders_", ""));
        target.setTransactionType(TransactionType.MARKET_PLACE_ORDERS.name());
        // amount
        final BigDecimal amountUnit = new BigDecimal(100);
        BigDecimal sourceAmount;
        final String amount = source.getAfter().getAmount();
        if (StringUtils.hasLength(amount)) {
            sourceAmount = new BigDecimal(amount);
        } else {
            sourceAmount = new BigDecimal("0");
        }
        target.setAmount(sourceAmount.multiply(amountUnit).longValue());
        // documentId
        target.setDocumentId("");
        target.setTradeTransactionType(TransactionType.MARKET_PLACE_ORDERS.name());
        // product code
        if ("GiftCard".equals(source.getAfter().getCategory())) {
            target.setProductCode(ProductCode.GIFTCARD.name());
        } else if ("PhoneTopup".equals(source.getAfter().getCategory())) {
            target.setProductCode(ProductCode.TOPUP.name());
        } else if ("Utility".equals(source.getAfter().getCategory())) {
            target.setProductCode(ProductCode.UTILITY.name());
        }

        target.setVendor(source.getAfter().getVendor_name());

        // createTime
        target.setCreateTime(
                DateUtil.UTCDateToTimestamp(source.getAfter().getCreate_time()) / 1000);

        Map<String, Object> others = new LinkedHashMap<>();
        others.put("transferErrorMsg", source.getAfter().getOrder_error_msg());
        others.put("skuCode", source.getAfter().getSku());
        others.put("paymentId", source.getAfter().getTransaction_id());
        others.put("requestId", source.getAfter().getIdempotency_key());
        others.put("submitUnixTime", source.getAfter().getSubmit_unix_time());
        others.put("transferStatusCode", source.getAfter().getOrder_status_code());
        target.setOthers(others);
    }


    @Mapping(target = "uniqueId", source = "after.transaction_id")
    @Mapping(target = "merchantReference", source = "after.transaction_id")
    @Mapping(target = "transactionStatus", source = "after.status")
    @Mapping(target = "currency", source = "after.currency")
    @Mapping(target = "flags", source = "after.flags")
    @Mapping(target = "submitTime", source = "after.submit_unix_time")
    @Mapping(target = "finalStatusTime", source = "after.final_status_unix_time")
    @Mapping(target = "tradeTransferStatus", source = "after.status")
    public abstract TransactionEventLogItem toEventLog(SubAccountPayBackEventLog vo);

    @AfterMapping
    void afterMapping(@MappingTarget final TransactionEventLogItem target,
                      final SubAccountPayBackEventLog source) {
        // direction code
        target.setDirectionCode(DirectionCode.SETTLED.name());
        // merchant code
        target.setMerchantCode(source.getSource().getTable().replace("sub_account_payback_", ""));
        //documentId
        target.setDocumentId("");

        final BigDecimal amountUnit = new BigDecimal(100);
        final BigDecimal sourceAmount = new BigDecimal(source.getAfter().getAmount());
        target.setAmount(sourceAmount.multiply(amountUnit).longValue());
        target.setCountry(CountryCode.MX.name());
        target.setTransactionType(TransactionType.PAY_IN.name());
        target.setTradeTransactionType(TransactionType.PAY_IN.name());

        ObjectId id = (ObjectId) source.get_id();
        target.setEventTime(id.getTimestamp());

        if (source.getAfter().getSub_account_id().startsWith("646")) {
            target.setVendor("UNIPAGOS");
        } else if (source.getAfter().getSub_account_id().startsWith("706")) {
            target.setVendor("ARCUS");
        }

        // createTime
        target.setCreateTime(
                DateUtil.UTCDateToTimestamp(source.getAfter().getCreate_time()) / 1000);

        // product code
        target.setProductCode(ProductCode.SPEI_VA.name());
        Map<String, String> others = new LinkedHashMap<>();
        others.put("transferErrorMsg", source.getAfter().getVendor_error_message());
        others.put("paymentId", source.getAfter().getTransaction_id());
        others.put("accountId", source.getAfter().getSub_account_id());
        List<SubAccountEventLog> sub_account_info = source.getAfter().getSub_account_info();
        if (sub_account_info != null && !sub_account_info.isEmpty()) {
            others.put("requestId", sub_account_info.get(0).getAfter().getIdempotency_key());
        } else {
            others.put("requestId", "");
        }
        target.setOthers(others);
    }

}
