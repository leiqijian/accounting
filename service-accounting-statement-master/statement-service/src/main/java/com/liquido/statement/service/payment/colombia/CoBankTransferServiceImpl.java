package com.liquido.statement.service.payment.colombia;

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
import com.liquido.statement.pojo.bo.PaymentCoBankTransferPayoutBo;
import com.liquido.statement.pojo.dto.PaymentPayoutDto;
import com.liquido.statement.pojo.dto.PaymentTokenDto;
import com.liquido.statement.pojo.dto.payment.CoBankTransferPayoutResult;
import com.liquido.statement.pojo.entity.TransactionPayout;
import com.liquido.statement.service.payment.PaymentConstant;
import com.liquido.statement.service.payment.PaymentPayoutHandler;
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
 * Country: Colombia
 * Bank Transfer Payment
 */
@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
@PaymentPayoutHandler(PaymentChannelEnum.CO_BANK_TRANSFER)
public class CoBankTransferServiceImpl implements PaymentPayoutService {

    private final PaymentTokenManager paymentTokenManager;

    @Value("${statement.payment.payout.colombia.bank-transfer.api-url:}")
    private String apiUrl;
    @Value("${statement.payment.payout.colombia.bank-transfer.query-result-url:}")
    private String queryResultUrl;

    @Override
    public PaymentPayoutDto payout(final TransactionPayout payoutVo) {
        log.info("begin process co bank transfer payment payout payoutVo={}", payoutVo);

        if (payoutVo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("colombia bank transfer payment amount must be greater than 0");
        }

        final PaymentTokenDto authDto = paymentTokenManager.getPaymentAccessToken();
        try {
            final Map<String, String> headers = new HashedMap(3);
            headers.put("Content-Type", ContentType.APPLICATION_JSON.toString());
            headers.put(PaymentConstant.HEADER_AUTHORIZATION,
                    authDto.getTokenType().concat(" ").concat(authDto.getAccessToken()));
            headers.put(PaymentConstant.HEADER_X_API_KEY, payoutVo.getPaymentConfig().getApiKey());

            final PaymentCoBankTransferPayoutBo payoutBo = this.buildPayoutData(payoutVo);
            log.info("payment payout co bank transfer request orderId={}, payoutBo={}",
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
            log.info("payment payout co bank transfer orderId={}, response={}",
                    payoutVo.getUniqueId(),
                    responseDto);

            if (Objects.isNull(responseDto)) {
                throw StatementExceptionCode.PAYMENT_PAYOUT_FAIL.exception();
            }

            responseDto.setPaymentConfigId(payoutVo.getPaymentConfig().getId());
            responseDto.setUniqueId(payoutVo.getUniqueId());
            log.info("end process co bank transfer payout orderId={}", payoutVo.getUniqueId());
            return responseDto;
        } catch (Exception e) {
            log.error("execute payout co bank transfer error: orderId={}",
                    payoutVo.getUniqueId(), e);

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
    public CoBankTransferPayoutResult queryPayoutResult(final TransactionPayout queryVo) {

        // get payout result from remote
        final CoBankTransferPayoutResult result = this.getPayoutResult(queryVo);

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

    private CoBankTransferPayoutResult getPayoutResult(final TransactionPayout payout) {
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

            log.info("query payment payout co bank transfer request params={}", payout);
            final CoBankTransferPayoutResult responseDto =
                    OkHttpClientUtil.doGet(apiUrl, headers, new TypeReference<>() {
                    });
            log.info("query payment payout co bank transfer response={}", responseDto);

            if (Objects.isNull(responseDto) || responseDto.getStatusCode() != 200) {
                throw StatementExceptionCode.QUERY_PAYMENT_RESULT_FAIL.exception();
            }

            return responseDto;
        } catch (Exception e) {
            log.error("query payment payout co bank transfer error", e);
            throw StatementExceptionCode.QUERY_PAYMENT_RESULT_FAIL.exception();
        }
    }

    private CoBankTransferPayoutResult mockResult(final TransactionPayout payout) {
        if (Objects.isNull(payout.getTargetInfo())) {
            log.error("buildPayout parameter fail, targetInfo is not config");
            throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
        }

        final LocalDateTime mockTime = LocalDateTimeUtil.nowUtc();
        return CoBankTransferPayoutResult.builder()
                .transferStatus("SETTLED")
                .statusCode(200)
                .transferStatusCode(200)
                .finalStatusTime(mockTime.format(LocalDateTimeUtil.FORMAT_DATETIME) + " GMT+00:00")
                .createTime(mockTime.format(LocalDateTimeUtil.FORMAT_DATETIME) + " GMT+00:00")
                .build();
    }

    private PaymentCoBankTransferPayoutBo buildPayoutData(final TransactionPayout payout) {
        final Map<String, String> info = JsonUtil.toMap(payout.getTargetInfo());
        if (Objects.isNull(info)) {
            throw CommonExceptionCode.PARAMETER_MISSING.exception("payment targetInfo");
        }

        if (!List.of(CurrencyEnum.USD, CurrencyEnum.COP).contains(payout.getCurrency())) {
            throw StatementExceptionCode.INCONSISTENT_CURRENCY_TYPES.exception();
        }

        if (payout.getAmount().compareTo(BigDecimal.ZERO) <= 0
                || StringUtils.isBlank(info.get("targetName"))
                || StringUtils.isBlank(info.get("targetDocument"))
                || StringUtils.isBlank(info.get("targetDocumentType"))
                || StringUtils.isBlank(info.get("targetBankCode"))
                || StringUtils.isBlank(info.get("targetBankAccountId"))
                || StringUtils.isBlank(info.get("targetBankAccountType"))) {
            throw CommonExceptionCode.PARAMETER_MISSING.exception("parameter targetInfo missing");
        }

        return PaymentCoBankTransferPayoutBo.builder()
                /* required args */
                .idempotencyKey(payout.getUniqueId().toString())
                .country(payout.getCountryCode().getCode())
                .targetName(info.get("targetName"))
                .targetDocument(info.get("targetDocument"))
                .targetDocumentType(info.get("targetDocumentType"))
                .targetBankCode(info.get("targetBankCode"))
                .targetBankAccountId(info.get("targetBankAccountId"))
                .targetBankAccountType(info.get("targetBankAccountType"))
                .amountInCents(payout.getAmount())
                .currency(payout.getCurrency().getCode())

                /* non-required */
                .targetEmail(StringUtils.defaultIfBlank(info.get("targetEmail"), ""))
                .targetLastName(StringUtils.defaultIfBlank(info.get("targetLastName"), ""))
                .targetPhone(StringUtils.defaultIfBlank(info.get("targetPhone"), ""))
                .targetBirthDate(StringUtils.defaultIfBlank(info.get("targetBirthDate"), ""))
                .targetBankAgency(StringUtils.defaultIfBlank(info.get("targetBankAgency"), ""))
                .build();
    }
}
