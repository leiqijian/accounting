package com.liquido.statement.service.payment.mexico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.OkHttpClientUtil;
import com.liquido.statement.common.Constant;
import com.liquido.statement.enums.PaymentTransactionStatusEnum;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.PaymentSpeiPayoutBo;
import com.liquido.statement.pojo.dto.PaymentPayoutDto;
import com.liquido.statement.pojo.dto.PaymentTokenDto;
import com.liquido.statement.pojo.dto.payment.SpeiPayoutResult;
import com.liquido.statement.pojo.entity.TransactionPayout;
import com.liquido.statement.service.payment.PaymentConstant;
import com.liquido.statement.service.payment.PaymentPayoutService;
import com.liquido.statement.service.payment.PaymentTokenManager;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * Country: Mexico
 * Payment SPEI
 */
@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class SpeiPayoutServiceImpl implements PaymentPayoutService {

    private final PaymentTokenManager paymentTokenManager;

    @Value("${statement.payment.payout.mexico.spei.api-url:}")
    private String apiUrl;
    @Value("${statement.payment.payout.mexico.spei.query-result-url:}")
    private String queryResultUrl;

    @Override
    public PaymentChannelEnum getPayoutChannel() {
        return PaymentChannelEnum.SPEI;
    }

    @Override
    public PaymentPayoutDto payout(final TransactionPayout payoutVo) {
        log.info("begin process spei payment payout payoutVo={}", payoutVo);

        if (payoutVo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("payment amount must be greater than 0");
        }

        /** get access token */
        final PaymentTokenDto authDto = paymentTokenManager.getPaymentAccessToken();
        try {
            final Map<String, String> headers = new HashedMap(3);
            headers.put("Content-Type", ContentType.APPLICATION_JSON.toString());
            headers.put(PaymentConstant.HEADER_AUTHORIZATION,
                    authDto.getTokenType().concat(" ").concat(authDto.getAccessToken()));
            headers.put(PaymentConstant.HEADER_X_API_KEY, payoutVo.getPaymentConfig().getApiKey());

            final PaymentSpeiPayoutBo params = this.buildPayoutData(payoutVo);
            log.info("payment payout spei request orderId={}, apiUrl={}, headers={}, params={}",
                    payoutVo.getUniqueId(), apiUrl, headers, params);

            // If mockSwitch is ON
            if (payoutVo.getPaymentConfig().getMockSwitch()) {
                return PaymentPayoutDto.builder()
                        .statusCode(200)
                        .transferStatusCode(200)
                        .uniqueId(payoutVo.getUniqueId())
                        .transferStatus(PaymentTransactionStatusEnum.IN_PROGRESS.getCode())
                        .transactionId(String.valueOf(SnowflakeIdUtil.generate()))
                        .build();
            }
            final PaymentPayoutDto responseDto =
                    OkHttpClientUtil.postWithJson(apiUrl, params, headers,
                            new TypeReference<>() {
                            });
            log.info("payment payout spei orderId={}, response={}", payoutVo.getUniqueId(),
                    responseDto);

            if (Objects.isNull(responseDto) || responseDto.getStatusCode() != 200) {
                log.error("call payment fail requestParams={}, ResponseDto={}", payoutVo,
                        responseDto);
                throw StatementExceptionCode.PAYMENT_PAYOUT_FAIL.exception();
            }

            responseDto.setPaymentConfigId(payoutVo.getPaymentConfig().getId());
            responseDto.setUniqueId(payoutVo.getUniqueId());
            log.info("end process payout orderId={}", payoutVo.getUniqueId());
            return responseDto;
        } catch (Exception e) {
            log.error("execute payout spei error: orderId={}", payoutVo.getUniqueId(), e);
            throw StatementExceptionCode.PAYMENT_PAYOUT_FAIL.exception();
        }
    }

    @Override
    public SpeiPayoutResult queryPayoutResult(final TransactionPayout vo) {

        // get payout result from remote
        final SpeiPayoutResult result = this.getPayoutResult(vo);

        if (StringUtils.isNotBlank(result.getFinalStatusTime())) {
            final ZonedDateTime zonedFinalStatusTime =
                    ZonedDateTime.parse(result.getFinalStatusTime(),
                            LocalDateTimeUtil.FORMAT_DATETIME_Z);
            result.setFinalStatusTimeUtc(zonedFinalStatusTime
                    .withZoneSameInstant(Constant.COMMON.ZONE_UTC).toLocalDateTime());
        }

        if (StringUtils.isNotBlank(result.getCreateTime())) {
            final ZonedDateTime zoneCreateTime = ZonedDateTime.parse(result.getCreateTime(),
                    LocalDateTimeUtil.FORMAT_DATETIME_Z);
            result.setCreateTimeUtc(
                    zoneCreateTime.withZoneSameInstant(Constant.COMMON.ZONE_UTC).toLocalDateTime());
        }

        return result;
    }

    private SpeiPayoutResult getPayoutResult(final TransactionPayout payout) {
        if (payout.getPaymentConfig().getMockSwitch()) {
            return this.mockResult(payout);
        }

        /* get access token */
        final PaymentTokenDto token = paymentTokenManager.getPaymentAccessToken();

        try {
            final Map<String, String> headers = new HashedMap(2);
            headers.put(PaymentConstant.HEADER_AUTHORIZATION,
                    token.getTokenType().concat(" ").concat(token.getAccessToken()));
            headers.put(PaymentConstant.HEADER_X_API_KEY, payout.getPaymentConfig().getApiKey());

            final String apiUrl;
            if (queryResultUrl.endsWith("/")) {
                apiUrl = queryResultUrl + payout.getUniqueId();
            } else {
                apiUrl = queryResultUrl + "/" + payout.getUniqueId();
            }

            log.info("query payment payout spei request params={}", payout);
            final SpeiPayoutResult responseDto =
                    OkHttpClientUtil.doGet(apiUrl, headers, new TypeReference<>() {
                    });
            log.info("query payment payout spei response={}", responseDto);

            if (Objects.isNull(responseDto)) {
                throw StatementExceptionCode.QUERY_PAYMENT_RESULT_FAIL.exception();
            }

            return responseDto;
        } catch (Exception e) {
            log.error("query payment payout spei error", e);
            throw StatementExceptionCode.QUERY_PAYMENT_RESULT_FAIL.exception();
        }
    }

    private SpeiPayoutResult mockResult(final TransactionPayout payout) {
        if (Objects.isNull(payout.getTargetInfo())) {
            log.error("buildPayout parameter fail, targetInfo is not config");
            throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
        }

        final Map<String, String> targetInfo = JsonUtil.toMap(payout.getTargetInfo());

        final LocalDateTime mockTime = LocalDateTimeUtil.nowUtc();
        return SpeiPayoutResult.builder()
                .transferStatus("SETTLED")
                .statusCode(200)
                .transferStatusCode(200)
                .finalStatusTime(mockTime.format(LocalDateTimeUtil.FORMAT_DATETIME) + " GMT+00:00")
                .createTime(mockTime.format(LocalDateTimeUtil.FORMAT_DATETIME) + " GMT+00:00")
                .targetBankAccountId(targetInfo.get("targetBankAccountId"))
                .targetName(targetInfo.get("targetName"))
                .build();
    }

    private PaymentSpeiPayoutBo buildPayoutData(final TransactionPayout payout) {
        final Map<String, String> info = JsonUtil.toMap(payout.getTargetInfo());

        if (Objects.isNull(info)) {
            throw CommonExceptionCode.PARAMETER_MISSING.exception("payment targetInfo");
        }

        if (!List.of(CurrencyEnum.USD, CurrencyEnum.MXN).contains(payout.getCurrency())) {
            throw StatementExceptionCode.INCONSISTENT_CURRENCY_TYPES.exception();
        }

        if (payout.getAmount().compareTo(BigDecimal.ZERO) <= 0
                || StringUtils.isBlank(info.get("targetName"))
                || StringUtils.isBlank(info.get("targetBankAccountId"))) {

            log.error("TransactionPayout parameter missing payoutVo={}", payout);
            throw CommonExceptionCode.PARAMETER_MISSING.exception("parameter targetInfo missing");
        }

        return PaymentSpeiPayoutBo.builder()
                /* required args */
                .idempotencyKey(payout.getUniqueId().toString())
                .country(payout.getCountryCode().getCode())
                .currency(payout.getCurrency().getCode())
                .amountInCents(payout.getAmount())
                .targetName(info.get("targetName"))
                .targetBankAccountId(info.get("targetBankAccountId"))

                /* non-required */
                .targetLastName(StringUtils.defaultIfBlank(info.get("targetLastName"), ""))
                .targetDocument(StringUtils.defaultIfBlank(info.get("targetDocument"), ""))
                .targetEmail(StringUtils.defaultIfBlank(info.get("targetEmail"), ""))
                .targetBankId(StringUtils.defaultIfBlank(info.get("targetBankId"), ""))
                .targetBankName(StringUtils.defaultIfBlank(info.get("targetBankName"), ""))
                .targetBankCode(StringUtils.defaultIfBlank(info.get("targetBankCode"), ""))
                .targetBankBranchId(StringUtils.defaultIfBlank(info.get("targetBankBranchId"), ""))
                .comment(StringUtils.defaultIfBlank(info.get("comment"), ""))
                .build();
    }
}
