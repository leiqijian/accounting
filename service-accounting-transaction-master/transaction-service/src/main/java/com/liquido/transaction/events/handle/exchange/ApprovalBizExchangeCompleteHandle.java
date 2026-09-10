package com.liquido.transaction.events.handle.exchange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.liquido.base.enums.BusinessTypeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.OperateSourceEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.dto.DailyExchangeRateDto;
import com.liquido.statement.pojo.vo.QueryDailyExchangeRateVo;
import com.liquido.statement.pojo.vo.TransactionBizVo;
import com.liquido.transaction.enums.ApprovalBizExchangeStatusEnum;
import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.enums.ExchangeAccountTypeEnum;
import com.liquido.transaction.pojo.bo.ApprovalBizExchangeConfigBo;
import com.liquido.transaction.pojo.bo.ApprovalBizHandleBo;
import com.liquido.transaction.pojo.bo.MerchantRatioBo;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizExchange;
import com.liquido.transaction.service.monitor.LarkRobotMonitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApprovalBizExchangeCompleteHandle implements BaseApprovalBizExchangeHandle {

    private final StatementApis.StatementFeign statementFeign;
    private final LarkRobotMonitor larkRobotMonitor;

    @Override
    public boolean isSupport(
            final Approval approval,
            final ApprovalBizExchange exchange) {

        return ApprovalStatusEnum.COMPLETED == approval.getStatus()
                && ApprovalBizExchangeStatusEnum.COMPLETED != exchange.getStatus()
                && (ExchangeAccountTypeEnum.PAY_IN == exchange.getExchangeAccountType()
                || ExchangeAccountTypeEnum.PAY_OUT == exchange.getExchangeAccountType());
    }

    @Override
    public ApprovalBizHandleBo<ApprovalBizExchangeStatusEnum> handle(
            final Approval approval,
            final ApprovalBizExchange exchange,
            final ApprovalBizExchangeConfigBo exchangeConfigBo) {

        String message;
        boolean executeResult = false;
        final BigDecimal exchangeRateToUsd = this.getUsdExchangeRate(approval, exchange);

        log.info("approval biz exchange online complete request statement exchange={}", exchange);
        try {
            final Map<CurrencyEnum, MerchantRatioBo> ratioBoMap =
                    getRatioBoMap(statementFeign, exchange);
            final ResponseDto<Void> responseDto =
                    statementFeign.exchangeApprovedPass(TransactionBizVo.builder()
                            .businessType(BusinessTypeEnum.EXCHANGE)
                            .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                            .transactionId(Optional.ofNullable(exchange.getTransactionId())
                                    .orElse(exchange.getId()))
                            .accountId(exchange.getAccountId())
                            .paymentChannel(PaymentChannelEnum.getDefaultChannel(
                                    exchange.getCountryCode()))
                            .transactionAmount(exchange.getExchangeAmount())
                            .operateSource(OperateSourceEnum.ONLINE)
                            .transactionAmountUsd(convertToUsd(exchange, ratioBoMap,
                                    exchange.getActualExchangeAmountTargetCurrency()))
                            .transactionCurrency(exchange.getExchangeCurrency())
                            .exchangeRate(exchange.getMerchantRate())
                            .exchangeLose(exchange.getRatioLose())
                            .exchangeRateUsd(exchangeRateToUsd)
                            .settlementAmount(exchange.getExchangeAmount())
                            .settlementCurrency(exchange.getExchangeCurrency())
                            .settlementAmountUsd(convertToUsd(exchange, ratioBoMap,
                                    exchange.getActualExchangeAmountTargetCurrency()))
                            .feeAmount(exchange.getDeductionFee())
                            .feeAmountUsd(convertToUsd(exchange, ratioBoMap,
                                    exchange.getDeductionFeeTargetCurrency()))
                            .taxAmount(BigDecimal.ZERO)
                            .taxAmountUsd(BigDecimal.ZERO)
                            .incomeInfo(buildBizIncomeInfo(exchange, ratioBoMap))
                            .subMerchantId(exchange.getSubMerchantId())
                            .costInfo(TransactionBizVo.BizCostInfo.builder()
                                    .costFee(exchange.getExtraCost()).build())
                            .build());

            executeResult = responseDto.isSuccess();
            message = responseDto.getMsg();
            if (!executeResult) {
                log.error("approval exchange, statement request error, "
                                + "transfer out failed,errorMessage:{}, ID: {}",
                        message, exchange.getId());
            }
        } catch (Exception e) {
            log.error("approval exchange error, error: ", e);
            message = e.getMessage();
        }

        return executeResult
                ? ApprovalBizHandleBo.success(ApprovalBizExchangeStatusEnum.COMPLETED)
                : ApprovalBizHandleBo.error(ApprovalBizExchangeStatusEnum.FAILED, message);
    }

    private TransactionBizVo.BizIncomeInfo buildBizIncomeInfo(
            final ApprovalBizExchange exchange,
            final Map<CurrencyEnum, MerchantRatioBo> ratioBoMap) {

        final BigDecimal currencyRate = BigDecimal.ZERO.compareTo(exchange.getCurrencyRate()) == 0
                ? exchange.getMerchantRate() : exchange.getCurrencyRate();

        final BigDecimal exchangeFee = exchange.getExchangeAmount()
                .divide(currencyRate, RoundingMode.HALF_UP)
                .subtract(exchange.getActualExchangeAmountTargetCurrency());

        final BigDecimal incomeAmount = exchangeFee.multiply(exchange.getCurrencyRate());
        final BigDecimal incomeAmountUsd = CurrencyEnum.USD == exchange.getTargetCurrency()
                ? exchangeFee
                : incomeAmount.divide(
                ratioBoMap.get(CurrencyEnum.USD).getExchangeRate(), RoundingMode.HALF_UP);

        return TransactionBizVo.BizIncomeInfo.builder()
                .incomeAmount(incomeAmount)
                .incomeAmountUsd(incomeAmountUsd)
                .build();
    }

    private BigDecimal getUsdExchangeRate(
            final Approval approval,
            final ApprovalBizExchange exchange) {

        try {
            if (!CurrencyEnum.USD.equals(exchange.getTargetCurrency())) {
                LocalDateTime createdTime = exchange.getCreatedTime();
                LocalDateTime exchangeTime = createdTime.withMinute(0).withSecond(0).withNano(0);
                ResponseDto<DailyExchangeRateDto> exchangeRateResp =
                        statementFeign.queryDailyExchangeRate(QueryDailyExchangeRateVo.builder()
                                .merchantId(exchange.getMerchantId())
                                .accountId(exchange.getAccountId())
                                .sourceCurrency(CurrencyEnum.USD)
                                .targetCurrency(exchange.getSourceCurrency())
                                .exchangeTime(exchangeTime)
                                .build());

                if (!ResponseDto.isFail(exchangeRateResp)
                        && Objects.nonNull(exchangeRateResp.getData())) {
                    return exchangeRateResp.getData().getExchangeRate();
                }
            }
        } catch (Exception e) {
            log.error("approval exchange get fx rate error, error: ", e);
            final String content = String.format("**Merchant Code:** %s\\n"
                            + "**Country:** %s\\n"
                            + "**Transaction Type:** %s\\n"
                            + "**Account Id:** %s\\n"
                            + "**Approval Status:** %s\\n"
                            + "**Approval Biz Status:** %s\\n"
                            + "**Exchange Time:** %s (UTC+0)\\n"
                            + "**Source Currency:** %s"
                            + "**Target Currency:** %s"
                            + "**Transaction Id:** %s",
                    exchange.getMerchantCode(),
                    exchange.getCountryCode(),
                    exchange.getTransactionTypeCode(),
                    exchange.getAccountId(),
                    approval.getStatus(),
                    exchange.getStatus(),
                    exchange.getCreatedTime().withMinute(0).withSecond(0),
                    CurrencyEnum.USD.getCode(),
                    exchange.getSourceCurrency(),
                    exchange.getTransactionId()
            );

            larkRobotMonitor.warn("Get Exchange Rate Error", content, "");
        }

        return exchange.getMerchantRate();
    }
}

