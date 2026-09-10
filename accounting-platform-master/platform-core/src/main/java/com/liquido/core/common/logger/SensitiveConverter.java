package com.liquido.core.common.logger;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.core.common.utils.DesensitizeUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Desensitization Converter
 */
@Slf4j
public class SensitiveConverter {

    public static String convert(final String source, final SensitiveType sensitiveType) {
        try {
            switch (sensitiveType) {
                case NONE:
                    return source;

                case GENERAL:
                case MOBILE:
                case ID_CARD:
                case WECHAT_NO:
                case LOGIN_NAME:
                case DOCUMENT_ID:
                case FIXED_PHONE:
                case ACCOUNT_NUMBER:
                case BANK_CARD_NUMBER:
                    return DesensitizeUtil.convert(source);

                case NICK_NAME:
                case REAL_NAME:
                    return DesensitizeUtil.convertRealName(source);

                case EMAIL:
                    return DesensitizeUtil.convertEmail(source);

                case ZH_ADDRESS:
                    return DesensitizeUtil.convertZhAddress(source);

                case US_ADDRESS:
                    return DesensitizeUtil.convertUsAddress(source);

                case PASSWORD:
                case SHIELD:
                default:
                    return "********";
            }
        } catch (Exception e) {
            log.error("SensitiveConverter.convert error:{}", e.getMessage(), e);
        }

        return "********";
    }

    /**
     * Collection class attribute desensitization
     *
     * @param source
     * @param sensitiveType
     *
     * @return
     */
    public static Object collectionConvert(final Collection<String> source,
                                           final SensitiveType sensitiveType) {
        try {
            if (CollectionUtils.isEmpty(source)) {
                return source;
            }

            return source.stream()
                    .map(item -> convert(item,
                            Optional.ofNullable(sensitiveType).orElse(SensitiveType.NONE)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("SensitiveConverter.collectionConvert error:{}", e.getMessage(), e);
        }
        return "******";

    }
}
