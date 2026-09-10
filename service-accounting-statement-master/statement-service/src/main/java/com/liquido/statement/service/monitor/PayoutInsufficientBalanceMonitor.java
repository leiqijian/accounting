package com.liquido.statement.service.monitor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.properties.StatementProperties;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.monitor.lark.LarkRobotMonitor;

import com.google.common.collect.Maps;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

/**
 * The Payout Account Insufficient Balance Reminder Event Listener
 */
@Slf4j
@Service("payoutInsufficientBalanceMonitor")
@RequiredArgsConstructor
public class PayoutInsufficientBalanceMonitor implements StatementMonitor {
    private final BaseService baseService;
    private final AccountService accountService;
    private final FreeMarkerConfigurer configurer;
    private final LarkRobotMonitor larkRobotMonitor;
    private final StatementProperties.PayOutInsufficientBalanceAlert properties;

    @Async("monitorExecutor")
    public void monitor() {

        final int minute = LocalDateTimeUtil.nowUtc().getMinute();
        // Trigger a monitoring alarm every hour
        if (minute >= 5) {
            return;
        }
        // Get config about payOut insufficient balance
        final List<AccountDto> accounts = accountService.queryAllAccount().stream()
                .filter(v -> TransactionTypeCodeEnum.PAY_OUT == v.getTransactionTypeCode())
                .filter(account -> (account.getLatestDailyBalance()
                        .add(account.getSubTotalAmount())).compareTo(
                        BigDecimal.ZERO) < 0).collect(Collectors.toList());

        // Send Alarm Email
        this.sendAlarm(accounts);
    }

    private void sendAlarm(final List<AccountDto> accountList) {
        if (ObjectUtils.isEmpty(accountList)) {
            return;
        }

        accountList.sort(Comparator.comparing(AccountDto::getCountryCode)
                .thenComparing(x -> x.getLatestDailyBalance().add(x.getSubTotalAmount())));

        final List<InsufficientBalanceMonitor.MailNotifyInfo> notifyList = new ArrayList<>();
        final StringBuilder line = new StringBuilder();
        line.append("The balance of the payout accounts is insufficient\\n\\n");

        for (final AccountDto account : accountList) {
            final MerchantDto merchant = baseService.getMerchantById(account.getMerchantId());

            // excluding internal merchants
            if (!Optional.ofNullable(merchant.getInnerFlag()).orElse(true)) {
                line.append("**")
                        .append(account.getCountryCode().getCode())
                        .append(" | ").append(merchant.getCode())
                        .append(" | ").append("Balance: ")
                        .append(AmountUtil.centToYuan(
                                account.getSubTotalAmount().add(account.getLatestDailyBalance())))
                        .append("(")
                        .append(account.getCurrency().getCode())
                        .append(")")
                        .append("**\\n");

                notifyList.add(InsufficientBalanceMonitor.MailNotifyInfo.builder()
                        .merchantName(merchant.getCode())
                        .countryCode(account.getCountryCode())
                        .currency(account.getCurrency())
                        .balance(AmountUtil.centToYuan(
                                account.getSubTotalAmount().add(account.getLatestDailyBalance())))
                        .build());
            }
        }

        // send lark notify
        larkRobotMonitor.warn("Payout Insufficient Balance Account", line.toString(),
                "账户预警阈值: Payout实时余额 < 0",
                properties.getAlarmRobot().getWebhook(), properties.getAlarmRobot().getSignKey());

        notifyList.sort(
                Comparator.comparing(InsufficientBalanceMonitor.MailNotifyInfo::getCountryCode)
                        .thenComparing(InsufficientBalanceMonitor.MailNotifyInfo::getBalance));

        this.sendAlarmEmail(notifyList);
    }

    @SneakyThrows
    private void sendAlarmEmail(final List<InsufficientBalanceMonitor.MailNotifyInfo> dataList) {
        if (StringUtils.isBlank(properties.getEmail())) {
            return;
        }

        final Template template = configurer.getConfiguration()
                .getTemplate("payout-insufficient-balance-inner-reminder-email-template.ftl");
        final Map<String, List<InsufficientBalanceMonitor.MailNotifyInfo>> dataMap =
                Maps.newHashMap();
        dataMap.put("dataList", dataList);
        final String content =
                FreeMarkerTemplateUtils.processTemplateIntoString(template, dataMap);

        baseService.sendMail("Payout Insufficient Balance Account",
                Arrays.asList(properties.getEmail().split(",")), content);
    }
}
