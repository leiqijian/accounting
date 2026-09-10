package com.liquido.statement.exception;

import java.util.Objects;

import com.liquido.core.common.exception.BusinessException;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.exception.ErrorWrapper;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.Getter;

/**
 * Worker service error code
 * code range:[300000~399999]
 */
@Getter
public enum StatementExceptionCode implements ErrorWrapper {

    UNKNOWN_ACCOUNT(300000, "Unknown Account"),

    UNKNOWN_GLOBAL_ACCOUNT(300001, "Unknown Global Account"),

    TRANSACTION_TYPE_UNDEFINED(300002, "The transactionType undefined"),

    INCONSISTENT_CURRENCY_TYPES(300003, "Inconsistent currency types, operation failed"),

    ACCOUNT_NOT_FOUND(300004, "The account is not found, account type: {0}"),

    ACCOUNT_NOT_FOUND_BY_ACCOUNT_IDENTIFIER(300005,
            "The account is not found, accountIdentifier：{0}"),

    ACCOUNT_CURRENCY_INCONSISTENT(300006, "Account currency type is inconsistent"),


    ACCOUNT_TOPUP_NOT_SUPPORT(300010, "This account does not support topup"),

    ACCOUNT_WITHDRAWAL_NOT_SUPPORT(300011, "This account does not support withdrawal"),

    ACCOUNT_INTERNAL_TRANSFER_NOT_SUPPORT(300012,
            "This account does not support internal transfer"),

    ACCOUNT_INTERNAL_TRANSFER_CONFIG_UNDEFINED(300013, "Internal transfer config undefined"),

    ACCOUNT_PAYMENT_LINK_NOT_SUPPORT(300014, "This account does not support payment link"),

    INTERNAL_ACCOUNT_TRANSFER_ERROR(300015, "Internal Account Transfer Error:{0}"),

    GET_ACCOUNT_BALANCE_ERROR(300016, "get account balance error"),

    INSUFFICIENT_EXTRACTABLE_BALANCE(300098,
            "Insufficient extractable balance, Current extractable balance {0}({1})"),

    INSUFFICIENT_ACCOUNT_BALANCE(300099, "Insufficient account balance"),

    SETTLEMENT_STRATEGY_UNDEFINED(300100, "Settlement strategy undefined"),

    TRANSACTION_SETTLEMENT_FAIL(300101, "Transaction settlement fail, error: {0}"),

    ACCOUNT_DAILY_BILL_INIT_FAIL(300102, "Account daily billId init fail"),

    GET_ACCOUNT_LOCKED_FAIL(300103, "Failed to lock account"),

    GET_ACCOUNT_IN_PROGRESS_LOCKED_FAIL(300104, "Failed to lock account in progress"),

    ACCOUNT_BALANCE_CHANGE_FAILED(300105, "Failed to change account balance"),

    ACCOUNT_DAILY_CUT_FAILED(300106, "Account daily cut operation failed"),

    IN_PROGRESS_PROCESS_SYNC_FAIL(300107, "in progress process sync failed, error: {0}"),

    IN_PROGRESS_PROCESS_RETRY_FAILED(300108, "in progress process retry failed"),

    TRANSACTION_RECALCULATION(300120, "System is processing the bill, please try again later"),

    GET_EXCHANGE_RATE_FAIL(300210, "Get exchange rate fail"),

    GET_REALTIME_EXCHANGE_RATE_FAIL(300211, "Get realtime exchange rate fail"),

    TRANSACTION_MERCHANT_GET_FAIL(3000220, "Get merchant fail, merchant id or code: {0}"),

    ACCOUNT_PROCESSING_DAILY_CUT(300300, "System processing daily cut now, please wait"),

    ACCOUNT_ALREADY_DAILY_CUT(300301, "The account has completed the day cut operation"),

    PAYMENT_CONFIG_UNDEFINED(300398, "The payment config undefined"),

    ACCOUNT_DEPOSIT_UNDEFINED(300399, "The account deposit undefined"),

    PAYMENT_CHANNEL_UNDEFINED(300400, "Payment channel undefined"),

    ACCOUNT_TRANSFER_OUT_FAIL(300401, "Account transfer out fail"),

    GET_PAYMENT_TOKEN_FAIL(300410, "Get payment token fail"),

    PAYMENT_PAYOUT_FAIL(300420, "Payment payout fail"),

    GET_PAYMENT_LINK_FAIL(300421, "Generate payment link fail"),

    GET_INSTALLMENTS_PLAN_FAIL(300422, "Get installments plan fail"),

    QUERY_PAYMENT_RESULT_FAIL(300430, "Query Payment result fail"),

    PAYMENT_AMOUNT_ERROR(300431, "Payment amount error"),

    CANCEL_HOLDING_TRANSACTION_FAIL(300450, "Cancel holding transaction fail"),

    ACCOUNT_TOPUP_FAIL(300500, "Account topup fail"),

    ACCOUNT_FROZEN_EXTRACTABLE_BALANCE_FAIL(300510, "Account frozen extractable balance fail"),

    ACCOUNT_UNFROZEN_EXTRACTABLE_BALANCE_FAIL(300511, "Account unfrozen extractable balance fail"),

    ACCOUNT_REDUCE_FROZEN_AMOUNT_FAIL(300512, "Account reduce frozen amount fail"),

    TRANSACTION_SUMMARY_INIT_FAIL(300513, "Transaction summary init fail"),

    CHARGE_BACK_ORDER_REPETITION_DEFENSE(300514, "Repetition defense"),

    CHARGE_BACK_ORDER_REPETITION_ACCEPT(300515, "Repetition accept"),

    MERCHANT_AND_ACCOUNT_NO_PATTERN(300517, "Merchant and account no pattern"),

    FIND_SUB_ACCOUNT_FAIL_PARAM_ERROR(300518, "Find sub account fail param error"),

    SUB_ACCOUNT_ALREADY_DAILY_CUT(300519, "The sub account has completed the day cut operation"),

    SUB_ACCOUNT_DAILY_CUT_FAIL(300520, "SubAccount daily cut operation failed, error:{0}"),

    SUB_MERCHANT_UNDEFINE(300521, "SubMerchant undefine"),

    SUB_ACCOUNT_UNDEFINE(300522, "SubAccount undefine"),

    BATCH_WITHDRAWAL_FAIL(300523, "Batch withdrawal apply fail, error:{0}"),

    UPDATE_BATCH_WITHDRAWAL_STATE_FAIL(300524, "Update state failed"),

    BATCH_WITHDRAWAL_APPLY_FAIL(300525, "Batch withdrawal apply fail, error:{0}"),
    ;

    /* error code */
    private final Integer code;

    /* error description */
    private final String message;

    StatementExceptionCode(final Integer code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static BusinessException reException(final Integer code, final String message) {
        return new BusinessException(code, message);
    }

    public static BusinessException reException(final ResponseDto<?> vo) {
        if (Objects.isNull(vo)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        return reException(vo.getCode(), vo.getMsg());
    }
}
