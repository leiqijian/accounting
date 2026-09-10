package com.liquido.statement.service.payment.link;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.liquido.base.BaseApis;
import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.DictionaryTypeEnum;
import com.liquido.base.enums.ProductCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.PaymentLinkProductDto;
import com.liquido.core.common.exception.BusinessException;
import com.liquido.core.common.utils.OkHttpClientUtil;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountBo;
import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.PaymentConfigBo;
import com.liquido.statement.pojo.bo.PaymentLinkBo;
import com.liquido.statement.pojo.bo.PaymentLinkConfigBo;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.PaymentLinkDto;
import com.liquido.statement.pojo.dto.PaymentTokenDto;
import com.liquido.statement.pojo.dto.payment.PaymentLinkInstallmentsPlanDto;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.CreatePaymentLinkVo;
import com.liquido.statement.pojo.vo.InstallmentsPlanCheckVo;
import com.liquido.statement.pojo.vo.QueryInstallmentsPlanVo;
import com.liquido.statement.pojo.vo.QueryUniqueAccountVo;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.payment.PaymentConstant;
import com.liquido.statement.service.payment.PaymentTokenManager;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class PaymentLinkServiceImpl implements PaymentLinkService {
    private final ModelMapper modelMapper;
    private final BaseService baseService;
    private final AccountService accountService;
    private final PaymentTokenManager paymentTokenManager;
    private final BaseApis.BaseFeign baseFeign;

    @Value("${statement.payment.payment-link.api-url:}")
    private String apiUrl;

    @Value("${statement.payment.installment-plan.api-url:}")
    private String installmentPlanApiUrl;

    @Override
    public PaymentLinkDto generatePaymentLink(final CreatePaymentLinkVo vo) {
        log.info("begin generate payment-link params={}", vo);

        final AccountBo accountInfo = accountService.findById(vo.getAccountId());
        if (TransactionTypeCodeEnum.PAY_IN != accountInfo.getTransactionTypeCode()) {
            throw StatementExceptionCode.ACCOUNT_PAYMENT_LINK_NOT_SUPPORT.exception();
        }

        final PaymentConfigBo paymentConfig =
                modelMapper.convertBo(baseService.queryPaymentConfig(vo.getAccountId()));

        /** get access token */
        final PaymentTokenDto authDto = paymentTokenManager.getPaymentAccessToken();
        try {
            final Map<String, String> headers = new HashedMap(3);
            headers.put("Content-Type", ContentType.APPLICATION_JSON.toString());
            headers.put(PaymentConstant.HEADER_AUTHORIZATION,
                    authDto.getTokenType().concat(" ").concat(authDto.getAccessToken()));
            headers.put(PaymentConstant.HEADER_X_API_KEY, paymentConfig.getApiKey());

            final PaymentLinkBo params = PaymentLinkBo.builder()
                    .orderId(StringUtils.isBlank(vo.getOrderId()) ? UUID.randomUUID().toString() :
                            vo.getOrderId().trim())
                    .email(StringUtils.defaultIfBlank(vo.getEmail(), ""))
                    .documentId(StringUtils.defaultIfBlank(vo.getDocumentId(), ""))
                    .name(StringUtils.defaultIfBlank(vo.getName(), ""))
                    .phone(StringUtils.defaultIfBlank(vo.getPhone(), ""))
                    .country(vo.getCountry())
                    .currency(vo.getCurrency())
                    .amount(vo.getAmount())
                    .allowPaymentMethods(
                            convertSystemProductCodes(vo.getAllowPaymentMethods(), vo.getCountry()))
                    .installmentPlanId(vo.getInstallmentPlanId())
                    .build();
            log.info("build payment link request vo={}, apiUrl={}, headers={}, params={}",
                    vo, apiUrl, headers, params);

            // If mockSwitch is ON
            if (paymentConfig.getMockSwitch()) {
                return PaymentLinkDto.builder()
                        .linkId(params.getOrderId())
                        .paymentLink("https://merchant-v2.liquido.com/just/testing/mock")
                        .build();
            }
            final PaymentLinkDto responseDto =
                    OkHttpClientUtil.postWithJsonReturnBody(apiUrl, params, headers,
                            new TypeReference<>() {
                            });
            log.info("call remote service response params={}, response={}", params, responseDto);

            if (StringUtils.isNotBlank(responseDto.getMessage())) {
                throw new BusinessException(
                        StatementExceptionCode.GET_PAYMENT_LINK_FAIL.getCode(),
                        responseDto.getMessage());
            } else {
                responseDto.setMessage("Success generate");
            }
            log.info("end generate payment-link params={}, response={}", params, responseDto);
            return responseDto;
        } catch (BusinessException e) {
            throw StatementExceptionCode.reException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("payment generate payment-link error", e);
            throw StatementExceptionCode.GET_PAYMENT_LINK_FAIL.exception();
        }
    }

    @Override
    public Collection<String> convertSystemProductCodes(
            final Collection<ProductCodeEnum> productCodes,
            final CountryCodeEnum countryCode) {
        if (CollectionUtils.isEmpty(productCodes)) {
            return Set.of();
        }

        final Map<String, PaymentLinkProductDto> dictMap =
                baseFeign.queryDictAllValue(DictionaryTypeEnum
                        .PAYMENT_LINK_PRODUCT, PaymentLinkProductDto.class, countryCode.getCode());

        final Map<ProductCodeEnum, String> productMap = new HashMap<>();
        dictMap.forEach((k, v) -> productMap.put(v.getProductCode(), k));

        return productCodes.stream()
                .map(e -> productMap.getOrDefault(e, e.getCode()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<PaymentLinkInstallmentsPlanDto> queryInstallmentsPlan(
            final QueryInstallmentsPlanVo vo) {
        final AccountDto accountDto =
                accountService.queryUniqueAccount(QueryUniqueAccountVo.builder()
                        .countryCode(vo.getCountryCode())
                        .merchantId(vo.getMerchantId())
                        .transactionTypeCode(vo.getTransactionTypeCode())
                        .build());

        if (!(Optional.ofNullable(accountDto)
                .map(AccountDto::getAccountConfig)
                .map(AccountConfigDto::getConfigData)
                .map(AccountConfigData::getPaymentLinkConfig)
                .map(PaymentLinkConfigBo::getInstallments).orElse(false))) {
            return null;
        }

        final PaymentConfigBo paymentConfig =
                modelMapper.convertBo(baseService.queryPaymentConfig(accountDto.getId()));

        log.info("payment link installments list paymentConfig id={}", paymentConfig.getId());

        /** get access token */
        final PaymentTokenDto authDto = paymentTokenManager.getPaymentAccessToken();
        try {
            final Map<String, String> headers = new HashedMap(3);
            headers.put("Content-Type", ContentType.APPLICATION_JSON.toString());
            headers.put(PaymentConstant.HEADER_AUTHORIZATION,
                    authDto.getTokenType().concat(" ").concat(authDto.getAccessToken()));
            headers.put(PaymentConstant.HEADER_X_API_KEY, paymentConfig.getApiKey());

            final List<PaymentLinkInstallmentsPlanDto> responseDto =
                    OkHttpClientUtil.doGet(installmentPlanApiUrl, headers,
                            new TypeReference<>() {
                            });

            if (Objects.isNull(responseDto)) {
                throw StatementExceptionCode.GET_INSTALLMENTS_PLAN_FAIL.exception();
            }
            return responseDto.stream().filter(v -> v.getType().equals("INSTALLMENT"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("get installments plan error", e);
            throw StatementExceptionCode.GET_INSTALLMENTS_PLAN_FAIL.exception();
        }

    }

    @Override
    public Boolean installmentsPlanCheck(final InstallmentsPlanCheckVo vo) {
        final AccountDto accountDto =
                accountService.queryUniqueAccount(QueryUniqueAccountVo.builder()
                        .countryCode(vo.getCountryCode())
                        .merchantId(vo.getMerchantId())
                        .transactionTypeCode(vo.getTransactionTypeCode())
                        .build());

        return Optional.ofNullable(accountDto)
                .map(AccountDto::getAccountConfig)
                .map(AccountConfigDto::getConfigData)
                .map(AccountConfigData::getPaymentLinkConfig)
                .map(PaymentLinkConfigBo::getInstallments).orElse(false);
    }
}
