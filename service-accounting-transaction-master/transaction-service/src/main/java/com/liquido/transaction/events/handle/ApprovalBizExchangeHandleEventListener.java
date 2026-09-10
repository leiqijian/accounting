package com.liquido.transaction.events.handle;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.transaction.Transactional;

import com.liquido.transaction.common.Constant;
import com.liquido.transaction.enums.ApprovalBizExchangeStatusEnum;
import com.liquido.transaction.events.handle.exchange.BaseApprovalBizExchangeHandle;
import com.liquido.transaction.pojo.bo.ApprovalBizExchangeConfigBo;
import com.liquido.transaction.pojo.bo.ApprovalBizHandleBo;
import com.liquido.transaction.pojo.bo.event.ApprovalBizExchangeFinishEvent;
import com.liquido.transaction.pojo.bo.event.ApprovalBizExchangeFinishEventBo;
import com.liquido.transaction.pojo.bo.event.ApprovalCompleteBizExchangeEvent;
import com.liquido.transaction.pojo.bo.event.ApprovalCompleteBizExchangeEventBo;
import com.liquido.transaction.pojo.entity.Approval;
import com.liquido.transaction.pojo.entity.ApprovalBizExchange;
import com.liquido.transaction.repository.ApprovalBizExchangeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class ApprovalBizExchangeHandleEventListener {

    private final ApplicationEventPublisher publisher;
    private final ApprovalBizExchangeRepository approvalBizExchangeRepository;
    private final List<BaseApprovalBizExchangeHandle> exchangeHandles;

    @Async
    @TransactionalEventListener
    public void approvalCompleteExchange(final ApprovalCompleteBizExchangeEvent event) {

        log.info("approval biz exchange pre handle event->{}", event);
        final ApprovalCompleteBizExchangeEventBo bo = event.getEvent();
        final ApprovalBizExchange exchange = bo.getExchange();
        final Approval approval = bo.getApproval();
        final ApprovalBizExchangeConfigBo exchangeConfigBo = bo.getExchangeConfigBo();

        final BaseApprovalBizExchangeHandle bizExchangeHandle = exchangeHandles.stream()
                .filter(handle -> handle.isSupport(approval, exchange))
                .findFirst().orElse(null);

        if (Objects.isNull(bizExchangeHandle)) {
            return;
        }
        final ApprovalBizHandleBo<ApprovalBizExchangeStatusEnum> handleResult =
                bizExchangeHandle.handle(approval, exchange, exchangeConfigBo);

        if (Objects.isNull(handleResult)) {
            return;
        }
        if (handleResult.getHandleFlag()) {
            exchange.setStatus(handleResult.getSuccessStatus());
            exchange.setFailCount(Constant.BIZ_APPROVAL.ZERO_COUNT);
            if (ApprovalBizExchangeStatusEnum.COMPLETED == exchange.getStatus()) {
                exchange.setFailCount(Constant.BIZ_APPROVAL.SUCCESS_COUNT);
            }
        } else {
            log.error("approval exchange handle result fail,message={}", handleResult.getMessage());
            exchange.setFailCount(Optional.ofNullable(exchange.getFailCount()).map(count -> ++count)
                    .orElse(Constant.BIZ_APPROVAL.MIN_FAIL_COUNT));

            if (exchange.getFailCount() >= Constant.BIZ_APPROVAL.MAX_FAIL_COUNT
                    || !handleResult.getFailStatus().getNeedRetry()) {
                exchange.setStatus(handleResult.getFailStatus());
                exchange.setFailCount(Constant.BIZ_APPROVAL.MAX_FAIL_COUNT);
            }
        }
        approvalBizExchangeRepository.saveAndFlush(exchange);

        if (Boolean.TRUE.equals(exchange.getStatus().getFinalHandle())) {
            // publish approval finish event
            log.info("approval biz exchange post handle result->{}", event);
            publisher.publishEvent(new ApprovalBizExchangeFinishEvent(
                    ApprovalBizExchangeFinishEventBo.builder()
                            .exchange(exchange)
                            .approvalStatus(approval.getStatus())
                            .executeResult(handleResult.getHandleFlag())
                            .message(handleResult.getMessage())
                            .build()));
        }
    }

}


