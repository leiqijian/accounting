package com.liquido.statement.feign;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.liquido.base.BaseApis;
import com.liquido.base.enums.FeeGroupEnum;
import com.liquido.base.enums.OperationMethodEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.base.pojo.dto.ApmCostConfigDto;
import com.liquido.base.pojo.dto.ApmCostConfigurationDto;
import com.liquido.base.pojo.dto.CardCostConfigurationDto;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.base.pojo.dto.MonthlyFeeConfigurationDto;
import com.liquido.base.pojo.dto.PaymentConfigDto;
import com.liquido.base.pojo.dto.SubMerchantDto;
import com.liquido.base.pojo.vo.ApmCostConfigVo;
import com.liquido.base.pojo.vo.EmailAttachmentMessageVo;
import com.liquido.base.pojo.vo.EmailMessageVo;
import com.liquido.base.pojo.vo.FileBytesVo;
import com.liquido.base.pojo.vo.ListMerchantVo;
import com.liquido.base.pojo.vo.QueryApmCostConfigVo;
import com.liquido.base.pojo.vo.QueryMerchantVo;
import com.liquido.base.pojo.vo.QueryPaymentConfigVo;
import com.liquido.base.pojo.vo.QuerySubMerchantListVo;
import com.liquido.base.pojo.vo.QuerySubMerchantVo;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.exception.StatementExceptionCode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class BaseService {
    private final RedisCacheUtil redisCacheUtil;
    private final BaseApis.BaseFeign baseFeign;

    public PaymentConfigDto queryPaymentConfig(final Long accountId) {
        final ResponseDto<PaymentConfigDto> responseDto = baseFeign.queryPaymentConfig(
                QueryPaymentConfigVo.builder().accountId(accountId).build());
        if (ResponseDto.isFail(responseDto) || Objects.isNull(responseDto.getData())) {
            log.error("get payment-link config fail, accountId={}", accountId);
            throw StatementExceptionCode.PAYMENT_CONFIG_UNDEFINED.exception();
        }

        return responseDto.getData();
    }

    public PaymentConfigDto queryPaymentConfig(final Long accountId,
                                               final PaymentChannelEnum paymentChannel,
                                               final OperationMethodEnum operationMethod) {

        final String cacheKey = operationMethod.name()
                .concat(Objects.nonNull(paymentChannel) ? (":" + paymentChannel.getCode()) : "")
                .concat(":").concat(accountId.toString());
        final PaymentConfigDto dto =
                redisCacheUtil.getCacheMapValue(CacheConstant.PAYMENT_CONFIG_HASH, cacheKey);
        if (Objects.nonNull(dto)) {
            return dto;
        }

        final QueryPaymentConfigVo config = new QueryPaymentConfigVo();
        config.setAccountId(accountId);
        config.setOperationMethod(operationMethod);
        if (Objects.nonNull(paymentChannel)) {
            config.setPaymentChannel(paymentChannel);
        }

        final ResponseDto<PaymentConfigDto> responseDto = baseFeign.queryPaymentConfig(config);
        if (ResponseDto.isFail(responseDto)) {
            log.warn("get payment config fail, accountId={}, productCode={}, operationMethod={}",
                    accountId, paymentChannel, operationMethod);
            return null;
        }

        redisCacheUtil.setCacheMapValue(CacheConstant.PAYMENT_CONFIG_HASH, cacheKey,
                responseDto.getData());
        redisCacheUtil.expire(CacheConstant.PAYMENT_CONFIG_HASH, 4, TimeUnit.HOURS);

        return responseDto.getData();
    }

    @Async("monitorExecutor")
    public void sendMail(final String title,
                         final List<String> mailTo,
                         final String content) {
        final EmailMessageVo mail = new EmailMessageVo();
        mail.setSubject(title);
        mail.setTo(mailTo);
        mail.setContent(content);

        final ResponseDto<String> responseDto = baseFeign.sendHtmlTextMessage(mail);
        if (ResponseDto.isFail(responseDto)) {
            throw StatementExceptionCode.reException(responseDto);
        }
    }

    @Async("monitorExecutor")
    public void sendMail(final String title,
                         final List<String> mailTo,
                         final String content,
                         final List<FileBytesVo> attachments) {

        final EmailAttachmentMessageVo mail = new EmailAttachmentMessageVo();
        mail.setSubject(title);
        mail.setTo(mailTo);
        mail.setContent(content);
        mail.setHtml(true);
        mail.setFileBytesList(attachments);

        final ResponseDto<String> responseDto = baseFeign.sendWithAttachmentMessage(mail);
        if (ResponseDto.isFail(responseDto)) {
            throw StatementExceptionCode.reException(responseDto);
        }
    }

    @Cacheable(cacheNames = "LOCAL:30s#REDIS:-1", key = "'MERCHANT_INFO:ID:' + #merchantId")
    public MerchantDto getMerchantById(final Long merchantId) {
        if (Objects.isNull(merchantId)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
        }

        final ResponseDto<MerchantDto> responseDto = baseFeign.getEffectiveMerchantInfo(
                QueryMerchantVo.builder().id(merchantId).build()
        );

        if (responseDto.isFail() || Objects.isNull(responseDto.getData())) {
            throw StatementExceptionCode.reException(responseDto);
        }

        return responseDto.getData();
    }


    @Cacheable(cacheNames = "LOCAL:30s#REDIS:-1", key = "'MERCHANT_INFO:CODE:' + #merchantCode")
    public MerchantDto getMerchantByCode(final String merchantCode) {
        if (StringUtils.isBlank(merchantCode)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
        }
        return CheckResponseUtil.checkAndReturnResponseData(
                baseFeign.getEffectiveMerchantInfo(
                        QueryMerchantVo.builder().code(merchantCode).build()));
    }

    @CacheEvict(cacheNames = "LOCAL:30s#REDIS:-1", key = "'MERCHANT_INNER_INFO'")
    public void cleanInnerMerchantCache() {
    }

    @Cacheable(cacheNames = "LOCAL:30s#REDIS:-1", key = "'MERCHANT_INNER_INFO'",
            unless = "#result==null || #result.isEmpty()")
    public List<MerchantDto> getInnerMerchant() {
        final ResponseDto<List<MerchantDto>> responseDto = baseFeign.listMerchant(
                ListMerchantVo.builder().innerFlag(true).build());

        if (responseDto.isFail() || Objects.isNull(responseDto.getData())) {
            throw StatementExceptionCode.reException(responseDto);
        }

        return responseDto.getData();
    }

    public ApmCostConfigurationDto loadApmCostConfig(final Integer activeVersion) {
        final ResponseDto<ApmCostConfigurationDto> responseDto =
                baseFeign.queryApmCostConfig(QueryApmCostConfigVo.builder()
                        .activeVersion(Optional.ofNullable(activeVersion).orElse(-1)).build());
        if (ResponseDto.isFail(responseDto)) {
            log.error("query apm cost config fail, responseDto={}", responseDto);
            throw CommonExceptionCode.SYSTEM_SERVICE_ERROR.exception();
        }

        return responseDto.getData();
    }

    public List<ApmCostConfigDto> getApmCostConfig(
            final List<ApmCostConfigDto> configList,
            final ApmCostConfigVo vo) {

        if (CollectionUtils.isEmpty(configList)) {
            return Collections.emptyList();
        }

        final List<ApmCostConfigDto> resultList = Lists.newArrayList();

        /* Customize(FEE) config */
        final List<ApmCostConfigDto> costFeeList = configList.stream()
                .filter(item -> item.getAccountId().equals(vo.getAccountId())
                        && item.getVendorCode() == vo.getVendor()
                        && item.getProductCode() == vo.getProductCode()
                        && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup())
                .collect(Collectors.toList());

        /* if no customize FEE, then use default apm config */
        if (CollectionUtils.isEmpty(costFeeList)) {
            costFeeList.addAll(configList.stream()
                    .filter(item -> item.getAccountId().equals(0L)
                            && item.getCountryCode() == vo.getCountry()
                            && item.getTransactionTypeCode() == vo.getTransactionType()
                            && item.getVendorCode() == vo.getVendor()
                            && item.getProductCode() == vo.getProductCode()
                            && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup())
                    .collect(Collectors.toList()));
        }

        /* Customize FEE(Shopify Fee) config */
        costFeeList.addAll(configList.stream()
                .filter(item -> item.getAccountId().equals(vo.getAccountId())
                        && Objects.isNull(item.getVendorCode())
                        && Objects.isNull(item.getProductCode())
                        && FeeGroupEnum.TRANSACTION_FEE == item.getFeeGroup())
                .collect(Collectors.toList()));

        /* Customize(TAX, FX) config */
        final List<ApmCostConfigDto> costTaxFxList = configList.stream()
                .filter(item -> vo.getAccountId().equals(item.getAccountId())
                        && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup()))
                .collect(Collectors.toList());

        /* if no customize(TAX, FX), then use default tax fx config */
        if (CollectionUtils.isEmpty(costTaxFxList)) {
            costTaxFxList.addAll(configList.stream()
                    .filter(item -> item.getAccountId().equals(0L)
                            && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup())
                            && item.getCountryCode() == vo.getCountry()
                            && item.getTransactionTypeCode() == vo.getTransactionType()
                            && Objects.isNull(item.getVendorCode())
                            && Objects.isNull(item.getProductCode()))
                    .collect(Collectors.toList()));
        }

        /* Cost adjustment(TAX, FX) config */
        costTaxFxList.addAll(configList.stream()
                .filter(item -> item.getAccountId().equals(0L)
                        && FeeGroupEnum.TAX_FX_GROUP.contains(item.getFeeGroup())
                        && item.getCountryCode() == vo.getCountry()
                        && item.getTransactionTypeCode() == vo.getTransactionType()
                        && item.getVendorCode() == vo.getVendor()
                        && (item.getProductCode() == vo.getProductCode()))
                .collect(Collectors.toList()));

        resultList.addAll(costFeeList);
        resultList.addAll(costTaxFxList);

        // deduplication
        final List<ApmCostConfigDto> dataList = Lists.newArrayList(resultList.stream()
                .collect(Collectors.toMap(ApmCostConfigDto::getId,
                        Function.identity(), (x, y) -> x)).values());
        // sort by id
        dataList.sort(Comparator.comparing(ApmCostConfigDto::getId));

        return dataList;
    }

    /**
     * payin card(credit-card, debit-card) transaction cost config
     *
     * @return
     */
    public CardCostConfigurationDto getCardCostConfig() {

        final ResponseDto<CardCostConfigurationDto> responseDto = baseFeign.queryCardCostConfig();
        if (ResponseDto.isFail(responseDto)) {
            log.error("get payin card transaction cost config fail:{}", responseDto);
            throw CommonExceptionCode.SYSTEM_SERVICE_ERROR.exception();
        }

        return responseDto.getData();
    }

    public List<Long> queryFeeOnFeeMonthlyConfigByFeeIds(List<Long> monthlyFeeConfigIds) {
        final ResponseDto<List<MonthlyFeeConfigurationDto>> listResponseDto =
                baseFeign.queryByIds(monthlyFeeConfigIds);
        if (listResponseDto.isFail() || CollectionUtils.isEmpty(listResponseDto.getData())) {
            return Collections.emptyList();
        }

        return listResponseDto.getData().stream().map(MonthlyFeeConfigurationDto::getId)
                .collect(Collectors.toList());
    }


    public List<SubMerchantDto> querySubMerchantList(final Long merchantId) {
        final ResponseDto<List<SubMerchantDto>> listResponseDto =
                baseFeign.querySubMerchantListByMerchantId(QuerySubMerchantVo.builder()
                        .merchantId(merchantId)
                        .build());

        if (ResponseDto.isFail(listResponseDto)
                || CollectionUtils.isEmpty(listResponseDto.getData())) {
            return Collections.emptyList();
        }

        return listResponseDto.getData();
    }


    public List<SubMerchantDto> querySubMerchantIdByMerchantId(
            final Long merchantId,
            Set<String> subMerchantIds) {

        subMerchantIds =
                subMerchantIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());

        if (org.apache.commons.collections4.CollectionUtils.isEmpty(subMerchantIds)) {
            return Collections.emptyList();
        }

        final String cacheKey = String.format("%s:%s", "SUB_MERCHANT", merchantId);
        final List<SubMerchantDto> resultList =
                redisCacheUtil.getMultiCacheMapValue(cacheKey, subMerchantIds);
        final Set<String> existSubMerchantIds = resultList.stream()
                .map(SubMerchantDto::getSubMerchantId).collect(Collectors.toSet());
        final Set<String> needReloadIds = Sets.difference(subMerchantIds, existSubMerchantIds);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(needReloadIds)) {
            return resultList;
        }
        final ResponseDto<List<SubMerchantDto>> subMerchantResp =
                baseFeign.queryBySubMerchantId(QuerySubMerchantListVo.builder()
                        .merchantId(merchantId)
                        .subMerchantId(needReloadIds)
                        .build());

        if (subMerchantResp.isFail() || Objects.isNull(subMerchantResp.getData())) {
            log.error("get sub merchant data fail:{}", subMerchantResp);
            throw CommonExceptionCode.SYSTEM_SERVICE_ERROR.exception();
        }

        final List<SubMerchantDto> dataList = subMerchantResp.getData();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(dataList)) {
            final Map<String, SubMerchantDto> dtoMap = dataList.stream()
                    .collect(Collectors.toMap(
                            SubMerchantDto::getSubMerchantId,
                            Function.identity(), (x, y) -> y));

            redisCacheUtil.setCacheMap(cacheKey, dtoMap);
            resultList.addAll(dataList);
        }
        return resultList;
    }

}
