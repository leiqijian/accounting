package com.liquido.aqueducts.util;

import java.util.HashMap;
import java.util.Map;

import com.liquido.aqueducts.commons.enums.CountryCode;
import com.liquido.aqueducts.commons.enums.PaymentMethodCode;
import com.liquido.aqueducts.commons.enums.ProductCode;
import com.liquido.aqueducts.vo.document.eventlog.PayinEventLog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class PayinEventLogUtil {

    public static Map<String, Object> getRefundAccountInfo(final PayinEventLog payinEventLog) {
        Map<String, Object> refundAccountInfo = null;
        try {
            if (StringUtils.hasText(payinEventLog.getAfter().getRefund_additional_info())) {
                ObjectMapper mapper = new ObjectMapper();
                refundAccountInfo = new HashMap<>();
                JsonNode refundAdditionalInfo =
                        mapper.readTree(payinEventLog.getAfter().getRefund_additional_info());
                if (refundAdditionalInfo.hasNonNull("manualRefund")) {
                    refundAccountInfo.put("manualRefund",
                            refundAdditionalInfo.get("manualRefund").asBoolean());
                }
                if (refundAdditionalInfo.hasNonNull("bankTransferAccountInfo")) {
                    final JsonNode bankTransferAccountInfo =
                            refundAdditionalInfo.get("bankTransferAccountInfo");
                    refundAccountInfo.put("bankAccountType",
                            bankTransferAccountInfo.get("bankAccountType").asText());
                    refundAccountInfo.put("bankAccountNumber",
                            bankTransferAccountInfo.get("bankAccountNumber").asText());
                    refundAccountInfo.put("bankCode",
                            bankTransferAccountInfo.get("bankCode").asText());
                    refundAccountInfo.put("bankBranchId",
                            bankTransferAccountInfo.get("bankBranchId").asText());
                    refundAccountInfo.put("beneficiaryName",
                            bankTransferAccountInfo.get("beneficiaryName").asText());
                    if (bankTransferAccountInfo.hasNonNull("document")) {
                        final JsonNode document = bankTransferAccountInfo.get("document");
                        refundAccountInfo.put("documentId", document.get("documentId").asText());
                        refundAccountInfo.put("type", document.get("type").asText());
                    }
                }
            }
        } catch (Exception e) {
            log.error("There was an error in the refundAccountInfo, ", e);
        }
        return refundAccountInfo;
    }

    public static String paymentMethodToProductCode(String paymentMethod, String country) {
        if (PaymentMethodCode.CREDIT_CARD.name().equals(paymentMethod) ||
                PaymentMethodCode.DEBIT_CARD.name().equals(paymentMethod)) {
            return ProductCode.CARD.name();
        } else if (PaymentMethodCode.PIX_STATIC_QR.name().equals(paymentMethod) ||
                PaymentMethodCode.PIX_DYNAMIC_QR.name().equals(paymentMethod)) {
            return ProductCode.PIX.name();
        } else if (PaymentMethodCode.BOLETO.name().equals(paymentMethod)) {
            return ProductCode.BOLETO.name();
        } else if (PaymentMethodCode.BANK_TRANSFER.name().equals(paymentMethod)) {
            if (CountryCode.MX.name().equals(country)) {
                // MX & virgo -> SPEI_BANK_TRANSFER
                return ProductCode.SPEI_BANK_TRANSFER.name();
            } else if (CountryCode.BR.name().equals(country)) {
                // BR & virgo -> TED
                return ProductCode.TED.name();
            }
        } else if (PaymentMethodCode.CLABE_ACCOUNT.name().equals(paymentMethod)) {
            return ProductCode.SPEI_VA.name();
        }
        return paymentMethod;
    }

}
