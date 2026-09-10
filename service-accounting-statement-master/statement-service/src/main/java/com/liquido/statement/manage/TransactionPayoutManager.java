package com.liquido.statement.manage;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.liquido.base.enums.OperationMethodEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.base.pojo.dto.PaymentConfigDto;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.JsonUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.enums.PaymentTransactionStatusEnum;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountBo;
import com.liquido.statement.pojo.bo.PaymentConfigBo;
import com.liquido.statement.pojo.dto.PaymentPayoutDto;
import com.liquido.statement.pojo.dto.payment.BasePayoutResult;
import com.liquido.statement.pojo.dto.payment.QueryPayoutResultDto;
import com.liquido.statement.pojo.entity.TransactionPayout;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.pojo.vo.BatchTransactionPayoutVo;
import com.liquido.statement.pojo.vo.PaymentPayoutVo;
import com.liquido.statement.pojo.vo.QueryPayoutResultVo;
import com.liquido.statement.pojo.vo.TransactionPayoutVo;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.TransactionPayoutService;
import com.liquido.statement.service.payment.PaymentPayoutFactory;
import com.liquido.statement.service.payment.convert.PayoutResultConvert;
import com.liquido.statement.service.payment.convert.PayoutResultConvertFactory;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

/**
 * TransactionPayoutManage:
 * Call the trading system to initiate the PAY-OUT operation(the account amount does not change)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionPayoutManager {
    private final ModelMapper modelMapper;
    private final BaseService baseService;
    private final RedisDistLock redisDistLock;
    private final AccountService accountService;
    private final PaymentPayoutFactory paymentPayoutFactory;
    private final PayoutResultConvertFactory convertFactory;
    private final TransactionPayoutService transactionPayoutService;

    public PaymentPayoutDto executePayout(final PaymentPayoutVo order) {
        final TransactionPayoutVo payoutVo = TransactionPayoutVo.builder()
                .uniqueId(order.getUniqueId())
                .paymentChannel(order.getPaymentChannel())
                .amount(order.getAmount())
                .currency(order.getCurrency())
                .targetInfo(order.getTargetInfo())
                .remark(order.getRemark())
                .build();

        final List<PaymentPayoutDto> result = this.executePayout(
                BatchTransactionPayoutVo.builder()
                        .requestId(order.getRequestId())
                        .accountId(order.getAccountId())
                        .payoutList(Lists.newArrayList(payoutVo))
                        .build());

        if (CollectionUtils.isEmpty(result)) {
            throw StatementExceptionCode.PAYMENT_PAYOUT_FAIL.exception();
        }

        return result.get(0);
    }

    public List<PaymentPayoutDto> executePayout(final BatchTransactionPayoutVo order) {
        /* Pre-check repeat submit (prevent duplicate submissions) */
        this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER,
                order.getRequestId(), 1, TimeUnit.DAYS);

        if (CollectionUtils.isEmpty(order.getPayoutList())) {
            return Collections.emptyList();
        }

        // Build data
        final List<TransactionPayout> entryList = this.buildTransactionPayoutList(order);
        // Batch save order
        final List<TransactionPayout> payoutList = transactionPayoutService.batchSave(entryList);

        return payoutList.stream().map(this::processPayout).collect(Collectors.toList());
    }

    private List<TransactionPayout> buildTransactionPayoutList(
            final BatchTransactionPayoutVo order) {

        final List<TransactionPayout> entryList = Lists.newArrayList();
        final AccountBo account = accountService.findById(order.getAccountId());

        for (final TransactionPayoutVo item : order.getPayoutList()) {
            final PaymentConfigBo paymentConfig =
                    this.getPaymentConfig(account.getId(), item.getPaymentChannel());

            entryList.add(TransactionPayout.builder()
                    .id(SnowflakeIdUtil.generate())
                    .uniqueId(item.getUniqueId())
                    .accountId(account.getId())
                    .paymentConfig(paymentConfig)
                    .countryCode(account.getCountryCode())
                    .paymentChannel(paymentConfig.getPaymentChannel())
                    .amount(item.getAmount())
                    .currency(item.getCurrency())
                    .targetInfo(item.getTargetInfo())
                    .transactionStatus(PaymentTransactionStatusEnum.PENDING)
                    .delayExecuteTime(LocalDateTimeUtil.nowUtc())
                    .createdTime(LocalDateTimeUtil.nowUtc())
                    .version(1)
                    .delFlag(Boolean.FALSE)
                    .remark(item.getRemark()).build());
        }

        return entryList;
    }

    private PaymentPayoutDto processPayout(final TransactionPayout payout) {
        try {
            if (payout.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw StatementExceptionCode.PAYMENT_AMOUNT_ERROR.exception();
            }
            return PaymentPayoutDto.builder()
                    .statusCode(200)
                    .transferStatusCode(200)
                    .uniqueId(payout.getUniqueId())
                    .transferStatus(PaymentTransactionStatusEnum.PENDING.getCode())
                    .build();

        } catch (Exception e) {
            log.error("TransactionPayoutManager.processPayout error: uniqueId={}",
                    payout.getUniqueId(), e);
            payout.setTransactionStatus(PaymentTransactionStatusEnum.FAILED);
            transactionPayoutService.save(payout);

            return PaymentPayoutDto.builder()
                    .uniqueId(payout.getUniqueId())
                    .statusCode(500)
                    .errorMsg(e.getMessage())
                    .transferStatus(PaymentTransactionStatusEnum.FAILED.getCode())
                    .transferStatusCode(500).transferErrorMsg(e.getMessage()).build();
        }
    }

    public QueryPayoutResultDto queryPayoutResult(final QueryPayoutResultVo vo) {
        final TransactionPayout payout = transactionPayoutService.findByUniqueId(vo.getUniqueId());

        final BasePayoutResult result =
                paymentPayoutFactory.getProviderFactory(payout.getPaymentChannel())
                        .queryPayoutResult(payout);

        final QueryPayoutResultDto queryResult = this.convertResult(result);

        if (PaymentTransactionStatusEnum.IN_PROGRESS == payout.getTransactionStatus()
                && PaymentTransactionStatusEnum.IN_PROGRESS
                != PaymentTransactionStatusEnum.parse(queryResult.getTransferStatus())) {
            payout.setTransactionStatus(
                    PaymentTransactionStatusEnum.parse(queryResult.getTransferStatus()));

            // update status;
            payout.setSettleTime(queryResult.getFinalStatusTime());
            payout.setUpdatedTime(LocalDateTimeUtil.nowUtc());
            payout.setPaymentResponse(JsonUtil.toJson(result));
            transactionPayoutService.save(payout);
        }

        return queryResult;
    }

    private QueryPayoutResultDto convertResult(final BasePayoutResult dto) {
        final Pair<PayoutResultConvert<? extends BasePayoutResult>, Class<?>> pair =
                convertFactory.getConvertProvider(dto.getProductCode());

        final PayoutResultConvert resultConvert = pair.getLeft();
        return resultConvert.convertResult(dto);
    }


    private PaymentConfigBo getPaymentConfig(final Long accountId,
                                             final PaymentChannelEnum paymentChannel) {
        final PaymentConfigDto paymentConfig =
                baseService.queryPaymentConfig(accountId, paymentChannel,
                        OperationMethodEnum.MANUAL);
        if (Objects.isNull(paymentConfig)) {
            log.error("Manual payment config undefined, accountId={}, paymentChannel={}", accountId,
                    paymentChannel.getCode());
            throw StatementExceptionCode.PAYMENT_CONFIG_UNDEFINED.exception();
        }

        return modelMapper.convertBo(paymentConfig);
    }
}
