package com.liquido.statement.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.pojo.bo.AccountConfigData;
import com.liquido.statement.pojo.bo.DepositConfigBo;
import com.liquido.statement.pojo.dto.AccountConfigDto;
import com.liquido.statement.pojo.dto.PageAccountDto;
import com.liquido.statement.pojo.entity.AccountBalanceSnapshot;
import com.liquido.statement.pojo.entity.QAccountBalanceSnapshot;
import com.liquido.statement.pojo.mapper.ModelMapper;
import com.liquido.statement.repository.AccountBalanceSnapshotRepository;
import com.liquido.statement.service.BalanceSnapshotService;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceSnapshotServiceImpl implements BalanceSnapshotService {


    private final ModelMapper modelMapper;
    private final JPAQueryFactory jpaQueryFactory;
    private final AccountBalanceSnapshotRepository repository;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdate(final PageAccountDto dto) {

        log.info("save or update account balance snapshot dto={}", dto);

        final QAccountBalanceSnapshot entity = QAccountBalanceSnapshot.accountBalanceSnapshot;
        final AccountBalanceSnapshot snapshot = jpaQueryFactory.select(entity).from(entity)
                .where(entity.id.eq(dto.getId()))
                .limit(1)
                .fetchOne();

        log.info("account balance snapshot exist={}", Objects.nonNull(snapshot));
        final LocalDateTime now = LocalDateTimeUtil.nowUtc();
        if (Objects.nonNull(snapshot)) {
            //update
            log.info("BalanceSnapshotServiceImpl.update snapshot={}", dto);
            snapshot.setLatestDailyBalance(dto.getLatestDailyBalance());
            snapshot.setSubTotalAmount(dto.getSubTotalAmount());
            snapshot.setTotalBalance(dto.getTotalBalance());
            snapshot.setExtractableBalance(dto.getExtractableBalance());
            snapshot.setAccountConfig(dto.getAccountConfigDto());
            snapshot.setExchangeRateCurrency(dto.getExchangeRateCurrency());
            snapshot.setHoldingLimit(dto.getHoldingLimit());
            snapshot.setHoldingAmount(dto.getHoldingAmount());
            snapshot.setAvailableAmount(dto.getAvailableAmount());
            snapshot.setUnavailableAmount(dto.getUnavailableAmount());
            snapshot.setPendingAmount(dto.getPendingAmount());
            snapshot.setCurrency(dto.getCurrency());
            snapshot.setTimezone(dto.getTimezone());
            snapshot.setTimezoneName(dto.getTimezoneName());
            snapshot.setUpdatedTime(now);

            snapshot.setRiskReserveHoldAmount(Optional.ofNullable(dto.getAccountConfigDto()).map(
                    AccountConfigDto::getConfigData).map(AccountConfigData::getDepositConfig).map(
                    DepositConfigBo::getAmount).orElse(BigDecimal.ZERO));

            snapshot.setLegalHoldAmount(Optional.ofNullable(dto.getAccountConfigDto()).map(
                    AccountConfigDto::getConfigData).map(AccountConfigData::getDepositConfig).map(
                    DepositConfigBo::getLegalHoldAmount).orElse(BigDecimal.ZERO));

            repository.saveAndFlush(snapshot);

        } else {
            log.info("BalanceSnapshotServiceImpl.insert snapshot={}", dto);
            final AccountBalanceSnapshot newSnapshot = modelMapper.convertToEntity(dto);
            newSnapshot.setCreatedTime(now);
            newSnapshot.setUpdatedTime(now);

            repository.saveAndFlush(newSnapshot);
        }
    }
}
