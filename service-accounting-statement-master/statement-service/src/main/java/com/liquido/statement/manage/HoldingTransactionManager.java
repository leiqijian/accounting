package com.liquido.statement.manage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.AmountUtil;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.TransactionMoney;
import com.liquido.statement.pojo.entity.TransactionUnHold;
import com.liquido.statement.pojo.vo.CancelAccountHoldingVo;
import com.liquido.statement.pojo.vo.CancelHoldingDocumentVo;
import com.liquido.statement.pojo.vo.CancelHoldingTransactionVo;
import com.liquido.statement.service.AccountDailyInitService;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.TransactionMoneyService;
import com.liquido.statement.service.TransactionUnHoldService;
import com.liquido.statement.service.dailycut.DailyCutService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HoldingTransactionManager {
    private final AccountService accountService;
    private final DailyCutService dailyCutService;
    private final TransactionMoneyService transactionMoneyService;
    private final TransactionUnHoldService transactionUnHoldService;
    private final AccountDailyInitService accountDailyInitService;

    @Transactional(rollbackFor = Throwable.class)
    public Set<Long> cancelHoldingByDocumentIds(final CancelHoldingDocumentVo vo) {
        if (Objects.isNull(vo)
                || Objects.isNull(vo.getAccountId())
                || vo.getAccountId() <= 0
                || CollectionUtils.isEmpty(vo.getDocumentIds())) {
            return Collections.emptySet();
        }

        // Get all holding transaction list
        final List<TransactionMoney> holdingList =
                transactionMoneyService.queryHoldingOrderByDocumentIds(vo.getAccountId(),
                        vo.getDocumentIds());
        log.info("load holding transaction list from database account={}, documentId={}",
                vo.getAccountId(), vo.getDocumentIds());
        if (CollectionUtils.isEmpty(holdingList)) {
            return Collections.emptySet();
        }

        // Process cancel onHolding transaction
        return this.processCancelHolding(vo.getAccountId(), holdingList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Set<Long> cancelHoldingByTransactionIds(final CancelHoldingTransactionVo vo) {
        if (Objects.isNull(vo)
                || Objects.isNull(vo.getAccountId())
                || vo.getAccountId() <= 0
                || CollectionUtils.isEmpty(vo.getTransactionIds())) {
            return Collections.emptySet();
        }

        // get all holding transaction list
        final List<TransactionMoney> holdingList =
                transactionMoneyService.queryHoldingOrderByTransactionIds(vo.getAccountId(),
                        vo.getTransactionIds());
        log.info("load holding transaction list from database account={}, transactionIds={},"
                + " holdingList={}", vo.getAccountId(), vo.getTransactionIds(), holdingList);

        if (CollectionUtils.isEmpty(holdingList)) {
            return Collections.emptySet();
        }

        // Process cancel onHolding transaction
        return this.processCancelHolding(vo.getAccountId(), holdingList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Set<Long> cancelAllHoldingOrder(final CancelAccountHoldingVo vo) {
        if (Objects.isNull(vo)
                || Objects.isNull(vo.getAccountId())
                || vo.getAccountId() <= 0) {
            return Collections.emptySet();
        }

        // get all holding transaction list
        final List<TransactionMoney> holdingList =
                transactionMoneyService.queryHoldingOrderByTransactionIds(vo.getAccountId(),
                        Collections.emptySet());
        log.info("load holding transaction list from database account={}, transactionIds={}, "
                + "holdingList={}", vo.getAccountId(), holdingList);

        if (CollectionUtils.isEmpty(holdingList)) {
            return Collections.emptySet();
        }

        // Process cancel onHolding transaction
        return this.processCancelHolding(vo.getAccountId(), holdingList);
    }

    /**
     * Process cancel onHolding transaction
     *
     * @param accountId
     * @param holdingList
     * @return success transactionIds
     */
    private Set<Long> processCancelHolding(final Long accountId,
                                           final List<TransactionMoney> holdingList) {
        // get and lock account
        final String lockVal = DataUtil.getUuid();
        final Account account = accountService.getAccountLocked(accountId, lockVal);

        final LocalDateTime accountDateTime = LocalDateTimeUtil.nowUtcZonedDateTime()
                .withZoneSameInstant(ZoneId.of(account.getTimezone()))
                .toLocalDateTime();

        final boolean dailyCutCompleted = dailyCutService.checkDailyCutCompleted(accountId,
                accountDateTime.toLocalDate().minusDays(1));

        // need to increase extractableAmount orders
        final List<TransactionMoney> needUnfreezeList = Lists.newArrayList();
        for (final TransactionMoney transaction : holdingList) {
            // before today holding list
            if (transaction.getBeCreditedDate().isBefore(accountDateTime.toLocalDate())) {
                needUnfreezeList.add(transaction);
            }

            // today holding list and account has been executed daily-cut
            if (dailyCutCompleted && accountDateTime.toLocalDate()
                    .equals(transaction.getBeCreditedDate())) {
                needUnfreezeList.add(transaction);
            }
        }

        try {
            final Set<Long> holdingTransactionIdList = holdingList.stream()
                    .map(TransactionMoney::getTransactionId).collect(Collectors.toSet());
            final Set<Long> unfreezeTransactionIdList = needUnfreezeList.stream()
                    .map(TransactionMoney::getTransactionId).collect(Collectors.toSet());

            final BigDecimal holdingAmount = holdingList.stream()
                    .map(TransactionMoney::getBeCreditedAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            final BigDecimal unfreezeAmount = needUnfreezeList.stream()
                    .map(TransactionMoney::getBeCreditedAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // increase extractable balance
            if (CollectionUtils.isNotEmpty(needUnfreezeList)) {
                if (!accountService.increaseExtractableBalance(account, unfreezeAmount)) {
                    throw StatementExceptionCode.CANCEL_HOLDING_TRANSACTION_FAIL.exception();
                }
            }

            // cancel holding transaction
            final Set<Long> holdingIdList = holdingList.stream()
                    .map(TransactionMoney::getId).collect(Collectors.toSet());
            transactionMoneyService.cancelHoldingTransaction(
                    account.getId(), holdingIdList);

            // save holding transaction record;
            this.saveTransactionUnHold(account, holdingTransactionIdList,
                    unfreezeTransactionIdList, holdingAmount, unfreezeAmount, accountDateTime);

            return holdingTransactionIdList;
        } finally {
            accountService.releaseAccountLock(accountId, lockVal);
        }
    }

    private void saveTransactionUnHold(final Account account,
                                       final Set<Long> holdingTransactionIdList,
                                       final Set<Long> unfreezeTransactionIdList,
                                       final BigDecimal holdingAmount,
                                       final BigDecimal unfreezeAmount,
                                       final LocalDateTime accountDateTime) {


        final AccountDailyInitBo billInitInfo = accountDailyInitService.getDailyBillInitInfo(
                account.getId(), accountDateTime.toLocalDate());

        transactionUnHoldService.save(TransactionUnHold.builder()
                .id(SnowflakeIdUtil.generate())
                .accountId(account.getId())
                .billId(billInitInfo.getBillId())
                .unholdDate(accountDateTime.toLocalDate())
                .unholdTime(LocalDateTimeUtil.nowUtc())
                .unholdAmount(holdingAmount)
                .unfreezeAmount(unfreezeAmount)
                .currency(account.getCurrency())
                .unholdList(holdingTransactionIdList)
                .unfreezeList(unfreezeTransactionIdList)
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(0L)
                .updatedBy(0L)
                .version(0)
                .delFlag(Boolean.FALSE)
                .remark("")
                .build());

        log.info("Cancel holding transaction, accountId={}, totalHoldingAmount={}, "
                        + "unfreezeAmount={}({})",
                account.getId(), AmountUtil.centToYuan(holdingAmount),
                AmountUtil.centToYuan(unfreezeAmount),
                account.getCurrency());
    }
}
