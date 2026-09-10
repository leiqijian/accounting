package com.liquido.statement.service.monitor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.CurrencyEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.entity.QAccount;
import com.liquido.statement.service.monitor.lark.LarkRobotMonitor;

import com.google.common.collect.Maps;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;


@Slf4j
@RequiredArgsConstructor
@Service("insufficientBalanceMonitor")
public class InsufficientBalanceMonitor implements StatementMonitor {
    private final BaseService baseService;
    private final LarkRobotMonitor larkRobotMonitor;
    private final JPAQueryFactory jpaQueryFactory;
    private final FreeMarkerConfigurer configurer;

    @Value("#{'${statement.lark.inner-emails:}'.split(',')}")
    private List<String> mailTo;

    @Async("monitorExecutor")
    public void monitor() {
        final int minute = LocalDateTimeUtil.nowUtc().getMinute();
        // Trigger a monitoring alarm every hour
        if (minute >= 5) {
            return;
        }

        final List<InsufficientBalanceInfo> dataList = getInsufficientBalanceInfos();
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        dataList.sort(Comparator.comparing(InsufficientBalanceInfo::getCountryCode)
                .thenComparing(x -> x.getPayInBalance().add(x.getPayOutBalance())));

        final List<MailNotifyInfo> notifyList = new ArrayList<>();
        final StringBuilder line = new StringBuilder();
        line.append("The balance of the accounts is insufficient\\n\\n");

        for (final InsufficientBalanceInfo info : dataList) {
            final MerchantDto merchant = baseService.getMerchantById(info.merchantId);

            //excluding internal merchants
            if (!Optional.ofNullable(merchant.getInnerFlag()).orElse(true)) {
                line.append("**")
                        .append(info.getCountryCode().getCode())
                        .append(" | ").append(merchant.getCode())
                        .append(" | ").append("Balance: ")
                        .append(AmountUtil.centToYuan(info.payInBalance.add(info.payOutBalance)))
                        .append("(")
                        .append(Optional.ofNullable(info.payInCurrency)
                                .orElse(info.payOutCurrency).getCode())
                        .append(")")
                        .append("**\\n");

                notifyList.add(MailNotifyInfo.builder()
                        .merchantName(merchant.getCode())
                        .countryCode(info.getCountryCode())
                        .currency(Optional.ofNullable(info.payInCurrency)
                                .orElse(info.payOutCurrency))
                        .balance(AmountUtil.centToYuan(info.payInBalance.add(info.payOutBalance)))
                        .build());
            }
        }

        // send lark notify
        larkRobotMonitor.warn("Insufficient Balance Account", line.toString(),
                "账户预警阈值: Payin实时余额 + Payout实时余额 < 0");

        // send mail notify
        this.sendAlarmEmail(notifyList);
    }

    @SneakyThrows
    private void sendAlarmEmail(final List<MailNotifyInfo> dataList) {
        if (CollectionUtils.isEmpty(mailTo)) {
            return;
        }

        dataList.sort(Comparator.comparing(MailNotifyInfo::getCountryCode)
                .thenComparing(MailNotifyInfo::getBalance));

        final Template template = configurer.getConfiguration()
                .getTemplate("insufficient-balance-inner-reminder-email-template.ftl");
        final Map<String, List<MailNotifyInfo>> dataMap = Maps.newHashMap();
        dataMap.put("dataList", dataList);
        final String content =
                FreeMarkerTemplateUtils.processTemplateIntoString(template, dataMap);

        baseService.sendMail("Insufficient Balance Account", mailTo, content);
    }

    private List<InsufficientBalanceInfo> getInsufficientBalanceInfos() {
        final QAccount account = QAccount.account;

        final NumberExpression<BigDecimal> payInBalance = Expressions.cases()
                .when(account.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_IN))
                .then(account.latestDailyBalance.add(account.subTotalAmount))
                .otherwise(BigDecimal.ZERO);

        final NumberExpression<BigDecimal> payOutBalance = Expressions.cases()
                .when(account.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_OUT))
                .then(account.latestDailyBalance.add(account.subTotalAmount))
                .otherwise(BigDecimal.ZERO);

        final BooleanExpression existPayIn = Expressions.cases()
                .when(account.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_IN))
                .then(true)
                .otherwise(false);

        final BooleanExpression existPayOut = Expressions.cases()
                .when(account.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_OUT))
                .then(true)
                .otherwise(false);

        final EnumExpression<CurrencyEnum> payInCurrency = Expressions.cases()
                .when(account.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_IN))
                .then(account.currency)
                .otherwise((CurrencyEnum) null);

        final EnumExpression<CurrencyEnum> payOutCurrency = Expressions.cases()
                .when(account.transactionTypeCode.eq(TransactionTypeCodeEnum.PAY_OUT))
                .then(account.currency)
                .otherwise((CurrencyEnum) null);

        final QBean<InsufficientBalanceInfo> bean =
                Projections.fields(InsufficientBalanceInfo.class,
                        account.merchantId,
                        account.countryCode,
                        payInBalance.sum().as("payInBalance"),
                        payOutBalance.sum().as("payOutBalance"),
                        existPayIn.max().as("existPayIn"),
                        existPayOut.max().as("existPayOut"),
                        payInCurrency.max().as("payInCurrency"),
                        payOutCurrency.max().as("payOutCurrency")
                );

        return jpaQueryFactory.select(bean)
                .from(account)
                .groupBy(account.merchantId, account.countryCode)
                .having(payInBalance.sum().add(payOutBalance.sum()).lt(0))
                .orderBy(account.countryCode.desc())
                .fetch();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsufficientBalanceInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long merchantId;

        private CountryCodeEnum countryCode;

        private Boolean existPayIn;

        private Boolean existPayOut;

        private BigDecimal payInBalance;

        private BigDecimal payOutBalance;

        private CurrencyEnum payInCurrency;

        private CurrencyEnum payOutCurrency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MailNotifyInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private CountryCodeEnum countryCode;

        private String merchantName;

        private BigDecimal balance;

        private CurrencyEnum currency;

    }
}
