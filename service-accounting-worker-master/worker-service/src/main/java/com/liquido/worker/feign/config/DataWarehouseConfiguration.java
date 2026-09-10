package com.liquido.worker.feign.config;


import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.worker.common.Constant;
import com.liquido.worker.common.properties.WorkerProperties;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.feign.DataWarehouseFeign;
import com.liquido.worker.pojo.bo.DwPage;
import com.liquido.worker.pojo.bo.DwResponse;
import com.liquido.worker.pojo.dto.DwAuthDto;
import com.liquido.worker.pojo.dto.DwMetricSuccessRateDto;
import com.liquido.worker.pojo.dto.DwMetricTransactionDto;
import com.liquido.worker.pojo.dto.DwPageTransactionDto;
import com.liquido.worker.pojo.dto.DwQueryTransactionDto;
import com.liquido.worker.pojo.dto.DwSyncPaymentLinkDto;
import com.liquido.worker.pojo.dto.DwSyncShopifyDto;
import com.liquido.worker.pojo.dto.DwSyncShoplazzaDto;
import com.liquido.worker.pojo.dto.DwSyncTokenizationDto;
import com.liquido.worker.pojo.dto.DwSyncTransactionDto;
import com.liquido.worker.pojo.vo.DwAdvancedQueryTransactionVo;
import com.liquido.worker.pojo.vo.DwPageTransactionVo;
import com.liquido.worker.pojo.vo.DwQueryTransactionVo;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
public class DataWarehouseConfiguration implements FallbackFactory<DataWarehouseFeign> {

    private final WorkerProperties.DataWarehouseProperties dataWarehouseProperties;
    private final RedisCacheUtil redisCacheUtil;
    private final RestTemplate restTemplate;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {

            final Map<String, WorkerProperties.DataWarehouseEnvProperties> envMap =
                    dataWarehouseProperties.getEnv();

            if (Objects.isNull(envMap) || envMap.isEmpty()) {
                log.warn("Dw config is Empty");
                throw WorkerExceptionCode.DW_REQUEST_ERROR.exception();
            }

            final String env = template.headers().get("Env").stream().findAny().get();
            if (StringUtils.isBlank(env)) {
                log.warn("Dw request, Header Default-Env is empty");
                throw WorkerExceptionCode.DW_REQUEST_ERROR.exception();
            }

            if (!envMap.containsKey(env)) {
                log.warn("Dw env config doesn't have this env:" + env);
                throw WorkerExceptionCode.DW_REQUEST_ERROR.exception();
            }

            final WorkerProperties.DataWarehouseEnvProperties properties = envMap.get(env);
            template.target(properties.getUrl());

            if (!Optional.ofNullable(properties.getAuthNeed()).orElse(false)) {
                return;
            }
            final String key = String.format(Constant.CACHE.DW_AUTH_TOKEN, env);
            DwAuthDto dwAuthDto = redisCacheUtil.getCacheObject(key);
            if (Objects.isNull(dwAuthDto)) {
                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                params.add("grant_type", properties.getAuthGrantType());
                params.add("client_id", properties.getAuthClientId());
                params.add("client_secret", properties.getAuthClientSecret());
                final HttpEntity<MultiValueMap<String, Object>> httpEntity =
                        new HttpEntity<>(params, headers);
                dwAuthDto = restTemplate.exchange(properties.getAuthUrl(),
                        HttpMethod.POST, httpEntity, DwAuthDto.class).getBody();
                redisCacheUtil.setCacheObject(key, dwAuthDto,
                        dwAuthDto.getExpiresIn() - 300, TimeUnit.SECONDS);
            }

            template.header("Authorization",
                    dwAuthDto.getTokenType() + " " + dwAuthDto.getAccessToken());
        };
    }

    @Override
    public DataWarehouseFeign create(final Throwable cause) {
        return new DataWarehouseFeign() {
            @Override
            public DwResponse<DwPage<DwPageTransactionDto>> pageTransaction(
                    TransactionTypeCodeEnum env, Boolean tryAllEnvFlag,
                    DwPageTransactionVo vo) {
                return commonHandle(cause);
            }

            @Override
            public DwResponse<DwPage<String>> advancedPageTransaction(
                    TransactionTypeCodeEnum env, Boolean tryAllEnvFlag,
                    DwAdvancedQueryTransactionVo vo) {
                return commonHandle(cause);
            }

            @Override
            public DwResponse<String> queryUniqueId(TransactionTypeCodeEnum env,
                                                    Boolean tryAllEnvFlag,
                                                    String merchantCode,
                                                    String subAccountId,
                                                    String trackingId) {
                return commonHandle(cause);
            }

            @Override
            public DwResponse<DwQueryTransactionDto> queryTransaction(
                    TransactionTypeCodeEnum env, Boolean tryAllEnvFlag,
                    DwQueryTransactionVo vo) {
                return commonHandle(cause);
            }

            @Override
            public DwResponse<DwPage<DwMetricTransactionDto>> metricTransaction(
                    TransactionTypeCodeEnum env, Boolean tryAllEnvFlag, Long from,
                    Long to) {
                return commonHandle(cause);
            }

            @Override
            public DwResponse<DwPage<DwMetricSuccessRateDto>> metricSuccessRate(
                    TransactionTypeCodeEnum env, Boolean tryAllEnvFlag, Long from,
                    Long to) {
                return commonHandle(cause);
            }

            @Override
            public DwResponse<DwPage<DwSyncTransactionDto>> getTransactionData(
                    TransactionTypeCodeEnum env, Boolean tryAllEnvFlag, Long from,
                    Long to) {
                return commonHandle(cause);
            }

            @Override
            public DwResponse<DwPage<DwSyncTransactionDto>> getTransactionDataByObjectIdTime(
                    TransactionTypeCodeEnum env, Boolean tryAllEnvFlag, Long from,
                    Long to) {
                return commonHandle(cause);
            }

            @Override
            public DwResponse<DwPage<DwSyncPaymentLinkDto>> getPaymentLinkDataByObjectIdTime(
                    TransactionTypeCodeEnum env, Boolean tryAllEnvFlag, Long from,
                    Long to) {
                return commonHandle(cause);
            }

            @Override
            public DwResponse<DwPage<DwSyncShopifyDto>> getShopifyDataByObjectIdTime(
                    TransactionTypeCodeEnum env, Boolean tryAllEnvFlag, Long from,
                    Long to) {
                return commonHandle(cause);
            }

            @Override
            public DwResponse<DwPage<DwSyncShoplazzaDto>> getShoplazzaDataByObjectIdTime(
                    TransactionTypeCodeEnum env, Boolean tryAllEnvFlag, Long from,
                    Long to) {
                return commonHandle(cause);
            }

            @Override
            public DwResponse<DwPage<DwSyncTokenizationDto>> getTokenDataByObjectIdTime(
                    TransactionTypeCodeEnum env, Boolean tryAllEnvFlag, Long from,
                    Long to) {
                return commonHandle(cause);
            }
        };
    }

    private <T> DwResponse<T> commonHandle(final Throwable cause) {
        return DwResponse.<T>builder().code(500).message(cause.getMessage()).data(null).build();
    }

}
