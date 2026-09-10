package com.liquido.statement.service.settlement;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.liquido.base.enums.BusinessStrategyEnum;
import com.liquido.base.enums.DirectionTypeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.statement.exception.StatementExceptionCode;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * PayIn or Payout settlement provider factory
 */
@Slf4j
@Component
public class SettlementProviderFactory {

    private static final Map<BusinessStrategyEnum, AccountSettlementHandler> settlementProviderMap =
            Maps.newConcurrentMap();

    private SettlementProviderFactory(
            final ObjectProvider<List<AccountSettlementHandler>> provider) {

        final List<AccountSettlementHandler> strategyList = provider.getIfAvailable();
        if (!CollectionUtils.isEmpty(strategyList)) {
            settlementProviderMap.putAll(strategyList.stream()
                    .collect(Collectors.toMap(AccountSettlementHandler::getStrategy,
                            objSelf -> objSelf, (k1, k2) -> k1)));
        }
    }

    public <T extends AccountSettlementHandler> T getProviderFactory(final String transactionType,
                                                                     final String directionType) {
        if (StringUtils.isBlank(transactionType) || StringUtils.isBlank(directionType)) {
            throw StatementExceptionCode.SETTLEMENT_STRATEGY_UNDEFINED.exception();
        }

        return this.getProviderFactory(BusinessStrategyEnum.parse(transactionType, directionType));
    }

    public <T extends AccountSettlementHandler> T getProviderFactory(
            final TransactionTypeCodeEnum transactionTyp,
            final DirectionTypeEnum directionType) {

        if (Objects.isNull(transactionTyp) || Objects.isNull(directionType)) {
            throw StatementExceptionCode.SETTLEMENT_STRATEGY_UNDEFINED.exception();
        }

        return this.getProviderFactory(BusinessStrategyEnum.parse(transactionTyp, directionType));
    }

    public <T extends AccountSettlementHandler> T getProviderFactory(
            final BusinessStrategyEnum settlementStrategy) {

        if (settlementStrategy == null) {
            throw StatementExceptionCode.SETTLEMENT_STRATEGY_UNDEFINED.exception();
        }

        return (T) settlementProviderMap.get(settlementStrategy);
    }
}
