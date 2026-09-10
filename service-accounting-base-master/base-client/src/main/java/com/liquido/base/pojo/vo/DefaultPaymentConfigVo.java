package com.liquido.base.pojo.vo;

import java.math.BigDecimal;

import com.liquido.base.enums.CountryCodeEnum;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * the lightest default payment config creation parameter value object.
 * <p>
 */
public interface DefaultPaymentConfigVo {

    /**
     * which account belongs to.
     *
     * @return accounting account id, must not null
     */
    Long getAccountId();

    /**
     * account's country code
     *
     * @return {@link CountryCodeEnum}, must not null
     */
    CountryCodeEnum getCountryCode();

    /**
     * merchant's api key of transaction system.
     *
     * @return merchant's api key, must not blank
     */
    String getTransactionApiKey();

    /**
     * max amount in single transaction.
     *
     * @return amount, unit is cent, nullable
     */
    BigDecimal getMaxAmount();

    /**
     * config's extension config param.
     *
     * @return json param object, nullable
     */
    ObjectNode getJsonParam();

    /**
     * remark of payment config.
     *
     * @return remark, nullable
     */
    String getRemark();

}
