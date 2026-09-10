package com.liquido.core.common.logger;

import java.util.Arrays;

import com.liquido.core.common.exception.CommonExceptionCode;

import org.apache.commons.lang3.StringUtils;

/**
 * Sensitive field desensitization strategy enumeration class
 */

public enum SensitiveType {

    /**
     * Default insensitive fields (not desensitized)
     */
    NONE,

    /**
     * All contents of the field value are masked,
     * and the attribute value is replaced with '******'
     */
    SHIELD,

    GENERAL,

    /**
     * login name
     */
    LOGIN_NAME,

    /**
     * login password
     */
    PASSWORD,

    /**
     * user real name
     */
    REAL_NAME,

    /**
     * user nickname
     */
    NICK_NAME,

    /**
     * user identity
     */
    ID_CARD,

    /**
     * user identity documentId
     */
    DOCUMENT_ID,

    /**
     * bank account number
     */
    ACCOUNT_NUMBER,
    BANK_CARD,
    BANK_CARD_NUMBER,

    /**
     * email
     */
    EMAIL,

    /**
     * mobile
     */
    MOBILE,

    /**
     * wechatNo
     */
    WECHAT_NO,

    /**
     * fixed phoneNo
     */
    FIXED_PHONE,

    /**
     * user address detail(CHINA)
     */
    ZH_ADDRESS,

    /**
     * user address detail(UK, US)
     */
    US_ADDRESS,

    ;

    public static SensitiveType parse(String name) {
        if (StringUtils.isBlank(name)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
        }

        return Arrays.stream(SensitiveType.values()).filter(tmp -> tmp.name().equals(name.trim()))
                .findFirst().orElse(NONE);
    }

}
