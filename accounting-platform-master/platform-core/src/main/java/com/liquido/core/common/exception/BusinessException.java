package com.liquido.core.common.exception;

/**
 * Business layer exception base class
 * <p>
 * <ul>
 * <li>0: success, anything other than 0 is an exception error. </li>
 * <li>Common module: 1, Common module error code range: 1~99999</li>
 * <li>Service Accounting Base: 100, Base module error code range: 100000~149999</li>
 * <li>Service Accounting Worker: 150, Worker module error code range: 150000~199999</li>
 * <li>Service Accounting Dashboard: 200, Dashboard module error code range: 200000~299999</li>
 * <li>Service Accounting Statement: 300, Statement module error code range: 300000~399999</li>
 * <li>Service Accounting Transaction: 400, Transaction module error code range: 400000~499999</li>
 * <li>Service Accounting Risk: 500, Risk module error code range: 500000~599999</li>
 * <li>Service Accounting Admin: 600, Admin module error code range: 600000~699999</li>
 * <li>Service Accounting Report: 700, Report module error code range: 700000~799999</li>
 * </ul>
 * </p >
 */
public class BusinessException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public BusinessException() {
        super();
    }

    /**
     * @param code
     * @param message
     */
    public BusinessException(final Integer code, final String message) {
        super(code, message);
    }

    /**
     * @param code
     * @param message
     * @param cause
     */
    public BusinessException(final Integer code, final String message, final Throwable cause) {
        super(code, message, cause);
    }

    /**
     * @param message
     */
    public BusinessException(final String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public BusinessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param code
     * @param message
     * @param args
     */
    public BusinessException(final Integer code, final String message, final Object[] args) {
        super(code, message, args);
    }

    /**
     * @param code
     * @param message
     * @param tipContent
     */
    public BusinessException(final Integer code, final String message, final Object tipContent) {
        super(code, message, tipContent);
    }

    /**
     * @param code
     * @param message
     * @param cause
     * @param args
     */
    public BusinessException(final Integer code, final String message, final Throwable cause,
                             final Object[] args) {
        super(code, message, cause, args);
    }

    /**
     * @param message
     * @param args
     */
    public BusinessException(final String message, final Object[] args) {
        super(message, args);
    }

    /**
     * @param message
     * @param cause
     * @param args
     */
    public BusinessException(final String message, final Throwable cause, final Object[] args) {
        super(message, cause, args);
    }

}
