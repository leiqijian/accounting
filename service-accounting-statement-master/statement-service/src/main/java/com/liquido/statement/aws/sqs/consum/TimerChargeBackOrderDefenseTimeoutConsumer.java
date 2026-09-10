package com.liquido.statement.aws.sqs.consum;

import java.time.LocalDateTime;

import com.liquido.core.aws.sqs.BaseSqsMessageHandler;
import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.statement.aws.sqs.msg.TimerChargeBackOrderDefenseTimeoutMsg;
import com.liquido.statement.enums.TransactionChargeBackStatusEnum;
import com.liquido.statement.pojo.entity.QTransactionChargeBackOrder;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = {"statement.aws.sqs.queue.enable"}, havingValue = "true")
public class TimerChargeBackOrderDefenseTimeoutConsumer extends
        BaseSqsMessageHandler<TimerChargeBackOrderDefenseTimeoutMsg> {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void handle(final TimerChargeBackOrderDefenseTimeoutMsg message) {
        final LocalDateTime nowUtc = LocalDateTimeUtil.nowUtc();
        log.info("TimerChargeBackOrderDefenseTimeoutConsumer start {}", nowUtc);

        final QTransactionChargeBackOrder entity =
                QTransactionChargeBackOrder.transactionChargeBackOrder;

        long count = jpaQueryFactory.update(entity)
                .set(entity.status, TransactionChargeBackStatusEnum.DEFENSE_LOST)
                .set(entity.defenseDescription, "timed out not processed")
                .where(entity.status.eq(TransactionChargeBackStatusEnum.CHARGE_BACK)
                        .and(entity.defenseDeadline.before(nowUtc)))
                .execute();

        log.info("TimerChargeBackOrderDefenseTimeoutConsumer update count {}", count);
    }

    @Override
    public Class<TimerChargeBackOrderDefenseTimeoutMsg> messageType() {
        return TimerChargeBackOrderDefenseTimeoutMsg.class;
    }

}
