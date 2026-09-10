package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.properties.StatementProperties;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.TransactionCostExtra;
import com.liquido.statement.pojo.vo.TransactionCostExtraVo;
import com.liquido.statement.repository.TransactionCostExtraRepository;
import com.liquido.statement.service.AccountDailyInitService;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.TransactionCostExtraService;
import com.liquido.statement.service.monitor.lark.LarkRobotMonitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionCostExtraServiceImpl implements TransactionCostExtraService {

    private final BaseService baseService;
    private final AccountService accountService;
    private final LarkRobotMonitor larkRobotMonitor;
    private final TransactionCostExtraRepository repository;
    private final AccountDailyInitService accountDailyInitService;
    private final StatementProperties.CostIncomeExtraProperties costIncomeExtraProperties;

    @Override
    public void addTransactionCostExtra(final TransactionCostExtraVo vo) {
        final Account account = accountService.getById(vo.getAccountId());
        final MerchantDto merchant = baseService.getMerchantById(account.getMerchantId());

        LocalDate accountLocalDate = LocalDateTimeUtil.nowUtcZonedDateTime()
                .withZoneSameInstant(ZoneId.of(account.getTimezone())).toLocalDate();

        if (Objects.nonNull(vo.getTransactionDate())) {
            if (vo.getTransactionDate().isAfter(accountLocalDate)) {
                throw CommonExceptionCode.PARAMETER_ILLEGAL
                        .exception("The transactionDate cannot be filled with a future date");
            }

            accountLocalDate = vo.getTransactionDate();
        }


        final AccountDailyInitBo billInitInfo =
                accountDailyInitService.getDailyBillInitInfo(account.getId(), accountLocalDate);

        final TransactionCostExtra entity = TransactionCostExtra.builder()
                .billId(billInitInfo.getBillId())
                .billDate(billInitInfo.getTransactionDate())
                .businessTag(StringUtils.defaultIfBlank(vo.getBusinessTag(), "DEFAULT"))

                .countryCode(account.getCountryCode())
                .merchantCode(merchant.getCode())
                .merchantId(account.getMerchantId())
                .accountId(account.getId())

                .fee(Optional.ofNullable(vo.getFee()).orElse(BigDecimal.ZERO))
                .tax(Optional.ofNullable(vo.getTax()).orElse(BigDecimal.ZERO))
                .fx(Optional.ofNullable(vo.getFx()).orElse(BigDecimal.ZERO))

                .extraFee(Optional.ofNullable(vo.getExtraFee()).orElse(BigDecimal.ZERO))
                .extraTax(Optional.ofNullable(vo.getExtraTax()).orElse(BigDecimal.ZERO))
                .extraFx(Optional.ofNullable(vo.getExtraFx()).orElse(BigDecimal.ZERO))

                .exchangeFee(Optional.ofNullable(vo.getExchangeFee())
                        .orElse(BigDecimal.ZERO))
                .adjustmentFee(Optional.ofNullable(vo.getAdjustmentFee())
                        .orElse(BigDecimal.ZERO))
                .cdiProfitIncome(Optional.ofNullable(vo.getCdiProfitIncome())
                        .orElse(BigDecimal.ZERO))

                .costFee(Optional.ofNullable(vo.getCostFee()).orElse(BigDecimal.ZERO))
                .costTax(Optional.ofNullable(vo.getCostTax()).orElse(BigDecimal.ZERO))
                .costFx(Optional.ofNullable(vo.getCostFx()).orElse(BigDecimal.ZERO))
                .pricingCurrency(vo.getPricingCurrency())
                .settlementCurrency(vo.getSettlementCurrency())
                .exchangeRate(vo.getExchangeRate())

                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())

                .createdBy(Optional.ofNullable(vo.getCreatedBy()).orElse(0L))
                .updatedBy(Optional.ofNullable(vo.getUpdatedBy()).orElse(0L))
                .remark(StringUtils.defaultIfBlank(vo.getRemark(), ""))
                .build();

        repository.save(entity);

        larkInfoNotify(account, merchant, vo);
    }

    private void larkInfoNotify(final Account account,
                                final MerchantDto merchant,
                                final TransactionCostExtraVo vo) {
        final String countryCode = account.getCountryCode().getCode();
        final String merchantName = merchant.getName();
        final String transactionType = account.getTransactionTypeCode().getCode();
        final String operateUser = vo.getCreateName();
        final String pricingCurrency = vo.getPricingCurrency().getCode();
        final String settlementCurrency = vo.getSettlementCurrency().getCode();
        final String exchangeRate = vo.getExchangeRate().toString();

        StringBuilder content = new StringBuilder();

        content = StringUtils.isNotBlank(countryCode)
                ? content.append("**Country:** ").append(countryCode).append("\\n") : content;

        content = StringUtils.isNotBlank(merchantName)
                ? content.append("**Merchant:** ").append(merchantName).append("\\n") : content;

        content = StringUtils.isNotBlank(transactionType)
                ? content.append("**Transaction Type:** ").append(transactionType).append("\\n") :
                content;

        content = StringUtils.isNotBlank(pricingCurrency)
                ? content.append("**Pricing Currency:** ").append(pricingCurrency).append("\\n") :
                content;

        content = StringUtils.isNotBlank(settlementCurrency)
                ? content.append("**Settlement Currency:** ").append(settlementCurrency)
                .append("\\n") :
                content;

        content = StringUtils.isNotBlank(exchangeRate)
                ? content.append("**Fx Rate:** ").append(exchangeRate).append("\\n") :
                content;

        content = StringUtils.isNotBlank(operateUser)
                ? content.append("**Operator:** ").append(operateUser).append("\\n") :
                content;

        content = StringUtils.isNotBlank(vo.getBusinessTag())
                ? content.append("**Business Tag:** ").append(vo.getBusinessTag()).append("\\n") :
                content;

        content.append("\\n");
        content = larkInfoNotifyAppendAmount(content, "[Income] Fee", vo.getFee(),
                settlementCurrency);
        content = larkInfoNotifyAppendAmount(content, "[Income] Extra Fee", vo.getExtraFee(),
                settlementCurrency);
        content = larkInfoNotifyAppendAmount(content, "[Income] Tax", vo.getTax(),
                settlementCurrency);
        content = larkInfoNotifyAppendAmount(content, "[Income] Extra Tax", vo.getExtraTax(),
                settlementCurrency);
        content =
                larkInfoNotifyAppendAmount(content, "[Income] Fx", vo.getFx(), settlementCurrency);
        content = larkInfoNotifyAppendAmount(content, "[Income] Extra Fx", vo.getExtraFx(),
                settlementCurrency);
        content = larkInfoNotifyAppendAmount(content, "[Income] CDI Profit",
                vo.getCdiProfitIncome(), settlementCurrency);
        content = larkInfoNotifyAppendAmount(content, "[Income] Adjustment",
                vo.getAdjustmentFee(), settlementCurrency);
        content = larkInfoNotifyAppendAmount(content, "[Income] Exchange", vo.getExchangeFee(),
                settlementCurrency);
        content =
                larkInfoNotifyAppendAmount(content, "[Cost] Fee", vo.getCostFee(), pricingCurrency);
        content =
                larkInfoNotifyAppendAmount(content, "[Cost] Tax", vo.getCostTax(), pricingCurrency);
        content = larkInfoNotifyAppendAmount(content, "[Cost] Fx", vo.getCostFx(), pricingCurrency);

        final StatementProperties.AlarmRobot alarmRobot = costIncomeExtraProperties.getAlarmRobot();

        larkRobotMonitor.info("Add Extra Income/Cost", content.toString(), vo.getRemark(),
                alarmRobot.getWebhook(), alarmRobot.getSignKey());
    }

    private StringBuilder larkInfoNotifyAppendAmount(final StringBuilder content,
                                                     final String column,
                                                     final BigDecimal amount,
                                                     final String currency
    ) {
        if (Objects.nonNull(amount) && !BigDecimal.ZERO.equals(amount)) {
            return content.append(
                    String.format("**%s:** %s(%s)\\n", column, AmountUtil.centToYuan(amount),
                            currency));
        } else {
            return content;
        }
    }

}
