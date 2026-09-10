package com.liquido.base.enums;

import java.util.List;
import java.util.Objects;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.liquido.base.constant.dynamic.DynamicConstant;
import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

@Getter
@Slf4j
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class TransferErrorResultCodeEnum extends DynamicConstant<String> {

    private static final long serialVersionUID = 1L;

    private final Integer errorCode;
    private final String description;
    private final String type;
    public static final TransferErrorResultCodeEnum UNKNOWN =
            new TransferErrorResultCodeEnum("UNKNOWN", 99999, "Unknown", "Unknown");

    public static final TransferErrorResultCodeEnum INTERNAL_ERROR =
            new TransferErrorResultCodeEnum("INTERNAL_ERROR", 500, "Server internal error",
                    "OTHER");
    public static final TransferErrorResultCodeEnum INVALID_REQUEST =
            new TransferErrorResultCodeEnum("INVALID_REQUEST", 400, "Invalid request", "OTHER");
    public static final TransferErrorResultCodeEnum CREDENTIALS_LOCKED =
            new TransferErrorResultCodeEnum("CREDENTIALS_LOCKED", 402,
                    "The client account is locked.", "OTHER");
    public static final TransferErrorResultCodeEnum AUTHENTICATION_FAILED =
            new TransferErrorResultCodeEnum("AUTHENTICATION_FAILED", 403,
                    "The client failed to authorize with vendor", "OTHER");
    public static final TransferErrorResultCodeEnum NOT_FOUND =
            new TransferErrorResultCodeEnum("NOT_FOUND", 404,
                    "The requested resource is not found.", "OTHER");
    public static final TransferErrorResultCodeEnum BANK_NAME_NOT_FOUND =
            new TransferErrorResultCodeEnum("BANK_NAME_NOT_FOUND", 423,
                    "The target bank name was not found.", "OTHER");
    public static final TransferErrorResultCodeEnum BANK_CODE_NOT_FOUND =
            new TransferErrorResultCodeEnum("BANK_CODE_NOT_FOUND", 424,
                    "The target bank code was not found.", "OTHER");
    public static final TransferErrorResultCodeEnum BANK_BRANCH_NOT_FOUND =
            new TransferErrorResultCodeEnum("BANK_BRANCH_NOT_FOUND", 425,
                    "The target bank branch was not found.", "OTHER");
    public static final TransferErrorResultCodeEnum BENEFICIARY_DOCUMENT_INVALID =
            new TransferErrorResultCodeEnum("BENEFICIARY_DOCUMENT_INVALID", 426,
                    "The target beneficiary document is invalid", "OTHER");
    public static final TransferErrorResultCodeEnum BENEFICIARY_NAME_MISMATCH =
            new TransferErrorResultCodeEnum("BENEFICIARY_NAME_MISMATCH", 427,
                    "The target beneficiary name doesn't match the target account holder's name.",
                    "OTHER");
    public static final TransferErrorResultCodeEnum ACCOUNT_NOT_FOUND =
            new TransferErrorResultCodeEnum("ACCOUNT_NOT_FOUND", 428,
                    "The target account was not found.", "OTHER");
    public static final TransferErrorResultCodeEnum ACCOUNT_INVALID =
            new TransferErrorResultCodeEnum("ACCOUNT_INVALID", 429,
                    "The target account is invalid, mostly due to wrong format.", "OTHER");
    public static final TransferErrorResultCodeEnum ACCOUNT_LOCKED =
            new TransferErrorResultCodeEnum(" ACCOUNT_LOCKED", 430,
                    "The target account is locked, couldn't be used now.", "OTHER");
    public static final TransferErrorResultCodeEnum PIX_KEY_INVALID =
            new TransferErrorResultCodeEnum("PIX_KEY_INVALID", 431,
                    "The PIX Key provided is invalid.", "OTHER");
    public static final TransferErrorResultCodeEnum PAYER_DOCUMENT_INVALID =
            new TransferErrorResultCodeEnum("PAYER_DOCUMENT_INVALID", 432,
                    "The payer document is invalid.", "OTHER");
    public static final TransferErrorResultCodeEnum PAYMENT_REQUEST_NOT_FOUND =
            new TransferErrorResultCodeEnum("PAYMENT_REQUEST_NOT_FOUND", 441,
                    "The transaction Id is invalid.", "OTHER");
    public static final TransferErrorResultCodeEnum TRANSACTION_ALREADY_EXISTS =
            new TransferErrorResultCodeEnum("TRANSACTION_ALREADY_EXISTS", 442,
                    "The idempotency key provided is used before.", "OTHER");
    public static final TransferErrorResultCodeEnum TRANSACTION_PENDING =
            new TransferErrorResultCodeEnum("TRANSACTION_PENDING", 443,
                    "This transaction is pending for manually review.", "OTHER");
    public static final TransferErrorResultCodeEnum RESOURCE_DUPLICATED =
            new TransferErrorResultCodeEnum("RESOURCE_DUPLICATED", 444,
                    "The resource already exists.", "OTHER");
    public static final TransferErrorResultCodeEnum INVALID_PHONE_NUMBER =
            new TransferErrorResultCodeEnum("INVALID_PHONE_NUMBER", 445,
                    "The phone number is invalid.", "OTHER");
    public static final TransferErrorResultCodeEnum INVALID_CARD_NUMBER =
            new TransferErrorResultCodeEnum("INVALID_CARD_NUMBER", 446,
                    "The card number is invalid.", "OTHER");
    public static final TransferErrorResultCodeEnum INVALID_DOCUMENT =
            new TransferErrorResultCodeEnum("INVALID_DOCUMENT", 447, "The document is invalid.",
                    "OTHER");
    public static final TransferErrorResultCodeEnum INVALID_USER_ID =
            new TransferErrorResultCodeEnum("INVALID_USER_ID", 448, "The user id is invalid.",
                    "OTHER");
    public static final TransferErrorResultCodeEnum MISSING_USER_INFO =
            new TransferErrorResultCodeEnum("MISSING_USER_INFO", 449,
                    "The user information is missing.", "OTHER");
    public static final TransferErrorResultCodeEnum INSUFFICIENT_FUNDS =
            new TransferErrorResultCodeEnum("INSUFFICIENT_FUNDS", 450,
                    "The source account is out of balance.", "OTHER");
    public static final TransferErrorResultCodeEnum EACH_PAYMENT_LIMIT_EXCEEDED =
            new TransferErrorResultCodeEnum("EACH_PAYMENT_LIMIT_EXCEEDED", 451,
                    "The payment amount has reached the payment amount limit.", "OTHER");
    public static final TransferErrorResultCodeEnum DAILY_PAYMENT_LIMIT_EXCEEDED =
            new TransferErrorResultCodeEnum("DAILY_PAYMENT_LIMIT_EXCEEDED", 452,
                    "The total daily payment amount has reached the daily payment limit.", "OTHER");
    public static final TransferErrorResultCodeEnum MONTHLY_PAYMENT_LIMIT_EXCEEDED =
            new TransferErrorResultCodeEnum("MONTHLY_PAYMENT_LIMIT_EXCEEDED", 453,
                    "The total monthly payment amount has reached the monthly payment limit.",
                    "OTHER");
    public static final TransferErrorResultCodeEnum YEARLY_PAYMENT_LIMIT_EXCEEDED =
            new TransferErrorResultCodeEnum("YEARLY_PAYMENT_LIMIT_EXCEEDED", 454,
                    "The total yearly payment amount has reached the yearly payment limit.",
                    "OTHER");
    public static final TransferErrorResultCodeEnum AMOUNT_TOO_SMALL =
            new TransferErrorResultCodeEnum("AMOUNT_TOO_SMALL", 455,
                    "The payment amount is too small to be accepted.", "OTHER");
    public static final TransferErrorResultCodeEnum AMOUNT_LIMIT_EXCEEDED =
            new TransferErrorResultCodeEnum("AMOUNT_LIMIT_EXCEEDED", 456,
                    "The payment amount exceeds payee's transaction limit", "OTHER");
    public static final TransferErrorResultCodeEnum GATEWAY_TIMEOUT =
            new TransferErrorResultCodeEnum("GATEWAY_TIMEOUT", 460,
                    "The payment gateway connection time out.", "OTHER");
    public static final TransferErrorResultCodeEnum GATEWAY_ERROR =
            new TransferErrorResultCodeEnum("GATEWAY_ERROR", 461,
                    "The payment gateway had a problem.", "OTHER");
    public static final TransferErrorResultCodeEnum PAYMENT_OPERATION_TIMEOUT =
            new TransferErrorResultCodeEnum("PAYMENT_OPERATION_TIMEOUT", 462,
                    "The payment operation had a time out issue.", "OTHER");
    public static final TransferErrorResultCodeEnum PAYMENT_OPERATION_CANCELLED =
            new TransferErrorResultCodeEnum("PAYMENT_OPERATION_CANCELLED", 463,
                    "The payment transaction was cancelled.", "OTHER");
    public static final TransferErrorResultCodeEnum PAYMENT_REJECTED_BY_BANK =
            new TransferErrorResultCodeEnum("PAYMENT_REJECTED_BY_BANK", 464,
                    "The payment transaction was rejected by bank.", "OTHER");
    public static final TransferErrorResultCodeEnum ACCOUNTING_BILL_DAY_INVALID =
            new TransferErrorResultCodeEnum("ACCOUNTING_BILL_DAY_INVALID", 470,
                    "The date is invalid.", "OTHER");
    public static final TransferErrorResultCodeEnum ACCOUNTING_BILL_NOT_COME_OUT =
            new TransferErrorResultCodeEnum("ACCOUNTING_BILL_NOT_COME_OUT", 471,
                    "The bill according to date has not come out, please retry later.", "OTHER");
    public static final TransferErrorResultCodeEnum BALANCE_UNAVAILABLE =
            new TransferErrorResultCodeEnum("BALANCE_UNAVAILABLE", 472,
                    "balance is unavailable now, please retry later.", "OTHER");
    public static final TransferErrorResultCodeEnum INVALID_SKU =
            new TransferErrorResultCodeEnum("INVALID_SKU", 480,
                    "The SKU provided is invalid and was not found in the system", "OTHER");
    public static final TransferErrorResultCodeEnum INVALID_TOPUP_PHONE_NUMBER =
            new TransferErrorResultCodeEnum("INVALID_TOPUP_PHONE_NUMBER", 481,
                    "Carrier does not support the phone number", "OTHER");
    public static final TransferErrorResultCodeEnum INVALID_UTILITY_REFERENCE_NUMBER =
            new TransferErrorResultCodeEnum("INVALID_UTILITY_REFERENCE_NUMBER", 482,
                    "Carrier does not support the reference number", "OTHER");
    public static final TransferErrorResultCodeEnum INVALID_AMOUNT =
            new TransferErrorResultCodeEnum("INVALID_AMOUNT", 483,
                    "Payment amount is not valid or does not match the corresponding "
                            + "reference number", "OTHER");
    public static final TransferErrorResultCodeEnum INVALID_WHATSAPP_ID =
            new TransferErrorResultCodeEnum("INVALID_WHATSAPP_ID", 601,
                    "This phone number does not have an associated whatsapp account.", "OTHER");
    public static final TransferErrorResultCodeEnum USER_OPTED_OUT =
            new TransferErrorResultCodeEnum("USER_OPTED_OUT", 602,
                    "This user has opted out of our messaging service.", "OTHER");
    public static final TransferErrorResultCodeEnum SERVICE_DISABLED =
            new TransferErrorResultCodeEnum("SERVICE_DISABLED", 603,
                    "You don't have permission to use this feature, "
                            + "please contact your account manager or Liquido's support team.",
                    "OTHER");
    public static final TransferErrorResultCodeEnum MESSAGE_LIMIT_EXCEEDED =
            new TransferErrorResultCodeEnum("MESSAGE_LIMIT_EXCEEDED", 650, "Message limit reached.",
                    "OTHER");
    public static final TransferErrorResultCodeEnum REQUEST_TOO_FREQUENT =
            new TransferErrorResultCodeEnum("REQUEST_TOO_FREQUENT", 651,
                    "Processing message request with same user, please try later", "OTHER");
    public static final TransferErrorResultCodeEnum VENDOR_REJECT_NEED_FAILOVER =
            new TransferErrorResultCodeEnum("VENDOR_REJECT_NEED_FAILOVER", 1001,
                    "An error happened but can failover", "OTHER");
    public static final TransferErrorResultCodeEnum ACCOUNT_TYPE_MISMATCH =
            new TransferErrorResultCodeEnum("ACCOUNT_TYPE_MISMATCH", 1002,
                    "The payment is rejected because account type not correct", "OTHER");
    public static final TransferErrorResultCodeEnum VENDOR_OUTAGE =
            new TransferErrorResultCodeEnum("VENDOR_OUTAGE", 1003, "The vendor is in short outage",
                    "OTHER");
    public static final TransferErrorResultCodeEnum RECEIPIENT_PSP_OUTAGE =
            new TransferErrorResultCodeEnum("RECEIPIENT_PSP_OUTAGE", 1004,
                    "The target bank is in outage", "OTHER");
    public static final TransferErrorResultCodeEnum RISK_ANALYSIS_FAILED =
            new TransferErrorResultCodeEnum("RISK_ANALYSIS_FAILED", 1005,
                    "The transaction failed risk analysis", "ANTIFRAUD");
    public static final TransferErrorResultCodeEnum GIFT_CARD_OUT_OF_STOCK =
            new TransferErrorResultCodeEnum("GIFT_CARD_OUT_OF_STOCK", 1010,
                    "The Gift Card is out of stock at this moment", "OTHER");
    public static final TransferErrorResultCodeEnum FRAUD_CYBS =
            new TransferErrorResultCodeEnum("FRAUD_CYBS", 2301,
                    "The transaction failed cybersource analysis", "ANTIFRAUD");
    public static final TransferErrorResultCodeEnum ACQUIRER_INVALID_CARD_NUMBER =
            new TransferErrorResultCodeEnum("ACQUIRER_INVALID_CARD_NUMBER", 2401,
                    "Invalid card number - acquirer", "ACQUIRE");
    public static final TransferErrorResultCodeEnum ACQUIRER_INVALID_NSU_HOST =
            new TransferErrorResultCodeEnum("ACQUIRER_INVALID_NSU_HOST", 2402,
                    "Invalid or duplicate nsu host - acquirer", "ACQUIRE");
    public static final TransferErrorResultCodeEnum ACQUIRER_INVALID_MERCHANT =
            new TransferErrorResultCodeEnum("ACQUIRER_INVALID_MERCHANT", 2403,
                    "Invalid merchant - acquirer", "ACQUIRE");
    public static final TransferErrorResultCodeEnum ACQUIRER_ANTI_FRAUD =
            new TransferErrorResultCodeEnum("ACQUIRER_ANTI_FRAUD", 2404,
                    "Vendor anti-fraud - acquirer", "ACQUIRE");
    public static final TransferErrorResultCodeEnum ACQUIRER_EXCEEDS_AMOUNT_LIMIT =
            new TransferErrorResultCodeEnum("ACQUIRER_EXCEEDS_AMOUNT_LIMIT", 2405,
                    "Exceeds amount limit - acquirer", "ACQUIRE");
    public static final TransferErrorResultCodeEnum ACQUIRER_EXCEEDS_FREQUENCY_LIMIT =
            new TransferErrorResultCodeEnum("ACQUIRER_EXCEEDS_FREQUENCY_LIMIT", 2406,
                    "Exceeds count limit - acquirer", "ACQUIRE");
    public static final TransferErrorResultCodeEnum ACQUIRER_INVALID_CVV =
            new TransferErrorResultCodeEnum("ACQUIRER_INVALID_CVV", 2407, "Invalid CVV number",
                    "ACQUIRE");
    public static final TransferErrorResultCodeEnum ACQUIRER_REJECTED =
            new TransferErrorResultCodeEnum("ACQUIRER_REJECTED", 2408,
                    "Acquirer can't process the transaction. Contact acquirer", "ACQUIRE");
    public static final TransferErrorResultCodeEnum ISSUER_NOT_AUTHORIZED_NO_RETRY =
            new TransferErrorResultCodeEnum("ISSUER_NOT_AUTHORIZED_NO_RETRY", 2501,
                    "Issuer not authorized. Do not retry", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_NOT_AUTHORIZED_RETRY =
            new TransferErrorResultCodeEnum("ISSUER_NOT_AUTHORIZED_RETRY", 2502,
                    "Issuer not authorized. Try later", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_UNAVAILABLE_RETRY =
            new TransferErrorResultCodeEnum("ISSUER_UNAVAILABLE_RETRY", 2503,
                    "Issuer unavailable. Try later", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_SUSPECTED_FRAUD =
            new TransferErrorResultCodeEnum("ISSUER_SUSPECTED_FRAUD", 2504,
                    "Issuer suspected fraud", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_INSUFFICIENT_FUNDS =
            new TransferErrorResultCodeEnum("ISSUER_INSUFFICIENT_FUNDS", 2505,
                    "Insufficient funds - issuer", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_EXCEEDS_AMOUNT_LIMIT =
            new TransferErrorResultCodeEnum("ISSUER_EXCEEDS_AMOUNT_LIMIT", 2506,
                    "Exceeds amount limit - issuer", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_EXCEEDS_FREQUENCY_LIMIT =
            new TransferErrorResultCodeEnum("ISSUER_EXCEEDS_FREQUENCY_LIMIT", 2507,
                    "Exceeds frequency limit - issuer", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_INVALID_CARD_NUMBER =
            new TransferErrorResultCodeEnum("ISSUER_INVALID_CARD_NUMBER", 2508,
                    "Invalid card number - issuer", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_BLOCKED_CARD =
            new TransferErrorResultCodeEnum("ISSUER_BLOCKED_CARD", 2509, "Blocked card - issuer",
                    "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_EXPIRED_CARD =
            new TransferErrorResultCodeEnum("ISSUER_EXPIRED_CARD", 2510, "Expired card - issuer",
                    "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_NEW_CARD_NOT_ACTIVATED =
            new TransferErrorResultCodeEnum("ISSUER_NEW_CARD_NOT_ACTIVATED", 2511,
                    "New card not activated - issuer", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_RESTRICTED_CARD =
            new TransferErrorResultCodeEnum("ISSUER_RESTRICTED_CARD", 2512,
                    "Restricted card - issuer", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_CARD_NOT_SUPPORTED_FOR_ONLINE =
            new TransferErrorResultCodeEnum("ISSUER_CARD_NOT_SUPPORTED_FOR_ONLINE", 2513,
                    "Card does not support online purchases - issuer", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_INVALID_CVV =
            new TransferErrorResultCodeEnum("ISSUER_INVALID_CVV", 2514,
                    "Invalid CVV/CVC2 code entered - issuer", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_INVALID_EFFECTIVE_DATE =
            new TransferErrorResultCodeEnum("ISSUER_INVALID_EFFECTIVE_DATE", 2515,
                    "Invalid effective date - issuer", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_INSTALLMENT_NOT_SUPPORTED =
            new TransferErrorResultCodeEnum("ISSUER_INSTALLMENT_NOT_SUPPORTED", 2516,
                    "Card does not support installment", "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_LOST_CARD =
            new TransferErrorResultCodeEnum("ISSUER_LOST_CARD", 2517, "Card reported as lost",
                    "OTHER");
    public static final TransferErrorResultCodeEnum ISSUER_INVALID_REGION =
            new TransferErrorResultCodeEnum("ISSUER_INVALID_REGION", 2518,
                    "Restricted card: card invalid in region or country", "OTHER");
    public static final TransferErrorResultCodeEnum NETWORK_UNAVAILABLE_RETRY =
            new TransferErrorResultCodeEnum("NETWORK_UNAVAILABLE_RETRY", 2601,
                    "Network unavailable. Try later", "NETWORK");
    public static final TransferErrorResultCodeEnum NETWORK_SUSPECTED_FRAUD =
            new TransferErrorResultCodeEnum("NETWORK_SUSPECTED_FRAUD", 2602,
                    "AMEX suspected fraud/travel warning", "NETWORK");
    public static final TransferErrorResultCodeEnum NETWORK_SECURITY_BREACH =
            new TransferErrorResultCodeEnum("NETWORK_SECURITY_BREACH", 2603, "Visa security breach",
                    "NETWORK");


    public TransferErrorResultCodeEnum(final String code,
                                       final Integer errorCode,
                                       final String description,
                                       final String type) {
        super(code);
        this.errorCode = errorCode;
        this.description = description;
        this.type = type;
    }

    public static TransferErrorResultCodeEnum parseStr(final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return parse(TransferErrorResultCodeEnum.class, code);
    }

    public static TransferErrorResultCodeEnum parseNum(final Integer code) {
        final List<TransferErrorResultCodeEnum> list =
                DynamicConstant.values(TransferErrorResultCodeEnum.class);
        for (final TransferErrorResultCodeEnum resultCodeEnum : list) {
            if (resultCodeEnum.getErrorCode().equals(code)) {
                return resultCodeEnum;
            }
        }
        return null;
    }

    @Converter
    public static class Convert implements AttributeConverter<TransferErrorResultCodeEnum, String> {

        @Override
        public String convertToDatabaseColumn(final TransferErrorResultCodeEnum enumValue) {
            if (Objects.isNull(enumValue)) {
                throw CommonExceptionCode.PARAMETER_MISSING.exception("TransferResultCode");
            }
            return enumValue.getCode();
        }

        @Override
        public TransferErrorResultCodeEnum convertToEntityAttribute(final String dbValue) {
            if (NumberUtils.isParsable(dbValue)) {
                return parseNum(Integer.parseInt(dbValue));
            }
            return parseStr(dbValue);
        }
    }
}
