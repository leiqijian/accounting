package com.liquido.statement.event.listener;

import java.util.Objects;

import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.base.pojo.dto.MerchantDto;
import com.liquido.statement.event.DailyCutSuccessEvent;
import com.liquido.statement.feign.BaseService;
import com.liquido.statement.pojo.bo.DailyCutSuccessBo;
import com.liquido.statement.service.GlobalAccountService;
import com.liquido.statement.service.monitor.lark.LarkRobotMonitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Global Payout main account auto recharge the payout subAccount
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoChargeSubAccountEventListener {

    private final GlobalAccountService globalAccountService;

    private final LarkRobotMonitor larkRobotMonitor;

    private final BaseService baseService;

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void autoChargeSubAccountEventListener(final DailyCutSuccessEvent event) {
        log.info("[DailyCutSuccessEvent] Global Payout main account "
                + "auto recharge the payout subAccount");

        if (Objects.isNull(event)
                || Objects.isNull(event.getEventArgs())
                || CollectionUtils.isEmpty(event.getEventArgs().getBillList())) {
            return;
        }

        final long start = System.currentTimeMillis();
        log.info("[DailyCutSuccessEvent] begin process payout account auto recharge. eventArgs={}",
                event.getEventArgs());

        for (final DailyCutSuccessBo bo : event.getEventArgs().getBillList()) {
            if (TransactionTypeCodeEnum.PAY_OUT == bo.getTransactionTypeCode()) {
                try {
                    globalAccountService.autoRechargeToSubAccount(bo.getAccountId());
                } catch (Exception e) {
                    log.error("Auto Recharge SubAccount Error:", e);
                    larkRobotMonitor.error("Auto Recharge SubAccount Error",
                            buildAutoChargeSubAccountContent(bo), e.getMessage());
                }
            }
        }

        log.info("[DailyCutSuccessEvent] end process payout account auto recharge. ts={}ms",
                (System.currentTimeMillis() - start));
    }


    public String buildAutoChargeSubAccountContent(final DailyCutSuccessBo bo) {
        final MerchantDto merchantDto = baseService.getMerchantById(bo.getMerchantId());

        return new StringBuilder()
                .append("**Merchant:** ")
                .append(merchantDto.getCode()).append("\\n")
                .append("**AccountId :** ")
                .append(bo.getAccountId()).append("\\n")
                .append("**BillId :** ")
                .append(bo.getBillId()).append("\\n")
                .append("**Country :** ")
                .append(bo.getCountryCode().getCode()).append("\\n")
                .append("**Transaction Type :** ")
                .append(bo.getTransactionTypeCode().getCode()).append("\\n")
                .toString();
    }
}
