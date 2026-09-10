package com.liquido.transaction.events.handle.exchange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.PaymentChannelEnum;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.StatementApis;
import com.liquido.statement.pojo.vo.OfflineExchangeVo;
import com.liquido.transaction.enums.ApprovalBizExchangeStatusEnum;
import com.liquido.transaction.enums.ApprovalStatusEnum;
import com.liquido.transaction.enums.ExchangeAccountTypeEnum;
import com.liquido.transaction.pojo.bo.ApprovalBizExchangeConfigBo;
import com.liquido.transaction.pojo.bo.ApprovalBizHandleBo;
import com.liquido.transaction.pojo.bo.MerchantRatioBo;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizExchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApprovalBizExchangeOfflineCompleteHandle implements BaseApprovalBizExchangeHandle {

    private final StatementApis.StatementFeign statementFeign;

    @Override
    public boolean isSupport(final Approval approval, final ApprovalBizExchange exchange) {
        return ApprovalStatusEnum.COMPLETED == approval.getStatus()
                && ApprovalBizExchangeStatusEnum.COMPLETED != exchange.getStatus()
                && ExchangeAccountTypeEnum.OFFLINE == exchange.getExchangeAccountType();
    }

    @Override
    public ApprovalBizHandleBo<ApprovalBizExchangeStatusEnum> handle(
            final Approval approval,
            final ApprovalBizExchange exchange,
            final ApprovalBizExchangeConfigBo exchangeConfigBo) {

        boolean executeResult = false;
        String message;
        log.info("approval biz exchange offline complete request statement exchange={}", exchange);
        try {
            final Map<CurrencyEnum, MerchantRatioBo> ratioBoMap =
                    getRatioBoMap(statementFeign, exchange);
            final ResponseDto<Void> responseDto =
                    statementFeign.offlineExchange(OfflineExchangeVo.builder()
                            .requestId(String.valueOf(SnowflakeIdUtil.generate()))
                            .accountId(exchange.getAccountId())
                            .paymentChannel(PaymentChannelEnum.getDefaultChannel(
                                    exchange.getCountryCode()))
                            .transactionAmount(exchange.getExchangeAmount())
                            .transactionAmountUsd(convertToUsd(exchange, ratioBoMap,
                                    exchange.getActualExchangeAmountTargetCurrency()))
                            .transactionCurrency(exchange.getExchangeCurrency())
                            .exchangeRate(exchange.getMerchantRate())
                            .exchangeLose(exchange.getRatioLose())
                            .exchangeRateUsd(exchange.getMerchantRate())
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
                            .build());

            executeResult = responseDto.isSuccess();
            message = responseDto.getMsg();
            if (!executeResult) {
                log.error("approval exchange, statement request error, transfer out failed,"
                        + "errorMessage:{}, ID: {}", message, exchange.getId());
            }
        } catch (Exception e) {
            log.error("approval exchange error, error: ", e);
            message = e.getMessage();
        }

        return executeResult
                ? ApprovalBizHandleBo.success(ApprovalBizExchangeStatusEnum.COMPLETED)
                : ApprovalBizHandleBo.error(ApprovalBizExchangeStatusEnum.FAILED, message);
    }

    public OfflineExchangeVo.BizIncomeInfo buildBizIncomeInfo(
            final ApprovalBizExchange exchange,
            final Map<CurrencyEnum, MerchantRatioBo> ratioBoMap) {

        final BigDecimal currencyRate =
                BigDecimal.ZERO.compareTo(exchange.getCurrencyRate()) == 0
                        ? exchange.getMerchantRate() : exchange.getCurrencyRate();

        final BigDecimal exchangeFee = exchange.getExchangeAmount()
                .divide(currencyRate, RoundingMode.HALF_UP)
                .subtract(exchange.getActualExchangeAmountTargetCurrency());

        final BigDecimal incomeAmount = exchangeFee.multiply(exchange.getCurrencyRate());
        final BigDecimal incomeAmountUsd = CurrencyEnum.USD == exchange.getTargetCurrency()
                ? exchangeFee
                : incomeAmount.divide(
                ratioBoMap.get(CurrencyEnum.USD).getExchangeRate(), RoundingMode.HALF_UP);

        return OfflineExchangeVo.BizIncomeInfo.builder()
                .incomeAmount(incomeAmount)
                .incomeAmountUsd(incomeAmountUsd)
                .build();
    }

}
