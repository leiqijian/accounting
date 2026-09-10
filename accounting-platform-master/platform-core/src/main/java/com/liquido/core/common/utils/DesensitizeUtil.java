package com.liquido.core.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Desensitization tools, sensitive information data filtering
 */
public class DesensitizeUtil {

    /**
     * common convert
     *
     * @param str
     *
     * @return
     */
    public static String convert(final String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }

        final int length = str.length();
        if (length <= 3) {
            return "***";
        }

        if (length > 3 && length <= 5) {
            return StringUtils.left(str, 1).concat(StringUtils.removeStart(
                    StringUtils.leftPad(StringUtils.right(str, 1), length, "*"), "*"));
        }

        if (length > 5 && length <= 10) {
            return StringUtils.left(str, 2).concat(StringUtils.removeStart(
                    StringUtils.leftPad(StringUtils.right(str, 2), length, "*"), "*"));
        }

        if (length > 10 && length <= 12) {
            return StringUtils.left(str, 3).concat(StringUtils.removeStart(
                    StringUtils.leftPad(StringUtils.right(str, 4), length, "*"), "*"));
        }

        if (length > 12 && length <= 16) {
            return StringUtils.left(str, 4).concat(StringUtils.removeStart(
                    StringUtils.leftPad(StringUtils.right(str, 4), length, "*"), "*"));
        }

        return StringUtils.left(str, 6).concat(StringUtils.removeStart(
                StringUtils.leftPad(StringUtils.right(str, 4), length, "*"), "*"));
    }


    /**
     * convertAccountName
     *
     * @param accountName
     *
     * @return
     */
    public static String convertAccountName(final String accountName) {
        return convert(accountName);
    }

    /**
     * convertRealName
     *
     * @param realName
     *
     * @return
     */
    /**
     * convertRealName
     *
     * @param realName
     *
     * @return
     */
    public static String convertRealName(final String realName) {
        if (StringUtils.isBlank(realName) || realName.length() < 2) {
            return "***";
        }

        if (realName.length() == 2) {
            return StringUtils.rightPad(StringUtils.left(realName, 1),
                    StringUtils.length(realName), "*");
        }

        if (realName.length() == 3) {
            return StringUtils.rightPad(StringUtils.left(realName, 1),
                    StringUtils.length(realName), "*");
        }

        return convert(realName);
    }

    /**
     * convertIdCard
     *
     * @param idCard
     *
     * @return
     */
    public static String convertIdCard(final String idCard) {
        return convert(idCard);
    }

    /**
     * convertMobile
     *
     * @param mobile
     *
     * @return
     */
    public static String convertMobile(final String mobile) {
        return convert(mobile);
    }

    /**
     * convertFixedPhoneNum
     *
     * @param phoneNum
     *
     * @return
     */
    public static String convertFixedPhoneNum(final String phoneNum) {
        return convert(phoneNum);
    }

    /**
     * convertBankCard
     *
     * @param bankCardNo
     *
     * @return
     */
    public static String convertBankCard(final String bankCardNo) {
        return convert(bankCardNo);
    }

    /**
     * convertEmail
     *
     * @param email
     *
     * @return
     */
    public static String convertEmail(final String email) {

        if (StringUtils.isBlank(email)) {
            return email;
        }

        if (!email.contains("@") || email.length() <= 3) {
            return convert(email);
        }

        final String[] temp = email.split("@");
        return convert(temp[0]) + "@" + temp[1];
    }

    /**
     * convertAddress(china)
     *
     * @param address
     *
     * @return
     */
    public static String convertZhAddress(final String address) {

        if (StringUtils.isBlank(address)) {
            return "";
        }

        final int length = address.length();
        if (length <= 3) {
            return "***";
        }

        if (length > 3 && length <= 5) {
            StringUtils.rightPad(StringUtils.left(address, 2), length, "*");
        }

        if (length > 5 && length <= 10) {
            StringUtils.rightPad(StringUtils.left(address, 3), length, "*");
        }

        if (length > 10 && length <= 12) {
            StringUtils.rightPad(StringUtils.left(address, 5), length, "*");
        }

        if (length > 12 && length <= 20) {
            StringUtils.rightPad(StringUtils.left(address, 6), length, "*");
        }

        return StringUtils.rightPad(StringUtils.left(address, 10), length, "*");
    }

    /**
     * convertAddress(US,UK)
     *
     * @param address
     *
     * @return
     */
    public static String convertUsAddress(final String address) {

        if (StringUtils.isBlank(address)) {
            return "";
        }

        final int length = address.length();
        if (length <= 3) {
            return "***";
        }

        if (length > 3 && length <= 5) {
            StringUtils.leftPad(StringUtils.right(address, 2), length, "*");
        }

        if (length > 5 && length <= 10) {
            StringUtils.leftPad(StringUtils.right(address, 3), length, "*");
        }

        if (length > 10 && length <= 12) {
            StringUtils.leftPad(StringUtils.right(address, 5), length, "*");
        }

        if (length > 12 && length <= 20) {
            StringUtils.leftPad(StringUtils.right(address, 6), length, "*");
        }

        return StringUtils.leftPad(StringUtils.right(address, 10), length, "*");
    }

    /**
     * convertWeChatNo
     *
     * @param wechatNo
     *
     * @return
     */
    public static String convertWeChatNo(final String wechatNo) {
        return convert(wechatNo);
    }
}
