package com.liquido.statement.service.payment;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.OkHttpClientUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.dto.PaymentTokenDto;

import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTokenManager {

    private final RedisCacheUtil redisCacheUtil;

    @Value("${statement.payment.auth.token-url:}")
    private String tokenUrl;
    @Value("${statement.payment.auth.client-id:}")
    private String clientId;
    @Value("${statement.payment.auth.client-secret:}")
    private String clientSecret;
    @Value("${statement.payment.auth.grant-type:client_credentials}")
    private String grantType;

    public PaymentTokenDto getPaymentAccessToken() {
        PaymentTokenDto tokenDto = redisCacheUtil.getCacheObject(CacheConstant.PAYMENT_TOKEN_KEY);
        if (Objects.nonNull(tokenDto)) {
            return tokenDto;
        }

        try {
            final Map<String, String> authVo = new HashedMap(3);
            authVo.put("client_id", clientId);
            authVo.put("client_secret", clientSecret);
            authVo.put("grant_type", grantType);

            final Map<String, String> headers = new HashedMap(1);
            headers.put("Content-Type", "application/x-www-form-urlencoded");

            log.info("get payment access token begin tokenUrl={}, authVo={}", tokenUrl, authVo);
            final String result = OkHttpClientUtil.postWithForm(tokenUrl, authVo, headers);
            log.info("get payment access token end, result={}", result);

            if (StringUtil.isBlank(result)) {
                log.error("get payment access token fail, result={}", result);
                throw StatementExceptionCode.GET_PAYMENT_TOKEN_FAIL.exception();
            }

            // token will expire after 3600 seconds
            tokenDto = JsonUtil.toBean(result, PaymentTokenDto.class);
            redisCacheUtil.setCacheObject(CacheConstant.PAYMENT_TOKEN_KEY, tokenDto,
                    tokenDto.getExpiresIn(), TimeUnit.SECONDS);

            return tokenDto;
        } catch (Exception e) {
            log.error("get payment access token error:", e);
            throw StatementExceptionCode.GET_PAYMENT_TOKEN_FAIL.exception();
        }
    }
}
