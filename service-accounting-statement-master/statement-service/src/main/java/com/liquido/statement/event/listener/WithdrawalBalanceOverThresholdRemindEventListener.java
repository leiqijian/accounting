package com.liquido.statement.event.listener;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.liquido.base.BaseApis;
import com.liquido.base.enums.LarkRemindRankEnum;
import com.liquido.base.pojo.vo.lark.LarkBathMessageVo;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.CheckResponseUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.mvc.dto.ResponseDto;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.event.WithdrawalBalanceOverThresholdRemindEvent;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.WithdrawalBalanceOverThresholdRemindConfigBo;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.AccountDto;
import com.liquido.statement.service.AccountService;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Insufficient Balance Alert
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalBalanceOverThresholdRemindEventListener {

    private final BaseService baseService;
    private final AccountService accountService;
    private final RedisCacheUtil redisCacheUtil;
    private final BaseApis.BaseFeign baseFeign;
    private final DecimalFormat df = new DecimalFormat("###,##0.00");

    @Async("monitorExecutor")
    @TransactionalEventListener(fallbackExecution = true)
    public void balanceOverThresholdRemindEventListener(
            final WithdrawalBalanceOverThresholdRemindEvent vo) {

        final List<AccountDto> accountDtos = accountService.queryAllAccount().stream()
                .filter(account -> Optional.ofNullable(account.getAccountConfig())
                        .map(AccountConfigDto::getConfigData)
                        .map(AccountConfigData::getWithdrawalBalanceOverThresholdRemindConfig)
                        .filter(config -> Boolean.TRUE.equals(config.getFlag())
                                && Objects.nonNull(config.getThresholdAmount())
                                && !CollectionUtils.isEmpty(config.getLarkUserIds()))
                        .isPresent()).collect(Collectors.toList());

        if (ObjectUtils.isEmpty(accountDtos)) {
            log.info("balance over threshold remind configs is empty");
            return;
        }
        log.info("balance over threshold remind config account ids={}",
                accountDtos.stream().map(AccountDto::getId).collect(Collectors.toList()));

        // Check if the account has insufficient balance
        final List<AccountDto> accounts = accountDtos.stream()
                .filter(account -> account.getExtractableBalance()
                        .compareTo(account.getAccountConfig().getConfigData()
                                .getWithdrawalBalanceOverThresholdRemindConfig()
                                .getThresholdAmount()) > 0)
                .filter(this::isNotRepeatAlarm).collect(Collectors.toList());

        // Send Alarm Email
        sendRemindMessage(accounts);
    }

    private boolean isNotRepeatAlarm(final AccountDto account) {
        final String key =
                String.format(CacheConstant.ACCOUNT_WITHDRAWAL_BALANCE_OVER_THRESHOLD_ALARM,
                        account.getId());
        return !Boolean.TRUE.equals(redisCacheUtil.<Boolean>getCacheObject(key));
    }


    private void sendRemindMessage(final List<AccountDto> accounts) {
        if (ObjectUtils.isEmpty(accounts)) {
            return;
        }
        log.info("send balance over threshold remind account ids={}",
                accounts.stream().map(AccountDto::getId).collect(Collectors.toList()));

        accounts.forEach(account -> {
            final WithdrawalBalanceOverThresholdRemindConfigBo config =
                    account.getAccountConfig().getConfigData()
                            .getWithdrawalBalanceOverThresholdRemindConfig();
            sendRemindMessageToLarkUser(account, config);
        });
    }

    @SneakyThrows
    private void sendRemindMessageToLarkUser(
            final AccountDto account,
            final WithdrawalBalanceOverThresholdRemindConfigBo config) {

        String sb = "**Merchant:** "
                + baseService.getMerchantById(account.getMerchantId()).getName()
                + "\\n"
                + "**Country:** " + account.getCountryCode().getCode() + "\\n"
                + "**TransactionType:** " + account.getTransactionTypeCode().getCode()
                + "\\n"
                + "**Item:** Withdrawable balance" + "\\n"
                + "**Threshold:** "
                + df.format(AmountUtil.centToYuan(config.getThresholdAmount()))
                + "(" + account.getCurrency().getCode() + ")\\n"
                + "**withdrawalAmount:** "
                + df.format(AmountUtil.centToYuan(account.getExtractableBalance()))
                + "(" + account.getCurrency().getCode() + ")\\n"
                + "**timeStamp:** "
                + LocalDateTimeUtil.utcToLocal(LocalDateTimeUtil.nowUtc(), "UTC+8")
                .format(LocalDateTimeUtil.FORMAT_DATETIME);

        final LarkBathMessageVo larkBathMessage =
                LarkBathMessageVo.builder().title("Automatic Reminder").content(sb)
                        .receiveIds(config.getLarkUserIds()).remindRank(LarkRemindRankEnum.WARN)
                        .build();

        final ResponseDto<Void> responseDto = baseFeign.bathSendLarkMessage(larkBathMessage);
        CheckResponseUtil.checkResponse(responseDto);

        final String key =
                String.format(CacheConstant.ACCOUNT_WITHDRAWAL_BALANCE_OVER_THRESHOLD_ALARM,
                        account.getId());

        redisCacheUtil.setCacheObject(key, true,
                (Optional.ofNullable(config.getAlarmCycleHours()).orElse(4) * 60),
                TimeUnit.MINUTES);
    }
}
