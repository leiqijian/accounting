package com.liquido.statement.service.dailycut;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.event.DailyCutSuccessEvent;
import com.liquido.statement.event.DailyCutSuccessEventArgs;
import com.liquido.statement.pojo.bo.DailyCutAccountBo;
import com.liquido.statement.pojo.bo.DailyCutSuccessBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountDailyBill;
import com.liquido.statement.pojo.vo.ReRunAccountDailyCutVo;
import com.liquido.statement.service.AccountDailyBillService;
import com.liquido.statement.service.AccountService;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@SuppressWarnings("PMD.AvoidReassigningLoopVariables")
@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class DailyCutServiceImpl implements DailyCutService {
    private final RedisCacheUtil redisCacheUtil;
    private final AccountService accountService;
    private final ApplicationEventPublisher eventPublisher;
    private final DailyCutProviderFactory dailyCutProviderFactory;
    private final AccountDailyBillService accountDailyBillService;

    public void runAccountDailyCut() {
        final List<DailyCutAccountBo> accountList = accountService.findAllAccountForDailyCut();
        if (CollectionUtils.isEmpty(accountList)) {
            return;
        }

        final ZonedDateTime utcTimeNow = LocalDateTimeUtil.nowUtcZonedDateTime();
        final List<CompletableFuture<DailyCutSuccessBo>> futureList = Lists.newArrayList();
        final long begin = System.currentTimeMillis();
        log.info("run account dailyCut begin");
        for (final DailyCutAccountBo account : accountList) {
            /* Transfer local dateTime to merchant account timezone */
            final LocalDate billDate = utcTimeNow
                    .withZoneSameInstant(ZoneId.of(account.getTimezone()))
                    .toLocalDate().minusDays(1);

            /* Pre-check account has completed the daily cut */
            if (this.checkDailyCutCompleted(account.getAccountId(), billDate)) {
                continue;
            }

            final CompletableFuture<DailyCutSuccessBo> dailyCutFuture =
                    dailyCutProviderFactory.getProviderFactory(account.getTransactionTypeCode())
                            .asyncHandleDailyCut(account.getAccountId(), billDate);

            futureList.add(dailyCutFuture);
        }
        log.info("run account dailyCut end ts={}ms", System.currentTimeMillis() - begin);

        // Waiting for all asynchronous tasks to complete
        CompletableFuture.allOf(
                futureList.toArray(new CompletableFuture[futureList.size()])).join();

        final List<DailyCutSuccessBo> resultList = futureList.stream()
                .map(CompletableFuture::join)
                .filter(item -> Objects.nonNull(item) && item.getBillId() > 0)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(resultList)) {
            log.info("publishDailyCutSuccessEvent dailyCutSuccessList={}", resultList);
            /* Post event after successful daily-cut */
            this.publishDailyCutSuccessEvent(resultList, Boolean.TRUE);
        }
    }

    @Override
    @Async("dailyCutTaskExecutor")
    public void reRunAccountDailyCut(final ReRunAccountDailyCutVo vo) {
        final List<Account> accountList = accountService.findByIds(vo.getAccountIdList());
        if (CollectionUtils.isEmpty(accountList)) {
            return;
        }

        final long beginTime = System.currentTimeMillis();
        final List<DailyCutSuccessBo> resultList = Lists.newArrayList();
        for (Account account : accountList) {
            final LocalDate accountCurrentDate = LocalDateTimeUtil.nowUtcZonedDateTime()
                    .withZoneSameInstant(ZoneId.of(account.getTimezone())).toLocalDate();

            // Unable to perform future day-cutting operations
            if (vo.getBeginDate().equals(accountCurrentDate)
                    || vo.getBeginDate().isAfter(accountCurrentDate)) {
                continue;
            }

            LocalDate beginDate = vo.getBeginDate();
            final LocalDate endDate = accountCurrentDate.isBefore(vo.getEndDate())
                    ? accountCurrentDate : vo.getEndDate();
            /* ReExecute account daily cut */
            while (beginDate.isBefore(endDate)) {
                try {
                    final DailyCutSuccessBo successBo = dailyCutProviderFactory.getProviderFactory(
                                    account.getTransactionTypeCode())
                            .syncHandleDailyCut(account.getId(), beginDate);
                    if (Objects.nonNull(successBo) && successBo.getBillId() > 0) {
                        resultList.add(successBo);
                    }
                } catch (Exception e) {
                    log.error("ReRunAccountDailyCut accountId={}, billDate={}, error: ",
                            account.getId(),
                            beginDate, e);
                }

                beginDate = beginDate.plusDays(1);

                /*Process daily cut refresh account info, refresh latest_daily_balance, version*/
                account = accountService.getById(account.getId());
            }
        }

        final long endTime = System.currentTimeMillis();
        log.info("reprocess account daily cut completed total ts={}", (endTime - beginTime));

        /* Post event after successful daily-cut */
        if (CollectionUtils.isNotEmpty(resultList)) {
            this.publishDailyCutSuccessEvent(resultList, Boolean.FALSE);
        }
    }

    @Override
    public void publishDailyCutSuccessEvent(final List<DailyCutSuccessBo> dataList,
                                            final boolean autoPayment) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        try {
            log.info("begin publish daily-cut success event, dataList={}", dataList);
            eventPublisher.publishEvent(new DailyCutSuccessEvent(DailyCutSuccessEventArgs.builder()
                    .billList(dataList).autoPayment(autoPayment).build()));
            log.info("end publish daily-cut success event, dataList={}", dataList);
        } catch (Exception e) {
            log.info("publish daily-cut success event, dataList={}", dataList, e);
        }
    }

    /**
     * pre-check whether the daily cut has been done
     */
    @Override
    public boolean checkDailyCutCompleted(final Long accountId, final LocalDate billDate) {
        final String dailyCutCacheKey = String.join(":", accountId.toString(),
                billDate.format(LocalDateUtil.FORMAT_YYYYMMDD));

        final Object dailyBillIdStr = redisCacheUtil.getCacheMapValue(
                CacheConstant.COMPLETED_DAILY_CUT, dailyCutCacheKey);

        return Optional.ofNullable(dailyBillIdStr).map(billId -> Long.valueOf(billId.toString()))
                .map(billId -> {
                    /* if account has completed the daily cut */
                    if (billId > 0L) {
                        log.info("this account has completed daily cut billId={}, "
                                + "dailyCutCacheKey={}", billId, dailyCutCacheKey);
                        return true;
                    }
                    return false;
                }).orElseGet(() -> {
                    /* if dailyBillId is null, check from database */
                    final AccountDailyBill existDailyBill =
                            accountDailyBillService.findAccountBillByBillDay(accountId, billDate);
                    if (Objects.nonNull(existDailyBill)) {
                        redisCacheUtil.setCacheMapValue(CacheConstant.COMPLETED_DAILY_CUT,
                                dailyCutCacheKey, existDailyBill.getId());
                        return true;
                    }

                    redisCacheUtil.setCacheMapValue(CacheConstant.COMPLETED_DAILY_CUT,
                            dailyCutCacheKey, -1L);
                    return false;
                });
    }
}
