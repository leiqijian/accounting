package com.liquido.worker.feign;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.dto.HourlyExchangeRateDto;
import com.liquido.statement.pojo.vo.BatchAddExchangeRateVo;
import com.liquido.statement.pojo.vo.BatchTransactionMoneyVo;
import com.liquido.statement.pojo.vo.DailyExchangeRateVo;
import com.liquido.statement.pojo.vo.ListHourlyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateListVo;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateVo;
import com.liquido.statement.pojo.vo.QueryUniqueAccountVo;
import com.liquido.statement.pojo.vo.RealTimeExchangeRateVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;
import com.liquido.worker.common.Constant;
import com.liquido.worker.exception.WorkerExceptionCode;
import com.liquido.worker.pojo.bo.TransactionMoneyBo;
import com.liquido.worker.pojo.mapper.ModelMapper;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementService {
    private final ModelMapper modelMapper;
    private final RedisCacheUtil redisCacheUtil;
    private final StatementApis.StatementFeign statementFeign;

    private static String buildCacheKey(final Long merchantId,
                                        final CountryCodeEnum countryCode,
                                        final TransactionTypeCodeEnum transactionTypeCode) {

        return String.join(":", merchantId.toString(), countryCode.getCode(),
                transactionTypeCode.getCode());
    }

    public AccountDto queryMerchantAccount(final Long merchantId,
                                           final CountryCodeEnum countryCode,
                                           final TransactionTypeCodeEnum transactionTypeCode) {
        log.info("queryMerchantAccount merchantId={}, countryCode={}, transactionTypeCode={}",
                merchantId, countryCode, transactionTypeCode);
        if (Objects.isNull(merchantId) || Objects.isNull(countryCode)
                || Objects.isNull(transactionTypeCode)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_BLANK.exception();
        }

        final String cacheKey = buildCacheKey(merchantId, countryCode, transactionTypeCode);
        final AccountDto dto =
                redisCacheUtil.getCacheMapValue(Constant.CACHE.MERCHANT_ACCOUNT_HASH, cacheKey);
        if (Objects.nonNull(dto)) {
            return dto;
        }

        final ResponseDto<AccountDto> responseDto =
                statementFeign.queryUniqueAccount(QueryUniqueAccountVo.builder()
                        .merchantId(merchantId)
                        .countryCode(countryCode)
                        .transactionTypeCode(transactionTypeCode)
                        .build());
        if (ResponseDto.isFail(responseDto) || Objects.isNull(responseDto.getData())) {
            throw CommonExceptionCode.DATA_NOT_FOUND.exception();
        }

        redisCacheUtil.setCacheMapValue(Constant.CACHE.MERCHANT_ACCOUNT_HASH, cacheKey,
                responseDto.getData());

        return responseDto.getData();
    }

    public ResponseDto<Void> batchSettlementTradeOrder(
            final String requestId,
            final List<TransactionMoneyBo> orderList) {

        final long begin = System.currentTimeMillis();
        if (CollectionUtils.isEmpty(orderList)) {
            throw CommonExceptionCode.PARAMETER_ILLEGAL_NULL.exception();
        }

        try {
            final List<TransactionMoneyVo> transactionMoneyList =
                    this.buildTransactionVo(requestId, orderList);

            log.info("batch process transaction settlement begin, requestId={}", requestId);
            final ResponseDto<Void> responseDto = statementFeign.batchTransactionSettlement(
                    BatchTransactionMoneyVo.builder()
                            .requestId(requestId)
                            .accountId(transactionMoneyList.get(0).getAccountId())
                            .transactionMoneyList(transactionMoneyList).build());
            log.info("batch process transaction statement end, ts={}ms, ResponseDto={}",
                    (System.currentTimeMillis() - begin), responseDto);

            if (ResponseDto.isFail(responseDto)) {
                throw WorkerExceptionCode.reException(responseDto);
            }

            return responseDto;
        } catch (Exception e) {
            log.error("batch process statement fail, error={}", e);
            throw e;
        }
    }

    private List<TransactionMoneyVo> buildTransactionVo(
            final String requestId,
            final List<TransactionMoneyBo> orderList) {

        final List<TransactionMoneyVo> dataList = Lists.newArrayList();
        for (final TransactionMoneyBo transactionOrder : orderList) {
            if (CollectionUtils.isEmpty(transactionOrder.getAdditionalCharge())) {
                transactionOrder.setAdditionalCharge(Lists.newArrayList());
            }

            final TransactionMoneyVo vo = modelMapper.convert(transactionOrder);
            vo.setRequestId(requestId);
            dataList.add(vo);
        }

        return dataList;
    }

    public List<AccountDto> queryAllAccount() {
        final ResponseDto<List<AccountDto>> responseDto = statementFeign.queryAllAccount();
        CheckResponseUtil.checkResponse(responseDto);
        return Optional.ofNullable(responseDto.getData()).orElse(List.of());
    }

    public void saveExchangeRate(final LocalDateTime obtainTime,
                                 final List<RealTimeExchangeRateVo> realTimeExchangeRateList,
                                 final List<DailyExchangeRateVo> accountExchangeRateList) {
        if (CollectionUtils.isEmpty(realTimeExchangeRateList)
                && CollectionUtils.isEmpty(accountExchangeRateList)) {
            return;
        }

        final ResponseDto<Void> responseDto = statementFeign.batchAddDailyExchangeRate(
                BatchAddExchangeRateVo.builder()
                        .obtainTime(obtainTime)
                        .realTimeExchangeRateList(realTimeExchangeRateList)
                        .accountExchangeRateList(accountExchangeRateList).build()
        );

        if (ResponseDto.isFail(responseDto)) {
            log.error("save exchange rate fail, ResponseDto: {}", responseDto);
            throw CommonExceptionCode.SYSTEM_SERVICE_ERROR.exception();
        }
    }

    public DailyExchangeRateDto queryDailyExchangeRate(final QueryDailyExchangeRateVo vo) {

        final ResponseDto<DailyExchangeRateDto> responseDto =
                statementFeign.queryDailyExchangeRate(vo);

        if (ResponseDto.isFail(responseDto)) {
            log.error("get exchange rate fail, ResponseDto: {}", responseDto);
            throw CommonExceptionCode.SYSTEM_SERVICE_ERROR.exception();
        }

        return responseDto.getData();
    }

    public List<DailyExchangeRateDto> queryDailyExchangeRate(final List<Long> accountIds,
                                                             final LocalDateTime startTime,
                                                             final LocalDateTime endTime) {
        final ResponseDto<List<DailyExchangeRateDto>> listResponseDto =
                statementFeign.queryDailyExchangeRateForList(QueryDailyExchangeRateListVo.builder()
                        .accountIds(accountIds).beginTime(startTime).endTime(endTime).build());
        CheckResponseUtil.checkResponse(listResponseDto);
        return listResponseDto.getData();
    }

    public List<HourlyExchangeRateDto> queryHourlyExchangeRate(final ListHourlyExchangeRateVo vo) {

        final ResponseDto<List<HourlyExchangeRateDto>> responseDto =
                statementFeign.listHourlyExchangeRate(vo);

        if (ResponseDto.isFail(responseDto)) {
            log.error("get hourly exchange rate fail, ResponseDto: {}", responseDto);
            throw CommonExceptionCode.SYSTEM_SERVICE_ERROR.exception();
        }

        return responseDto.getData();
    }

}
