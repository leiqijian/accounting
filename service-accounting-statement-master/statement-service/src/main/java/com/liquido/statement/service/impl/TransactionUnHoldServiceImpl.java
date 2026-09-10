package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import com.liquido.statement.pojo.bo.AccountDailyInitBo;
import com.liquido.statement.pojo.entity.Account;
import com.liquido.statement.pojo.entity.QTransactionUnHold;
import com.liquido.statement.pojo.entity.TransactionUnHold;
import com.liquido.statement.repository.TransactionUnHoldRepository;
import com.liquido.statement.service.TransactionUnHoldService;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionUnHoldServiceImpl implements TransactionUnHoldService {

    private final JPAQueryFactory jpaQueryFactory;
    private final TransactionUnHoldRepository transactionUnHoldRepository;

    @Override
    public BigDecimal statisticsDailyUnHoldAmount(final Account account,
                                                  final AccountDailyInitBo dailyInitBo) {

        final QTransactionUnHold entity = QTransactionUnHold.transactionUnHold;
        final BigDecimal totalAmount = jpaQueryFactory.select(entity.unfreezeAmount.sum()
                        .coalesce(BigDecimal.ZERO).as("totalAmount"))
                .from(entity)
                .where(entity.accountId.eq(account.getId())
                        .and(entity.billId.eq(dailyInitBo.getBillId()))
                        .and(entity.delFlag.eq(Boolean.FALSE)))
                .fetchOne();

        return Optional.ofNullable(totalAmount).orElse(BigDecimal.ZERO);
    }

    @Override
    public void save(final TransactionUnHold entity) {
        transactionUnHoldRepository.saveAndFlush(entity);
    }

}
