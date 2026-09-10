package com.liquido.statement.service.payment.brazil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
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
import com.liquido.statement.pojo.bo.PaymentPixPayoutBo;
import com.liquido.statement.pojo.dto.PaymentPayoutDto;
import com.liquido.statement.pojo.dto.PaymentTokenDto;
import com.liquido.statement.pojo.dto.payment.PixPayoutResult;
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
 * Country: Brazil
 * Payment PIX
 */
@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class PixPayoutServiceImpl implements PaymentPayoutService {

    private final PaymentTokenManager paymentTokenManager;

    @Value("${statement.payment.payout.brazil.pix.api-url:}")
    private String apiUrl;
    @Value("${statement.payment.payout.brazil.pix.query-result-url:}")
    private String queryResultUrl;

    @Override
    public PaymentChannelEnum getPayoutChannel() {
        return PaymentChannelEnum.PIX;
    }

    @Override
    public PaymentPayoutDto payout(final TransactionPayout payoutVo) {
        log.info("begin process pix payment payout payoutVo={}", payoutVo);

        if (payoutVo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("pix payment amount must be greater than 0");
        }

        final PaymentTokenDto authDto = paymentTokenManager.getPaymentAccessToken();
        try {
            final Map<String, String> headers = new HashedMap(3);
            headers.put("Content-Type", ContentType.APPLICATION_JSON.toString());
            headers.put(PaymentConstant.HEADER_AUTHORIZATION,
                    authDto.getTokenType().concat(" ").concat(authDto.getAccessToken()));
            headers.put(PaymentConstant.HEADER_X_API_KEY, payoutVo.getPaymentConfig().getApiKey());

            final PaymentPixPayoutBo payoutBo = this.buildPayoutData(payoutVo);
            log.info("payment payout pix request orderId={}, payoutBo={}",
                    payoutVo.getUniqueId(), payoutBo);

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
                    OkHttpClientUtil.postWithJson(apiUrl, payoutBo, headers, new TypeReference<>() {
                    });
            log.info("payment payout pix orderId={}, response={}", payoutVo.getUniqueId(),
                    responseDto);

            if (Objects.isNull(responseDto)) {
                throw StatementExceptionCode.PAYMENT_PAYOUT_FAIL.exception();
            }

            responseDto.setPaymentConfigId(payoutVo.getPaymentConfig().getId());
            responseDto.setUniqueId(payoutVo.getUniqueId());
            log.info("end process payout orderId={}", payoutVo.getUniqueId());
            return responseDto;
        } catch (Exception e) {
            log.error("execute payout pix error: orderId={}", payoutVo.getUniqueId(), e);
            return PaymentPayoutDto.builder()
                    .uniqueId(payoutVo.getUniqueId())
                    .statusCode(500)
                    .errorMsg(e.getMessage())
                    .transferStatus(PaymentTransactionStatusEnum.FAILED.getCode())
                    .transferStatusCode(500)
                    .transferErrorMsg(e.getMessage())
                    .build();
        }
    }

    @Override
    public PixPayoutResult queryPayoutResult(final TransactionPayout queryVo) {

        // get payout result from remote
        final PixPayoutResult result = this.getPayoutResult(queryVo);

        if (StringUtils.isNotBlank(result.getFinalStatusTime())) {
            final ZonedDateTime zonedFinalStatusTime =
                    ZonedDateTime.parse(result.getFinalStatusTime(),
                            LocalDateTimeUtil.FORMAT_DATETIME_Z);
            result.setFinalStatusTimeUtc(
                    zonedFinalStatusTime.withZoneSameInstant(Constant.COMMON.ZONE_UTC)
                            .toLocalDateTime());
        }

        if (StringUtils.isNotBlank(result.getCreateTime())) {
            final ZonedDateTime zoneCreateTime =
                    ZonedDateTime.parse(result.getCreateTime(),
                            LocalDateTimeUtil.FORMAT_DATETIME_Z);
            result.setCreateTimeUtc(
                    zoneCreateTime.withZoneSameInstant(Constant.COMMON.ZONE_UTC)
                            .toLocalDateTime());
        }

        return result;
    }

    private PixPayoutResult getPayoutResult(final TransactionPayout payout) {
        if (payout.getPaymentConfig().getMockSwitch()) {
            return this.mockResult(payout);
        }

        /** get access token */
        final PaymentTokenDto authDto = paymentTokenManager.getPaymentAccessToken();
        try {
            final Map<String, String> headers = new HashedMap(2);
            headers.put(PaymentConstant.HEADER_AUTHORIZATION,
                    authDto.getTokenType().concat(" ").concat(authDto.getAccessToken()));
            headers.put(PaymentConstant.HEADER_X_API_KEY, payout.getPaymentConfig().getApiKey());

            final String apiUrl;
            if (queryResultUrl.endsWith("/")) {
                apiUrl = queryResultUrl + payout.getUniqueId();
            } else {
                apiUrl = queryResultUrl + "/" + payout.getUniqueId();
            }

            log.info("query payment payout pix request params={}", payout);
            final PixPayoutResult responseDto =
                    OkHttpClientUtil.doGet(apiUrl, headers, new TypeReference<>() {
                    });
            log.info("query payment payout pix response={}", responseDto);

            if (Objects.isNull(responseDto) || responseDto.getStatusCode() != 200) {
                throw StatementExceptionCode.QUERY_PAYMENT_RESULT_FAIL.exception();
            }

            return responseDto;
        } catch (Exception e) {
            log.error("query payment payout pix error", e);
            throw StatementExceptionCode.QUERY_PAYMENT_RESULT_FAIL.exception();
        }
    }

    private PixPayoutResult mockResult(final TransactionPayout payout) {
        if (Objects.isNull(payout.getTargetInfo())) {
            log.error("buildPayout parameter fail, targetInfo is not config");
            throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
        }

        final LocalDateTime mockTime = LocalDateTimeUtil.nowUtc();
        return PixPayoutResult.builder()
                .transferStatus("SETTLED")
                .statusCode(200)
                .transferStatusCode(200)
                .finalStatusTime(mockTime.format(LocalDateTimeUtil.FORMAT_DATETIME) + " GMT+00:00")
                .createTime(mockTime.format(LocalDateTimeUtil.FORMAT_DATETIME) + " GMT+00:00")
                .build();
    }

    private PaymentPixPayoutBo buildPayoutData(final TransactionPayout payout) {
        final Map<String, String> info = JsonUtil.toMap(payout.getTargetInfo());
        if (Objects.isNull(info)) {
            throw CommonExceptionCode.PARAMETER_MISSING.exception("payment targetInfo");
        }

        if (!List.of(CurrencyEnum.USD, CurrencyEnum.BRL).contains(payout.getCurrency())) {
            throw StatementExceptionCode.INCONSISTENT_CURRENCY_TYPES.exception();
        }

        if (payout.getAmount().compareTo(BigDecimal.ZERO) <= 0
                || StringUtils.isBlank(info.get("targetName"))
                || StringUtils.isBlank(info.get("targetDocument"))
                || StringUtils.isBlank(info.get("targetBankAccountId"))
                || StringUtils.isBlank(info.get("targetBankAccountType"))) {
            throw CommonExceptionCode.PARAMETER_MISSING.exception("parameter targetInfo missing");
        }

        return PaymentPixPayoutBo.builder()
                /* required args */
                .idempotencyKey(payout.getUniqueId().toString())
                .targetName(info.get("targetName"))
                .targetDocument(info.get("targetDocument"))
                .targetPixKey(info.get("targetBankAccountId"))
                .targetPixKeyType(info.get("targetBankAccountType").toLowerCase(Locale.ROOT))
                .country(payout.getCountryCode().getCode())
                .currency(payout.getCurrency().getCode())
                .amountInCents(payout.getAmount())

                /* non-required */
                .targetLastName(StringUtils.defaultIfBlank(info.get("targetLastName"), ""))
                .targetEmail(StringUtils.defaultIfBlank(info.get("targetEmail"), ""))
                .targetPhone(StringUtils.defaultIfBlank(info.get("targetPhone"), ""))
                .targetBirthDate(StringUtils.defaultIfBlank(info.get("targetBirthDate"), ""))
                .build();
    }
}
