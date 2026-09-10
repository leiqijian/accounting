package com.liquido.statement.service.settlement;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.common.Constant;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.BatchAccountSettlementBo;
import com.liquido.statement.pojo.vo.BatchTransactionMoneyVo;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;
import com.liquido.statement.service.AccountService;
import com.liquido.statement.service.dailycut.DailyCutService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class TransactionSettlementServiceImpl implements TransactionSettlementService {
    private final RedisDistLock redisDistLock;
    private final AccountService accountService;
    private final DailyCutService dailyCutService;
    private final AccountSettlementService accountSettlementService;

    @Value("#{'${statement.settlement.pending.merchant-list:0}'.split(',')}")
    private List<Long> PENDING_SETTLEMENT_MERCHANT_LIST;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void batchProcessTransactionSettlement(final BatchTransactionMoneyVo batchOrder) {
        if (Objects.isNull(batchOrder)
                || CollectionUtils.isEmpty(batchOrder.getTransactionMoneyList())) {
            return;
        }

        /* Pre-check repeat submit (prevent duplicate submissions) */
        this.redisDistLock.checkRepeatRequest(CacheConstant.TRANSACTION_ORDER,
                batchOrder.getRequestId(), 2, TimeUnit.HOURS);

        /* Pre-check whether the current account has completed the daily-cut */
        this.preCheckDailyCut(batchOrder);

        /* Try to lock account */
        accountService.tryLockAccount(batchOrder.getAccountId(), batchOrder.getRequestId());
        try {
            /* Step1: prepare processing transaction settlement */
            accountSettlementService.beforeExecutionSettlement(batchOrder);

            /* Step2: batch save transaction order */
            final BatchAccountSettlementBo settlementBo =
                    accountSettlementService.batchSaveTransactionOrder(batchOrder);

            /* Step3: batch execution transaction settlement */
            accountSettlementService.batchExecutionSettlement(settlementBo);

            /* Step4: batch save realTime daily transaction summary */
            accountSettlementService.afterExecutionSettlement(settlementBo);

        } catch (DataIntegrityViolationException e) {
            if (e.getRootCause() instanceof SQLIntegrityConstraintViolationException) {
                log.error("unique key transactionId conflict, requestId={}",
                        batchOrder.getRequestId(), e);
                throw StatementExceptionCode.TRANSACTION_SETTLEMENT_FAIL.exception(
                        e, "unique key conflict");
            }
        } finally {
            accountService.releaseAccountLock(batchOrder.getAccountId(), batchOrder.getRequestId());
        }
    }

    /**
     * preCheck account daily-cut state
     *
     * @param batchOrder
     * @return
     */
    private void preCheckDailyCut(final BatchTransactionMoneyVo batchOrder) {
        final String dailyCutLockKey =
                CacheConstant.buildDailyCutLockKey(batchOrder.getAccountId());
        if (StringUtils.isNotBlank(redisDistLock.getValue(dailyCutLockKey))) {
            log.warn("Account is doing daily-cut now, settlement reject, "
                            + "requestId={}, accountId={}",
                    batchOrder.getRequestId(), batchOrder.getAccountId());
            throw StatementExceptionCode.ACCOUNT_PROCESSING_DAILY_CUT.exception();
        }

        for (final TransactionMoneyVo order : batchOrder.getTransactionMoneyList()) {

            // 00:00~00:15 all orders pending settlement;
            checkPendingSettlement(order);

            /* after process account daily cut completed,  save to cache */
            /* transactionTime is UTC+0 time, need to transfer to merchant-account time zone; */
            final LocalDateTime transactionTime = order.getTransactionTime()
                    .atZone(Constant.COMMON.ZONE_UTC)
                    .withZoneSameInstant(ZoneId.of(order.getAccountTimeZone()))
                    .toLocalDateTime();

            /* pre-check account has completed the daily cut */
            if (dailyCutService.checkDailyCutCompleted(order.getAccountId(),
                    transactionTime.toLocalDate())) {
                log.warn("this account has completed daily cut, transaction order clear reject "
                                + "requestId={}, transactionId={}",
                        batchOrder.getRequestId(), order.getTransactionId());

                throw StatementExceptionCode.ACCOUNT_ALREADY_DAILY_CUT.exception();
            }
        }
    }

    private void checkPendingSettlement(final TransactionMoneyVo order) {
        if (!PENDING_SETTLEMENT_MERCHANT_LIST.contains(order.getMerchantId())) {
            return;
        }

        final LocalDate nowDate = LocalDateTimeUtil.nowUtcZonedDateTime()
                .withZoneSameInstant(ZoneId.of(order.getAccountTimeZone())).toLocalDate();

        final LocalDate transactionDate = order.getTransactionTime()
                .atZone(Constant.COMMON.ZONE_UTC)
                .withZoneSameInstant(ZoneId.of(order.getAccountTimeZone()))
                .toLocalDate();

        // check account yesterday completed daily-cut
        if (!dailyCutService.checkDailyCutCompleted(order.getAccountId(),
                nowDate.minusDays(1)) && nowDate.equals(transactionDate)) {

            log.warn("CheckPendingSettlement >> System processing daily cut now, please wait, "
                            + "PENDING_SETTLEMENT_MERCHANT_LIST={}, order.merchantId={}",
                    PENDING_SETTLEMENT_MERCHANT_LIST, order.getMerchantId());
            throw StatementExceptionCode.ACCOUNT_PROCESSING_DAILY_CUT.exception();
        }
    }
}
