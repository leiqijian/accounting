package com.liquido.statement.service.monitor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.AccountDiagnostic;
import com.liquido.statement.pojo.bo.AccountReconciliationBo;
import com.liquido.statement.pojo.entity.QAccount;
import com.liquido.statement.pojo.entity.QAccountDailyBill;
import com.liquido.statement.service.TransactionMoneyService;
import com.liquido.statement.service.monitor.lark.LarkRobotMonitor;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RefreshScope
@RequiredArgsConstructor
@Service("accountReconciliationMonitor")
public class AccountReconciliationMonitor implements StatementMonitor {
    private final BaseService baseService;
    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final LarkRobotMonitor larkRobotMonitor;
    private final TransactionMoneyService transactionMoneyService;

    @Override
    @Async("monitorExecutor")
    public void monitor() {
        final List<AccountReconciliationBo> dataList = this.queryAccountDiffList();
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        dataList.sort(Comparator.comparing(AccountReconciliationBo::getCountryCode));

        this.monitorPayIn(dataList);

        this.monitorPayOut(dataList);
    }

    private void monitorPayIn(final List<AccountReconciliationBo> dataList) {
        final StringBuilder line = new StringBuilder();
        dataList.stream()
                .filter(bo -> TransactionTypeCodeEnum.PAY_IN.equals(bo.getTransactionTypeCode()))
                .forEach(bo -> line.append(this.getAccountReconciliationInfo(bo)));

        if (line.length() == 0) {
            return;
        }

        larkRobotMonitor.error("Inconsistent Account(PAY-IN) Amounts", line.toString(),
                "DayEndBalance + CurrentIncurredAmt = "
                        + "ExtractableAmt + FrozenAmt + ExchangeAmt + PendingAmt + HoldingAmt");
    }

    private void monitorPayOut(final List<AccountReconciliationBo> dataList) {
        final StringBuilder line = new StringBuilder();
        dataList.stream()
                .filter(bo -> TransactionTypeCodeEnum.PAY_OUT.equals(bo.getTransactionTypeCode()))
                .forEach(bo -> line.append(this.getAccountReconciliationInfo(bo)));

        if (line.length() == 0) {
            return;
        }

        larkRobotMonitor.error("Inconsistent Account(PAY-OUT) Amounts", line.toString(),
                "PayOut: LatestDailyBalance + SubTotalAmount = "
                        + "ExtractableBalance + FrozenAmount + ExchangeAmount");
    }

    private String getAccountReconciliationInfo(final AccountReconciliationBo bo) {
        return new StringBuilder("**")
                .append(bo.getLastBillDate()).append(" | ")
                .append(bo.getTimezone()).append(" | ")
                .append(bo.getCountryCode().getCode()).append(" | ")
                .append(bo.getMerchantName()).append(" | ")
                .append(AmountUtil.centToYuan(bo.getAmountDiff()))
                .append("**\\n").toString();
    }

    private List<AccountReconciliationBo> queryAccountDiffList() {
        final QAccount account = QAccount.account;
        final QAccountDailyBill bill = QAccountDailyBill.accountDailyBill;
        final QBean<AccountReconciliationBo> bean =
                Projections.fields(AccountReconciliationBo.class,
                        account.id.as("accountId"),
                        account.merchantId,
                        account.countryCode,
                        account.currency,
                        account.transactionTypeCode,
                        account.timezone,
                        account.latestDailyBalance,
                        account.subTotalAmount,
                        account.extractableBalance,
                        account.frozenAmount,
                        account.exchangeAmount,
                        bill.billDate.max().as("lastBillDate"));
        final BooleanExpression condition = bill.delFlag.eq(Boolean.FALSE)
                .and(account.transactionTypeCode.in(
                        List.of(TransactionTypeCodeEnum.PAY_IN, TransactionTypeCodeEnum.PAY_OUT)));
        final List<AccountReconciliationBo> dataList = jpaQueryFactory.select(bean)
                .from(account)
                .innerJoin(bill).on(account.id.eq(bill.accountId))
                .where(condition)
                .groupBy(account.id)
                .fetch();

        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        final List<AccountReconciliationBo> waitWarnAccountList = Lists.newArrayList();
        final ZonedDateTime utcZonedDateTime = LocalDateTimeUtil.nowUtcZonedDateTime();
        for (final AccountReconciliationBo bo : dataList) {
            final LocalDate merchantDate = utcZonedDateTime
                    .withZoneSameInstant(ZoneId.of(bo.getTimezone())).toLocalDate();

            if (Objects.isNull(bo.getLastBillDate())) {
                bo.setLastBillDate(merchantDate.minusDays(1));
                bo.setNextBillDate(merchantDate);
            } else {
                bo.setNextBillDate(bo.getLastBillDate().plusDays(1));
            }

            bo.setTotalBalance(bo.getLatestDailyBalance().add(bo.getSubTotalAmount()));

            final BigDecimal pendingAmount = transactionMoneyService.queryPendingBalance(
                    bo.getAccountId(), bo.getNextBillDate());

            final BigDecimal holdingAmount =
                    transactionMoneyService.getHoldingBalance(bo.getAccountId());

            bo.setPendingAmount(Objects.nonNull(pendingAmount) ? pendingAmount : BigDecimal.ZERO);
            bo.setHoldingAmount(Objects.nonNull(holdingAmount) ? holdingAmount : BigDecimal.ZERO);

            final MerchantDto merchant = baseService.getMerchantById(bo.getMerchantId());
            bo.setMerchantName(Objects.nonNull(merchant)
                    ? merchant.getCode() : "-UNKNOWN-");

            /* amountDiff =
                totalBalance - (extractableAmt + frozenAmt + exchangeAmt + pendingAmt + holdingAmt)
             */
            bo.setAmountDiff(bo.getTotalBalance().subtract(bo.getExtractableBalance()
                    .add(bo.getFrozenAmount())
                    .add(bo.getExchangeAmount())
                    .add(pendingAmount)
                    .add(holdingAmount)));

            if (bo.getAmountDiff().compareTo(BigDecimal.ZERO) != 0) {
                waitWarnAccountList.add(bo);
            }
        }

        return diagnosticAlarm(waitWarnAccountList);
    }

    private List<AccountReconciliationBo> diagnosticAlarm(
            final List<AccountReconciliationBo> bos) {

        final List<String> bosAccountIds = Lists.newArrayList();
        final List<AccountReconciliationBo> actualWarnList = Lists.newArrayList();

        final Map<String, AccountDiagnostic> recordedMap =
                redisCacheUtil.getCacheMap(CacheConstant.ACCOUNT_DIAGNOSE_KEY);

        for (final AccountReconciliationBo bo : bos) {
            final String accountId = bo.getAccountId().toString();
            bosAccountIds.add(accountId);

            final AccountDiagnostic ad = recordedMap.get(accountId);
            // initialization diagnostic
            if (Objects.isNull(ad)) {
                recordedMap.put(accountId, new AccountDiagnostic(System.currentTimeMillis(), 1));
                continue;
            }

            // cumulative alarm
            ad.setCount(ad.getCount() + 1);
            if (ad.getCount() >= 3) {
                actualWarnList.add(bo);
            }
        }

        recordedMap.keySet().stream().filter(v -> !bosAccountIds.contains(v))
                .collect(Collectors.toList()).forEach(v -> {
                    redisCacheUtil.delCacheMapValue(CacheConstant.ACCOUNT_DIAGNOSE_KEY, v);
                    recordedMap.remove(v);
                });

        redisCacheUtil.setCacheMap(CacheConstant.ACCOUNT_DIAGNOSE_KEY, recordedMap);
        return actualWarnList;
    }
}
