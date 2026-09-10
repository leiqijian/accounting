package com.liquido.aqueducts.mapper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

import com.liquido.aqueducts.commons.enums.PaymentMethodCode;
import com.liquido.aqueducts.commons.enums.PaymentStatusCode;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.commons.enums.TransactionType;
import com.liquido.aqueducts.util.CommonUtil;
import com.liquido.aqueducts.util.DateUtil;
import com.liquido.aqueducts.util.JsonUtil;
import com.liquido.aqueducts.util.PayinEventLogUtil;
import com.liquido.aqueducts.util.PayoutEventLogUtil;
import com.liquido.aqueducts.vo.document.eventlog.MarketPlaceOrderEventLog;
import com.liquido.aqueducts.vo.document.eventlog.PayinEventLog;
import com.liquido.aqueducts.vo.document.eventlog.PayoutBackEventLog;
import com.liquido.aqueducts.vo.document.eventlog.PayoutEventLog;
import com.liquido.aqueducts.vo.document.eventlog.SubAccountPayBackEventLog;
import com.liquido.aqueducts.vo.payout.BankTransferPaymentInfo;
import com.liquido.aqueducts.vo.payout.PayeeInfo;
import com.liquido.aqueducts.vo.payout.PaymentInfo;
import com.liquido.aqueducts.vo.payout.PixPaymentInfo;
import com.liquido.aqueducts.vo.response.Account;
import com.liquido.aqueducts.vo.response.BankTransferDetail;
import com.liquido.aqueducts.vo.response.CardInfo;
import com.liquido.aqueducts.vo.response.CepCredentials;
import com.liquido.aqueducts.vo.response.ClabeAccountTransferDetail;
import com.liquido.aqueducts.vo.response.Information;
import com.liquido.aqueducts.vo.response.MarketPlaceDetail;
import com.liquido.aqueducts.vo.response.PayCashDetail;
import com.liquido.aqueducts.vo.response.PayerInfo;
import com.liquido.aqueducts.vo.response.PayinBankTransferDetail;
import com.liquido.aqueducts.vo.response.PayinCardDetail;
import com.liquido.aqueducts.vo.response.PayinDefaultDetail;
import com.liquido.aqueducts.vo.response.PayinPixDetail;
import com.liquido.aqueducts.vo.response.PayinSpeiVaDetail;
import com.liquido.aqueducts.vo.response.PayoutBeneficiary;
import com.liquido.aqueducts.vo.response.PayoutDetail;
import com.liquido.aqueducts.vo.response.PayoutOthers;
import com.liquido.aqueducts.vo.response.PixCredentials;
import com.liquido.aqueducts.vo.response.ProcessingInfo;
import com.liquido.aqueducts.vo.response.SubAccountInfo;
import com.liquido.aqueducts.vo.response.TransactionLifeCycle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class TransactionDetailsMapper {

    public static PayoutDetail fromPayoutBackEventLogList(
            final List<PayoutBackEventLog> eventLogs) {
        if (eventLogs.isEmpty()) {
            return null;
        }

        final PayoutDetail detail = new PayoutDetail();
        final PayoutBackEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);
        detail.setUniqueId(lastRecord.getAfter().getIdempotency_key());
        detail.setMerchantReference(lastRecord.getAfter().getIdempotency_key());
        final BigDecimal amountUnit = new BigDecimal(100);
        final BigDecimal sourceAmount = new BigDecimal(lastRecord.getAfter().getAmount());
        detail.setAmount(sourceAmount.multiply(amountUnit).longValue());
        detail.setCurrency(lastRecord.getAfter().getCurrency());
        detail.setStatus(lastRecord.getAfter().getStatus());
        detail.setTradeTransferStatus(lastRecord.getAfter().getStatus());
        detail.setTradeTransactionType(TransactionType.PAY_OUT.name());
        detail.setTransferErrorMsg(lastRecord.getAfter().getTransfer_error_msg());
        if (lastRecord.getAfter().getSettle_vendor() != null) {
            detail.setSettleVendor(lastRecord.getAfter().getSettle_vendor());
        } else {
            detail.setSettleVendor(lastRecord.getAfter().getVendor_name());
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final long createTime =
                    sdf.parse(lastRecord.getAfter().getCreate_time()).getTime() / 1000;
            detail.setCreateTime(createTime);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        // life cycle
        detail.setLifeCycle(new ArrayList<>());
        for (PayoutBackEventLog eventLog : eventLogs) {
            detail.getLifeCycle().add(
                    TransactionLifeCycle.builder()
                            .uniqueId(detail.getUniqueId())
                            .amount(detail.getAmount())
                            .currency(detail.getCurrency())
                            .operation(eventLog.getOp())
                            .transactionStatus(eventLog.getAfter().getStatus())
                            .timestamp(eventLog.getSource().getTs_ms() / 1000)
                            .build()
            );
        }

        String fullName = PayoutEventLogUtil.getName(lastRecord);

        // beneficiary
        detail.setBeneficiary(
                PayoutBeneficiary.builder()
                        .accountName(fullName)
                        .targetAccountId(lastRecord.getAfter().getTarget_account_id())
                        .bankCode(lastRecord.getAfter().getBank_code())
                        .bankName(lastRecord.getAfter().getBank_name())
                        .branch(lastRecord.getAfter().getBranch_id())
                        .document(lastRecord.getAfter().getTarget_document_id())
                        .email(lastRecord.getAfter().getTarget_email())
                        .phone(lastRecord.getAfter().getTarget_phone())
                        .pixKey(lastRecord.getAfter().getPix_key())
                        .pixKeyType(lastRecord.getAfter().getPix_key_type())
                        .build()
        );

        // other
        detail.setOthers(PayoutOthers.builder()
                .transactionId(lastRecord.getAfter().getTransaction_id())
                .externalId(lastRecord.getAfter().getExternal_id())
                .referenceNumber(lastRecord.getAfter().getReference_number())
                .build());

        // pix credentials
        // just get data when after.status = SETTLED and after.payment_type = PIX
        if (ProductCode.PIX.name().equalsIgnoreCase(lastRecord.getAfter().getPayment_type()) &&
                PaymentStatusCode.SETTLED.name()
                        .equalsIgnoreCase(lastRecord.getAfter().getStatus())) {
            PixCredentials payoutPixCredentials = PixCredentials.builder()
                    .amount(detail.getAmount())
                    .name(fullName)
                    .documentId(lastRecord.getAfter().getTarget_document_id())
                    .pixId(lastRecord.getAfter().getPix_end_to_end_id())
                    .build();
            try {
                payoutPixCredentials.setDate(
                        DateUtil.formatGMTDate(lastRecord.getAfter().getFinal_status_time()));
            } catch (ParseException | NullPointerException e) {
                payoutPixCredentials.setDate(lastRecord.getAfter().getFinal_status_time());
            }
            detail.setPixCredentials(payoutPixCredentials);
        } else {
            detail.setPixCredentials(null);
        }

        // CEP credentials data
        final CepCredentials cepCredentials = new CepCredentials();
        cepCredentials.setVendorName(Objects.nonNull(lastRecord.getAfter().getSettle_vendor()) ?
                lastRecord.getAfter().getSettle_vendor() :
                lastRecord.getAfter().getVendor_name());
        cepCredentials.setBankCode(lastRecord.getAfter().getBank_code());
        cepCredentials.setBankId(lastRecord.getAfter().getBank_id());
        cepCredentials.setBankName(lastRecord.getAfter().getBank_name());
        cepCredentials.setAmount(lastRecord.getAfter().getAmount());
        cepCredentials.setStatus(lastRecord.getAfter().getStatus());
        try {
            cepCredentials.setFinalStatusTime(
                    DateUtil.formatGMTDate(lastRecord.getAfter().getFinal_status_time()));
        } catch (ParseException | NullPointerException e) {
            cepCredentials.setFinalStatusTime(lastRecord.getAfter().getFinal_status_time());
        }
        if (StringUtils.hasText(lastRecord.getAfter().getReference_number())) {
            cepCredentials.setReferenceNumber(lastRecord.getAfter().getReference_number());
        } else {
            // get reference_number from payment_info
            try {
                ObjectMapper mapper = new ObjectMapper();
                if (StringUtils.hasText(lastRecord.getAfter().getPayment_info())) {
                    JsonNode paymentInfo = mapper.readTree(lastRecord.getAfter().getPayment_info());
                    if (paymentInfo.hasNonNull("transferPaymentInfo")
                            &&
                            paymentInfo.get("transferPaymentInfo").hasNonNull("referenceNumber")) {
                        cepCredentials.setReferenceNumber(
                                paymentInfo.get("transferPaymentInfo").get("referenceNumber")
                                        .asText());
                    }
                }
            } catch (JsonProcessingException ignored) {
            }
        }
        cepCredentials.setTargetAccountId(lastRecord.getAfter().getTarget_account_id());
        detail.setCepCredentials(cepCredentials);
        return detail;
    }

    public static PayoutDetail fromPayoutEventLogList(final List<PayoutEventLog> eventLogs) {
        if (eventLogs.isEmpty()) {
            return null;
        }
        final PayoutDetail detail = new PayoutDetail();
        final PayoutEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);
        detail.setUniqueId(lastRecord.getAfter().getIdempotency_key());
        detail.setMerchantReference(lastRecord.getAfter().getIdempotency_key());
        detail.setAmount(lastRecord.getAfter().getAmount());
        detail.setCurrency(lastRecord.getAfter().getCurrency());
        detail.setStatus(lastRecord.getAfter().getStatus());
        detail.setTradeTransferStatus(lastRecord.getAfter().getStatus());
        detail.setTradeTransactionType(TransactionType.PAY_OUT.name());
        detail.setTransferErrorMsg(lastRecord.getAfter().getTransfer_error_msg());
        if (lastRecord.getAfter().getSettle_vendor() != null) {
            detail.setSettleVendor(lastRecord.getAfter().getSettle_vendor());
        } else {
            detail.setSettleVendor(lastRecord.getAfter().getVendor_name());
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final long createTime =
                    sdf.parse(lastRecord.getAfter().getCreate_time()).getTime() / 1000;
            detail.setCreateTime(createTime);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        // life cycle
        detail.setLifeCycle(new ArrayList<>());
        for (PayoutEventLog eventLog : eventLogs) {
            detail.getLifeCycle().add(
                    TransactionLifeCycle.builder()
                            .uniqueId(detail.getUniqueId())
                            .amount(detail.getAmount())
                            .currency(detail.getCurrency())
                            .operation(eventLog.getOp())
                            .transactionStatus(eventLog.getAfter().getStatus())
                            .timestamp(eventLog.getSource().getTs_ms() / 1000)
                            .build()
            );
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PayeeInfo payeeInfo = PayeeInfo.builder().build();
        PaymentInfo paymentInfo = PaymentInfo.builder()
                .pix(PixPaymentInfo.builder().build())
                .bankTransfer(BankTransferPaymentInfo.builder().build())
                .build();
        try {
            payeeInfo = mapper.readValue(
                    lastRecord.getAfter().getPayee_info(), PayeeInfo.class);
            paymentInfo = Optional.of(mapper.readValue(
                            lastRecord.getAfter().getPayment_info(), PaymentInfo.class))
                    .orElse(new PaymentInfo());
            if (paymentInfo.getBankTransfer() == null) {
                paymentInfo.setBankTransfer(BankTransferPaymentInfo.builder().build());
            } else if (paymentInfo.getPix() == null) {
                paymentInfo.setPix(PixPaymentInfo.builder().build());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String fullName = PayoutEventLogUtil.getName(paymentInfo,
                payeeInfo, lastRecord.getAfter().getResponse());

        // beneficiary
        detail.setBeneficiary(
                PayoutBeneficiary.builder()
                        .accountName(fullName)
                        .targetAccountId(paymentInfo.getBankTransfer().getTargetBankAccountId())
                        .bankCode(paymentInfo.getBankTransfer().getTargetBankCode())
                        .bankName(paymentInfo.getBankTransfer().getTargetBankName())
                        .branch(paymentInfo.getBankTransfer().getTargetBankAgency())
                        .document(payeeInfo.getTargetDocumentId())
                        .email(payeeInfo.getTargetEmail())
                        .phone(payeeInfo.getTargetPhone())
                        .pixKey(paymentInfo.getPix().getPixKey())
                        .pixKeyType(paymentInfo.getPix().getPixKeyType())
                        .build()
        );

        // other
        detail.setOthers(PayoutOthers.builder()
                .transactionId(lastRecord.getAfter().getTransaction_id())
                .externalId(lastRecord.getAfter().getExternal_id())
                .referenceNumber(lastRecord.getAfter().getReference_number())
                .build());

        // pix credentials
        // just get data when after.status = SETTLED and after.payment_type = PIX
        if (ProductCode.PIX.name().equalsIgnoreCase(lastRecord.getAfter().getPayment_type()) &&
                PaymentStatusCode.SETTLED.name()
                        .equalsIgnoreCase(lastRecord.getAfter().getStatus())) {
            PixCredentials payoutPixCredentials = PixCredentials.builder()
                    .amount(detail.getAmount())
                    .name(fullName)
                    .documentId(payeeInfo.getTargetDocumentId())
                    .pixId(paymentInfo.getPix().getPixEndToEndId())
                    .build();
            try {
                payoutPixCredentials.setDate(
                        DateUtil.formatUTCDate(lastRecord.getAfter().getFinal_status_time()));
            } catch (ParseException | NullPointerException e) {
                payoutPixCredentials.setDate(lastRecord.getAfter().getFinal_status_time());
            }
            detail.setPixCredentials(payoutPixCredentials);
        } else {
            detail.setPixCredentials(null);
        }

        // CEP credentials data
        final CepCredentials cepCredentials = new CepCredentials();
        cepCredentials.setVendorName(Objects.nonNull(lastRecord.getAfter().getSettle_vendor()) ?
                lastRecord.getAfter().getSettle_vendor() :
                lastRecord.getAfter().getVendor_name());
        cepCredentials.setBankCode(paymentInfo.getBankTransfer().getTargetBankCode());
        cepCredentials.setBankId(paymentInfo.getBankTransfer().getTargetBankId());
        cepCredentials.setBankName(paymentInfo.getBankTransfer().getTargetBankName());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        cepCredentials.setAmount(
                decimalFormat.format(lastRecord.getAfter().getAmount().doubleValue() / 100));
        cepCredentials.setStatus(lastRecord.getAfter().getStatus());
        try {
            cepCredentials.setFinalStatusTime(
                    DateUtil.formatUTCDate(lastRecord.getAfter().getFinal_status_time()));
        } catch (ParseException | NullPointerException e) {
            cepCredentials.setFinalStatusTime(lastRecord.getAfter().getFinal_status_time());
        }
        if (StringUtils.hasText(lastRecord.getAfter().getReference_number())) {
            cepCredentials.setReferenceNumber(lastRecord.getAfter().getReference_number());
        } else {
            // get reference_number from payment_info
            cepCredentials.setReferenceNumber(
                    paymentInfo.getBankTransfer().getReferenceNumber());
        }
        cepCredentials.setTargetAccountId(paymentInfo.getBankTransfer().getTargetBankAccountId());
        detail.setCepCredentials(cepCredentials);
        return detail;
    }


    public static PayinPixDetail payinEventLogsToPixDetail(List<PayinEventLog> eventLogs) {
        final PayinPixDetail detail = new PayinPixDetail();
        if (eventLogs.isEmpty()) {
            return null;
        }
        final PayinEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);
        detail.setUniqueId(lastRecord.getAfter().getIdempotency_key());
        detail.setMerchantReference(lastRecord.getAfter().getIdempotency_key());
        detail.setAmount(Long.parseLong(lastRecord.getAfter().getAmount()));
        detail.setCurrency(lastRecord.getAfter().getCurrency());
        detail.setStatus(lastRecord.getAfter().getTransfer_status());
        detail.setTradeTransferStatus(lastRecord.getAfter().getTransfer_status());
        detail.setTradeTransactionType(lastRecord.getAfter().getTransaction_type());
        detail.setTransferErrorMsg(lastRecord.getAfter().getTransfer_error_msg());
        if (lastRecord.getAfter().getSettle_vendor() != null) {
            detail.setSettleVendor(lastRecord.getAfter().getSettle_vendor());
        } else {
            detail.setSettleVendor(lastRecord.getAfter().getVendor());
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final long createTime =
                    sdf.parse(lastRecord.getAfter().getCreate_time()).getTime() / 1000;
            detail.setCreateTime(createTime);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        // life cycle
        detail.setLifeCycle(new ArrayList<>());
        for (PayinEventLog eventLog : eventLogs) {
            detail.getLifeCycle().add(
                    TransactionLifeCycle.builder()
                            .uniqueId(detail.getUniqueId())
                            .amount(detail.getAmount())
                            .currency(detail.getCurrency())
                            .operation(eventLog.getOp())
                            .transactionStatus(eventLog.getAfter().getTransfer_status())
                            .timestamp(eventLog.getSource().getTs_ms() / 1000)
                            .build()
            );
        }

        // payer
        detail.setPayer(new PayerInfo());
        detail.getPayer().setReference(lastRecord.getAfter().getReference_id());
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (StringUtils.hasText(lastRecord.getAfter().getPayer())) {
                JsonNode payerNode = mapper.readTree(lastRecord.getAfter().getPayer());
                if (payerNode.hasNonNull("name")) {
                    detail.getPayer().setName(payerNode.get("name").asText());
                }
                if (payerNode.hasNonNull("phone")) {
                    detail.getPayer().setPhone(payerNode.get("phone").asText());
                }
                if (payerNode.hasNonNull("email")) {
                    detail.getPayer().setEmail(payerNode.get("email").asText());
                }
                if (payerNode.hasNonNull("document")) {
                    if (payerNode.get("document").isValueNode()) {
                        detail.getPayer().setDocumentId(payerNode.get("document").asText());
                    } else if (payerNode.get("document").hasNonNull("documentId") &&
                            payerNode.get("document").hasNonNull("type")
                    ) {
                        detail.getPayer().setDocumentId(
                                payerNode.get("document").get("documentId").asText());
                        detail.getPayer()
                                .setDocumentType(payerNode.get("document").get("type").asText());
                    }
                }
            }
            if (StringUtils.hasText(lastRecord.getAfter().getRisk_data())) {
                JsonNode riskDataNode = mapper.readTree(lastRecord.getAfter().getRisk_data());
                if (riskDataNode.hasNonNull("ipAddress")) {
                    detail.getPayer().setIp(riskDataNode.get("ipAddress").asText());
                }
            }
            if (StringUtils.hasText(lastRecord.getAfter().getOrder_info())) {
                JsonNode orderInfo = mapper.readTree(lastRecord.getAfter().getOrder_info());
                if (orderInfo.hasNonNull("orderId")) {
                    detail.getPayer().setOrderId(orderInfo.get("orderId").asText());
                }
            }
        } catch (JsonProcessingException ignored) {
        }

        // pixCredentials
        // just get data when after.transfer_status = SETTLED and payment_method = PIX_DYNAMIC_QR or PIX_STATIC_QR
        if (PaymentMethodCode.PIX_DYNAMIC_QR.name()
                .equalsIgnoreCase(lastRecord.getAfter().getPayment_method()) ||
                PaymentMethodCode.PIX_STATIC_QR.name()
                        .equalsIgnoreCase(lastRecord.getAfter().getPayment_method()) &&
                        PaymentStatusCode.SETTLED.name()
                                .equalsIgnoreCase(lastRecord.getAfter().getTransfer_status())) {
            PixCredentials payinPixCredentials = PixCredentials.builder()
                    .amount(detail.getAmount())
                    .name(detail.getPayer().getName())
                    .documentId(detail.getPayer().getDocumentId())
                    .description(lastRecord.getAfter().getPayer_comment())
                    .build();
            try {
                ObjectMapper mapper = new ObjectMapper();
                if (StringUtils.hasText(lastRecord.getAfter().getPayment_info())) {
                    JsonNode paymentInfoNode =
                            mapper.readTree(lastRecord.getAfter().getPayment_info());
                    if (paymentInfoNode.hasNonNull("pixPaymentInfo")) {
                        if (paymentInfoNode.get("pixPaymentInfo").isValueNode()) {
                            payinPixCredentials.setPixId(
                                    paymentInfoNode.get("pixPaymentInfo").asText());
                        } else if (paymentInfoNode.get("pixPaymentInfo")
                                .hasNonNull("pixEndToEndId")) {
                            payinPixCredentials.setPixId(
                                    paymentInfoNode.get("pixPaymentInfo").get("pixEndToEndId")
                                            .asText());
                        }
                    }
                }
                payinPixCredentials.setDate(
                        DateUtil.formatUTCDate(lastRecord.getAfter().getFinal_status_time()));
            } catch (JsonProcessingException ignored) {
            } catch (ParseException | NullPointerException parseException) {
                payinPixCredentials.setDate(lastRecord.getAfter().getFinal_status_time());
            }
            detail.setPixCredentials(payinPixCredentials);
        } else {
            detail.setPixCredentials(null);
        }

        // refundAccountInfo
        detail.setRefundAccountInfo(PayinEventLogUtil.getRefundAccountInfo(lastRecord));

        return detail;
    }

    public static PayinCardDetail payinEventLogsToCardDetail(List<PayinEventLog> eventLogs) {
        final PayinCardDetail detail = new PayinCardDetail();
        if (eventLogs.isEmpty()) {
            return null;
        }
        final PayinEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);
        detail.setUniqueId(lastRecord.getAfter().getIdempotency_key());
        detail.setMerchantReference(lastRecord.getAfter().getIdempotency_key());
        detail.setAmount(Long.parseLong(lastRecord.getAfter().getAmount()));
        detail.setCurrency(lastRecord.getAfter().getCurrency());
        detail.setStatus(lastRecord.getAfter().getTransfer_status());
        detail.setTradeTransferStatus(lastRecord.getAfter().getTransfer_status());
        detail.setTradeTransactionType(lastRecord.getAfter().getTransaction_type());
        detail.setTransferErrorMsg(lastRecord.getAfter().getTransfer_error_msg());
        if (lastRecord.getAfter().getSettle_vendor() != null) {
            detail.setSettleVendor(lastRecord.getAfter().getSettle_vendor());
        } else {
            detail.setSettleVendor(lastRecord.getAfter().getVendor());
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final long createTime =
                    sdf.parse(lastRecord.getAfter().getCreate_time()).getTime() / 1000;
            detail.setCreateTime(createTime);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        // life cycle
        detail.setLifeCycle(new ArrayList<>());
        for (PayinEventLog eventLog : eventLogs) {
            detail.getLifeCycle().add(
                    TransactionLifeCycle.builder()
                            .uniqueId(detail.getUniqueId())
                            .amount(detail.getAmount())
                            .currency(detail.getCurrency())
                            .operation(eventLog.getOp())
                            .transactionStatus(eventLog.getAfter().getTransfer_status())
                            .timestamp(eventLog.getSource().getTs_ms() / 1000)
                            .build()
            );
        }

        ObjectMapper mapper = new ObjectMapper();
        // payer
        detail.setPayer(new PayerInfo());
        detail.getPayer().setReference(lastRecord.getAfter().getReference_id());
        detail.getPayer().setDescription(lastRecord.getAfter().getDescription());
        try {
            if (StringUtils.hasText(lastRecord.getAfter().getPayer())) {
                JsonNode payerNode = mapper.readTree(lastRecord.getAfter().getPayer());
                if (payerNode.hasNonNull("name")) {
                    detail.getPayer().setName(payerNode.get("name").asText());
                }
                if (payerNode.hasNonNull("phone")) {
                    detail.getPayer().setPhone(payerNode.get("phone").asText());
                }
                if (payerNode.hasNonNull("email")) {
                    detail.getPayer().setEmail(payerNode.get("email").asText());
                }
                if (payerNode.hasNonNull("document")) {
                    if (payerNode.get("document").isValueNode()) {
                        detail.getPayer().setDocumentId(payerNode.get("document").asText());
                    } else if (payerNode.get("document").hasNonNull("documentId") &&
                            payerNode.get("document").hasNonNull("type")
                    ) {
                        detail.getPayer().setDocumentId(
                                payerNode.get("document").get("documentId").asText());
                        detail.getPayer()
                                .setDocumentType(payerNode.get("document").get("type").asText());
                    }
                }
            }
            if (StringUtils.hasText(lastRecord.getAfter().getRisk_data())) {
                JsonNode riskDataNode = mapper.readTree(lastRecord.getAfter().getRisk_data());
                if (riskDataNode.hasNonNull("ipAddress")) {
                    detail.getPayer().setIp(riskDataNode.get("ipAddress").asText());
                }
            }
            if (StringUtils.hasText(lastRecord.getAfter().getOrder_info())) {
                JsonNode orderInfo = mapper.readTree(lastRecord.getAfter().getOrder_info());
                if (orderInfo.hasNonNull("orderId")) {
                    detail.getPayer().setOrderId(orderInfo.get("orderId").asText());
                }
            }
        } catch (JsonProcessingException ignored) {
        }

        // card info
        detail.setCard(new CardInfo());
        try {
            detail.getCard().setPaymentMethod(lastRecord.getAfter().getPayment_method());
            if (StringUtils.hasText(lastRecord.getAfter().getPayment_info())) {
                JsonNode cardNode = mapper.readTree(lastRecord.getAfter().getPayment_info());
                if (cardNode.hasNonNull("cardPaymentInfo") &&
                        cardNode.get("cardPaymentInfo").hasNonNull("displayedCardInfo")
                ) {
                    JsonNode displayedCardInfo =
                            cardNode.get("cardPaymentInfo").get("displayedCardInfo");
                    detail.getCard().setBin(displayedCardInfo.get("bin").asText());
                    detail.getCard().setBrand(displayedCardInfo.get("brand").asText());
                    detail.getCard().setLast4(displayedCardInfo.get("last4").asText());
                    detail.getCard()
                            .setCardHolder(displayedCardInfo.get("cardHolderName").asText());
                    detail.getCard()
                            .setExpirationMonth(displayedCardInfo.get("expirationMonth").asText());
                    detail.getCard()
                            .setExpirationYear(displayedCardInfo.get("expirationYear").asText());
                }
                final JsonNode additionalCardInfo = Optional.of(cardNode)
                        .map(e -> e.get("cardPaymentInfo"))
                        .map(e -> e.get("additionalCardInfo"))
                        .orElse(null);
                if (Objects.nonNull(additionalCardInfo)) {
                    detail.getCard().setIssuerBank(Optional.of(additionalCardInfo)
                            .map(e -> e.get("issuerBank")).map(JsonNode::asText).orElse(""));
                    detail.getCard().setCountry(Optional.of(additionalCardInfo)
                            .map(e -> e.get("country")).map(JsonNode::asText).orElse(""));
                    detail.getCard().setAccountFundingSource(Optional.of(additionalCardInfo)
                            .map(e -> e.get("accountFundingSource")).map(JsonNode::asText)
                            .orElse(""));
                }
                final JsonNode cardPaymentInfo = Optional.of(cardNode)
                        .map(e -> e.get("cardPaymentInfo"))
                        .orElse(null);
                if (Objects.nonNull(cardPaymentInfo)) {
                    detail.getCard().setCardId(Optional.of(cardPaymentInfo)
                            .map(e -> e.get("cardId")).map(JsonNode::asText)
                            .map(CommonUtil::maskString)
                            .orElse(""));
                }
            }
        } catch (JsonProcessingException ignored) {
        }

        detail.setInformation(new ProcessingInfo());
        detail.getInformation().setCreationDate(detail.getCreateTime());
        detail.getInformation().setPaymentFlow(lastRecord.getAfter().getPayment_flow());
        detail.getInformation().setTransferErrorMsg(lastRecord.getAfter().getTransfer_error_msg());
        detail.getInformation()
                .setTransferStatusCode(lastRecord.getAfter().getTransfer_status_code());
        try {
            if (StringUtils.hasText(lastRecord.getAfter().getPayment_info())) {
                final JsonNode paymentInfo =
                        mapper.readTree(lastRecord.getAfter().getPayment_info());
                detail.getInformation().setUse3ds(Optional.ofNullable(paymentInfo)
                        .map(e -> e.get("cardPaymentInfo")).map(e -> e.get("card3dsInfo"))
                        .map(e -> e.get("use3ds")).map(JsonNode::asBoolean).orElse(null));
                detail.getInformation().setInstallments(Optional.ofNullable(paymentInfo)
                        .map(e -> e.get("cardPaymentInfo"))
                        .map(e -> e.get("installments")).map(JsonNode::asInt).orElse(null));
            }
        } catch (Exception ignored) {
        }


        return detail;
    }

    public static PayCashDetail payinEventLogsToPayCashDetail(List<PayinEventLog> eventLogs) {
        final PayCashDetail detail = new PayCashDetail();
        if (eventLogs.isEmpty()) {
            return null;
        }
        final PayinEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);
        detail.setUniqueId(lastRecord.getAfter().getIdempotency_key());
        detail.setMerchantReference(lastRecord.getAfter().getIdempotency_key());
        detail.setAmount(Long.parseLong(lastRecord.getAfter().getAmount()));
        detail.setCurrency(lastRecord.getAfter().getCurrency());
        detail.setStatus(lastRecord.getAfter().getTransfer_status());
        detail.setTradeTransferStatus(lastRecord.getAfter().getTransfer_status());
        detail.setTradeTransactionType(lastRecord.getAfter().getTransaction_type());
        detail.setTransferErrorMsg(lastRecord.getAfter().getTransfer_error_msg());
        if (lastRecord.getAfter().getSettle_vendor() != null) {
            detail.setSettleVendor(lastRecord.getAfter().getSettle_vendor());
        } else {
            detail.setSettleVendor(lastRecord.getAfter().getVendor());
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final long createTime =
                    sdf.parse(lastRecord.getAfter().getCreate_time()).getTime() / 1000;
            detail.setCreateTime(createTime);
        } catch (ParseException ignored) {
        }

        // life cycle
        detail.setLifeCycle(new ArrayList<>());
        for (PayinEventLog eventLog : eventLogs) {
            detail.getLifeCycle().add(
                    TransactionLifeCycle.builder()
                            .uniqueId(detail.getUniqueId())
                            .amount(detail.getAmount())
                            .currency(detail.getCurrency())
                            .operation(eventLog.getOp())
                            .transactionStatus(eventLog.getAfter().getTransfer_status())
                            .timestamp(eventLog.getSource().getTs_ms() / 1000)
                            .build()
            );
        }

        ObjectMapper mapper = new ObjectMapper();
        // payer
        detail.setPayer(new PayerInfo());
        detail.getPayer().setReference(lastRecord.getAfter().getReference_id());
        try {
            if (StringUtils.hasText(lastRecord.getAfter().getPayer())) {
                JsonNode payerNode = mapper.readTree(lastRecord.getAfter().getPayer());
                if (payerNode.hasNonNull("name")) {
                    detail.getPayer().setName(payerNode.get("name").asText());
                }
                if (payerNode.hasNonNull("phone")) {
                    detail.getPayer().setPhone(payerNode.get("phone").asText());
                }
                if (payerNode.hasNonNull("email")) {
                    detail.getPayer().setEmail(payerNode.get("email").asText());
                }
                if (payerNode.hasNonNull("document")) {
                    if (payerNode.get("document").isValueNode()) {
                        detail.getPayer().setDocumentId(payerNode.get("document").asText());
                    } else if (payerNode.get("document").hasNonNull("documentId") &&
                            payerNode.get("document").hasNonNull("type")
                    ) {
                        detail.getPayer().setDocumentId(
                                payerNode.get("document").get("documentId").asText());
                        detail.getPayer()
                                .setDocumentType(payerNode.get("document").get("type").asText());
                    }
                }
            }
            if (StringUtils.hasText(lastRecord.getAfter().getRisk_data())) {
                JsonNode riskDataNode = mapper.readTree(lastRecord.getAfter().getRisk_data());
                if (riskDataNode.hasNonNull("ipAddress")) {
                    detail.getPayer().setIp(riskDataNode.get("ipAddress").asText());
                }
            }
        } catch (JsonProcessingException ignored) {
        }

        // Information
        detail.setInformation(Information.builder()
                .referenceCode(lastRecord.getAfter().getVendor_transaction_id())
                .build());

        return detail;
    }

    public static MarketPlaceDetail marketPlaceEventLogsToDetail(
            final List<MarketPlaceOrderEventLog> eventLogs) {
        final MarketPlaceDetail detail = new MarketPlaceDetail();
        if (eventLogs.isEmpty()) {
            return null;
        }

        final MarketPlaceOrderEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);
        detail.setUniqueId(lastRecord.getAfter().getIdempotency_key());
        detail.setMerchantReference(lastRecord.getAfter().getIdempotency_key());
        final BigDecimal amountUnit = new BigDecimal(100);
        BigDecimal sourceAmount;
        final String amount = lastRecord.getAfter().getAmount();
        if (StringUtils.hasLength(amount)) {
            sourceAmount = new BigDecimal(amount);
        } else {
            sourceAmount = new BigDecimal("0");
        }
        detail.setAmount(sourceAmount.multiply(amountUnit).longValue());
        detail.setCurrency(lastRecord.getAfter().getCurrency());
        detail.setStatus(lastRecord.getAfter().getOrder_status());
        detail.setTransactionType(TransactionType.MARKET_PLACE_ORDERS.name());
        detail.setTransferErrorMsg(lastRecord.getAfter().getOrder_error_msg());
        if (lastRecord.getAfter().getVendor_name() != null) {
            detail.setSettleVendor(lastRecord.getAfter().getVendor_name());
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final long createTime =
                    sdf.parse(lastRecord.getAfter().getCreate_time()).getTime() / 1000;
            detail.setCreateTime(createTime);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        // life cycle
        detail.setLifeCycle(new ArrayList<>());
        for (MarketPlaceOrderEventLog eventLog : eventLogs) {
            detail.getLifeCycle().add(
                    TransactionLifeCycle.builder()
                            .uniqueId(detail.getUniqueId())
                            .amount(detail.getAmount())
                            .currency(detail.getCurrency())
                            .operation(eventLog.getOp())
                            .transactionStatus(eventLog.getAfter().getOrder_status())
                            .timestamp(eventLog.getSource().getTs_ms() / 1000)
                            .build()
            );
        }

        Map<String, String> skuDetail = new LinkedHashMap<>();
        skuDetail.put("sku", lastRecord.getAfter().getSku());
        skuDetail.put("description", lastRecord.getAfter().getDescription());
        detail.setSkuDetail(skuDetail);

        return detail;
    }

    public static PayinBankTransferDetail payinEventLogsToTedDetail(List<PayinEventLog> eventLogs) {
        if (eventLogs.isEmpty()) {
            return null;
        }
        final PayinBankTransferDetail detail = new PayinBankTransferDetail();
        updateDefaultDetailData(detail, eventLogs);
        final PayinEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);

        // account
        ObjectMapper mapper = new ObjectMapper();
        Account account = new Account();
        account.setCreateTimestamp(
                DateUtil.UTCDateToTimestamp(lastRecord.getAfter().getCreate_time()) / 1000);
        try {
            if (StringUtils.hasText(lastRecord.getAfter().getPayment_info())) {
                JsonNode payerNode = mapper.readTree(lastRecord.getAfter().getPayment_info());
                if (payerNode.hasNonNull("bankTransferPaymentInfo") &&
                        payerNode.get("bankTransferPaymentInfo").hasNonNull("recipient")) {
                    JsonNode recipient = payerNode.get("bankTransferPaymentInfo").get("recipient");
                    if (recipient.hasNonNull("bankAccountNumber")) {
                        account.setAccountId(recipient.get("bankAccountNumber").asText());
                    }
                    if (recipient.hasNonNull("beneficiaryName")) {
                        account.setAccountName(recipient.get("beneficiaryName").asText());
                    }
                    if (recipient.hasNonNull("document") &&
                            recipient.get("document").hasNonNull("documentId")) {
                        account.setDocumentId(recipient.get("document").get("documentId").asText());
                    }
                }
            }
        } catch (JsonProcessingException ignored) {
        }
        detail.setAccount(account);

        return detail;
    }

    public static PayinBankTransferDetail payinEventLogsToSpeiDetail(
            List<PayinEventLog> eventLogs) {
        if (eventLogs.isEmpty()) {
            return null;
        }
        final PayinBankTransferDetail detail = new PayinBankTransferDetail();
        updateDefaultDetailData(detail, eventLogs);
        final PayinEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);

        // account
        ObjectMapper mapper = new ObjectMapper();
        Account account = new Account();
        account.setCreateTimestamp(
                DateUtil.UTCDateToTimestamp(lastRecord.getAfter().getCreate_time()) / 1000);
        try {
            if (StringUtils.hasText(lastRecord.getAfter().getPayment_info())) {
                JsonNode payerNode = mapper.readTree(lastRecord.getAfter().getPayment_info());
                if (payerNode.hasNonNull("bankTransferPaymentInfo")) {
                    if (payerNode.get("bankTransferPaymentInfo").hasNonNull("referenceNumber")) {
                        account.setReferenceNumber(
                                payerNode.get("bankTransferPaymentInfo").get("referenceNumber")
                                        .asText());
                    }
                    if (payerNode.get("bankTransferPaymentInfo").hasNonNull("recipient")) {
                        JsonNode recipient =
                                payerNode.get("bankTransferPaymentInfo").get("recipient");
                        if (recipient.hasNonNull("bankAccountNumber")) {
                            account.setAccountId(recipient.get("bankAccountNumber").asText());
                        }
                        if (recipient.hasNonNull("beneficiaryName")) {
                            account.setAccountName(recipient.get("beneficiaryName").asText());
                        }
                    }
                }
            }
        } catch (JsonProcessingException ignored) {
        }
        detail.setAccount(account);

        return detail;
    }

    public static PayinSpeiVaDetail payinEventLogsToSpeiVaDetail(List<PayinEventLog> eventLogs) {
        if (eventLogs.isEmpty()) {
            return null;
        }
        final PayinSpeiVaDetail detail = new PayinSpeiVaDetail();
        updateDefaultDetailData(detail, eventLogs);
        final PayinEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);

        // sub account
        detail.setSubAccount(new SubAccountInfo());
        detail.getSubAccount().setAccountId(
                JsonUtil.getJsonText(
                        JsonUtil.parseJson(
                                        lastRecord.getAfter().getPayment_info())
                                .get("clabeAccountPaymentInfo").get("arcusPaymentInfo"),
                        "beneficiaryAccountNumber"));

        // transfer detail
        detail.setTransferDetail(Optional.ofNullable(lastRecord.getAfter().getPayment_info())
                .map(JsonUtil::parseJson)
                .map(x -> x.get("clabeAccountPaymentInfo"))
                .map(x -> x.get("arcusPaymentInfo"))
                .map(x -> ClabeAccountTransferDetail.builder()
                        .paymentTime(Optional.ofNullable(x.get("paymentTime"))
                                .map(JsonNode::asText)
                                .map(v -> {
                                    try {
                                        return DateUtil.formatUTCDate(v);
                                    } catch (ParseException e) {
                                        log.error("paymentTime format error: " + e.getMessage());
                                        return null;
                                    }
                                })
                                .orElse(null))
                        .beneficiaryAccountNumber(
                                Optional.ofNullable(x.get("beneficiaryAccountNumber"))
                                        .map(JsonNode::asText).orElse(null))
                        .senderName(Optional.ofNullable(x.get("senderName"))
                                .map(JsonNode::asText).orElse(null))
                        .trackingId(Optional.ofNullable(x.get("trackingId"))
                                .map(JsonNode::asText).orElse(null))
                        .senderAccountNumber(Optional.ofNullable(x.get("senderAccountNumber"))
                                .map(JsonNode::asText).orElse(null))
                        .referenceId(lastRecord.getAfter().getReference_id())
                        .referenceNumber(Optional.ofNullable(x.get("referenceNumber"))
                                .map(JsonNode::asText).orElse(null))
                        .build())
                .orElse(null));

        return detail;
    }

    public static BankTransferDetail payinEventLogToBankTransferDetail(
            List<PayinEventLog> eventLogs) {
        if (eventLogs.isEmpty()) {
            return null;
        }
        final BankTransferDetail detail = new BankTransferDetail();
        updateDefaultDetailData(detail, eventLogs);
        detail.setInformation(new Information());
        final PayinEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (StringUtils.hasText(lastRecord.getAfter().getPayment_info())) {
                JsonNode payerNode = mapper.readTree(lastRecord.getAfter().getPayment_info());
                if (payerNode.hasNonNull("bankTransferPaymentInfo") &&
                        payerNode.get("bankTransferPaymentInfo").hasNonNull("referenceNumber")) {
                    detail.getInformation().setReferenceCode(
                            payerNode.get("bankTransferPaymentInfo").get("referenceNumber")
                                    .asText());
                }
            }
        } catch (JsonProcessingException ignored) {
        }
        return detail;
    }

    public static PayinSpeiVaDetail subAccountEventLogsToSpeiVaDetail(
            final List<SubAccountPayBackEventLog> eventLogs) {
        final PayinSpeiVaDetail detail = new PayinSpeiVaDetail();
        if (eventLogs.isEmpty()) {
            return null;
        }

        final SubAccountPayBackEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);
        detail.setUniqueId(lastRecord.getAfter().getTransaction_id());
        detail.setMerchantReference(lastRecord.getAfter().getTransaction_id());
        final BigDecimal amountUnit = new BigDecimal(100);
        final BigDecimal sourceAmount = new BigDecimal(lastRecord.getAfter().getAmount());
        detail.setAmount(sourceAmount.multiply(amountUnit).longValue());
        detail.setCurrency(lastRecord.getAfter().getCurrency());
        detail.setStatus(lastRecord.getAfter().getStatus());
        detail.setTradeTransferStatus(lastRecord.getAfter().getStatus());
        detail.setTradeTransactionType(TransactionType.PAY_IN.name());
        detail.setTransferErrorMsg(lastRecord.getAfter().getVendor_error_message());
        if (lastRecord.getAfter().getSub_account_id().startsWith("646")) {
            detail.setSettleVendor("UNIPAGOS");
        } else if (lastRecord.getAfter().getSub_account_id().startsWith("706")) {
            detail.setSettleVendor("ARCUS");
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final long createTime =
                    sdf.parse(lastRecord.getAfter().getCreate_time()).getTime() / 1000;
            detail.setCreateTime(createTime);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        // life cycle
        detail.setLifeCycle(new ArrayList<>());
        for (SubAccountPayBackEventLog eventLog : eventLogs) {
            detail.getLifeCycle().add(
                    TransactionLifeCycle.builder()
                            .uniqueId(detail.getUniqueId())
                            .amount(detail.getAmount())
                            .currency(detail.getCurrency())
                            .operation(eventLog.getOp())
                            .transactionStatus(eventLog.getAfter().getStatus())
                            .timestamp(eventLog.getSource().getTs_ms() / 1000)
                            .build()
            );
        }

        // sub-account
        detail.setSubAccount(new SubAccountInfo());
        detail.getSubAccount().setAccountId(lastRecord.getAfter().getSub_account_id());
        return detail;
    }

    public static PayinDefaultDetail payinEventLogsToDefaultDetail(List<PayinEventLog> eventLogs) {
        if (eventLogs.isEmpty()) {
            return null;
        }
        final PayinDefaultDetail detail = new PayinDefaultDetail();
        updateDefaultDetailData(detail, eventLogs);
        return detail;
    }

    private static void updateDefaultDetailData(PayinDefaultDetail detail,
                                                List<PayinEventLog> eventLogs) {
        final PayinEventLog lastRecord = eventLogs.get(eventLogs.size() - 1);
        detail.setUniqueId(lastRecord.getAfter().getIdempotency_key());
        detail.setMerchantReference(lastRecord.getAfter().getIdempotency_key());
        detail.setAmount(Long.parseLong(lastRecord.getAfter().getAmount()));
        detail.setCurrency(lastRecord.getAfter().getCurrency());
        detail.setStatus(lastRecord.getAfter().getTransfer_status());
        detail.setTradeTransferStatus(lastRecord.getAfter().getTransfer_status());
        detail.setTradeTransactionType(lastRecord.getAfter().getTransaction_type());
        detail.setTransferErrorMsg(lastRecord.getAfter().getTransfer_error_msg());
        if (lastRecord.getAfter().getSettle_vendor() != null) {
            detail.setSettleVendor(lastRecord.getAfter().getSettle_vendor());
        } else {
            detail.setSettleVendor(lastRecord.getAfter().getVendor());
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final long createTime =
                    sdf.parse(lastRecord.getAfter().getCreate_time()).getTime() / 1000;
            detail.setCreateTime(createTime);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        // life cycle
        detail.setLifeCycle(new ArrayList<>());
        for (PayinEventLog eventLog : eventLogs) {
            detail.getLifeCycle().add(
                    TransactionLifeCycle.builder()
                            .uniqueId(detail.getUniqueId())
                            .amount(detail.getAmount())
                            .currency(detail.getCurrency())
                            .operation(eventLog.getOp())
                            .transactionStatus(eventLog.getAfter().getTransfer_status())
                            .timestamp(eventLog.getSource().getTs_ms() / 1000)
                            .build()
            );
        }
    }

}

