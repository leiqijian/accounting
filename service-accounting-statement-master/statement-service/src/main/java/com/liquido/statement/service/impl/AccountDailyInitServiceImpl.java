package com.liquido.statement.service.impl;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.liquido.core.common.cache.RedisCacheUtil;
import com.liquido.core.common.cache.RedisDistLock;
import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.DataUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.statement.common.cache.CacheConstant;
import com.liquido.statement.exception.StatementExceptionCode;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.DailyInitKey;
import com.liquido.statement.pojo.entity.AccountDailyInit;
import com.liquido.statement.pojo.entity.QAccountDailyInit;
import com.liquido.statement.repository.AccountDailyInitRepository;
import com.liquido.statement.service.AccountDailyInitService;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountDailyInitServiceImpl implements AccountDailyInitService {
    private final RedisDistLock redisDistLock;
    private final RedisCacheUtil redisCacheUtil;
    private final JPAQueryFactory jpaQueryFactory;
    private final AccountDailyInitRepository accountDailyInitRepository;

    private final LoadingCache<DailyInitKey, AccountDailyInitBo> localCache = Caffeine.newBuilder()
            // Maximum number of caches
            .maximumSize(200)
            // Fixed time expires after last write
            .expireAfterWrite(30, TimeUnit.SECONDS)
            // If the value in the cache is empty or null, trigger reload from the database
            .build(this::getDailyBillInitInfo);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AccountDailyInitBo getDailyBillInitInfo(final Long accountId,
                                                   final LocalDate transactionDate) {
        return localCache.get(DailyInitKey.builder().accountId(accountId)
                .transactionDate(transactionDate).build());
    }

    @Override
    public List<AccountDailyInitBo> queryDailyBillInitList(
            final Collection<Long> accountIds,
            final LocalDate beginDate,
            final LocalDate endDate) {

        final QAccountDailyInit entity = QAccountDailyInit.accountDailyInit;
        final QBean<AccountDailyInitBo> bean = Projections.fields(AccountDailyInitBo.class,
                entity.id.as("billId"),
                entity.accountId,
                entity.transactionDate);

        BooleanExpression condition = entity.transactionDate.goe(beginDate)
                .and(entity.transactionDate.loe(endDate));
        if (CollectionUtils.isNotEmpty(accountIds)) {
            condition = condition.and(entity.accountId.in(accountIds));
        }

        return jpaQueryFactory.select(bean)
                .from(entity)
                .where(condition)
                .orderBy(entity.id.asc())
                .fetch();
    }

    private AccountDailyInitBo getDailyBillInitInfo(final DailyInitKey param) {
        final String cacheKey = buildCacheKey(param.getAccountId(), param.getTransactionDate());

        // get from redis cache;
        AccountDailyInitBo dataInfo = redisCacheUtil
                .getCacheObject(String.format(CacheConstant.ACCOUNT_DAILY_INIT, cacheKey));
        if (Objects.nonNull(dataInfo)) {
            log.info("load from remote cache param={}, dataInfo={}", param, dataInfo);
            return dataInfo;
        }

        log.info("load from database param={}", param);
        // query from database
        final AccountDailyInit existRecord =
                accountDailyInitRepository.findByAccountIdAndTransactionDate(
                        param.getAccountId(), param.getTransactionDate());

        if (Objects.nonNull(existRecord)) {
            dataInfo = AccountDailyInitBo.builder()
                    .billId(existRecord.getId())
                    .accountId(existRecord.getAccountId())
                    .transactionDate(existRecord.getTransactionDate()).build();

            redisCacheUtil.setCacheObject(
                    String.format(CacheConstant.ACCOUNT_DAILY_INIT, cacheKey),
                    dataInfo, 3, TimeUnit.DAYS);

            return dataInfo;
        }

        // init account daily bill info
        final String lockVal = DataUtil.getUuid();
        try {
            boolean lockSuccess = redisDistLock.tryLock(
                    String.format(CacheConstant.ACCOUNT_DAILY_INIT_LOCK, cacheKey), lockVal,
                    600, TimeUnit.SECONDS);
            if (!lockSuccess) {
                log.error("Account daily billId init fail account={}, transactionDate={}",
                        param.getAccountId(), param.getTransactionDate());
                throw StatementExceptionCode.ACCOUNT_DAILY_BILL_INIT_FAIL.exception();
            }

            log.info("init new daily billId begin, accountId={}, transactionDate={}",
                    param.getAccountId(), param.getTransactionDate());

            final Long billId = SnowflakeIdUtil.generate();
            // if not exist renew and save to database
            final AccountDailyInit newRecord = AccountDailyInit.builder()
                    .id(billId)
                    .accountId(param.getAccountId())
                    .transactionDate(param.getTransactionDate())
                    .version(1).build();
            accountDailyInitRepository.saveAndFlush(newRecord);

            // Set to cache;
            dataInfo = AccountDailyInitBo.builder()
                    .billId(billId)
                    .accountId(param.getAccountId())
                    .transactionDate(param.getTransactionDate()).build();
            redisCacheUtil.setCacheObject(
                    String.format(CacheConstant.ACCOUNT_DAILY_INIT, cacheKey),
                    dataInfo, 3, TimeUnit.DAYS);

            log.info("init new daily billId end, accountId={}, initInfo={}",
                    newRecord.getAccountId(), dataInfo);

            return dataInfo;
        } finally {
            boolean res = redisDistLock.unlock(
                    String.format(CacheConstant.ACCOUNT_DAILY_INIT_LOCK, cacheKey), lockVal);
            log.info("daily bill init info unlock result={}", res);
        }
    }

    private static String buildCacheKey(final Long accountId, final LocalDate transactionDate) {
        return String.join(":", accountId.toString(),
                transactionDate.format(LocalDateUtil.FORMAT_YYYYMMDD));
    }
}
