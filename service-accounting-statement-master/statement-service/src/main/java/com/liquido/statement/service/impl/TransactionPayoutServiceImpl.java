package com.liquido.statement.service.impl;

import java.util.List;

import com.liquido.base.enums.SettleStatusEnum;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.enums.PaymentTransactionStatusEnum;
import com.liquido.statement.manage.BizTransferOutManager;
import com.liquido.statement.pojo.dto.PaymentPayoutDto;
import com.liquido.statement.pojo.entity.QTransactionPayout;
import com.liquido.statement.pojo.entity.TransactionPayout;
import com.liquido.statement.repository.TransactionPayoutRepository;
import com.liquido.statement.service.TransactionPayoutService;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionPayoutServiceImpl implements TransactionPayoutService {
    private final JPAQueryFactory jpaQueryFactory;
    private final BizTransferOutManager bizTransferOutManager;
    private final TransactionPayoutRepository transactionPayoutRepository;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public TransactionPayout save(final TransactionPayout entity) {
        return transactionPayoutRepository.saveAndFlush(entity);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<TransactionPayout> batchSave(final List<TransactionPayout> payoutList) {
        return transactionPayoutRepository.saveAllAndFlush(payoutList);
    }

    @Override
    public TransactionPayout findByUniqueId(final Long uniqueId) {
        return transactionPayoutRepository.findByUniqueId(uniqueId);
    }

    @Override
    public List<TransactionPayout> findDelayPayoutOrder() {
        final QTransactionPayout entity = QTransactionPayout.transactionPayout;
        return jpaQueryFactory.select(entity).from(entity)
                .where(entity.transactionStatus.eq(PaymentTransactionStatusEnum.WAITING)
                        .and(entity.delayExecuteTime.loe(LocalDateTimeUtil.nowUtc())))
                .fetch();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void postProcessPayoutOrder(final TransactionPayout payout,
                                       final PaymentPayoutDto result) {
        payout.setTransactionStatus(
                PaymentTransactionStatusEnum.parse(result.getTransferStatus()));
        this.save(payout);
    }

    @Override
    public SettleStatusEnum transferStatus(final String transferStatus) {
        if (StringUtils.isBlank(transferStatus)) {
            return SettleStatusEnum.PROCESSING;
        }

        switch (transferStatus) {
            case "IN_PROGRESS":
                return SettleStatusEnum.PROCESSING;
            case "SETTLED":
                return SettleStatusEnum.SUCCESS;
            case "FAILED":
            case "REJECTED":
            default:
                return SettleStatusEnum.FAILED;
        }
    }
}
