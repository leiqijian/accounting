package com.liquido.worker.exception;

import java.util.Objects;

import com.liquido.core.common.exception.BusinessException;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.exception.ErrorWrapper;
import com.liquido.core.mvc.dto.ResponseDto;

/**
 * Worker service error code
 * code range:[150000~199999]
 */
public enum WorkerExceptionCode implements ErrorWrapper {

    TRANSACTION_TYPE_UNDEFINED(150000, "The TransactionType Undefined: {0}"),

    TASK_LOCKED_FAIL(150001, "Get task transaction lock failed"),

    GET_EXCHANGE_RATE_TOKEN_FAIL(150002, "Get exchange rate token failed"),

    GET_EXCHANGE_RATE_FAIL(150003, "Get exchange rate fail, target currency: {0}"),

    FEE_RATE_CONFIG_NOT_FOUND(150004, "Fee rate configuration not found"),

    UNKNOWN_MERCHANT_INFO(150005, "Unknown merchant, code: {0}"),

    NOT_FILE_OF_BUCKET(150006, "File not found in bucket"),

    PARAMETER_ILLEGAL(150007, "UploadKey Must contain bucket path"),

    LOAD_FEE_CONFIG_FAILURE(150008, "Load fee config failed, unique id: {0}"),

    LOAD_ACCOUNT_PRODUCT_FAILURE(150009, "Load account product config failed"),

    FEE_CONFIG_INVALID(150010, "Fee configuration invalid, transaction id: {0}"),

    SINGLE_BATCH_LIMIT_ILLEGAL(150011, "Single batch can process up to {0} records"),

    SINGLE_BATCH_LOCK_TASK_FAIL(150012, "Batch lock task fail"),

    DATA_NOT_MATCH_ACCOUNT_ILLEGAL(150013,
            "The accounts corresponding to this batch of task data are inconsistent"),

    FEE_CALCULATION_RETRY_FAILED(150014,
            "Fee calculation retry failed, return to the sqs waiting for reprocess"),

    FEE_CALCULATION_ALREADY_DONE(150015, "The data is already handled"),

    FEE_CALCULATION_DATA_NOT_SETTLE(150016, "The data {0} is not settle, need wait"),

    FEE_CALCULATION_SYNC_ERROR(150017, "The task fee calculation sync error"),

    DATA_SYNC_NOT_FOUND_SETTLED_FEE(150018, "Doesn't found settled fee"),

    DATA_RE_RUN_NOT_FOUND_CALCULATE_NODE(150019, "Doesn't found calculate node data"),

    FEE_CALCULATION_RECORDS_NOT_MATCH(150020,
            "The number of settlement fee records does not match the configured nums"),

    WORKDAY_UNDEFINED(150030, "workday undefined"),

    TRADING_MODEL_UNDEFINED(150031, "Trading model undefined"),

    FEE_CALCULATION_SYNC_DATA_WAREHOUSE_VISIT_FAILED(150100,
            "Fee calculation sync, data warehouse visit failed, "
                    + "params: [from: {0}, to: {1}], error message: {2}"),

    SQS_TIMER_EXECUTE_ERROR(150103, "SQS timer execute error, queue: {0}, msg: {1}"),

    PRODUCT_DOES_NOT_EXIST(150104, "The product does not exist under the merchant"),

    DATA_WAREHOUSE_ERROR(150105, "Data warehouse error, msg: {0}"),

    CANCEL_HOLDING_TRANSACTION_FAIL(150201, "Cancel holding transaction fail"),

    UNKNOWN_ACCOUNT_INFO(150303, "unknown account"),

    UNKNOWN_PRODUCT_INFO(150304, "unknown product"),

    LOAD_FX_LOSE_FAILURE(150305, "Load fxLose failed"),

    QUERY_TRANSACTION_METRIC_DATA_ERROR(150306, "query transaction metric data not be null"),

    QUERY_TRANSACTION_RATIO_DATA_ERROR(150307, "query transaction ratio data not be null"),

    CHARGE_BACK_ORDER_REPETITION_DEFENSE(150400, "repetition defense"),

    CHARGE_BACK_ORDER_REPETITION_ACCEPT(150401, "repetition accept"),

    DW_REQUEST_ERROR(150500, "Dw request error"),


    ;

    /* error code */
    private final Integer code;

    /* error description */
    private final String message;


    WorkerExceptionCode(final Integer code, final String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
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
