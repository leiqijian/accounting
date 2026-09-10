package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.liquido.core.common.snowflake.SnowflakeIdUtil;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.bo.AccountStatementBizBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.AccountStatementBiz;
import com.liquido.statement.pojo.entity.QAccountStatementBiz;
import com.liquido.statement.repository.AccountStatementBizRepository;
import com.liquido.statement.service.AccountStatementBizService;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountStatementBizServiceImpl implements AccountStatementBizService {

    private final JPAQueryFactory jpaQueryFactory;
    private final AccountStatementBizRepository accountStatementBizRepository;


    @Override
    public BigDecimal statisticsDailyBill(final Account account,
                                          final AccountDailyInitBo latestDailyInitBo) {

        final QAccountStatementBiz entity = QAccountStatementBiz.accountStatementBiz;
        return jpaQueryFactory.select(entity.extractableAmount.sum()
                        .coalesce(BigDecimal.ZERO).as("totalAmount"))
                .from(entity)
                .where(entity.accountId.eq(account.getId())
                        .and(entity.billId.eq(latestDailyInitBo.getBillId())))
                .fetchOne();
    }

    @Override
    public AccountStatementBiz saveAccountStatementBiz(final AccountStatementBizBo bo) {
        return accountStatementBizRepository.saveAndFlush(AccountStatementBiz.builder()
                .id(SnowflakeIdUtil.generate())
                .requestId(bo.getRequestId())
                .transactionId(bo.getTransactionId())
                .merchantId(bo.getMerchantId())
                .subMerchantId(bo.getSubMerchantId())
                .accountId(bo.getAccountId())
                .billId(bo.getBillId())
                .businessType(bo.getBusinessType())
                .financeType(bo.getFinanceType())
                .transactionTime(bo.getTransactionTime())
                .extractableAmount(
                        Optional.ofNullable(bo.getExtractableAmount()).orElse(BigDecimal.ZERO))
                .frozenAmount(Optional.ofNullable(bo.getFrozenAmount()).orElse(BigDecimal.ZERO))
                .exchangeAmount(Optional.ofNullable(bo.getExchangeAmount()).orElse(BigDecimal.ZERO))
                .currency(bo.getCurrency())
                .createdTime(LocalDateTimeUtil.nowUtc())
                .updatedTime(LocalDateTimeUtil.nowUtc())
                .createdBy(Optional.ofNullable(bo.getCreatedBy()).orElse(0L))
                .updatedBy(Optional.ofNullable(bo.getUpdatedBy()).orElse(0L))
                .version(1)
                .delFlag(Boolean.FALSE)
                .remark(bo.getRemark())
                .build());
    }

    @Override
    public List<AccountStatementBiz> saveAccountStatementBiz(
            final List<AccountStatementBizBo> bos) {
        return accountStatementBizRepository.saveAllAndFlush(bos.stream()
                .map(bo -> AccountStatementBiz.builder()
                        .id(SnowflakeIdUtil.generate())
                        .requestId(bo.getRequestId())
                        .transactionId(bo.getTransactionId())
                        .merchantId(bo.getMerchantId())
                        .subMerchantId(bo.getSubMerchantId())
                        .accountId(bo.getAccountId())
                        .billId(bo.getBillId())
                        .businessType(bo.getBusinessType())
                        .financeType(bo.getFinanceType())
                        .transactionTime(bo.getTransactionTime())
                        .extractableAmount(
                                Optional.ofNullable(bo.getExtractableAmount())
                                        .orElse(BigDecimal.ZERO))
                        .frozenAmount(Optional.ofNullable(bo.getFrozenAmount())
                                .orElse(BigDecimal.ZERO))
                        .exchangeAmount(Optional.ofNullable(bo.getExchangeAmount())
                                .orElse(BigDecimal.ZERO))
                        .currency(bo.getCurrency())
                        .createdTime(LocalDateTimeUtil.nowUtc())
                        .updatedTime(LocalDateTimeUtil.nowUtc())
                        .createdBy(Optional.ofNullable(bo.getCreatedBy()).orElse(0L))
                        .updatedBy(Optional.ofNullable(bo.getUpdatedBy()).orElse(0L))
                        .version(1)
                        .delFlag(Boolean.FALSE)
                        .remark(bo.getRemark())
                        .build()).collect(Collectors.toList()));
    }

}
