package com.liquido.statement.service.settlement.handler;

import java.math.BigDecimal;
import java.util.List;

import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.statement.pojo.vo.TransactionMoneyVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * From CO country and status must be IN_PROGRESS -> REJECTED
 * just fee is charged, and the principal amount is not deducted.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutRejectedDebitHandler extends AbstractAccountSettlementHandler {

    @Override
    public BusinessStrategyEnum getStrategy() {
        return BusinessStrategyEnum.PAY_OUT_REJECTED_DEBIT;
    }

    @Override
    public void beforeExecuteSettlement(final List<TransactionMoneyVo> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        for (final TransactionMoneyVo order : orderList) {
            // Reset order amount and settlement amount
            order.setAmount(BigDecimal.ZERO);
            order.setSettlementAmount(BigDecimal.ZERO);
            order.setSettlementAmountUsd(BigDecimal.ZERO);

        }
    }
}
