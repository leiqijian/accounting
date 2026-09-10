package com.liquido.statement.event.listener;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.event.InsufficientBalanceAlertEvent;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.BalanceAlarmConfigBo;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.service.AccountService;

import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

/**
 * The Payout Account Insufficient Balance Reminder Event Listener
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InsufficientBalanceReminderEventListener {
    private final BaseService baseService;
    private final AccountService accountService;
    private final RedisCacheUtil redisCacheUtil;
    private final FreeMarkerConfigurer configurer;
    private final DecimalFormat df = new DecimalFormat("###,##0.00");

    @Async("monitorExecutor")
    @TransactionalEventListener(fallbackExecution = true)
    public void insufficientBalanceEventListener(final InsufficientBalanceAlertEvent event) {
        log.info("[InsufficientBalanceReminderEventListener] payout insufficient balance "
                + "reminder monitor");

        final List<AccountDto> accounts = accountService.queryAllAccount().stream()
                // Get all account alert config of payout/marketplace
                .filter(account -> List.of(TransactionTypeCodeEnum.PAY_OUT,
                                TransactionTypeCodeEnum.MARKET_PLACE_ORDERS)
                        .contains(account.getTransactionTypeCode()))
                // Check if the account has insufficient balance
                .filter(account -> Optional.ofNullable(account.getAccountConfig())
                        .map(AccountConfigDto::getConfigData)
                        .map(AccountConfigData::getBalanceAlarm)
                        .filter(config -> Boolean.TRUE.equals(config.getFlag())
                                && !CollectionUtils.isEmpty(config.getEmail())
                                && Objects.nonNull(config.getAmountLimit()))
                        .map(config ->
                                account.getLatestDailyBalance().add(account.getSubTotalAmount())
                                        .subtract(config.getAmountLimit())
                                        .compareTo(BigDecimal.ZERO) <= 0)
                        .orElse(false))
                .filter(this::isNotRepeatAlarm)
                .collect(Collectors.toList());

        // Send Alarm Email
        sendAlarmEmail(accounts);
    }

    private boolean isNotRepeatAlarm(final AccountDto account) {
        final String key = String.format(CacheConstant.ACCOUNT_BALANCE_ALARM_KEY, account.getId());
        return !Boolean.TRUE.equals(redisCacheUtil.<Boolean>getCacheObject(key));
    }

    private void sendAlarmEmail(final List<AccountDto> accounts) {
        if (CollectionUtils.isEmpty(accounts)) {
            return;
        }

        accounts.forEach(account -> {
            final BalanceAlarmConfigBo config =
                    account.getAccountConfig().getConfigData().getBalanceAlarm();

            sendAlarmEmail(account, config);
            final String key = String.format(CacheConstant.ACCOUNT_BALANCE_ALARM_KEY,
                    account.getId());
            redisCacheUtil.setCacheObject(key, true,
                    (Optional.ofNullable(config.getAlarmCycleHours()).orElse(8) * 60) - 30,
                    TimeUnit.MINUTES);
        });
    }

    @SneakyThrows
    private void sendAlarmEmail(final AccountDto account, final BalanceAlarmConfigBo config) {
        final Template template = configurer.getConfiguration()
                .getTemplate("insufficient-balance-reminder-email-template.ftl");
        // Dashboard Insufficient Balance Reminder
        final Map<String, String> paramMap = Map.of("merchantName",
                baseService.getMerchantById(account.getMerchantId()).getName(),
                "accountInfo", account.getCountryCode().getCountryName()
                        + "-" + account.getTransactionTypeCode().getRemark(),
                "amountLimit", df.format(AmountUtil.centToYuan(config.getAmountLimit())),
                "currency", config.getCurrencyEnum().getCode(),
                "currentAmount", df.format(AmountUtil.centToYuan(
                        account.getLatestDailyBalance().add(account.getSubTotalAmount())))
        );

        final String content =
                FreeMarkerTemplateUtils.processTemplateIntoString(template, paramMap);

        baseService.sendMail("Insufficient Balance Reminder", config.getEmail(), content);
    }

}
