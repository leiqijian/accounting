package com.liquido.statement.manage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.base.enums.OperateModeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountDailyBill;
import com.liquido.statement.pojo.entity.AccountTransferConfig;
import com.liquido.statement.pojo.vo.AccountTransferVo;
import com.liquido.statement.service.AccountDailyBillService;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.AccountTransferConfigService;
import com.liquido.statement.service.InternalAccountTransferService;
import com.liquido.statement.service.monitor.lark.LarkRobotMonitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

/**
 * Within the same merchant Payin account extractable balance
 * auto transferred to Payout account balance
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InternalAccountTransferManager {

    private final BaseService baseService;
    private final AccountService accountService;
    private final LarkRobotMonitor larkRobotMonitor;
    private final AccountDailyBillService accountDailyBillService;
    private final AccountTransferConfigService accountTransferConfigService;
    private final InternalAccountTransferService internalAccountTransferService;

    /**
     * execute at after daily-cut success
     */
    public void defaultProcessInternalAccountTransfer(final AccountTransferVo vo) {
        log.info("default process internal account transfer vo={}", vo);
        final String lockVal = DataUtil.getUuid();
        Long payoutAccountId = null;
        try {

            // set default time if null
            vo.setTransactionTime(Optional.ofNullable(vo.getTransactionTime())
                    .orElse(LocalDateTimeUtil.nowUtc()));
            vo.setSettlementTime(Optional.ofNullable(vo.getSettlementTime())
                    .orElse(LocalDateTimeUtil.nowUtc()));

            // Step1: get transfer config */
            final AccountTransferConfig config =
                    accountTransferConfigService.findByMerchantIdAndPayinAccountId(
                            vo.getMerchantId(), vo.getPayinAccountId());

            if (Objects.isNull(config)) {
                log.warn("account transfer config not fund, params={}", vo);
                return;
            }

            /* if timerCron is not blank
               use this.customizeProcessInternalAccountTransfer() to execute payin to payout
               @see com.liquido.statement.manage.InternalAccountTransferManager
               .customizeProcessInternalAccountTransfer()
            */
            if (StringUtils.isNotBlank(config.getTimerCron())) {
                log.info("Use customize process internal account transfer vo={}", vo);
                return;
            }

            /* Step2: get and lock payin account */
            final Account payinAccount =
                    accountService.getAccountLocked(vo.getPayinAccountId(), lockVal);
            if (TransactionTypeCodeEnum.PAY_IN != payinAccount.getTransactionTypeCode()) {
                log.error("The payinAccount[{}] not support internal transfer out.",
                        payinAccount.getId());
                throw StatementExceptionCode.ACCOUNT_INTERNAL_TRANSFER_NOT_SUPPORT.exception();
            }

            final BigDecimal transactionAmount = Optional.ofNullable(vo.getTransactionAmount())
                    .orElse(BigDecimal.ZERO);

            if (transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw StatementExceptionCode.TRANSACTION_SETTLEMENT_FAIL.exception(
                        "Transaction Amount Invalid");
            }

            final BigDecimal extractableBalance = payinAccount.getExtractableBalance();
            if (Objects.isNull(extractableBalance)
                    || extractableBalance.compareTo(BigDecimal.ZERO) <= 0
                    || extractableBalance.compareTo(transactionAmount) < 0) {
                log.error("The payinAccount[{}] sufficient extractable balance. "
                                + "extractableBalance={}, transactionAmount={}",
                        payinAccount.getId(),
                        extractableBalance, transactionAmount);
                throw StatementExceptionCode.INSUFFICIENT_EXTRACTABLE_BALANCE.exception(
                        AmountUtil.centToYuan(payinAccount.getExtractableBalance()),
                        payinAccount.getCurrency());
            }

            // Step3: get and lock payout account */
            final Account payoutAccount =
                    accountService.getAccountLocked(config.getPayoutAccountId(), lockVal);
            payoutAccountId = payoutAccount.getId();

            if (TransactionTypeCodeEnum.PAY_OUT != payoutAccount.getTransactionTypeCode()
                    || !payinAccount.getMerchantId().equals(payoutAccount.getMerchantId())
                    || payinAccount.getCurrency() != payoutAccount.getCurrency()) {
                log.error("The payoutAccount[{}] not support internal transfer out.",
                        payoutAccount.getId());
                throw StatementExceptionCode.ACCOUNT_INTERNAL_TRANSFER_NOT_SUPPORT.exception();
            }
            // Step4: execute auto transfer */
            try {
                internalAccountTransferService.executeInternalTransfer(
                        payinAccount, payoutAccount, vo, config);
            } catch (Exception e) {
                log.error("process internal account transfer error:", e);
                larkRobotMonitor.error("Default Internal Account Transfer Error",
                        buildInternalAccountContent(vo), e.getMessage());
                throw StatementExceptionCode.INTERNAL_ACCOUNT_TRANSFER_ERROR.exception(e);
            }
        } finally {
            accountService.releaseAccountLock(vo.getPayinAccountId(), lockVal);
            if (Objects.nonNull(payoutAccountId)) {
                accountService.releaseAccountLock(payoutAccountId, lockVal);
            }
        }
    }

    /**
     * Execute once every hour form customize aws-timer
     */
    public void customizeProcessInternalAccountTransfer() {

        // Step1: get customize timer transfer config */
        final List<AccountTransferConfig> configs =
                accountTransferConfigService.queryCustomizeTimerConfig();
        if (CollectionUtils.isEmpty(configs)) {
            return;
        }

        final List<AccountTransferConfig> configList = configs.stream()
                .filter(x -> StringUtils.isNotBlank(x.getTimerCron()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(configList)) {
            return;
        }

        final LocalDateTime utcNow = LocalDateTimeUtil.nowUtc();
        for (final AccountTransferConfig config : configList) {

            final String lockVal = DataUtil.getUuid();
            try {

                /* Step2: get and lock payin account */
                final Account payinAccount =
                        accountService.getAccountLocked(config.getPayinAccountId(), lockVal);
                if (Objects.isNull(payinAccount)) {
                    continue;
                }

                // UTC+0 Cron
                final String cron = config.getTimerCron().trim();

                // UTC+0 now
                final LocalDateTime utcTimeNow = utcNow.withSecond(0).withNano(0);

                final LocalDateTime cronTimeNow = CronExpression.parse(cron).next(utcTimeNow)
                        .withSecond(0).withNano(0);

                // The execution time has not been reached
                if (!utcTimeNow.equals(cronTimeNow)) {
                    continue;
                }

                final LocalDate accountNowDate = LocalDateTimeUtil.nowUtcZonedDateTime()
                        .withZoneSameInstant(ZoneId.of(payinAccount.getTimezone()))
                        .toLocalDate();

                final AccountDailyBill bill = accountDailyBillService.findAccountBillByBillDay(
                        payinAccount.getId(), accountNowDate.minusDays(1));
                if (Objects.isNull(bill) || Objects.isNull(bill.getDailyExtractableInfo())) {
                    log.warn("customize process internal account transfer warn: dail bill not fund"
                                    + " accountId={}, billDate={} ",
                            payinAccount.getId(), accountNowDate.minusDays(1));
                    continue;
                }

                final BigDecimal transactionAmount = Optional.ofNullable(
                                bill.getDailyExtractableInfo().getCurrentExtractableEndBalance())
                        .orElse(BigDecimal.ZERO)
                        .add(Optional.ofNullable(bill.getDailyExtractableInfo()
                                .getNextTnExtractableAmount()).orElse(BigDecimal.ZERO));

                if (transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                final BigDecimal extractableBalance = payinAccount.getExtractableBalance();
                if (Objects.isNull(extractableBalance)
                        || extractableBalance.compareTo(BigDecimal.ZERO) <= 0
                        || extractableBalance.compareTo(transactionAmount) < 0) {
                    log.error("The payinAccount[{}] sufficient extractable balance. "
                                    + "extractableBalance={}, transactionAmount={}",
                            payinAccount.getId(),
                            extractableBalance, transactionAmount);
                    throw StatementExceptionCode.INSUFFICIENT_EXTRACTABLE_BALANCE.exception(
                            AmountUtil.centToYuan(payinAccount.getExtractableBalance()),
                            payinAccount.getCurrency());
                }

                // Step3: get and lock payout account */
                final Account payoutAccount =
                        accountService.getAccountLocked(config.getPayoutAccountId(), lockVal);
                if (TransactionTypeCodeEnum.PAY_OUT != payoutAccount.getTransactionTypeCode()
                        || !payinAccount.getMerchantId().equals(payoutAccount.getMerchantId())
                        || payinAccount.getCurrency() != payoutAccount.getCurrency()) {
                    log.error("The payoutAccount[{}] not support internal transfer out.",
                            payoutAccount.getId());
                    throw StatementExceptionCode.ACCOUNT_INTERNAL_TRANSFER_NOT_SUPPORT.exception();
                }


                // Step4: execute auto transfer */
                final AccountTransferVo transferVo = AccountTransferVo.builder()
                        .merchantId(config.getMerchantId())
                        .payinAccountId(config.getPayinAccountId())
                        .transactionAmount(transactionAmount)
                        .operateMode(OperateModeEnum.AUTO)
                        .transactionTime(LocalDateTimeUtil.nowUtc())
                        .settlementTime(LocalDateTimeUtil.nowUtc())
                        .build();
                try {

                    log.info("customize process internal account transfer begin vo={}", transferVo);
                    internalAccountTransferService.executeInternalTransfer(
                            payinAccount, payoutAccount, transferVo, config);
                    log.info("customize process internal account transfer end vo={}", transferVo);

                } catch (Exception e) {
                    log.error("customize process internal account transfer vo:{}", transferVo, e);
                    larkRobotMonitor.error("Customize Internal Account Transfer Error",
                            buildInternalAccountContent(transferVo), e.getMessage());
                    throw StatementExceptionCode.INTERNAL_ACCOUNT_TRANSFER_ERROR.exception(e);
                }
            } finally {
                accountService.releaseAccountLock(config.getPayinAccountId(), lockVal);
                accountService.releaseAccountLock(config.getPayoutAccountId(), lockVal);
            }
        }
    }


    public String buildInternalAccountContent(final AccountTransferVo vo) {
        final MerchantDto merchantDto = baseService.getMerchantById(vo.getMerchantId());

        return new StringBuilder()
                .append("**Merchant:** ")
                .append(merchantDto.getCode()).append("\\n")
                .append("**PAY-IN Account :** ")
                .append(vo.getPayinAccountId()).append("\\n").toString();
    }
}
